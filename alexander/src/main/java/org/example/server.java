package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class server {

    public static void main(String[] args) {
        SpringApplication.run(server.class, args);
    }

    @RestController
    @RequestMapping("/api")
    public class DataController {

        private Map<String, Object> receivedData;

        @PostMapping("/receive")
        public Map<String, Object> receiveData(@RequestBody Map<String, Object> data) {
            System.out.println("Data received: " + data);
            this.receivedData = data;
            return Map.of("message", "Data received correctly", "receivedData", data);
        }

        @GetMapping("/getData")
        public Map<String, Object> getData() {
            if (this.receivedData != null) {
                return Map.of("message", "Data retrieved successfully", "data", this.receivedData);
            } else {
                return Map.of("message", "No data received yet");
            }
        }



    }
}
