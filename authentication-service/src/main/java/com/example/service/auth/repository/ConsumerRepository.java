package com.example.service.auth.repository;

import com.example.service.auth.domain.Consumer;
import com.example.service.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, String> {

}
