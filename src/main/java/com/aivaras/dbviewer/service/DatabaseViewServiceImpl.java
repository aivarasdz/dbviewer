package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.data.repository.ConnectionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.aivaras.dbviewer.util.JdbcUtils.executeQuery;
import static com.aivaras.dbviewer.util.JdbcUtils.getConnection;
import static com.aivaras.dbviewer.util.StringUtils.throwExceptionIfNotIdentifierPattern;

@Service
public class DatabaseViewServiceImpl implements DatabaseViewService {

    @Autowired
    private ConnectionDetailsRepository repository;


    @Override
    public List<Map<String, Object>> getDatabaseSchemas(Long connectionDetailsId) {
        String query = "SELECT schema_name FROM information_schema.schemata";
        ConnectionDetails details = repository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        return (List<Map<String, Object>>) getResultList(this::extractResults, details, query);
    }

    @Override
    public List<Map<String, Object>> getTablesForSchema(Long connectionDetailsId, String schemaName) {
        String query = "SELECT * FROM information_schema.tables WHERE table_schema = ?";
        ConnectionDetails details = repository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        return (List<Map<String, Object>>) getResultList(this::extractResults, details, query, schemaName);
    }

    @Override
    public List<Map<String,Object>> getColumnsForTableInSchema(Long connectionDetailsId, String tableName, String schemaName) {
        String query = "SELECT * FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
        ConnectionDetails details = repository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        return (List<Map<String, Object>>) getResultList(this::extractResults, details, query, schemaName, tableName);
    }

    @Override
    public List<Map<String, Object>> getRowsForTableInSchema(Long connectionDetailsId, String tableName, String schemaName, Long offset, Long limit) {
        throwExceptionIfNotIdentifierPattern(tableName, schemaName);
        String query = String.format("SELECT * FROM \"%s\".%s LIMIT ? OFFSET ?", schemaName, tableName);
        ConnectionDetails details = repository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        return (List<Map<String, Object>>) getResultList(this::extractResults, details, query, limit, offset);
    }

    private List<?> getResultList(Function<ResultSet, List<?>> fun, ConnectionDetails details, String query, Object ... params){
        try (Connection connection = getConnection(details)){
            ResultSet rs = executeQuery(connection, query, params);
            return fun.apply(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected exception thrown on database schema query.");
        }
    }

    private List<Map<String, Object>> extractResults(ResultSet rs) {
        List<Map<String, Object>> tableList = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> columnData = new HashMap<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    columnData.put(metaData.getColumnName(i), rs.getObject(i));
                }
                tableList.add(columnData);
            }
            return tableList;
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected exception thrown on database schema query.");
        }
    }
}
