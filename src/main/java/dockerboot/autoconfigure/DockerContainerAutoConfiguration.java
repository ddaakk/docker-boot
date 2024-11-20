package dockerboot.autoconfigure;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import dockerboot.DockerContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

/**
 * Auto-configuration class for Docker container management.
 * This class automatically configures Docker clients and container managers based on properties.
 * It is activated when DockerClient is present on the classpath and creates necessary beans
 * for Docker container management.
 */
@AutoConfiguration
@ConditionalOnClass(DockerClient.class)
@EnableConfigurationProperties(DockerProperties.class)
public class DockerContainerAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(DockerContainerAutoConfiguration.class);

    /**
     * Creates and configures a DockerClient bean if one is not already present in the context.
     * The client is configured using the provided DockerProperties.
     *
     * @param properties the Docker configuration properties
     * @return configured DockerClient instance
     */
    @Bean
    @ConditionalOnMissingBean
    public DockerClient dockerClient(DockerProperties properties) {
        logger.info("Configuring Docker client with host: {}", properties.getHost());

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(properties.getHost())
                .withDockerTlsVerify(properties.isTlsVerify())
                .withRegistryUrl(properties.getRegistryUrl())
                .withRegistryUsername(properties.getRegistryUsername())
                .withRegistryPassword(properties.getRegistryPassword())
                .build();

        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    /**
     * Inner configuration class responsible for creating and registering DockerContainerManager beans.
     * This class implements ApplicationContextAware to get access to the Spring application context,
     * which is needed for dynamic bean registration.
     */
    @Configuration
    public class DockerContainerManagerConfiguration implements ApplicationContextAware {

        private ApplicationContext applicationContext;

        /**
         * Sets the ApplicationContext that this object runs in.
         * Implemented from ApplicationContextAware interface.
         *
         * @param applicationContext the ApplicationContext object to be used by this object
         */
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        /**
         * Initializes and registers DockerContainerManager beans for each configured container.
         * This method is called after the bean has been constructed and dependencies injected.
         * It creates a separate DockerContainerManager bean for each enabled container configuration
         * found in the properties.
         *
         * The method performs the following steps:
         * 1. Retrieves DockerProperties and DockerClient from the application context
         * 2. Creates a DockerContainerManager for each enabled container configuration
         * 3. Registers each manager as a Spring bean with a unique name
         */
        @PostConstruct
        public void registerContainerManagers() {
            DockerProperties properties = applicationContext.getBean(DockerProperties.class);
            DockerClient dockerClient = applicationContext.getBean(DockerClient.class);
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

            if (properties.getContainers() == null) {
                logger.warn("No Docker container properties found");
                return;
            }

            logger.info("Registering container managers for {} containers", properties.getContainers().size());

            properties.getContainers().forEach((key, containerProps) -> {
                if (containerProps.isEnabled()) {
                    BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                            .genericBeanDefinition(DockerContainerManager.class)
                            .addConstructorArgValue(key)
                            .addConstructorArgValue(dockerClient)
                            .addConstructorArgValue(containerProps);

                    // Register bean with a unique name
                    String beanName = key + "ContainerManager";
                    beanFactory.registerBeanDefinition(beanName, definitionBuilder.getBeanDefinition());

                    logger.info("Registered container manager bean: {}", beanName);
                }
            });
        }
    }
}