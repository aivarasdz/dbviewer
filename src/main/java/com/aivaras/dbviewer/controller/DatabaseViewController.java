package com.aivaras.dbviewer.controller;

import com.aivaras.dbviewer.service.DatabaseViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DatabaseViewController {

    @Autowired
    private DatabaseViewService databaseViewService;

    @GetMapping(path = "/db/view/{id}")
    public List<Map<String, Object>> getSchemas(@PathVariable("id") Long connectionDetailsId){
        return databaseViewService.getDatabaseSchemas(connectionDetailsId);
    }

    @GetMapping(path = "/db/view/{id}/tables")
    public List<Map<String, Object>> getSchemaTables(@PathVariable("id") Long connectionDetailsId, @RequestParam("schema") String schemaName){
        return databaseViewService.getTablesForSchema(connectionDetailsId, schemaName);
    }

    @GetMapping(path = "/db/view/{id}/tables/columns")
    public List<Map<String, Object>> getTableColumns(
            @PathVariable("id") Long connectionDetailsId,
            @RequestParam("schema") String schemaName,
            @RequestParam("table") String tableName
    ){
        return databaseViewService.getColumnsForTableInSchema(connectionDetailsId, tableName, schemaName);
    }

    @GetMapping(path = "/db/view/{id}/tables/rows")
    public List<Map<String, Object>> getTableRows(
            @PathVariable("id") Long connectionDetailsId,
            @RequestParam("schema") String schemaName,
            @RequestParam("table") String tableName,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Long offset,
            @RequestParam(value = "limit", required = false, defaultValue = "100") Long limit
    ){
        return databaseViewService.getRowsForTableInSchema(connectionDetailsId, tableName, schemaName, offset, limit);
    }
}
