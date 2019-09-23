package com.aivaras.dbviewer.controller;

import com.aivaras.dbviewer.data.model.ColumnStatistics;
import com.aivaras.dbviewer.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/db/{id}/statistics/columns")
    public ColumnStatistics getColumnStatistics(@PathVariable("id") Long connectionDetailsId, @RequestParam("schema") String schemaName, @RequestParam("table") String tableName, @RequestParam("column") String columnName){
        return statisticsService.getColumnStatistics(connectionDetailsId, schemaName, tableName, columnName);
    }
    @GetMapping("/db/{id}/statistics/tables")
    public Map<String, Long> getTableStatistics(@PathVariable("id") Long connectionDetailsId, @RequestParam("schema") String schemaName, @RequestParam("table") String tableName) {
        return statisticsService.getTableStatistics(connectionDetailsId, schemaName, tableName);
    }
}
