package scalefocus.blogapp.service;

import scalefocus.blogapp.domain.Blog;

public interface KafkaConsumerService {

    void receiveCreateMessage(Blog blog);
    void receiveUpdateMessage(Blog blog);
    void receiveDeleteMessage(Blog blog);
}