# Coronavirus Detector Android
This app uses Bluetooth and Wifi to detect presence of supposedly COVID-19 infected people.


I built this app during the start of pandemic to detect presence of people, which can later be tracked in case anybody is diagnosed with COVID-19

The app stores the id of person, which can be changed at later stage.

The App uses Bluetooth & Wifi to whichever is present to detect the presence of another installed app in the vicinity.

Functionality:
1) Login using phone ( Firebase ) 
      Setup Firebase as given here: https://firebase.google.com/docs/android/setup and add generated google-services.json file in app directory
2) Request e-pass
3) Chat based Q&A for Covid Detection
4) Android Service to detect presence of people(installed app) around.  <--- Main app code
5) FAQ

All the data are available on a Django Dashboard that i will open source soon ping me if you need now( ankitsinha611@gmail.com ).

