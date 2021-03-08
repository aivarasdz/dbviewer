package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.data.model.ConnectionDetailsModel;
import com.aivaras.dbviewer.data.repository.ConnectionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConnectionDetailsServiceImpl implements ConnectionDetailsService {

    @Autowired
    private ConnectionDetailsRepository repository;

    @Override
    public ConnectionDetailsModel addConnectionDetails(ConnectionDetailsModel model) {
        return mapToModel(repository.save(mapToDetails(model)));
    }

    @Override
    public ConnectionDetailsModel updateConnectionDetails(ConnectionDetailsModel model) {
        if (model.getId() == null) {
            throw new IllegalArgumentException("id can't be null");
        }
        if (!repository.existsById(model.getId())){
            throw new EntityNotFoundException();
        }
        ConnectionDetails details = repository.save(mapToDetails(model));
        return mapToModel(details);
    }

    @Override
    public ConnectionDetailsModel getConnectionDetails(Long id) {
        return mapToModel(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<ConnectionDetailsModel> getConnectionDetailsList() {
        return repository.findAll().stream().map(this::mapToModel).collect(Collectors.toList());
    }

    @Override
    public void deleteConnectionDetails(Long id) {
        if (!repository.existsById(id)){
            throw new EntityNotFoundException();
        }
        repository.deleteById(id);
    }

    private ConnectionDetails mapToDetails(ConnectionDetailsModel model){
        ConnectionDetails details = new ConnectionDetails();
        details.setId(model.getId());
        details.setName(model.getName());
        details.setDatabaseName(model.getDatabaseName());
        details.setHost(model.getHost());
        details.setPort(model.getPort());
        details.setUsername(model.getUsername());
        details.setPassword(model.getPassword());
        return details;
    }

    private ConnectionDetailsModel mapToModel(ConnectionDetails details) {
        ConnectionDetailsModel model = new ConnectionDetailsModel();
        model.setId(details.getId());
        model.setName(details.getName());
        model.setDatabaseName(details.getDatabaseName());
        model.setHost(details.getHost());
        model.setPort(details.getPort());
        model.setUsername(details.getUsername());
        model.setPassword(details.getPassword());
        return model;
    }
}
