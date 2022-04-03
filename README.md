# Parking Assist

## Summary:
Android app development with ``Android Studio`` platform.<br />
Displaying user generated pinpoints of available parking locations on map, according to user preferences.<br />
Implementing ``Google maps api`` & ``maps sdk for android``.<br />
Implementing ``Firebase authentication & real-time database``.<br />

# The App
## Login:

![Login](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Login.png)

## Registration:
![Register](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Register.png)

## Main App Navigation:
![Navigation](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Navigation.png)

## Map And Markers:
![Map&Markers](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Map%26Markers.png)

## Settings Apply:
![SettingsApply](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/SettingsApply.png)

## Google Maps Navigation:
![GoogleMapsNavigation](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/GoogleMapsNavigation.png)

# Implementations:
## Firebase:
Implementing Firebase Authentication service for a secure system while improving the sign-in and onboarding experience.
Implementing Firebase Real-Time Database for saving & loading parking location changes in real-time & the user's information, including Preferred settings.
````
1.Create an account on the Firebase site: https://firebase.google.com/ <br />
2.Create a project.<br />
3.Connect android studio to the Firebase account via the tools bar on android studio.<br />
4.Update dependencies.<br />
5.Follow the instuctions for each service that is needed.<br />
````
![Firebase](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/%E2%80%8F%E2%80%8FFirebase.png)

## Google Maps:

````
1.Create an account on the Google Maps Platform site: https://developers.google.com/maps <br />
2.Create a project.<br />
3.Choose an api & sdk. (geoAPI in this app) <br />
4.Create Credentials for the api.<br />
5.Download and add the Google-services.json.<br />
6.Update the api key given by the site.<br />
5.In build gradle Note the google services plugin: 'com.google.gms.google-services'.<br />
6.Update dependencies.<br />
````
![GoogleMaps](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/GoogleMaps.png)
