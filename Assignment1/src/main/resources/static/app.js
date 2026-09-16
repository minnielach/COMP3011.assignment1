// stores the recorder, microphone, and audio parts
let recorder;
let record;
let audios = [];

// get the buttons and status from the HTML
const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const statusText = document.getElementById("status");

// start button which starts recording when clicked
startButton.onclick = async function() {
	// clear the old audio
	audios = [];

	// ask the user to allow for microphone 
	record = await navigator.mediaDevices.getUserMedia({ audio: true });
	
	// creates a new recorder using the microphone
	recorder = new MediaRecorder(record);
	
	// starts to save each piece of audio whilst recording
	recorder.ondataavailable = function (event) {

		audios.push(event.data);
		
	};
	
	// combine all of recorded partsinto one file
	recorder.onstop = function() {
		const audioBlob = new Blob(audios, {
			// keep the same audio file
			type: recorder.mimeType
		});
		
		//print the audio file in the logs so it can be tested
		console.log(audioBlob);
		
		// create a form for the audio
		const form = new formData();
		
		// add the audio file to the form
		form.append("file", audioBlob, "recording.webm");
		
		// update the page to let the user know it is processing
		statusText.textContent = "Processing...";
		
		// sends the audio parts to the backend
		fetch("/api/transcribe", {
			method: "POST" ,
			body: form
		})
		
		.then(response => response.text())
		.then(text => {
			
			// display the trasncription
			document.getElementById("transcript").textContent = text;
			
			// put the text back
			statusText.textContent = "Ready to record...";
			
		})
	};
	
	// starts recording
	recorder.start();
	
	// update the page to let user know it is recording
	statusText.textContent = "Recording...";
	
	// disable the start button and allow the stop button to be pressed
	startButton.disabled = true;
	stopButton.disabled = false;
	
};

// stop button which stops the recording when clicked
stopButton.onclick = function() {
	// stops the recorder
	recorder.stop();
	
	// turn off the microphone 
	record.getTracks().forEach(track=>track.stop());
	
	// update the page to let the user know the recording has stopped
	statusText.textContent = "Recording stopped";
	
	// disable the stop button and allow the start button to be pressed
	startButton.disabled = false;
	stopButton.disabled = true;
};

