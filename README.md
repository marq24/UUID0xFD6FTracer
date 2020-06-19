# UUID 0xFD6F Tracer ![AppLogo](/app/src/main/res/mipmap-hdpi/ic_launcher_round.png)
## English
Main purpose of the app is to scann your current enviroment for active devices running an application that make use of Google's & Apple's exposurenotification Framework - this Framework ist use by some Corona App's in order to exchange information between mobile phones.

So this app will show you how many other devices are currently visible to your running Corona-App (like the Corona-Warn-App from the RKI) - So when you are at a place with some people you can judge how confident you could be concerning the possibility to be warned if anybody in this group will report to be infected later.

APK-Download directly here from GitHub: [UUID0xFD6F_v0.9.0.8.apk](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.0.8/UUID0xFD6F_v0.9.0.8.apk)

Join OPEN Beta @ Google Play (open on your mobile device)
[__not avialable yet__](https://play.google.com/apps/testing/com.emacberry.uuid0xfd6ftracer)

## Deutsch
Ich habe eine Funktion in der Corona-Warn-App des RKI's vermisst. Ich weiß, **ich habe sie installiert & aktiviert** - aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben Menschen um mich herum ebenfalls die App am Start?

Für Android habe ich deswegen eine kleine App geschrieben (natürlich kostenlos und ohne Werbung), mit der man angezeigt bekommt wie viele unterschiedliche Geräte um Euch herum ein Corona-Warn-App "Begegnungs-Erkennungs-Signal" senden.

Bis das ganze über den GooglePlayStore verfügbar ist, dauert leider noch ein paar Tage (ich warte auf die Freigabe), könnt Ihr Euch das APK einfach direkt von GitHub holen (mein erstes eigenes OpenSourceProjekt) und als SideLoad auf Eurem Android Telefon installieren. wenn Die Corona-Warn-App vom RKI bei Euch auf dem Gerät läuft, dann sollte auch der Tracer laufen.

APK-Download hier von GitHub: [UUID0xFD6F_v0.9.0.8.apk](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.0.8/UUID0xFD6F_v0.9.0.8.apk)

### Technisch
Der gemeinsam von Google und Apple entwickelte Standard über den die möglichen Kontakt-Informationen ausgetauscht werden, basiert darauf, dass auf dem Mobiltelefon ein BluetoothLE Beacon mit der **UUID 0xFD6F** gestartet wird.

Die App scannt die Umgebung permanent nach Beacons mit dieser UUID und zeigt Euch die Summe der gefunden "Sender" sowohl als System-Benachrichtigung - als auch in der App selbst an. Die UI ist hässlich, aber für das Anzeigen einer Ziffer sicherlich ausreichend. UI/UUX Hilfe ist erwünscht!

Den Scann kann von Hand angehalten oder neu starten- allerdings bin ich selbst von dem sehr geringen Akku-Verbrauch des Dienstes überrascht! Ich habe die App über Nacht laufen lassen und noch nicht mal 3% Akku benötigt.

Da die App Bluetooth verwendet, benötigt sie das Android Recht "Eure Position zu ermitteln" (*seuftz*) - natürlich macht sie das nicht - aber da theoretisch die Möglichkeit bestünde, gibt es diese Warnung vom Betriebsystem.

### Nutzen für Euch
Wer also in eine Traube von Menschen steht und sich fragt, ob wenigstens einer mit Eurer Corona-Warn-App (zumindest theoretisch) Daten austauschen könnte, dann könnt Ihr dies jetzt sehen!

Die App ermittelt nur, wie viel Beacons in Eurer Umgebung gestartet sind (das eigene Telefon zählt **nicht** mit)- ob die Corona-Warn-App ein anderes Gerät in seine interne Liste von möglichen Kontakten aufnimmt oder nicht liegt aber an vielen weiteren Faktoren!

Mit der App könnt Ihr also sehen, wie viele Mitmenschen um Euch herum den ExposureNotification Dienst ihres Mobiltelefones aktiviert haben (meiner _ganz persönlichen Meinung nach_ also Verantwortungsbewußt und solidarisch sind). Dies geht natürlich auch mit jeder anderen BluetoothLE Scanner App wie z.B. [RaMBLE](https://play.google.com/store/apps/details?id=com.contextis.android.BLEScanner&hl=en) und dem Filtern auf die UUID 0xFD6F.

### Möglicher Mißbrauch
Diese App ermöglicht die Kontrolle von Menschen, ob Sie die Corona-Warn-App aktiviert haben oder nicht (z.B. in Vereinzelungsanlagen oder bei Zugangskontrollen) - Diese Kontrolle ist jedoch technisch ebenfalls (mit anderen Mitteln) ohne Probleme möglich.

---

## Corona Skeptiker
Für die Verschwörungstheoretiker unter Euch: Für Euch ist die App auch super! Checkt, wie viel Verräter es in Euren eigenen Reihen gibt!
