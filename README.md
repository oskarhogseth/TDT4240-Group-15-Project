# Word Duel 🎮  
*Fight your friends with words instead of violence*  

---

## Table of Contents  
- [Introduction](#introduction)  
- [Features](#features)  
- [Setup & Installation](#setup--installation)  
  - [Prerequisites](#prerequisites)  
  - [Step-by-Step Guide](#step-by-step-guide)  
- [How to Play](#how-to-play)  
  - [Game Modes](#game-modes)  
  - [Controls & Mechanics](#controls--mechanics)  

---

## Introduction  
**Word Duel** is a multiplayer Android game developed by Group 15 for the *TDT4240 Software Architecture* course. Built with **LibGDX**, **Java**, and **Firebase**, the game challenges players to form valid words from randomized letter sets under timed rounds. Key design focuses include:  
- **Modifiability**: MVC architecture for easy feature updates.  
- **Performance**: Offline dictionary preprocessing and real-time Firebase synchronization.  
- **Usability**: Intuitive UI with leaderboards and tutorials.  

---

## Features  
- 🕹️ **Multiplayer Lobbies**: Create/join games via PIN codes.  
- 🏆 **Real-Time Leaderboards**: Track global scores using Firebase.  
- ⏳ **Timed Rounds**: 3-7 rounds with countdown timers.  
- 📖 **Custom Dictionary**: Preprocessed word list for fast validation.  
- 🎵 **Adjustable Settings**: Toggle music and difficulty (Normal/Hard).

---

## Setup & Installation  

### Prerequisites  
- Android Studio ([Download](https://developer.android.com/studio))  
- Physical device or emulator with **Android 5.0+**  

### Step-by-Step Guide  
1. **Clone the Repository**  
   ```bash  
   git clone git@github.com:oskarhogseth/TDT4240-Group-15-Project.git
   
2. **Import Project into Android Studio**  
   - Open Android Studio → `File → Open` → Select the project directory.  
   - Wait for Gradle to sync dependencies.  

3. **Configure Emulator (Recommended)**  
   - Go to `Tools → Device Manager → Create Virtual Device`.  
   - Select **Medium Phone API 34** → Download **Android 14 (UpsideDownCake)**.  
   *⚠️ A known emulator bug could happen when attempting to create game and a workaround is to create a Pixel 5 virtual device with Android 14 (UpsideDownCake) instead*  

4. **Run the Game**  
   - Connect a device or select the emulator.  
   - Click the **Run** button (green play icon) in Android Studio.
  
---

## How to Play  

### Game Modes  
1. **Create Game**  
   - Set nickname, rounds (3/5/7), and difficulty.  
   - Share the generated PIN with others.  
2. **Join Game**  
   - Enter a PIN and nickname to join an existing lobby.  
3. **Single-Player**  
   - Create a lobby and start alone.  

### Controls & Mechanics  
- **Letter Tiles**: Tap to form words (3+ letters).  
- **Submit**: Press the submit button to validate words.  
- **Score**: Earn points for valid words; no penalties for invalid guesses.  
- **Leaderboard**: View rankings after finishing all rounds.  
