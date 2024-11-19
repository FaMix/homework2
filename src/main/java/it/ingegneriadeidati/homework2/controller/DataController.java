package it.ingegneriadeidati.homework2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import it.ingegneriadeidati.homework2.service.SearchEngine;

import java.util.List;
import java.util.Map;

@Controller
public class DataController {

	private final SearchEngine searchEngine;
	private Map<String, List<String>> receivedData;
	private Map<String, List<String>> receivedDataAll;

	@Autowired
	public DataController(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	@GetMapping("/")
	public String index() {
		return "web.html";  // This will resolve to resources/static/web.html
	}

	/**
	 * Endpoint to receive data via POST request.
	 *
	 * @param data incoming data
	 * @return a response indicating the data was received successfully
	 */
	@PostMapping("/receive")
	@ResponseBody
	public Map<String, Object> receiveData(@RequestBody Map<String, Object> data) {
		System.out.println("Data received: " + data);
		this.receivedData = this.searchEngine.search(data, "Separated");

		return Map.of(
				"message", "Data received successfully",
				"receivedData", this.receivedData
				);
	}

	/**
	 * Endpoint to receive data via POST request.
	 *
	 * @param data incoming data
	 * @return a response indicating the data was received successfully
	 */
	@PostMapping("/receiveAll")
	@ResponseBody
	public Map<String, Object> receiveDataAll(@RequestBody Map<String, Object> data) {
		System.out.println("Data received: " + data);
		this.receivedDataAll = this.searchEngine.search(data, "All together");
		return Map.of("message", "Data received correctly", "receivedData", this.receivedDataAll);
	}

	/**
	 * Endpoint to retrieve the last received data via GET request.
	 *
	 * @return the last received data or a message indicating no data is available
	 */
	@GetMapping("/data")
	@ResponseBody
	public Map<String, Object> getReceivedData() {
		if (this.receivedData != null) {
			return Map.of(
					"message", "Data retrieved successfully",
					"receivedData", this.receivedData
					);
		} else {
			return Map.of("message", "No data available");
		}
	}

	/**
	 * Endpoint to retrieve the last received data via GET request.
	 *
	 * @return the last received data or a message indicating no data is available
	 */
	@GetMapping("/dataAll")
	@ResponseBody
	public Map<String, Object> getReceivedDataAll() {
		if (this.receivedDataAll != null) {
			return Map.of("message", "Data retrieved successfully", "receivedData", this.receivedDataAll);
		} else {
			return Map.of("message", "No data available");
		}
	}
}
