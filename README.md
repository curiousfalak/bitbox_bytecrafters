# bitbox_bytecrafters _Nature_lens
# 🌊 Nature Lens(AI POWERED APP)

An Android app built with Kotlin + Jetpack Compose for real-time marine biodiversity monitoring and threat reporting. It uses on-device AI, geotagging, and a responsive UI to empower users in marine conservation efforts.

---

## 🚀 Project Overview

**Marine Species Watch** enables users to:

- Identify marine species using an AI model  
- Automatically tag sightings with location data  
- Report threats like poaching or coral bleaching  
- Get instant feedback via in-app alerts

---

## ✅ Core MVP Features Implemented

### 📸 AI-Powered Species Recognition

- Integrated a custom **TensorFlow Lite** model trained with a **Convolutional Neural Network (CNN)**  
- Capable of classifying the following 9 marine species:
- 0 - Black Sea Sprat
1 - Gilt-Head Bream
2 - Hourse Mackerel
3 - Red Mullet
4 - Red Sea Bream
5 - Sea Bass
6 - Shrimp
7 - Striped Red Mullet
8 - Trout

- On-device classification ensures real-time predictions without internet dependency  
- **Training Dataset**: [A Large-Scale Fish Dataset](https://www.kaggle.com/datasets/crowww/a-large-scale-fish-dataset) from Kaggle, with thousands of fish images across diverse species

### 📍 Real-Time Geotagging

- Captures GPS latitude and longitude during image uploads  
- Automatically attaches coordinates to every species report

### 🚨 Threat Reporting

- Simple form/button to report illegal activities like:
- 🐠 Poaching  
- 🪸 Coral Bleaching  
- 🌊 Pollution or other marine hazards

### 🔔 Alert Notification System

- Sends **local Android notifications** based on the type of reported threat  


---

## 🛠️ Tech Stack

- **Jetpack Compose** – Declarative UI toolkit  
- **Kotlin** – Programming language  
- **TensorFlow Lite** – For on-device AI inference  
- **CNN Architecture** – Used to train the fish classification model  
- **Android Location Services** – For GPS data  
- **Local Notifications** – For alert system

---

## 📦 Project Structure (MVP)

/app ├── ui/ # Jetpack Compose UI screens ├── data/ # Local data handling ├── model/ # Data classes ├── utils/ # Location, notification helpers └── MainActivity.kt 

---

## 📅 Timeline

This MVP was developed within a **12-hour hackathon sprint**.  
Focus was on delivering essential features to demonstrate real-world potential.

---

## 🌐 Future Enhancements

- 🗺 Interactive biodiversity map with heatmap overlay  
- 🧠 Admin dashboard with filters (species, threat type, region)  
- 🎖 Gamified user badge system  
- 👥 Crowd verification of sightings  
- 📊 Data visualizations (e.g., species density by region)  
- 🔄 Firebase or Node.js backend for data sync and management

---

## 🤝 Contributing

Want to help expand the project post-hackathon?  
Open issues or contribute via pull requests to support marine conservation tech.

---

## 📣 Acknowledgments

- [A Large-Scale Fish Dataset](https://www.kaggle.com/datasets/crowww/a-large-scale-fish-dataset)  
- Open-source contributors and marine conservation researchers  
- Hackathon organizers for the opportunity and platform( Google Developer Group @ JIIT128)

---

## 📸 Screenshots

![Geotagged Preview](https://github.com/curiousfalak/bitbox_bytecrafters/raw/main/WhatsApp%20Image%202025-04-20%20at%2013.05.19_72953a2b.jpg?raw=true)




