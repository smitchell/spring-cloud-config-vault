package com.example.service.geography.test;

import com.example.service.geography.domain.UrbanArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class UrbanAreaFactory {
    public static final String NAME = "Abbeville, LA Urban Cluster";

    public static UrbanArea build() {
        UrbanArea urbanArea = new UrbanArea();

        urbanArea.setGeoId("00037");
        urbanArea.setName(NAME);
        urbanArea.setType("C");
        urbanArea.setAreaLand(29189594);
        urbanArea.setAreaWater(298416);
        urbanArea.setLandSqMiles(BigDecimal.valueOf(11.27).setScale(2, RoundingMode.HALF_UP));
        urbanArea.setWaterSqMiles(BigDecimal.valueOf(0.115).setScale(2, RoundingMode.HALF_UP));
        urbanArea.setLatitude(BigDecimal.valueOf(29.967156).setScale(6, RoundingMode.HALF_UP));
        urbanArea.setLongitude(BigDecimal.valueOf(-92.095966).setScale(6, RoundingMode.HALF_UP));
        return urbanArea;
    }

    public static Page<UrbanArea> buildPages() {
        UrbanArea urbanArea = build();
        Page<UrbanArea> pages = new Page<UrbanArea>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super UrbanArea, ? extends U> function) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 50;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<UrbanArea> getContent() {
                return Collections.singletonList(urbanArea);
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return Sort.by("name").ascending();
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<UrbanArea> iterator() {
                return Collections.singletonList(urbanArea).iterator();
            }
        };
        return pages;
    }
}
