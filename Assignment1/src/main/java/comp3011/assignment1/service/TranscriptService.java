package comp3011.assignment1.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.client.*;
import org.springframework.web.multipart.*;
import tools.jackson.databind.*;


// handles the audio before transcription
@Service
public class TranscriptService {
	
	// store api key
	private final String apiKey;
	
	// store the input and output tokens
	private long inputTokens = 0;
	
	private long outputTokens = 0;
	
	// store total subtitle transcription usage in seconds
	private double totalSeconds = 0;
	
	// get api key from the environment
	public TranscriptService(@Value("${OPENAI_API_KEY}") String apiKey) {
		
		this.apiKey = apiKey;
	}
	
	// receives the audio file form the controller
	public String transcribe(MultipartFile file) throws IOException {
		
		// test if the audio is received 
		System.out.println("Audio name: " + file.getOriginalFilename());
		System.out.println("Audio size: " + file.getSize());
		System.out.println("Audio type: " + file.getContentType());
		
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
		
		// use Openai model for normal speech transcription
		form.add("model", "gpt-4o-mini-transcribe");
		
		
		// send this form and information to the OpenAI
		String response = RestClient.create().post().uri("https://api.openai.com/v1/audio/transcriptions").contentType(MediaType.MULTIPART_FORM_DATA).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).body(form).retrieve().body(String.class);
		
		// turn the response into a JSON that is readable
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.readTree(response);
		
		// store and get the usage
		JsonNode usage = result.get("usage");
		
		// add the token if usage is available
		if (usage != null) {
			inputTokens += usage.path("input_tokens").asLong();
			outputTokens += usage.path("output_tokens").asLong();
		}
		
		// return the result back to the frontend
		return result.path("text").asString();
		
	}
	
	// transcribe audio for video subtitles 
	public String transcribeSubtitles(MultipartFile file) throws IOException {
		
		// convert the audio file into a resource for the API request
		ByteArrayResource audio = new ByteArrayResource(file.getBytes()) {
			
			// get file name
			@Override 
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};
		
		// create a form to send to send to openAI
		MultiValueMap<String, Object> subtitleForm = new LinkedMultiValueMap<>();
		
		// add the audio file so OpenAI can transcribe it
		subtitleForm.add("file", audio);
		
		// use Whisper-1 to generate timestamped transcription
		subtitleForm.add("model", "whisper-1");
		
		// return detailed JSON so the timestamps are able to be accessed
		subtitleForm.add("response_format", "verbose_json");
		
		// get the start and end time of the speech in the video segment 
		subtitleForm.add("timestamp_granularities[]", "segment");
		
		// get the start and end time of individual words
		subtitleForm.add("timestamp_granularities[]", "word");
		
		// send the audio and subtitle info to openAI
		String subtitleResponse = RestClient.create().post().uri("https://api.openai.com/v1/audio/transcriptions").contentType(MediaType.MULTIPART_FORM_DATA).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).body(subtitleForm).retrieve().body(String.class);
		
		// get the number of seconds transcribed by Whisper
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.readTree(subtitleResponse);
		
		// get the info from JSON response
		JsonNode usage = result.path("usage");
		 
		// add the seconds to the total usage
		if (!usage.isMissingNode()) {
			synchronized (this) {
				totalSeconds += usage.path("seconds").asDouble();
			}
		}

		// return the transcription and timestamps to the controller
		return subtitleResponse;
		
	}
	
	// return total input tokens
	public long getInputTokens() {
	 	return inputTokens;
	}
	
	// return total output tokens
	public long getOutputTokens() {
		return outputTokens;
	}
	
	// return the total seconds of audio 
	public synchronized double getTotalSeconds() {
		return totalSeconds;
	}

}
