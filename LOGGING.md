# Logging UUID 0xFD6F

## GPSLogger II ![AppLogo](/misc/docs/gpsl-icon.png)
Mit der aktuellsten Version der App GPSLogger (v2.0.0.116) ist es möglich die empfangenen Informationen (Zeit & Ort) zu speichern. Die App die seit über 10 Jahren immer wieder weiter entwicklet wird, hat natürlich noch eine vielzahl von Funktionen mehr - aber wenn man nicht nur wissen möchte, wieviele BluetoothLE Beacons aktuell um einen herum ein ExposureNotification Signal senden, sondern diese Information in reklation zu Zeit und Ort stehen möchte, dann ist dies über diese App möglich.

Der GPSLogger II ist natürlich auch kostenfrei und enthält keine Werbung

## Logging verwenden
Zunächst muss man in den Einstellungen der App das Aufzeichnen der BluetoothLE Beacons mit der UUID 0xFD6F aktivieren. Hierzu startet man die app, öffnet das Menu und wächlt den Menüpunkt 'Einstellungen'. Aus der Liste von Optionen wählt man den obersten Punkt 'Allgemeine Einstellungen...'. Zuletzt muss der Schalter 'UUID 0xFD6F Beacon Scanner' aktiviert werden. (Diese Einstellung muss natürlich nur einmal erfolgen.)

Um Ihr System nicht unnötig zu belasten, sollten der 'UUID GPSLogger II Scann Dienst' und der 'UUID 0xFD6F Tracer' nicht gleichzeitig laufen - es schadet aber auch nicht wirklich.

Sobald der Scenner in den Einstellunge aktiviert wurde, kann die Aufzeichnung über den 'REC' Button in der Hauptleiste gestartet werden... Nach einer kurzen Zeit sollten alle Beacons der Umgebung angezeigt werden. Bitte beachten, dass die Beacons regelmäßig Ihrer Addresse ändern und deswegen als neuer Beacon erkannt werden.

![Sample](/misc/docs/gpsl-uuid.png)

Wichtig ist noch das man die Vektor-Karten Darstellung bei der Karte verwendet - Dies kann man in den Einstellungen wie folgt Prüfen:
1. Menu: Einstellungen...
1. Darstellung & Applikationsstart
1. Karten & Strecken Ansichtseinstellungen...
1. Option 'Vektor Karten Darstellung' aktivieren

Wenn diese option nicht aktiviert ist, werden derzeit keine Beacons angezeigt (ist noch auf der TODO-List).

Wenn man diese Option aktiviert muss der GPSLogger neu gestartet werden.

## Zählweise
Lore Ipsum - Hallo Ralph 


## Download
Die App ist kostenlos & ohne Werbung im GoolePlayStore verfügbar - https://play.google.com/store/apps/details?id=com.emacberry.gpslogger

[![Google Play](/misc/playstore/google-play-badge_de.png)](https://play.google.com/store/apps/details?id=com.emacberry.gpslogger)
