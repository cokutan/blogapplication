package scalefocus.blogapp.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import scalefocus.blogapp.events.BlogDeletedEvent;
import scalefocus.blogapp.repository.opensearch.BlogElasticSearchRepository;


@Component
@Slf4j
@RequiredArgsConstructor
public class BlogDeletedEventListener {

    private final BlogElasticSearchRepository blogElasticSearchRepository;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processBlogDeletedEvent(BlogDeletedEvent event) {
        log.info("Delete Event received: " + event);
        blogElasticSearchRepository.delete(event.getBlog());
    }
}
