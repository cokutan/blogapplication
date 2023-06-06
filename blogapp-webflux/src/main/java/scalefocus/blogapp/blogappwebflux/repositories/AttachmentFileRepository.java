package scalefocus.blogapp.blogappwebflux.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import scalefocus.blogapp.blogappwebflux.domain.AttachmentFile;

public interface AttachmentFileRepository extends ReactiveMongoRepository<AttachmentFile, String> {
    public Mono<Void> deleteAttachmentFileById(String id);
}
