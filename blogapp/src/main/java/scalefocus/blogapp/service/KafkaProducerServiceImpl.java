package scalefocus.blogapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.Blog;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Blog> kafkaTemplate;

    @Override
    public void sendCreateMessage(Blog blog) {
        log.info("create message sent to kafka: {}", blog);
        kafkaTemplate.send("createBlog", blog);
    }

    @Override
    public void sendUpdateMessage(Blog blog) {
        log.info("update message sent to kafka: {}", blog);
        kafkaTemplate.send("updateBlog", blog);
    }

    @Override
    public void sendDeleteMessage(Blog blog) {
        log.info("message sent to kafka: {}", blog);
        kafkaTemplate.send("deleteBlog", blog);
    }
}