package com.example.service.geography.test.integration;

import com.example.service.geography.domain.UrbanArea;
import com.example.service.geography.test.UrbanAreaFactory;
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
    public void testCreateUrbanArea() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testFindUrbanAreaById() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanAreas/".concat(urbanArea.getGeoId()))
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Ignore
    @Test
    public void testFindUrbanAreaByName() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
        mockMvc.perform(get("/urbanAreas/search/findByName".concat(urbanArea.getName()))
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testUpdateUrbanArea() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        urbanArea.setType("T");
        json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(put("/urbanAreas/".concat(urbanArea.getGeoId()))
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Ignore
    @Test
    public void testListUrbanAreas() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .content(json)
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanAreas/search/findAll"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("$[0].name").value(urbanArea.getName()))
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"));

    }

    @Test
    public void testFindUrbanArea() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanAreas/".concat(urbanArea.getGeoId()))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value(urbanArea.getName()));

    }

    @Ignore
    @Test
    public void testPageUrbanArea() throws Exception {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        String json = new ObjectMapper().writeValueAsString(urbanArea);
        mockMvc.perform(post("/urbanAreas")
                .contentType("application/json;charset=UTF-8")
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/urbanAreas/search/findAll")
                .param("page", "0")
                .param("limit", "50")
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].name").value(urbanArea.getName()))
                .andExpect(
                        header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"));
    }

}
