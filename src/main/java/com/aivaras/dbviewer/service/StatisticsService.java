package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.model.ColumnStatistics;

import java.util.Map;

public interface StatisticsService {
    ColumnStatistics getColumnStatistics(Long connectionDetailsId, String schemaName, String tableName, String columnName);

    Map<String, Long> getTableStatistics(Long connectionDetailsId, String schemaName, String tableName);
}
