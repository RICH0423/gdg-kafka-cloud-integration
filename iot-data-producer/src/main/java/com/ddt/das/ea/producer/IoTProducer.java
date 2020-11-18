package com.ddt.das.ea.producer;

import com.ddt.das.ea.model.SensorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author rich
 */
@Slf4j
public class IoTProducer {
    public static final String TOPIC_NAME = "iot-topic";

    public static void main(String[] args) {
        Properties props = getProperties();

        ObjectMapper objectMapper = new ObjectMapper();
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            try {
                log.info("Start sending messages ...");

                Stream.generate(SensorData::create).forEach(d -> {
                    try {
                        String json = objectMapper.writeValueAsString(d);
                        producer.send(new ProducerRecord<>(TOPIC_NAME, json)).get();  //fire and forget
                        log.info("Send messages: {} to Kafka successfully", json);
                    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
                        log.error("send message error!", e);
                    }
                    sleep5Sec();
                });

            } catch (Exception e) {
                log.error("Unexpected error!", e);
            }
        }
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("retries", "1");
        props.put("request.timeout.ms", "5000");
        return props;
    }

    private static void sleep5Sec() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            log.error("Unexpected error", e);
            Thread.currentThread().interrupt();
        }
    }
}
