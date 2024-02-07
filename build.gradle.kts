plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.saqsi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.saqsi"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("edu.cmu.pocketsphinx:pocketsphinx-android:5prealpha-SNAPSHOT")
    implementation ("edu.cmu.pocketsphinx:jsapi:1.0-SNAPSHOT")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs\\cmu_time_awb.jar"))
    implementation(files("libs\\cmu_us_kal.jar"))
    implementation(files("libs\\cmudict04.jar"))
    implementation(files("libs\\cmulex.jar"))
    implementation(files("libs\\cmutimelex.jar"))
    implementation(files("libs\\en_us.jar"))
    implementation(files("libs\\freetts.jar"))
    implementation(files("libs\\freetts-jsapi10.jar"))
    implementation(files("libs\\jsapi.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}