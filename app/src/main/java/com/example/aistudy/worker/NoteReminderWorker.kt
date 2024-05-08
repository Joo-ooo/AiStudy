package com.example.aistudy.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aistudy.service.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

    /**
     * A Hilt-injected worker class extending CoroutineWorker, designed to execute background tasks for sending
     * reminder notifications for notes. Utilizes Android's WorkManager and Hilt's dependency injection to manage
     * work requests and dependencies respectively.
     *
     * Core Functionality:
     * - override suspend fun doWork(): The primary work method for the worker, executed asynchronously on a background
     *   thread provided by WorkManager. This function is where the worker's main task is defined.
     *
     * Implementation Details:
     * - Inside doWork(), a notification is shown using the NotificationService utility class. The notification details
     *   (title and message) are retrieved from inputData, a property of WorkerParameters, allowing dynamic content
     *   to be passed to the worker at enqueue time.
     * - The useTranscriptChannel flag is set to false, indicating that the default notification channel is to be used
     *   rather than a specialized one for transcripts.
     * - Returns Result.success() upon successful execution of the work, signaling to WorkManager that the task completed
     *   successfully.
     */

@HiltWorker
class NoteReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        NotificationService.showNotification(
            context = context,
            title = inputData.getString("title").toString(),
            message = inputData.getString("message").toString(),
            useTranscriptChannel = false
        )
        return Result.success()
    }
}