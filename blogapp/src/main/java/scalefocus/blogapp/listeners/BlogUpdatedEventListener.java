package scalefocus.blogapp.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import scalefocus.blogapp.events.BlogUpdatedEvent;
import scalefocus.blogapp.repository.opensearch.BlogOpenSearchRepository;


@Component
@Slf4j
@RequiredArgsConstructor
public class BlogUpdatedEventListener {

    private final BlogOpenSearchRepository blogElasticSearchRepository;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processBlogCreatedEvent(BlogUpdatedEvent event) {
        log.info("Update Event received: " + event);
        blogElasticSearchRepository.update(event.getBlog());
    }
}
