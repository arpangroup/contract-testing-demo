package com.arpan.__kafka_json_consumer.consumer;

import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class StudentConsumer {

    @KafkaListener(topics = "student-data", groupId = "group_id", containerFactory = "myAvroConsumerFactory")
    public void listen(ConsumerRecord<String, GenericRecord> consumerRecord, Acknowledgment acknowledgment) {
    //public void listen(ConsumerRecord<String, Student> consumerRecord, Acknowledgment acknowledgment) {
        log.info("**************** kafka listen to topic-partition-offset: {}-{}-{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
        String key = consumerRecord.key();
        GenericRecord  value = consumerRecord.value(); //Student value = consumerRecord.value();
        log.info("Data Consumed: {}-{}", key, value.toString());
        acknowledgment.acknowledge();
        log.info("acknowledged {}-{}", consumerRecord.partition(), consumerRecord.offset());
        //return "processed a new student: " + value;
    }
}
