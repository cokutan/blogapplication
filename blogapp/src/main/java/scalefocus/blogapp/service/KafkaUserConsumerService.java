package scalefocus.blogapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.repository.BlogUserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaUserConsumerService {

  private final BlogUserRepository blogUserRepository;

  @KafkaListener(topics = {"createUser"})
  public void receiveCreateMessage(@Payload BlogUser blogUser) {
    if (!blogUserRepository.existsByUsername(blogUser.getUsername())) {
      blogUserRepository.save(blogUser);
    }
  }
}
