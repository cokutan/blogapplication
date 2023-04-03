package scalefocus.blogapp.blogappwebflux.repositories;

import scalefocus.blogapp.blogappwebflux.domain.AttachmentFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AttachementFileRepository extends ReactiveCrudRepository<AttachmentFile, Long> {
}
