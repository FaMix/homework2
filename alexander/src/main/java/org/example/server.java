package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class server {

    public static void main(String[] args) {
        SpringApplication.run(server.class, args);
    }

    @RestController
    @RequestMapping("/api")
    public class DataController {

        private Map<String, List<String>> receivedData;

        @PostMapping("/receive")
        public Map<String, Object> receiveData(@RequestBody Map<String, Object> data) {
            System.out.println("Data received: " + data);
            //System.out.println(data.getClass());
            SearchEngine engine = new SearchEngine();
            this.receivedData = engine.search(data);

            return Map.of("message", "Data received correctly", "receivedData", this.receivedData);
        }
        @GetMapping("/data")
        public Map<String, Object> getReceivedData() {
            if (receivedData != null) {
                return Map.of("message", "Data retrieved successfully", "receivedData", receivedData);
            } else {
                return Map.of("message", "No data available");
            }
        }
    }
}
