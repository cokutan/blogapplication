package scalefocus.blogapp.containers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class OpenSearchTestContainer extends GenericContainer<OpenSearchTestContainer> {

    public OpenSearchTestContainer() {
 super(DockerImageName.parse("opensearchproject/opensearch").withTag("latest"));
        withExposedPorts(9200, 9200);
        withEnv("discovery.type", "single-node");
        withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true");
        withEnv("DISABLE_SECURITY_PLUGIN", "true");
        withCreateContainerCmdModifier(cmd -> cmd.withName("opensearch-tescontainer"));
       /* setWaitStrategy((new HttpWaitStrategy())
                .forPort(9200)
                .forStatusCodeMatching(r -> r == 200 || r == 401))*/;
    }

}