plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.jetbrains.kotlin.android)
   id("com.google.devtools.ksp")
   id("kotlin-parcelize")
   id("com.google.dagger.hilt.android")
}

android {
   namespace = "com.nicelydone.androidfundamentalfirstsubmission"
   compileSdk = 34

   defaultConfig {
      applicationId = "com.nicelydone.androidfundamentalfirstsubmission"
      minSdk = 24
      targetSdk = 34
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildFeatures {
      viewBinding = true
      buildConfig = true
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )
         buildConfigField("String", "BASE_URL", "\"https://event-api.dicoding.dev\"")
      }
      debug {
         buildConfigField("String", "BASE_URL", "\"https://event-api.dicoding.dev\"")
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }
   kotlinOptions {
      jvmTarget = "1.8"
   }
}

dependencies {
   implementation(libs.lottie)
   implementation(libs.smoothbottombar)
   implementation(libs.retrofit)
   implementation(libs.logging.interceptor)
   implementation(libs.converter.gson)
   implementation(libs.glide)
   implementation(libs.circleimageview)
   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)
   implementation(libs.androidx.activity)
   implementation(libs.androidx.constraintlayout)
   implementation(libs.androidx.navigation.fragment.ktx)
   implementation(libs.androidx.navigation.ui.ktx)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)

   implementation("androidx.room:room-runtime:2.6.1")
   ksp("androidx.room:room-compiler:2.6.1")
   implementation("androidx.room:room-ktx:2.6.1")

   implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

   implementation("com.google.dagger:hilt-android:2.51.1")
   ksp("com.google.dagger:hilt-android-compiler:2.51.1")

}