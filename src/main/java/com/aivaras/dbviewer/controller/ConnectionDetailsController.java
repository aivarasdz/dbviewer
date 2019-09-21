package com.aivaras.dbviewer.controller;

import com.aivaras.dbviewer.data.model.ConnectionDetailsModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/db")
public class ConnectionDetailsController {

    @PutMapping
    public void createConnection(@RequestBody ConnectionDetailsModel model, HttpServletRequest request, HttpServletResponse response){
        throw new UnsupportedOperationException("not implemented");
    }

    @PostMapping(path = "/{id}")
    public ConnectionDetailsModel updateConnection(@RequestBody ConnectionDetailsModel model, HttpServletRequest request, HttpServletResponse response){
        throw new UnsupportedOperationException("not implemented");
    }

    @DeleteMapping(path = "/{id}")
    public void deleteConnection(@PathVariable long id, HttpServletRequest request, HttpServletResponse response){
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping(path = "/{id}")
    public void getConnection(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping
    public void getConnectionList(HttpServletRequest request, HttpServletResponse response){
        throw new UnsupportedOperationException("not implemented");
    }
}
