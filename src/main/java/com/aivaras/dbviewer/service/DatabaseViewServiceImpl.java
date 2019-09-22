package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.data.repository.ConnectionDetailsRepository;
import com.aivaras.dbviewer.exception.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

@Service
public class DatabaseViewServiceImpl implements DatabaseViewService {

    private static final String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";

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
        Pattern pattern = Pattern.compile("[a-z][a-zA-Z0-9_]+");
        if (!pattern.matcher(tableName).matches()){
            throw new IllegalArgumentException("Table name not allowed: " + tableName);
        }
        if (!pattern.matcher(schemaName).matches()){
            throw new IllegalArgumentException("Schema name not allowed: " + schemaName);
        }
        String query = String.format("SELECT * FROM \"%s\".%s LIMIT ? OFFSET ?", schemaName, tableName);
        ConnectionDetails details = repository.findById(connectionDetailsId).orElseThrow(EntityNotFoundException::new);
        return (List<Map<String, Object>>) getResultList(this::extractResults, details, query, limit, offset);
    }

    private List<?> getResultList(Function<ResultSet, List<?>> fun, ConnectionDetails details, String query, Object ... params){
        try (Connection connection = getConnection(details)){
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++){
                statement.setObject(i + 1, params[i]);
            }
            ResultSet rs = statement.executeQuery();
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

    private Connection getConnection(ConnectionDetails details)  {
        try {
            Class.forName(POSTGRES_DRIVER_CLASS_NAME);
            return DriverManager.getConnection(getJdbcUrl(details), details.getUsername(), details.getPassword());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(String.format("JDBC driver was not found by class name: %s", POSTGRES_DRIVER_CLASS_NAME), e);
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected exception thrown on connection to database attempt", e);
        }
    }

    private String getJdbcUrl(ConnectionDetails details) {
        return String.format("jdbc:postgresql://%s:%d/%s", details.getHost(), details.getPort(), details.getDatabaseName());
    }


}
