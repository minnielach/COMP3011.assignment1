package comp3011.assignment1.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// takes the request from the frontend 
@RestController
public class TranscriptController {
	
	// receive the audio file
	@PostMapping("/api/transcribe")
	
	public String transcribe(@RequestParam("file") MultipartFile file) {
		
		//return the file name 
		return file.getOriginalFilename();
	}

}
