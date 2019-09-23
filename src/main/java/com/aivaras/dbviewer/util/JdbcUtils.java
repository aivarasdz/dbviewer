package com.aivaras.dbviewer.util;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.exception.ConfigurationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtils {

    private static final String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public static ResultSet executeQuery(Connection connection, String query, Object ... param) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int index = 0, queryPos = 1; index < param.length; index++, queryPos++){
            statement.setObject(queryPos, param[index]);
        }
        return statement.executeQuery();
    }

    public static Connection getConnection(ConnectionDetails details)  {
        try {
            Class.forName(POSTGRES_DRIVER_CLASS_NAME);
            return DriverManager.getConnection(getJdbcUrl(details), details.getUsername(), details.getPassword());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(String.format("JDBC driver was not found by class name: %s", POSTGRES_DRIVER_CLASS_NAME), e);
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected exception thrown on connection to database attempt", e);
        }
    }

    private static String getJdbcUrl(ConnectionDetails details) {
        return String.format("jdbc:postgresql://%s:%d/%s", details.getHost(), details.getPort(), details.getDatabaseName());
    }
}
