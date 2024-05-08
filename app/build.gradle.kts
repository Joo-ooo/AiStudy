plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.8.0"
}

android {
    namespace = "com.example.aistudy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aistudy"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/INDEX.LIST"
            excludes += "mozilla/public-suffix-list.txt"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material:1.6.4")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.wear.compose:compose-material-core:1.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")

    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.5")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // DataStore Preferences
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Room Components
    val room_version = "2.6.1"
    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version") // Paging 3 Integration

    // Navigation
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // Paging
    implementation("androidx.paging:paging-compose:1.0.0-alpha17")

    // Work Manager
    val work_version = "2.7.1"
    implementation("androidx.work:work-runtime:$work_version") //(Java only)
    implementation("androidx.work:work-runtime-ktx:$work_version") //Kotlin + coroutines

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")


    // Json Serialisation
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1") // Or the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1") // Or the latest version
    implementation("com.google.code.gson:gson:2.9.1")

    // Microsoft Speech-To-Text API
    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.36.0")

    // ARSceneview
    implementation("io.github.sceneview:arsceneview:2.1.0")

    // Unit Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.5")
//    androidTestImplementation("org.mockito:mockito-core:5.9.0")
//    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}