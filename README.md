# Wine Glass Vibrator Android App

An Android application that detects sound frequencies in real-time and reproduces them to make wine glasses vibrate. This app demonstrates acoustic resonance by capturing audio, analyzing frequencies using FFT (Fast Fourier Transform), and playing them back at the detected frequency.

## Features

- **Real-time Frequency Detection**: Captures audio from the microphone and identifies dominant frequencies
- **FFT Analysis**: Uses Fast Fourier Transform to analyze audio spectrum
- **Frequency Playback**: Reproduces detected frequencies through the phone's speaker
- **Clean UI**: Material Design interface with frequency display
- **Completely Phone-Hosted**: No server required - everything runs locally on your device

## How It Works

1. **Audio Capture**: The app uses Android's AudioRecord API to capture audio from the microphone at 44.1 kHz
2. **FFT Analysis**: Captured audio is processed using FFT to identify the dominant frequency
3. **Frequency Playback**: The detected frequency is synthesized as a pure sine wave and played through the speaker
4. **Wine Glass Resonance**: When the played frequency matches the glass's natural resonant frequency, it vibrates

## Building the App

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- Android SDK (API 24 or higher)
- JDK 17
- Android device or emulator with API 24+

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd RemoteWork
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the APK**:
   ```bash
   ./gradlew assembleDebug
   ```

   Or use Android Studio: `Build > Build Bundle(s) / APK(s) > Build APK(s)`

4. **Install on your phone**:

   **Option A - Via Android Studio**:
   - Connect your phone via USB
   - Enable USB debugging in Developer Options
   - Click "Run" in Android Studio

   **Option B - Manual Installation**:
   - Build APK using gradlew or Android Studio
   - Find APK at: `app/build/outputs/apk/debug/app-debug.apk`
   - Transfer APK to your phone
   - Enable "Install from Unknown Sources" in Settings
   - Open and install the APK

## Using the App

1. **Launch the app** on your Android device

2. **Grant Microphone Permission**:
   - The app will request microphone access
   - Tap "Allow" to enable frequency detection

3. **Detect Frequency**:
   - Tap "Start Listening"
   - Make a sound near your wine glass (tap it gently or rub the rim)
   - The app will display the detected frequency in Hz
   - Wine glasses typically resonate between 400-600 Hz

4. **Play Frequency**:
   - Once a frequency is detected, tap "Play Frequency"
   - Increase your phone's volume to maximum
   - Place the phone speaker close to the wine glass
   - The glass should start vibrating at its resonant frequency

## Tips for Best Results

- **Fill the Glass**: Different water levels change the resonant frequency
- **Clean Glass**: Works best with thin, crystal wine glasses
- **Volume**: Set phone volume to maximum for stronger vibration
- **Distance**: Place phone speaker 1-3 inches from the glass
- **Environment**: Works best in quiet environments
- **Tap Gently**: When detecting frequency, tap the glass rim gently

## Technical Details

### Architecture

- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Audio Sample Rate**: 44,100 Hz
- **Audio Format**: 16-bit PCM
- **FFT Implementation**: Custom Cooley-Tukey algorithm

### Key Components

- `MainActivity.kt`: UI controller and lifecycle management
- `AudioProcessor.kt`: Handles audio recording and playback
- `FFTAnalyzer.kt`: Frequency analysis using FFT
- `activity_main.xml`: Material Design UI layout

### Permissions

- `RECORD_AUDIO`: Required for microphone access
- `MODIFY_AUDIO_SETTINGS`: For audio playback control

## Physics Behind It

Wine glasses have a natural resonant frequency determined by:
- Glass thickness
- Glass material (crystal vs regular glass)
- Glass shape and size
- Amount of liquid in the glass

When sound waves at the resonant frequency hit the glass, it absorbs energy efficiently and begins to vibrate. This is the same principle that can shatter glass at high enough amplitudes.

## Troubleshooting

**App crashes on start**: Ensure you've granted microphone permission

**No frequency detected**:
- Check microphone isn't blocked
- Make sure you're in a quiet environment
- Try tapping the glass harder

**Glass doesn't vibrate**:
- Increase phone volume to maximum
- Move phone closer to glass
- Try different glasses (crystal works best)
- Ensure frequency is accurately detected (400-600 Hz range)

**Playback is distorted**:
- This is normal at very high volumes
- Try slightly lower volume

## Future Enhancements

- Adjustable frequency tuning
- Frequency range filtering
- Recording and saving detected frequencies
- Visualize audio waveform and spectrum
- Support for multiple frequency harmonics

## License

This project is open source and available for educational purposes.

## Safety Note

This app generates loud tones. Use reasonable volume levels to protect your hearing and avoid damaging speakers or shattering glass.
