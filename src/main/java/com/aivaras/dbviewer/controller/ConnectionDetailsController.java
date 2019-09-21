package com.aivaras.dbviewer.controller;

import com.aivaras.dbviewer.data.model.ConnectionDetailsModel;
import com.aivaras.dbviewer.service.ConnectionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/db")
public class ConnectionDetailsController {

    @Autowired
    private ConnectionDetailsService connectionDetailsService;

    @PutMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createConnection(@RequestBody ConnectionDetailsModel model){
        connectionDetailsService.addConnectionDetails(model);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ConnectionDetailsModel updateConnection(@RequestBody ConnectionDetailsModel model){
        return connectionDetailsService.updateConnectionDetails(model);
    }

    @DeleteMapping(path = "/db/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteConnection(@PathVariable Long id){
        connectionDetailsService.deleteConnectionDetails(id);
    }

    @GetMapping
    public List<ConnectionDetailsModel> getConnectionList(){
        return connectionDetailsService.getConnectionDetailsList();
    }

    @GetMapping(path = "/db/{id}")
    public ConnectionDetailsModel getConnection(@PathVariable Long id) {
        return connectionDetailsService.getConnectionDetails(id);
    }
}
