package scalefocus.blogapp.service;

import scalefocus.blogapp.domain.Blog;

public interface KafkaProducerService {

  void sendUpdateMessage(Blog blog);

  void sendCreateMessage(Blog blog);

  void sendDeleteMessage(Blog blog);
}
