package comp3011.assignment1.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
	
	// store the time the server started
	private final Instant serverStart = Instant.now();
	
	// return the server uptime information
	@GetMapping("/api/v1/admin/uptime")
	public Map<String, Object> getUptime() {
	
	// get current time 
	Instant now = Instant.now();
	
	// calculate how many seconds the server has been running
	double uptimeSeconds = Duration.between(serverStart, now).toMillis() / 1000.0;
	
	// return the up time information as a JSON 
	return Map.of("utcServerStart", serverStart.toString(), "utcNow", now.toString(), "serverUptimeSeconds", uptimeSeconds);
	
	}
}