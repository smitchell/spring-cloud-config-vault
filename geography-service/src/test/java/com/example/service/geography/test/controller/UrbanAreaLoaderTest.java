package com.example.service.geography.test.controller;

import com.example.service.geography.controller.UrbanAreaLoader;
import com.example.service.geography.repository.UrbanAreaRepository;
import com.example.service.geography.test.UrbanAreaFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UrbanAreaLoaderTest {

    @Autowired
    private UrbanAreaLoader urbanAreaLoader;

    @Autowired
    private UrbanAreaRepository urbanAreaRepository;

    @Value("${importFilePath}")
    private String importFilePath;

    @Test
    public void testIsLoaded() {
        boolean isLoaded = urbanAreaLoader.isLoaded();
        assertThat(isLoaded, equalTo(false));
        urbanAreaRepository.save(UrbanAreaFactory.build());
        isLoaded = urbanAreaLoader.isLoaded();
        assertThat(isLoaded, equalTo(true));
    }

    @Test
    public void testLoadFile() throws IOException {
        int count = urbanAreaLoader.load();
        assertThat(count, greaterThan(0));
    }

    @Test
    public void testLoadFileWithParam() throws IOException {
        int count = urbanAreaLoader.loadFile("/2017_Gaz_ua_national.txt");
        assertThat(count, equalTo(5));
    }
}
