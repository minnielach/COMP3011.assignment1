package comp3011.assignment1.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.client.*;
import org.springframework.web.multipart.*;


// handles the audio before transcription
@Service
public class TranscriptService {
	
	// store api key
	private final String apiKey;
	
	// store the input and output tokens
	private long inputTokens = 0;
	
	private long outputTokens = 0;
	
	// get api key from the environment
	public TranscriptService(@Value("${OPENAI_API_KEY}") String apiKey) {
		
		this.apiKey = apiKey;
	}
	
	// receives the audio file form the controller
	public String transcribe(MultipartFile file) throws IOException {
		
		// turn the audio into ByteArray resource
		ByteArrayResource audio = new ByteArrayResource(file.getBytes()) {
			
			@Override
			public String getFilename() {
				
				// get the original file name
				return file.getOriginalFilename();
			}
		};
		
		// create a form to give to OpenAI
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		
		// add the audio file to the form
		form.add("file", audio);
		
		// tell OpenAI the speech model to use
		form.add("model", "gpt-4o-mini-transcribe");
		
		// send this form and information to the OpenAI
		return RestClient.create().post().uri("https://api.openai.com/v1/audio/transcriptions").contentType(MediaType.MULTIPART_FORM_DATA).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).body(form).retrieve().body(String.class);
	}
	
	// return total input tokens
	public long getInputTokens() {
		return inputTokens;
	}
	
	// return total output tokens
	public long getOutputTokens() {
		return outputTokens;
	}

}
