package scalefocus.blogapp.containers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class MongoDBTestContainer extends  MongoDBContainer {

  public MongoDBTestContainer() {
    super(DockerImageName.parse("mongo:7.0.0-rc2-jammy"));
    withCopyFileToContainer(
          MountableFile.forClasspathResource("mongo-init.js", 0777), "/docker-entrypoint-initdb.d/mongo-init.js");

    withExposedPorts(27017);
    setWaitStrategy(
        (new HttpWaitStrategy())
            .forPort(27017)
            .withReadTimeout(Duration.of(2, ChronoUnit.MINUTES))
            .forStatusCodeMatching(r -> r == 200 || r == 401));
    ;
  }
}
