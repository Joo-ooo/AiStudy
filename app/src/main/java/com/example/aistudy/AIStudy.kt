package com.example.aistudy

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.aistudy.service.NotificationService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

    /**
     * AIStudy is the main Application class for an Android application utilizing Hilt for dependency injection
     * across the entire app. It also configures WorkManager with Hilt's worker factory to enable dependency injection
     * in WorkManager workers.
     *
     * Implements:
     * - Configuration.Provider: By implementing this interface, the application provides a custom WorkManager configuration.
     *   This is essential for integrating Hilt with WorkManager, allowing for dependency injection in WorkManager workers.
     *
     * Properties:
     * - hiltWorkerFactory: An instance of HiltWorkerFactory injected by Hilt. This factory is responsible for creating
     *   instances of workers, enabling them to have their dependencies injected.
     *
     * Functions:
     * - getWorkManagerConfiguration(): Overrides the method from Configuration.Provider. It specifies the WorkManager
     *   configuration to use throughout the application. Here, it sets the worker factory to HiltWorkerFactory, allowing
     *   for dependency injection in WorkManager workers.
     * - onCreate(): Overrides the Application's onCreate method. This is called when the application is starting,
     *   before any activity, service, or receiver objects (excluding content providers) have been created. Inside this
     *   method, the NotificationService's createNotificationChannel method is called to setup notification channels
     *   for the application. This is a necessary setup for sending notifications on Android O and above.
     *
     * Usage:
     * - As the main Application class, AIStudy initializes components needed for the application's operation, such as
     *   setting up Hilt for dependency injection and configuring WorkManager with a HiltWorkerFactory. Additionally,
     *   it ensures that notification channels are created upon the application's startup, preparing the app for
     *   sending notifications.
     */

@HiltAndroidApp
class AIStudy : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build()
    }

    override fun onCreate() {
        super.onCreate()
        NotificationService.createNotificationChannel(applicationContext)
    }
}