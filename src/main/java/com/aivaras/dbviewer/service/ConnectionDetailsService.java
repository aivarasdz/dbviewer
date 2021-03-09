package com.aivaras.dbviewer.service;

import com.aivaras.dbviewer.data.model.ConnectionDetailsModel;

import java.util.List;

public interface ConnectionDetailsService {

    ConnectionDetailsModel addConnectionDetails(ConnectionDetailsModel model);
    ConnectionDetailsModel updateConnectionDetails(ConnectionDetailsModel model);
    ConnectionDetailsModel getConnectionDetails(Long id);
    List<ConnectionDetailsModel> getConnectionDetailsList();
    void deleteConnectionDetails(Long id);
}
