package com.aivaras.dbviewer.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConnectionDetailsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testAddConnectionDetails() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/db").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateConnectionDetails() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/db").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteConnectionDetails() throws Exception {
        long id = 1;
        mvc.perform(MockMvcRequestBuilders.delete("/db/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetConnectionDetails() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/db").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetConnectionDetailsById() throws Exception {
        long id = 1;
        mvc.perform(MockMvcRequestBuilders.get("/db/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
