package com.example.service.geography.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Example Urban Area data from https://www.census.gov/geographies/reference-files/2017/geo/gazetter-file.html
 * ISO-8859-1
 */
@Entity
@Data
@EqualsAndHashCode
public class UrbanArea implements Serializable {

    @Id
    private String geoId;
    @Column(unique = true)
    private String name;
    private String type;
    private double areaLand;
    private double areaWater;
    private BigDecimal landSqMiles;
    private BigDecimal waterSqMiles;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
