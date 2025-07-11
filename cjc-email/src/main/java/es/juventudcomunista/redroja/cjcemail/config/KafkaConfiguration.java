package es.juventudcomunista.redroja.cjcemail.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic emailActivacionTopic() {
        return TopicBuilder.name("email-activacion")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailConvocatoriaTopic() {
        return TopicBuilder.name("email-convocatoria")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailOlvidadoPassTopic() {
        return TopicBuilder.name("email-olvidado-pass")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailEmailEditaReunionTopic() {
        return TopicBuilder.name("email-edita-reunion")
                .partitions(3)
                .replicas(1)
                .build();
    }
}