package com.example.geography.test;

import com.example.geography.domain.UrbanCluster;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UrbanClusterFactory {

    public static UrbanCluster build() {
        UrbanCluster urbanCluster = new UrbanCluster();

        urbanCluster.setGeoId("00037");
        urbanCluster.setName("Abbeville, LA Urban Cluster");
        urbanCluster.setType("C");
        urbanCluster.setAreaLand(29189594);
        urbanCluster.setAreaWater(298416);
        urbanCluster.setLandSqMiles(BigDecimal.valueOf(11.27).setScale(2, RoundingMode.HALF_UP));
        urbanCluster.setWaterSqMiles(BigDecimal.valueOf(0.115).setScale(2, RoundingMode.HALF_UP));
        urbanCluster.setLatitude(BigDecimal.valueOf(29.967156).setScale(6, RoundingMode.HALF_UP));
        urbanCluster.setLongitude(BigDecimal.valueOf(-92.095966).setScale(6, RoundingMode.HALF_UP));
        return urbanCluster;
    }
}
