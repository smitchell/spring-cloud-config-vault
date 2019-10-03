package com.example.service.geography.test.repository;

import com.example.service.geography.domain.UrbanArea;
import com.example.service.geography.repository.UrbanAreaRepository;
import com.example.service.geography.test.UrbanAreaFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class UrbanAreaRepositoryTest {

    @Autowired
    private UrbanAreaRepository urbanAreaRepository;

    @Test
    public void testCreateUrbanArea() {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        UrbanArea savedCluster = urbanAreaRepository.save(urbanArea);
        assertThat(savedCluster, equalTo(urbanArea));
        Optional<UrbanArea> optional = urbanAreaRepository.findById(urbanArea.getGeoId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(urbanArea));
    }
    @Test
    public void testFindUrbanAreaByName() {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        UrbanArea savedCluster = urbanAreaRepository.save(urbanArea);
        assertThat(savedCluster, equalTo(urbanArea));
        Optional<UrbanArea> optional = urbanAreaRepository.findByName(urbanArea.getName());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(urbanArea));
    }

    @Test
    public void testUpdateUrbanArea() {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        UrbanArea savedCluster = urbanAreaRepository.save(urbanArea);
        savedCluster.setType("T");
        urbanAreaRepository.save(savedCluster);
        Optional<UrbanArea> optional = urbanAreaRepository.findById(urbanArea.getGeoId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(savedCluster));
        assertThat(optional.get().getType(), equalTo("T"));
    }

    @Test
    public void testListUrbanAreas() {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        UrbanArea savedCluster = urbanAreaRepository.save(urbanArea);
        Iterable<UrbanArea> iterable = urbanAreaRepository.findAll();
        assertThat(iterable.iterator().hasNext(), is(true));
    }

    @Test
    public void testPageUrbanArea() {
        UrbanArea urbanArea = UrbanAreaFactory.build();
        UrbanArea savedCluster = urbanAreaRepository.save(urbanArea);
        Page<UrbanArea> results = urbanAreaRepository.findAll(PageRequest.of(0,10));
        assertThat(results, notNullValue());
        assertThat((int)results.getTotalElements(), greaterThan(0));
    }
}
