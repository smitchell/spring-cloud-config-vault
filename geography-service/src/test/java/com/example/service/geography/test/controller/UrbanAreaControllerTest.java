package com.example.service.geography.test.controller;

import com.example.service.geography.controller.UrbanAreaController;
import com.example.service.geography.repository.UrbanAreaRepository;
import com.example.service.geography.test.UrbanAreaFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UrbanAreaController.class)
public class UrbanAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrbanAreaRepository urbanAreaRepository;

    @Test
    @WithMockUser(username = "aUser")
    public void testPageUrbanAreas() throws Exception {
        final int page = 0;
        final int size = 50;
        given(urbanAreaRepository.findAll(PageRequest.of(page, size, Sort.by("name").ascending()))).willReturn(UrbanAreaFactory.buildPages());

        mockMvc.perform(MockMvcRequestBuilders.get("/listUrbanAreas")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").exists())
        .andExpect(jsonPath("$.content[0].name").value(UrbanAreaFactory.NAME));

    }

}
