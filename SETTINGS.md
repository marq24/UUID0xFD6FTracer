# Settings UUID 0xFD6F Tracer
![Sample](./misc/docs/settings01.png)

## Prioritized Background Activity
OFF - Battery optimization has been disabled - To enabled it again you need to do this via the general device settings.
ON - You should only enabled this setting when you encounter unexpected app terminations by the OS.

## Service Autostart
OFF
ON - The 'Background Tracer Service' will be automatically started after a device reboot (after successful login)

## Display total number
OFF
ON - The app also shows the total of all beacons found so far (restart the service to reset).

## Countries
![Sample](./misc/docs/settings02.png)

Countries might not be the best description here - since you can configure basically on which beacon UUIDs the App is
listening.

- UUID **0xFD6F**: Countries in which Apps are in place that make use of the ExposureNotification Framework developed by
Apple & Google. Please find a list below
- UUID **0xFD64**: TousAntiCovid (StopCovid France)
- UUID **0xFD6F** and **0xFD64**: So a combination of both UUIDs - where the app is going to display for each UUID a
separate counter (in the App - not in the system notification)

[List of countries that make use of the _Exposure Notification Framework_ (UUID 0xFD6F Beacaons)](./COUNTRIES.md)

## Expert Settings
### Group by Signal strength
Group the beacons by their signal strength into NEAR/MEDIUM/FAR
#### NEAR [def value: -82db] | MEDIUM [def value: -90db]
This means that if the signal strength is smaller the minus 82db, then the beacon is considered to be NEAR. Everything
between -82db and -90db will be considered as MIDDLE and everything below -90db will be listed at FAR.

So basically it's: 0db > NEAR-Range > -82db > MEDIUM-Range > -90db > FAR-Range > -∞db  

### Use Threshold
If the signal strength of a beacon is below the specified threshold it will not taken into account
