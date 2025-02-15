package ua.foxminded.universitycms.repository.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories("ua.foxminded.universitycms.repository")
@EnableTransactionManagement
public class SpringRepositoryConfig {

  @Configuration
  @Profile("local")
  @PropertySource("local.datasource")
  public static class LocaltDataSourceConfig {
  }

  @Configuration
  @Profile("docker")
  @PropertySource("docker.datasource")
  public static class DockerDataSourceConfig {
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Value("${datasource.driver}") String driverClassName,
      @Value("${datasource.url}") String url,
      @Value("${datasource.username}") String username,
      @Value("${datasource.password}") String password,
      @Value("${datasource.batch-size}") int batchSize) {
    Map<String, String> properties = new HashMap<>();
    properties.put("javax.persistence.jdbc.driver", driverClassName);
    properties.put("javax.persistence.jdbc.url", url);
    properties.put("javax.persistence.jdbc.user", username);
    properties.put("javax.persistence.jdbc.password", password);
    properties.put("hibernate.jdbc.batch_size", String.valueOf(batchSize));

    LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setJpaPropertyMap(properties);
    entityManagerFactory.setPackagesToScan("ua.foxminded.universitycms.domain");
    entityManagerFactory.setPersistenceUnitName("CRM");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

    return entityManagerFactory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }
}
