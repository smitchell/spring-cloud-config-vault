package com.example.geography.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode
public class UrbanCluster implements Serializable {

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
