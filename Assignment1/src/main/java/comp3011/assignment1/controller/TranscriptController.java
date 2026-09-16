package comp3011.assignment1.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
