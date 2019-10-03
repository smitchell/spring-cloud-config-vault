package com.example.service.geography.repository;

import com.example.service.geography.domain.UrbanArea;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface UrbanAreaRepository extends PagingAndSortingRepository<UrbanArea, String> {

    @RestResource
    Optional<UrbanArea> findByName(@Param("name") String name);
}
