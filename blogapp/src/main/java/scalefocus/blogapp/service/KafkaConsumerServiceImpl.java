package scalefocus.blogapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.repository.opensearch.BlogOpenSearchRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

  private final BlogOpenSearchRepository blogOpenSearchRepository;

  @KafkaListener(topics = {"createBlog"})
  public void receiveCreateMessage(@Payload Blog blog) {
    log.info("create message received: {}", blog);
    blogOpenSearchRepository.save(blog);
  }

  @KafkaListener(topics = {"updateBlog"})
  public void receiveUpdateMessage(@Payload Blog blog) {
    log.info("update message received: {}", blog);
    blogOpenSearchRepository.update(blog);
  }

  @KafkaListener(topics = {"deleteBlog"})
  public void receiveDeleteMessage(@Payload Blog blog) {
    log.info("delete message received: {}", blog);
    blogOpenSearchRepository.delete(blog);
  }
}
