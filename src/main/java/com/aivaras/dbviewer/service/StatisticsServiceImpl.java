package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.data.model.ColumnStatistics;
import com.aivaras.dbviewer.data.repository.ConnectionDetailsRepository;
import com.aivaras.dbviewer.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.aivaras.dbviewer.util.JdbcUtils.executeQuery;
import static com.aivaras.dbviewer.util.StringUtils.throwExceptionIfNotIdentifierPattern;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final static List<String> LONG_TYPES = Arrays.asList("int", "int2", "int4", "int8");
    private final static List<String> DOUBLE_TYPES = Arrays.asList("float", "float4", "float8");
    private final static List<String> STRING_TYPES = Arrays.asList("char", "varchar", "text");

    @Autowired
    private DatabaseViewService databaseViewService;
    @Autowired
    private ConnectionDetailsRepository connectionDetailsRepository;

    @Override
    public ColumnStatistics getColumnStatistics(Long connectionDetailsId, String schemaName, String tableName, String columnName) {
        throwExceptionIfNotIdentifierPattern(schemaName, tableName, columnName);
        String getColumnTypeQuery = "SELECT udt_name FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?";
        ConnectionDetails details = connectionDetailsRepository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        try (Connection connection = JdbcUtils.getConnection(details)){
            ResultSet rs = executeQuery(connection, getColumnTypeQuery, schemaName, tableName, columnName);
            if (rs.next()) {
                String columnType = rs.getString(1);
                return getColumnStatistics(connection, schemaName, tableName, columnName, columnType);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected sql exception thrown");
        }
        return null;
    }

    @Override
    public Map<String, Long> getTableStatistics(Long connectionDetailsId, String schemaName, String tableName) {
        throwExceptionIfNotIdentifierPattern(schemaName, tableName);
        ConnectionDetails connectionDetails = connectionDetailsRepository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        Long columnCount = getColumnCount(connectionDetailsId, schemaName, tableName);
        Map<String, Long> tableStatistics = new HashMap<>();
        tableStatistics.put("columnCount", columnCount);
        String query = String.format("SELECT count(*) FROM \"%s\".\"%s\"", schemaName, tableName);
        try (Connection connection = JdbcUtils.getConnection(connectionDetails)) {
            ResultSet rs = executeQuery(connection, query);
            if (rs.next()){
                Long count = rs.getLong(1);
                tableStatistics.put("rowCount", count);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected sql exception thrown");
        }
        return tableStatistics;
    }

    private Long getColumnCount(Long connectionDetailsId, String schemaName, String tableName){
        List columnList = databaseViewService.getColumnsForTableInSchema(connectionDetailsId, tableName, schemaName);
        return columnList != null && columnList.size() > 0 ? (long) columnList.size() : null;
    }

    private ColumnStatistics getColumnStatistics(Connection connection, String schemaName, String tableName, String columnName, String columnType) throws SQLException, UnsupportedOperationException {
        if (STRING_TYPES.contains(columnType)) return getStringStatistics(connection, schemaName, tableName, columnName);
        if (LONG_TYPES.contains(columnType)) return getLongStatistics(connection, schemaName, tableName, columnName);
        if (DOUBLE_TYPES.contains(columnType)) return getDoubleStatistics(connection, schemaName, tableName, columnName);
        throw new UnsupportedOperationException("Column type is not supported.");
    }

    private ColumnStatistics<String> getStringStatistics(Connection connection, String schemaName, String tableName, String columnName) throws SQLException {
        String maxStringValuesQuery = String.format("SELECT min(%s), max(%s) FROM \"%s\".\"%s\"", columnName, columnName, schemaName, tableName);
        ResultSet rs = connection.createStatement().executeQuery(maxStringValuesQuery);
        String min = null;
        String max = null;
        if (rs.next()){
            min = rs.getString(1);
            max = rs.getString(2);
            return new ColumnStatistics<>(columnName, min, max);
        }
        return new ColumnStatistics<>(columnName, min, max);
    }

    private ColumnStatistics<Long> getLongStatistics(Connection connection, String schemaName, String tableName, String columnName) throws SQLException {
        String maxStringValuesQuery = String.format("SELECT %s FROM \"%s\".\"%s\"", columnName, schemaName, tableName);
        ResultSet rs = connection.createStatement().executeQuery(maxStringValuesQuery);
        List<Long> valueList = new ArrayList<>();
        while (rs.next()){
            valueList.add(rs.getLong(1));
        }
        valueList = valueList.stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        LongSummaryStatistics statistics = valueList.stream().mapToLong(Long::longValue).summaryStatistics();
        Double median = null;
        if (!valueList.isEmpty()) {
            int size = valueList.size();
            int middle = size / 2;
            if (size % 2 == 0) {
                median = (valueList.get(middle - 1).doubleValue() + valueList.get(middle).doubleValue()) / 2.0;
            } else {
                median = valueList.get(middle).doubleValue();
            }
        }
        return new ColumnStatistics<>(columnName, statistics.getMin(), statistics.getMax(), statistics.getAverage(), median);
    }

    private ColumnStatistics<Double> getDoubleStatistics(Connection connection, String schemaName, String tableName, String columnName) throws SQLException {
        String maxStringValuesQuery = String.format("SELECT %s FROM \"%s\".\"%s\"", columnName, schemaName, tableName);
        ResultSet rs = connection.createStatement().executeQuery(maxStringValuesQuery);
        List<Double> valueList = new ArrayList<>();
        while (rs.next()){
            valueList.add(rs.getDouble(1));
        }
        valueList = valueList.stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        DoubleSummaryStatistics statistics = valueList.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        Double median = null;
        if (!valueList.isEmpty()) {
            int size = valueList.size();
            int middle = size / 2;
            if (size % 2 == 0) {
                median = (valueList.get(middle - 1) + valueList.get(middle)) / 2.0;
            } else {
                median = valueList.get(middle);
            }
        }
        return new ColumnStatistics<>(columnName, statistics.getMin(), statistics.getMax(), statistics.getAverage(), median);
    }


}
