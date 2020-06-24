# Logging UUID 0xFD6F

## GPSLogger II ![AppLogo](/misc/docs/gpsl-icon.png)
Mit der aktuellsten Version der App GPSLogger (v2.0.0.116) ist es möglich die empfangenen Informationen (Zeit & Ort) zu speichern. Die App die seit über 10 Jahren immer wieder weiter entwicklet wird, hat natürlich noch eine vielzahl von Funktionen mehr - aber wenn man nicht nur wissen möchte, wieviele BluetoothLE Beacons aktuell um einen herum ein ExposureNotification Signal senden, sondern diese Information in reklation zu Zeit und Ort stehen möchte, dann ist dies über diese App möglich.

Der GPSLogger II ist natürlich auch kostenfrei und enthält keine Werbung

## Download
Die App ist kostenlos & ohne Werbung im GoolePlayStore verfügbar - https://play.google.com/store/apps/details?id=com.emacberry.gpslogger

[![Google Play](/misc/playstore/google-play-badge_de.png)](https://play.google.com/store/apps/details?id=com.emacberry.gpslogger)

## Logging verwenden
Zunächst muss man in den Einstellungen der App das Aufzeichnen der BluetoothLE Beacons mit der UUID 0xFD6F aktivieren. Hierzu startet man die app, öffnet das Menu und wächlt den Menüpunkt 'Einstellungen'. Aus der Liste von Optionen wählt man den obersten Punkt 'Allgemeine Einstellungen...'. Zuletzt muss der Schalter 'UUID 0xFD6F Beacon Scanner' aktiviert werden. (Diese Einstellung muss natürlich nur einmal erfolgen.)

Um Ihr System nicht unnötig zu belasten, sollten der 'UUID GPSLogger II Scann Dienst' und der 'UUID 0xFD6F Tracer' nicht gleichzeitig laufen - es schadet aber auch nicht wirklich.

Sobald der Scanner in den Einstellunge aktiviert wurde, kann die Aufzeichnung über den 'REC' Button in der Hauptleiste gestartet werden... Nach einer kurzen Zeit sollten alle Beacons der Umgebung angezeigt werden. Bitte beachten, dass die Beacons regelmäßig Ihrer Addresse ändern und deswegen als neuer Beacon erkannt werden.

![Sample](/misc/docs/gpsl-uuid.png)

Wichtig ist noch das man die Vektor-Karten Darstellung bei der Karte verwendet - Dies kann man in den Einstellungen wie folgt Prüfen:
1. Menu: Einstellungen...
1. Darstellung & Applikationsstart
1. Karten & Strecken Ansichtseinstellungen...
1. Option 'Vektor Karten Darstellung' aktivieren

Wenn diese option nicht aktiviert ist, werden derzeit keine Beacons angezeigt (ist noch auf der TODO-List).

Wenn man diese Option aktiviert muss der GPSLogger neu gestartet werden.

## Zählweise
Im Gegensatz zum 'UUID 0xFD6D Tracer' zählt der GPSLogger aktuell nicht die "aktiven" Beacons in der Umgebung - sondern zählt die eindeutigen IDs. Man stelle sich das folgende Scenario vor:

Man selbst steht an einem festen Ort und es man begegnet immer wieder einer Person die ein Beacon gestartet hat. In diesem Fall zeit der UUID 0xFD6D Tracer einem immer wieder Null oder Eins an. Der GPSLogger II hingegen zählt jeden einzeln Beacon dem man begegnet ist - also wenn 10 Beacons an einem vorbei gehen, dann wird in der GPSLogger II App auch die Ziffer 10 angezeigt (an diesem Ort sind einem in Summe 10 Beacon ID's begegnet).

Ist man Stationär und es begegnen einem 10 unterschiedliche Beacons zur gleichen Zeit, zeigt der 'UUID 0xFD6D Tracer' 10 Active Sender und auch der GPSLogger wird einem die Zahl 10 anzeigen.

Nun ist das besondere Merkmal des Exposure Notification Frameworks, dass sich die Addrese des Beacons regelmäßig ändert (meiner bisherigen Erfahrung nach so alle 2min) - Diese Address-Änderung kann von keinem System von einem neuen Beacon unterschieden werden - es ist als nicht feststellbar, dass diese Neue-ID von dem gleichen Mobiltelefon aus gesendet wird oder aber sich ein neues Mobiltelefon in die Empfangsweite eingedrungen ist (dies ist ein echtes Privacy-Feature). 

Dem UUID 0xFD6D Tracer ist dieser Umstand "egal" - ein Beacon ist verschwunden - ein neues ist hinzugekommen - die Summe der Sender bleibt gleich. Anders in der Anzeige im GPSLogger - hier wird je neuer Address/Beacon der Zähler erhöht.
