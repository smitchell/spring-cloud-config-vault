package com.example.geography.test.repository;

import com.example.geography.domain.UrbanCluster;
import com.example.geography.repository.UrbanClusterRepository;
import com.example.geography.test.UrbanClusterFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringRunner.class)
@DataJpaTest()
public class UrbanClusterRepositoryTest {

    @Autowired
    private UrbanClusterRepository urbanClusterRepository;

    @Test
    public void testCreateUrbanCluster() {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        UrbanCluster savedCluster = urbanClusterRepository.save(urbanCluster);
        assertThat(savedCluster, equalTo(urbanCluster));
        Optional<UrbanCluster> optional = urbanClusterRepository.findById(urbanCluster.getGeoId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(urbanCluster));
    }
    @Test
    public void testFindUrbanClusterByName() {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        UrbanCluster savedCluster = urbanClusterRepository.save(urbanCluster);
        assertThat(savedCluster, equalTo(urbanCluster));
        Optional<UrbanCluster> optional = urbanClusterRepository.findByName(urbanCluster.getName());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(urbanCluster));
    }

    @Test
    public void testUpdateUrbanCluster() {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        UrbanCluster savedCluster = urbanClusterRepository.save(urbanCluster);
        savedCluster.setType("T");
        urbanClusterRepository.save(savedCluster);
        Optional<UrbanCluster> optional = urbanClusterRepository.findById(urbanCluster.getGeoId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), equalTo(savedCluster));
        assertThat(optional.get().getType(), equalTo("T"));
    }

    @Test
    public void testListUrbanClusters() {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        UrbanCluster savedCluster = urbanClusterRepository.save(urbanCluster);
        Iterable<UrbanCluster> iterable = urbanClusterRepository.findAll();
        assertThat(iterable.iterator().hasNext(), is(true));
    }

    @Test
    public void testPageUrbanCluster() {
        UrbanCluster urbanCluster = UrbanClusterFactory.build();
        UrbanCluster savedCluster = urbanClusterRepository.save(urbanCluster);
        Page<UrbanCluster> results = urbanClusterRepository.findAll(PageRequest.of(0,10));
        assertThat(results, notNullValue());
        assertThat((int)results.getTotalElements(), greaterThan(0));
    }
}
