package scalefocus.blogapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.BlogUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

  private final KafkaTemplate<String, BlogUser> kafkaTemplate;

  public void sendUserCreatedMessage(BlogUser user) {
    log.info("create message sent to kafka: {}", user);
    kafkaTemplate.send("createUser", user);
  }
}
