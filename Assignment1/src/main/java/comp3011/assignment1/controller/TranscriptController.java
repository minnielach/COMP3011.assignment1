package comp3011.assignment1.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

import comp3011.assignment1.service.*;

// takes the request from the frontend 
@RestController
public class TranscriptController {
	
	// store the transcription
	private final TranscriptService service;
	
	// connect the controller to the service
	public TranscriptController(TranscriptService service) {
		this.service = service;
	}
	
	// receive the audio file
	@PostMapping("/api/transcribe")
	
	public String transcribe(@RequestParam("file") MultipartFile file) throws IOException {
		
		//return the file name to the service
		return service.transcribe(file);
	}
	
	// receive audio from the video player to generate subtitles
	@PostMapping("/api/v1/subtitles") 
	public String subtitles(@RequestParam("file") MultipartFile file) throws IOException {
		
		return service.transcribeSubtitles(file);
	}
	
	// return the total seconds of audio transcribed
	@GetMapping("/api/v1/global/usage")
	public Map<String, Double> usage() {
		return Map.of("usage", service.getTotalSeconds());
	}

}
