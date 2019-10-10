package com.example.service.auth.service;

import com.example.service.auth.domain.Consumer;
import com.example.service.auth.domain.User;
import com.example.service.auth.repository.ConsumerRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;


@Service
public class ConsumerService implements ClientDetailsService {

  private final ConsumerRepository consumerRepository;

  @Autowired
  public ConsumerService(
          final ConsumerRepository consumerRepository) {
    this.consumerRepository = consumerRepository;
  }

  @PostConstruct
  public void postConstruct() {
    // Initialize the Consumer database
    generateConsumer("geography-client", "bcN2?NrNF");
    // Being lazy and reusing the secret
    generateConsumer("proxy-client", "bcN2?NrNF");
  }

  private void generateConsumer(String clientId, String clientSecret) {
    Optional<Consumer> optional = consumerRepository.findById(clientId);
    if (!optional.isPresent()) {
      final Consumer consumer = new Consumer();
      consumer.setClientId(clientId);
      consumer.setScopeCsv("read,write,trust");
      consumer.setAutoApproveCsv("true");
      consumer.setAuthorizedGrantTypesCsv("password,refresh_token,authorization_code");
      consumer.setAccessTokenValiditySeconds(3600);
      consumer.setRefreshTokenValiditySeconds(3600);
      consumer.setRegisteredRedirectUrisCsv("http://localhost:9000/login,");
      consumer.setClientSecret(new BCryptPasswordEncoder().encode(clientSecret));
      consumerRepository.save(consumer);
    }
  }

  @Override
  @Transactional
  public ClientDetails loadClientByClientId(String clientId) {
    Optional<Consumer> optional = consumerRepository.findById(clientId);
    return optional.orElse(null);
  }


}
