package scalefocus.blogapp.containers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.retry.support.RetryTemplateBuilder;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public final class MongoDBTestContainer extends MongoDBContainer {
  private static MongoDBTestContainer mongoDBTestContainer = null;

  private MongoDBTestContainer() {
    super(DockerImageName.parse("mongo:7.0.0-rc2-jammy"));
    withCopyFileToContainer(
        MountableFile.forClasspathResource("mongo-init.js", 0777),
        "/docker-entrypoint-initdb.d/mongo-init.js");

    withExposedPorts(27017);
    setWaitStrategy(
        (new HttpWaitStrategy())
            .forPort(27017)
            .withReadTimeout(Duration.of(2, ChronoUnit.MINUTES))
            .forStatusCodeMatching(r -> r == 200 || r == 401));
  }

  public static MongoDBTestContainer getInstance() {
      if(mongoDBTestContainer == null) {
          mongoDBTestContainer = new MongoDBTestContainer();
      }
    return mongoDBTestContainer;
  }

  public static void startInstance() {
    new RetryTemplateBuilder()
        .maxAttempts(3)
        .retryOn(ContainerLaunchException.class)
        .build()
        .execute(
            retryContext -> {
              System.out.printf(
                  "Starting MongoDB container attempt {%d}", retryContext.getRetryCount() + 1);
              getInstance().start();
              return null;
            });
  }
}
