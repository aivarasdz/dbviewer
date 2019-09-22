package com.aivaras.dbviewer.service;

import java.util.List;
import java.util.Map;

public interface DatabaseViewService {
    List<Map<String, Object>> getDatabaseSchemas(Long connectionDetailsId);

    List<Map<String, Object>> getTablesForSchema(Long connectionDetailsId, String schemaName);

    List<Map<String, Object>> getColumnsForTableInSchema(Long connectionDetailsId, String tableName, String schemaName);

    List<Map<String, Object>> getRowsForTableInSchema(Long connectionDetailsId, String tableName, String schemaName, Long offset, Long limit);
}
