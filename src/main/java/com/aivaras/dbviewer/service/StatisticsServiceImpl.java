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
        String statisticsQuery = String.format("SELECT min(%s), max(%s), avg(%s), count(*) FROM \"%s\".\"%s\"", columnName, columnName, columnName, schemaName, tableName);
        ResultSet statisticsResultSet = executeQuery(connection, statisticsQuery);
        if (!statisticsResultSet.next()){
            return null;
        }
        Long min = statisticsResultSet.getLong(1);
        Long max = statisticsResultSet.getLong(2);
        Double average = statisticsResultSet.getDouble(3);
        Long count = statisticsResultSet.getLong(4);
        String middleElementQuery = String.format("SELECT %s FROM \"%s\".\"%s\" ORDER BY %s LIMIT ? OFFSET ?", columnName, schemaName, tableName, columnName);
        Long offset = null;
        Integer limit = null;
        if (count % 2 == 0){
            offset = count / 2 - 1;
            limit = 2;
        } else {
            offset = count / 2;
            limit = 1;
        }
        ResultSet middleElementResult = executeQuery(connection, middleElementQuery, limit, offset);
        Double median = null;
        if (middleElementResult.next()){
            if (limit == 2){
                Long e1 = middleElementResult.getLong(1);
                middleElementResult.next();
                Long e2 = middleElementResult.getLong(1);
                median = (e1.doubleValue() + e2.doubleValue())/2;
            } else {
                median = (double) middleElementResult.getLong(1);
            }
        }
        return new ColumnStatistics<>(columnName, min, max, average, median);
    }

    private ColumnStatistics<Double> getDoubleStatistics(Connection connection, String schemaName, String tableName, String columnName) throws SQLException {
        String statisticsQuery = String.format("SELECT min(%s), max(%s), avg(%s), count(*) FROM \"%s\".\"%s\"", columnName, columnName, columnName, schemaName, tableName);
        ResultSet statisticsResultSet = executeQuery(connection, statisticsQuery);
        if (!statisticsResultSet.next()){
            return null;
        }
        Double min = statisticsResultSet.getDouble(1);
        Double max = statisticsResultSet.getDouble(2);
        Double average = statisticsResultSet.getDouble(3);
        Long count = statisticsResultSet.getLong(4);
        String middleElementQuery = String.format("SELECT %s FROM \"%s\".\"%s\" ORDER BY %s LIMIT ? OFFSET ?", columnName, schemaName, tableName, columnName);
        Long offset = null;
        Integer limit = null;
        if (count % 2 == 0){
            offset = count / 2 - 1;
            limit = 2;
        } else {
            offset = count / 2;
            limit = 1;
        }
        ResultSet middleElementResult = executeQuery(connection, middleElementQuery, limit, offset);
        Double median = null;
        if (middleElementResult.next()){
            if (limit == 2){
                double e1 = middleElementResult.getDouble(1);
                middleElementResult.next();
                double e2 = middleElementResult.getDouble(1);
                median = (e1 + e2)/2;
            } else {
                median = middleElementResult.getDouble(1);
            }
        }
        return new ColumnStatistics<>(columnName, min, max, average, median);
    }


}
