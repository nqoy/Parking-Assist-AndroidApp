# Parking Assist
Android app development with ``Android Studio`` platform.<br />
Implementing ``Firebase Services``.<br />
Implementing ``Google Maps Api & SDK``.<br />

## Summary:
To use Parking Assist app, each user must be registered with a verified email.<br />
Users can generate and see parking locations which are updated in real-time on the map by all users.<br />
Every user can set his parking location search preferences with 2 options: Distance from current location & if a parking has likes or not.<br />
Each parking location can be clicked on for additional information, can be liked by the user or removed, and the user can navigate to it.<br />

## Implementations:
### Firebase:
Implementing ``Firebase Authentication`` for a secure system while improving the sign-in and onboarding experience.<br />
Implementing ``Firebase Realtime Database`` for saving & loading parking location changes in real-time & the user's information, including Preferred settings.<br />
````
1.Create an account on the Firebase site: https://firebase.google.com/
2.Create a project.
3.Connect android studio to the Firebase account via the tools bar on android studio.
4.Update dependencies.
5.Follow the instructions for each service that is needed.
````
![Firebase](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/%E2%80%8F%E2%80%8FFirebase.png)

### Google Maps:
Implementing ``Google Geocoding API`` - Converting addresses into geographic coordinates and vice versa.<br />
Implementing ``Google Maps SDK For Android`` - Adds maps to the app using Google Maps data, map displays, map gesture responses and map objects like markers. <br />
````
1.Create an account on the Google-Maps Platform site: https://developers.google.com/maps
2.Create a project.
3.Choose an api & sdk.
4.Create Credentials for the api.
5.Download and add the Google-services.json.
6.Update the api key given by the site.
7.In build gradle Note the Google services plugin: 'com.google.gms.google-services'.
8.Update dependencies.
9.Follow further instructions from Google-Maps platform.
````
![GoogleMaps](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/GoogleMaps.png)

# The App 

## Login Page:
![Login](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Login.png)

## Registration:
![Register](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Register.png)

## Main App Navigation:
![Navigation](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Navigation.png)

## Map And Markers:
![Map&Markers](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/Map%26Markers.png)

## Settings:
![SettingsApply](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/SettingsApply.png)

## Google Maps Navigation:
![GoogleMapsNavigation](https://github.com/nqoy/Parking-Assist-AndroidApp/blob/main/GoogleMapsNavigation.png)

