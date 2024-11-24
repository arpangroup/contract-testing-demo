package com.arpan.__kafka_avro_producer.controller;

import com.arpan.__kafka_avro_producer.producer.StudentProducer;
import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
@Slf4j
public class StudentController {
    @Autowired
    StudentProducer studentProducer;

    @PostMapping("/createStudent")
    public String getDataForKafkaTopic(@RequestBody Student student) {
        log.info("send data to producer..... {}", student.toString());
        studentProducer.sendMessage(student);
        return "Sata Posted";
    }
}
