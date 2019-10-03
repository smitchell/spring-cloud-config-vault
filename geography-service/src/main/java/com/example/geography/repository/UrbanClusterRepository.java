package com.example.geography.repository;

import com.example.geography.domain.UrbanCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource
public interface UrbanClusterRepository extends CrudRepository<UrbanCluster, String> {

    Page<UrbanCluster> findAll(Pageable pageable);

    Optional<UrbanCluster> findByName(@Param("name") String name);

}
