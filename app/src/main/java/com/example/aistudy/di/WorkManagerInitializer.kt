package com.example.aistudy.di

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

    /**
     * WorkManagerInitializer is a Dagger-Hilt module designed to initialize WorkManager within the application's
     * global application context. It extends the Initializer interface, providing a custom initialization
     * strategy for WorkManager with a singleton scope. This allows WorkManager to be readily available
     * throughout the application, ensuring that background tasks can be scheduled and executed efficiently
     * and reliably. This module also logs the initialization process, aiding in debugging and verifying
     * the successful setup of WorkManager through Hilt dependency injection.
     */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerInitializer : Initializer<WorkManager> {
    @Provides
    @Singleton
    override fun create(@ApplicationContext context: Context): WorkManager {
        val configuration = Configuration.Builder().build()
        WorkManager.initialize(context, configuration)
        Log.d("Hilt Init", "WorkManager initialized by Hilt this time")
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return emptyList()
    }
}