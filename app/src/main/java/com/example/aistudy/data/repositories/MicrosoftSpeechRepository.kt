package com.example.aistudy.data.repositories

import android.util.Log
import com.microsoft.cognitiveservices.speech.CancellationReason
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

    /**
     * The MicrosoftSpeechRepository class encapsulates the functionality for converting speech
     * from a .wav audio file to text using Microsoft's Cognitive Services Speech SDK. It demonstrates
     * how to initialize and configure the speech recognizer, handle speech recognition events,
     * and format recognized speech into SubRip Text (SRT) format. The class uses a private API key
     * and specifies the region for the Azure Cognitive Services resource. It includes methods for
     * synchronous speech-to-text conversion and formatting the timestamp of recognized speech segments.
     */
class MicrosoftSpeechRepository {

    private val _apiKey: String = "628c38e9987e4ae9bc42333bfef89bc8"
    private val _region: String = "southeastasia"

    // This function takes a path to a .wav audio file and returns the transcribed text.
    fun convertAudioToText(wavFilePath: String): String = runBlocking {
        // StringBuilder to build the final transcribed text in SRT format.
        val srtContent = StringBuilder()

        // Configure the speech recognizer with the API key and region.
        val config = SpeechConfig.fromSubscription(_apiKey, _region)
        // Prepare the audio configuration with the input .wav file.
        AudioConfig.fromWavFileInput(wavFilePath).use { audioConfig ->
            // Initialize the speech recognizer.
            SpeechRecognizer(config, audioConfig).use { recognizer ->
                // This will be used to number the recognized phrases in the SRT format.
                var sequenceNumber = 1
                // Channel to signal when the recognition is done.
                val doneSignal = Channel<Boolean>(1)

                // Event listener for when a segment of speech is recognized.
                recognizer.recognized.addEventListener { _, event ->
                    val result = event.result
                    // If the speech is recognized, format it into the SRT content.
                    if (result.reason == ResultReason.RecognizedSpeech) {
                        val text = result.text.trim()
                        // Calculate the start time of the recognized speech.
                        val startTime = formatTime(result.offset.divide(BigInteger.valueOf(10000)).toLong())
                        // Calculate the end time of the recognized speech.
                        val endTime = formatTime(result.offset.add(result.duration).divide(BigInteger.valueOf(10000)).toLong())

                        // Append the transcribed text in SRT format.
                        srtContent.append("$sequenceNumber\n$startTime --> $endTime\n$text\n\n")
                        sequenceNumber++
                    }
                }

                // Event listener for when the recognition session is stopped.
                recognizer.sessionStopped.addEventListener { _, _ ->
                    // Send a signal that the session is complete.
                    doneSignal.trySend(true).isSuccess
                }

                // Event listener for when recognition is canceled.
                recognizer.canceled.addEventListener { _, event ->
                    if (event.reason == CancellationReason.EndOfStream) {
                        Log.d("SpeechSDK", "End of audio stream reached.")
                    } else {
                        Log.e("SpeechSDK", "Recognition canceled: ${event.reason} - ${event.errorDetails}")
                    }
                    // Send a signal that the recognition is complete, even if canceled.
                    doneSignal.trySend(true).isSuccess
                }

                // Start the recognition process asynchronously.
                recognizer.startContinuousRecognitionAsync().get()
                // Wait for the recognition to complete.
                doneSignal.receive()
                // Stop the recognition process asynchronously.
                recognizer.stopContinuousRecognitionAsync().get()
            }
        }

        // Return the built SRT content.
        srtContent.toString()
    }


    private fun formatTime(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS").withZone(ZoneOffset.UTC)
        return formatter.format(instant)
    }
}