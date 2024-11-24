package com.arpan.__kafka_json_producer.controller;

import com.arpan.__kafka_json_producer.producer.StudentProducer;
import com.arpan.__kafka_json_producer.util.AvroUtil;
import com.arpangroup.model.Student;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class StudentController {
    private final StudentProducer studentProducer;
    private final Environment environment;;

    @GetMapping("/ping")
    public String ping() {
        return "server is running on port %s  ......".formatted(environment.getProperty("local.server.port"));
    }

    @GetMapping("/student")
    public String getStudent() throws IOException {
        Student student = new Student(1, "Ram");
        return AvroUtil.convertAvroToJson(student, AvroUtil.studentSchema());
    }

    @PostMapping("/createStudent")
    public String getDataForKafkaTopic(@RequestBody Student student) {
        log.info("send data to producer..... {}", student.toString());
        studentProducer.sendMessage(student);
        return "student created";
    }
}
