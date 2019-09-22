package com.aivaras.dbviewer.integration;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import com.aivaras.dbviewer.data.model.ConnectionDetailsModel;
import com.aivaras.dbviewer.data.repository.ConnectionDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConnectionDetailsControllerTest {

    private static final String TEST_CONNECTION_NAME = "test";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ConnectionDetailsRepository repository;

    @Transactional
    @Test
    public void testAddConnectionDetails() throws Exception {
        ConnectionDetails details = getTestConnectionDetails(TEST_CONNECTION_NAME);
        RequestBuilder createRequest = MockMvcRequestBuilders.put("/db")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(details));
        mvc.perform(createRequest).andExpect(status().isCreated());
        Assert.assertTrue(repository.findByName(TEST_CONNECTION_NAME).isPresent());
    }

    @Transactional
    @Test
    public void testUpdateConnectionDetails() throws Exception {
        ConnectionDetails details = getTestConnectionDetails(TEST_CONNECTION_NAME);
        repository.save(details);
        String updatedName = TEST_CONNECTION_NAME + "_updated";
        details.setName(updatedName);
        String updatedDetails = getJsonString(details);

        RequestBuilder updateRequest =  MockMvcRequestBuilders.post("/db")
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedDetails)
                .contentType(MediaType.APPLICATION_JSON);

        ResultMatcher detailsWereUpdated = result -> {
            ConnectionDetailsModel model = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ConnectionDetailsModel.class);
            Assert.assertEquals(updatedName, model.getName());
            Assert.assertTrue(repository.findByName(updatedName).isPresent());
        };

        mvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andExpect(detailsWereUpdated);
    }

    @Transactional
    @Test
    public void testDeleteConnectionDetails() throws Exception {
        ConnectionDetails details = repository.save(getTestConnectionDetails("test"));
        RequestBuilder deleteRequest = MockMvcRequestBuilders.delete("/db/" + details.getId())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

        Assert.assertFalse("Object with id should be deleted.", repository.existsById(details.getId()));
    }

    @Transactional
    @Test
    public void testGetConnectionDetails() throws Exception {
        ConnectionDetails details = repository.save(getTestConnectionDetails("test"));
        RequestBuilder getRequest = MockMvcRequestBuilders.get("/db").accept(MediaType.APPLICATION_JSON);
        ResultMatcher detailsFound = result -> {
            String jsonResponse = result.getResponse().getContentAsString();
            List<ConnectionDetailsModel> modelList = new ObjectMapper().readValue(jsonResponse, new TypeReference<List<ConnectionDetailsModel>>() {});
            Assert.assertFalse(modelList.isEmpty());
            Assert.assertEquals(1, modelList.size());
            ConnectionDetailsModel model = modelList.get(0);
            Assert.assertEquals(details.getName(), model.getName());
        };
        mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(detailsFound);
    }

    @Transactional
    @Test
    public void testGetConnectionDetailsById() throws Exception {
        ConnectionDetails details = repository.save(getTestConnectionDetails("test"));
        RequestBuilder getByIdRequest = MockMvcRequestBuilders.get("/db/" + details.getId()).accept(MediaType.APPLICATION_JSON);

        ResultMatcher detailsFound = result -> {
            String jsonResponse = result.getResponse().getContentAsString();
            ConnectionDetailsModel model = new ObjectMapper().readValue(jsonResponse, ConnectionDetailsModel.class);
            Assert.assertEquals(details.getName(), model.getName());
        };

        mvc.perform(getByIdRequest)
                .andExpect(status().isOk())
                .andExpect(detailsFound);
    }

    private ConnectionDetails getTestConnectionDetails(String name) {
        return getTestConnectionDetails(name, "localhost", 5432, "test", "testUser", "password");
    }

    private ConnectionDetails getTestConnectionDetails(String name, String host, int port, String dbName, String username, String password) {
        ConnectionDetails details = new ConnectionDetails();
        details.setName(name);
        details.setHost(host);
        details.setPort(port);
        details.setDatabaseName(dbName);
        details.setUsername(username);
        details.setPassword(password);
        return details;
    }

    private String getJsonString(ConnectionDetails connectionDetails) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(connectionDetails);
    }
}
