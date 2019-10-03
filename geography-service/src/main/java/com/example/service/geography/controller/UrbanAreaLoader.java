package com.example.service.geography.controller;

import com.example.service.geography.repository.UrbanAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

@Controller
public class UrbanAreaLoader {

    private final String importFilePath;
    private final UrbanAreaRepository urbanAreaRepository;

    @Autowired
    public UrbanAreaLoader(
            @Value("${importFilePath}") final String importFilePath,
            final UrbanAreaRepository urbanAreaRepository) {
        this.importFilePath = importFilePath;
        this.urbanAreaRepository = urbanAreaRepository;
    }

    public int load() throws IOException {
        if (isLoaded()) {
            return 0;
        } else {
            return loadFile(importFilePath);
        }
    }

    public int loadFile(String filePath) throws IOException {
        BufferedReader reader = null;
        try {
            int count = 0;
            InputStream input = UrbanAreaLoader.class.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                if (count++ > 0) {
                    //process line
                }
                // read next line
                line = reader.readLine();
            }
            return count;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public boolean isLoaded() {
        return urbanAreaRepository.count() > 0;
    }
}
