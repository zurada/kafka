package com.example.kafkastreamsdemoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

import java.util.function.Function;

@SpringBootApplication
public class KafkaStreamsDemoConfigApplication implements ApplicationListener<ApplicationReadyEvent> {
    private static final String UNCAUGHT_EXCEPTION_MESSAGE = "Uncaught exception in KafkaStream. Shutting down Spring application.";
    Logger logger = LoggerFactory.getLogger(KafkaStreamsDemoConfigApplication.class);

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsDemoConfigApplication.class, args);
    }

    @Bean
    //todo serde on objects
    //todo deserialize Instant
    //todo tests
    //todo create producer
    public Function<KStream<String, TestObj>, KStream<String, TestObj>> process() {
        logger.info("Configuring KStream topology");

        return input ->
                input.peek((key, value) -> {
                    System.out.println("Key: " + key + " Value: " + value.firstName + " " + value.lastName + " " + value.createdDate);
                    throw new NullPointerException("blah");
                });
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var port = environment.getRequiredProperty("server.port", Integer.class);
        logger.info("Server started on port {}", port);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer customizer() {
        return fb -> {
            fb.setKafkaStreamsCustomizer(kafkaStreams -> kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
                logger.error(UNCAUGHT_EXCEPTION_MESSAGE, e);
                logger.error("We should send error with id {} and mgs {} to external system", e.getMessage().hashCode(), e.getMessage());
            }));
        };
    }
}
//320331849