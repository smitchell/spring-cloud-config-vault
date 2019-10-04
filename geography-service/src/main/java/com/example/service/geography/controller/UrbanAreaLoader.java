package com.example.service.geography.controller;

import com.example.service.geography.domain.UrbanArea;
import com.example.service.geography.repository.UrbanAreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class UrbanAreaLoader {

    private final String importFilePath;
    private final int importBatchSize;
    private final UrbanAreaRepository urbanAreaRepository;

    @Autowired
    public UrbanAreaLoader(
            @Value("${import.batch-size}") final int importBatchSize,
            @Value("${import.file-path}") final String importFilePath,
            final UrbanAreaRepository urbanAreaRepository) {
        this.importBatchSize = importBatchSize;
        this.importFilePath = importFilePath;
        this.urbanAreaRepository = urbanAreaRepository;
    }

    @PostConstruct
    public void setup() throws IOException {
        load();
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
            final int expectedColumns = 9;
            List<UrbanArea> batch = new ArrayList<>();
            while (line != null) {
                log.info("Loading " + line);
                if (count > 0) {
                    String[] values = line.split("\t");
                    batch.add(processRow(values));
                    if (batch.size() >= importBatchSize) {
                        urbanAreaRepository.saveAll(batch);
                        batch.clear();
                    }
                }
                count++;
                line = reader.readLine();
            }
            if (!batch.isEmpty()) {
                urbanAreaRepository.saveAll(batch);
            }
            return count;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private UrbanArea processRow(String[] values) {
        UrbanArea urbanArea = new UrbanArea();
        urbanArea.setGeoId(values[0]);
        urbanArea.setName(values[1]);
        urbanArea.setType(values[2]);
        urbanArea.setAreaLand(Double.parseDouble(values[3]));
        urbanArea.setAreaWater(Double.parseDouble(values[4]));
        urbanArea.setLandSqMiles(BigDecimal.valueOf(Double.parseDouble(values[5])).setScale(2, RoundingMode.HALF_UP));
        urbanArea.setWaterSqMiles(BigDecimal.valueOf(Double.parseDouble(values[6])).setScale(2, RoundingMode.HALF_UP));
        urbanArea.setLatitude(BigDecimal.valueOf(Double.parseDouble(values[7])).setScale(6, RoundingMode.HALF_UP));
        urbanArea.setLongitude(BigDecimal.valueOf(Double.parseDouble(values[8])).setScale(6, RoundingMode.HALF_UP));
        return urbanArea;
    }

    public boolean isLoaded() {
        return urbanAreaRepository.count() > 0;
    }
}
