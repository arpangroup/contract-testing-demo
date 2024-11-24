package com.arpan.__kafka_avro_producer.producer;

import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentProducer {
    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplate;

    @Value("${kafka.topic.name:kafka.topic.name}")
    private String topicName;

    public void sendMessage(Student student) {
        log.info("sendAvroData to topic: {}", topicName);
        String key = "Key" + String.format("%.3f", Math.random());
        kafkaTemplate.send(topicName, key, student);
        log.info("message published successfully: {}", student.toString());
    }
}
