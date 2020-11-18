package com.ddt.das.ea.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author rich
 */
@Slf4j
public class CommonUtil {

    public static void printRecord(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        int partition = record.partition();
        long offset = record.offset();
        long timestamp = record.timestamp();

        log.info("Topic: {}, partition: {}, offset: {} - record key: {}, value: {}, timestamp: {}",
                topic, partition, offset, record.key(), record.value(), timestamp);
    }
}
