package com.example.geography.test.integration;

import com.example.geography.domain.UrbanCluster;
import com.example.geography.test.UrbanClusterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "user", roles = "USER")
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateUrbanCluster() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Ignore
    @Test
    public void testFindUrbanClusterByName() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
        mockMvc.perform(get("/urbanClusters/search/findByName".concat(urbanCluster.getName()))
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateUrbanCluster() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        urbanCluster.setType("T");
        json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(put("/urbanClusters/".concat(urbanCluster.getGeoId()))
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Ignore
    @Test
    public void testListUrbanClusters() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .content(json)
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanClusters/search/findAll"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$[0].name").value(urbanCluster.getName()))
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"));

    }

    @Test
    public void testFindUrbanCluster() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanClusters/".concat(urbanCluster.getGeoId()))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value(urbanCluster.getName()));

    }

    @Ignore
    @Test
    public void testPageUrbanCluster() throws Exception {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanCluster);
        mockMvc.perform(post("/urbanClusters")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanCluster/search/pageAll")
                .param("page", "0")
                .param("limit", "50")
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].name").value(urbanCluster.getName()))
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"));
    }

}
