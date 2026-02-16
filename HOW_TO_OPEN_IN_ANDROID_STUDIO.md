# How to Open the Upgraded Project in Android Studio

This guide explains how to open and build the upgraded Android project (SDK 36) in Android Studio.

## Prerequisites

Before opening the project, ensure you have:

1. **Android Studio** - Version **Narwhal (2025.1)** or later recommended
   - Download from: https://developer.android.com/studio
   
2. **JDK 17** - Required for AGP 8.10+ and Gradle 8.10+
   - Android Studio usually includes JDK 17, but you can verify/download from: https://adoptium.net/

3. **Android SDK 36** - Will be installed automatically by Android Studio, or you can pre-install via SDK Manager

## Step-by-Step Instructions

### Option 1: Open from GitHub (Recommended)

1. **Clone or Pull the Repository**
   ```bash
   # If you haven't cloned yet:
   git clone https://github.com/ashwin-meta/Video-Maker.git
   cd Video-Maker
   
   # If you already have it cloned, pull the latest changes:
   git pull origin main
   
   # Or checkout the upgrade branch:
   git checkout copilot/upgrade-target-sdk-and-dependencies
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Click **"Open"** or **"File → Open"**
   - Navigate to the `Video-Maker` project folder
   - Select the folder and click **"OK"**

3. **Wait for Gradle Sync**
   - Android Studio will automatically detect the project
   - It will start syncing Gradle (downloading dependencies)
   - This may take 5-10 minutes on first sync
   - Watch the progress in the status bar at the bottom

4. **Install Missing SDK Components** (if prompted)
   - Android Studio may prompt to install:
     - Android SDK Platform 36
     - Build Tools 35.0.0
     - Android SDK Command-line Tools
   - Click **"Install"** or **"Accept"** to install these components

5. **Accept Licenses** (if prompted)
   - You may be asked to accept Android SDK licenses
   - Click **"Accept"** to continue

### Option 2: Import Existing Project

1. **Open Android Studio**
   - Launch Android Studio
   
2. **Import Project**
   - Click **"File → New → Import Project"**
   - Browse to the `Video-Maker` folder location
   - Select the folder containing `build.gradle` and `settings.gradle`
   - Click **"OK"**

3. **Follow Gradle Sync Process**
   - Same as Option 1, steps 3-5

## After Opening

### Verify Project Configuration

1. **Check Java Version**
   - Go to **"File → Project Structure → SDK Location"**
   - Ensure JDK version is 17 or higher
   - If not, download and select JDK 17

2. **Check Android SDK**
   - Go to **"Tools → SDK Manager"**
   - Ensure **Android 15.0 (API 36)** is installed
   - Install if missing

3. **Verify Gradle Wrapper**
   - The project should use Gradle 8.10.2 (configured in `gradle/wrapper/gradle-wrapper.properties`)
   - Android Studio will download this automatically

### Build the Project

1. **Clean Build**
   - Click **"Build → Clean Project"**
   - Wait for completion

2. **Build Project**
   - Click **"Build → Make Project"** (or press **Ctrl+F9** / **Cmd+F9**)
   - Wait for the build to complete
   - Check the **Build** output panel for any errors

3. **Run the App**
   - Connect an Android device or start an emulator
   - Click the **"Run"** button (green play icon)
   - Select your target device
   - The app will install and launch

## Troubleshooting

### Issue: "Failed to resolve com.android.tools.build:gradle:8.10.2"

**Solution:**
- Check your internet connection
- Ensure you can access `maven.google.com` and `dl.google.com`
- Try: **"File → Invalidate Caches / Restart"**
- In terminal, run: `./gradlew clean --refresh-dependencies`

### Issue: "Minimum supported Gradle version is X.X.X"

**Solution:**
- The project uses Gradle 8.10.2
- Android Studio should handle this automatically
- If issues persist, update Android Studio to the latest version

### Issue: "SDK location not found"

**Solution:**
- Go to **"File → Project Structure → SDK Location"**
- Set Android SDK location (usually: `~/Android/Sdk` on Mac/Linux or `C:\Users\YourName\AppData\Local\Android\Sdk` on Windows)

### Issue: "Java version mismatch"

**Solution:**
- The project requires JDK 17
- Go to **"File → Project Structure → SDK Location"**
- Under "JDK location", select or download JDK 17
- Restart Android Studio

### Issue: Gradle sync takes too long

**Solution:**
- First sync always takes longer (downloading dependencies)
- Ensure stable internet connection
- Check Gradle daemon: **"Settings → Build → Gradle → Gradle Daemon"** (should be enabled)
- Increase heap size in `gradle.properties`: `org.gradle.jvmargs=-Xmx4096m`

### Issue: Build fails with "Duplicate class" errors

**Solution:**
- The upgraded project removed duplicate dependencies
- Try: **"Build → Clean Project"**
- Then: Delete `.gradle` and `.idea` folders in project root
- Reopen project in Android Studio

## Project Structure Overview

```
Video-Maker/
├── app/                          # Main application module
│   ├── build.gradle             # App-level build configuration (SDK 36)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/
├── imageeditlibrary/            # Image editing library module
│   ├── build.gradle             # Library build configuration
│   └── src/
├── build.gradle                 # Project-level build configuration (AGP 8.10.2)
├── settings.gradle              # Project settings with repositories
├── gradle.properties            # Gradle optimization settings
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties  # Gradle 8.10.2
├── gradlew                      # Gradle wrapper script (Unix)
├── gradlew.bat                  # Gradle wrapper script (Windows)
└── UPGRADE_NOTES.md            # Detailed upgrade documentation
```

## Key Configuration Details

- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Min SDK**: 24 (Android 7.0)
- **Java Version**: 17
- **Gradle**: 8.10.2
- **Android Gradle Plugin**: 8.10.2

## Building from Command Line (Alternative)

If you prefer command line:

```bash
# Navigate to project directory
cd Video-Maker

# Make gradlew executable (Unix/Mac)
chmod +x gradlew

# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Additional Resources

- **Android Studio User Guide**: https://developer.android.com/studio/intro
- **Gradle Documentation**: https://docs.gradle.org/current/userguide/userguide.html
- **Android Developers**: https://developer.android.com/
- **Project Upgrade Notes**: See `UPGRADE_NOTES.md` in the project root

## Getting Help

If you encounter issues:

1. Check the **Build** output panel in Android Studio for detailed error messages
2. Review `UPGRADE_NOTES.md` for migration notes and known issues
3. Check if there are any error messages in the **Event Log** (bottom-right corner)
4. Try **"File → Invalidate Caches / Restart"**

---

**Note**: The first time you open the project after the upgrade, Gradle will download all dependencies (AGP 8.10.2, SDK 36 libraries, updated AndroidX libraries, etc.). This is normal and may take several minutes depending on your internet speed.

After the initial sync, subsequent builds will be much faster thanks to Gradle's caching.

**Happy Coding! 🚀**
