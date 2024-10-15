plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.jetbrains.kotlin.android)
   id("kotlin-kapt")
   id("kotlin-parcelize")
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
}