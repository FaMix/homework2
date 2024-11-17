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
        private Map<String, List<String>> receivedDataAll;

        @PostMapping("/receive")
        public Map<String, Object> receiveData(@RequestBody Map<String, Object> data) {
            System.out.println("Data received: " + data);
            //System.out.println(data.getClass());
            SearchEngine engine = new SearchEngine();
            this.receivedData = engine.search(data, "Separated");

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

        @PostMapping("/receiveAll")
        public Map<String, Object> receiveDataAll(@RequestBody Map<String, Object> data) {
            System.out.println("Data received: " + data);
            //System.out.println(data.getClass());
            SearchEngine engine = new SearchEngine();
            this.receivedDataAll = engine.search(data, "All together");

            return Map.of("message", "Data received correctly", "receivedData", this.receivedDataAll);
        }
        @GetMapping("/dataAll")
        public Map<String, Object> getReceivedDataAll() {
            if (receivedDataAll != null) {
                return Map.of("message", "Data retrieved successfully", "receivedData", receivedDataAll);
            } else {
                return Map.of("message", "No data available");
            }
        }
    }
}
