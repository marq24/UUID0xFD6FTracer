# UUID 0xFD6F Tracer ![AppLogo](./app/src/main/res/mipmap-hdpi/ic_launcher_round.png)

Main purpose of the app is to scan your current environment for active devices running an application that make use of
Google's & Apple's _Exposure Notification Framework_ (like the German CoronaWarnApp) or the StopCovid France App (which
is not based on the framework - but use a similar technology to trace randomized anonymous contact information).

[List of countries that make use of the _Exposure Notification Framework_ (UUID 0xFD6F Beacaons)](./COUNTRIES.md)

## Installation
### Get via F-Droid
[![F-Droid appstore](./misc/fdroid/320px-Get_it_on_F-Droid.svg.png)](https://f-droid.org/app/com.emacberry.uuid0xfd6fscan)

### Get via amazon appstore
[![amazon appstore](./misc/amazon/amazon-appstore-badge-en-black.png)](https://www.amazon.com/gp/product/B08CY7JY1P)

https://www.amazon.com/gp/product/B08CY7JY1P

### Sideload the APK (requires 'allow insecure apps' setting)
[Download Latest from UUID0xFD6FTracer.apk](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.1.7/UUID0xFD6F_v0.9.1.7.apk)

## Settings & Configuration
Additional information about the settings and options can be found on the separate [SETTINGS.md](./SETTINGS.md).

## Recording ![GPSLoggerII](./misc/docs/gpsl-icon.png)
I have kept this application as simple & lightweight as possible - when you like to record beacons and see them over a
timeline you can install the free available GPSLogger II app from Google PlayStore.

[Details & Download about GPSLogger II (free, no adds)](/LOGGING_de.md)

---
#### This app will not be available via Google's PlayStore
Also the second attempt failed after a few days - Google have decided that the App Title __Beacon UUID 0xFD6F Scanner__
is violating their's Developer Program Policies (8.3.).

I will give up here - it does not make any sense to me to try to argue with bot's and low payed support stuff - It am
just pissed that they did not provided this information earlier.

Have in mind this is not the first time - After the first version of the app was available in PlayStore it was removed
by Google after few days - I have tried to summarize here the communication with the *support* team - at the end the
**only** solution was to release the app with a different name & package.id (which is quite a shame). This is for sure
not easier for end users who have installed the previous app - since for them there is no working update path - they
need to deinstall the old app and reinstall the new from PlayStore - Thanks for NOTHING Google!
[If you like to read the details - just read here.](/GOOGLEPLAYSTORE.md)