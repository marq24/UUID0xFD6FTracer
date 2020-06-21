# UUID 0xFD6F Tracer ![AppLogo](/app/src/main/res/mipmap-hdpi/ic_launcher_round.png)
## English
Main purpose of the app is to scan your current environment for active devices running an application that make use of Google's & Apple's ExposureNotification Framework - this Framework ist use by some Corona App's in order to exchange information between mobile phones.

So this app will show you how many other devices are currently visible to your running Corona-App (like the Corona-Warn-App from the RKI) - So when you are at a place with some people you can judge how confident you could be concerning the possibility to be warned if anybody in this group will report to be infected later.

[![Google Play](/misc/playstore/google-play-badge_en.png)](https://play.google.com/store/apps/details?id=com.emacberry.uuid0xfd6ftracer)

Join OPEN Beta @ Google Play (open on your mobile device)

[Join BETA of UUID 0xFD6F Tracer @GoogelPlay](https://play.google.com/apps/testing/com.emacberry.uuid0xfd6ftracer)

alternative APK-Download directly here from GitHub: [LatestRelease](https://github.com/marq24/UUID0xFD6FTracer/releases/)

## Deutsch
Ich habe eine Funktion in der Corona-Warn-App des RKI's vermisst. Ich weiß, **ich habe sie installiert & aktiviert** - aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben Menschen um mich herum ebenfalls die App am Start?

Für Android habe ich deswegen eine kleine App geschrieben (natürlich kostenlos und ohne Werbung), mit der man angezeigt bekommt wie viele unterschiedliche Geräte um Euch herum ein Corona-Warn-App "Begegnungs-Erkennungs-Signal" senden.

Bis das ganze über den GooglePlayStore verfügbar ist, dauert leider noch ein paar Tage (ich warte auf die Freigabe), könnt Ihr Euch das APK einfach direkt von GitHub holen (mein erstes eigenes OpenSourceProjekt) und als SideLoad auf Eurem Android Telefon installieren. wenn Die Corona-Warn-App vom RKI bei Euch auf dem Gerät läuft, dann sollte auch der Tracer laufen.

[![Google Play](/misc/playstore/google-play-badge_de.png)](https://play.google.com/store/apps/details?id=com.emacberry.uuid0xfd6ftracer)

OPEN Beta @ Google Play beitreten (link direkt auf dem Mobiltelefon öffnen)

[BETA-Program von UUID 0xFD6F Tracer @GoogelPlay](https://play.google.com/apps/testing/com.emacberry.uuid0xfd6ftracer)

Oder alternativer APK-Download hier von GitHub: [Aktuellste APK](https://github.com/marq24/UUID0xFD6FTracer/releases/)

### Technisch
Der gemeinsam von Google und Apple entwickelte Standard über den die möglichen Kontakt-Informationen ausgetauscht werden, basiert darauf, dass auf dem Mobiltelefon ein BluetoothLE Beacon mit der **UUID 0xFD6F** gestartet wird.

Die App scannt die Umgebung permanent nach Beacons mit dieser UUID und zeigt Euch die Summe der gefunden "Sender" sowohl als System-Benachrichtigung - als auch in der App selbst an. Die UI ist hässlich, aber für das Anzeigen einer Ziffer sicherlich ausreichend. UI/UUX Hilfe ist erwünscht!

Den Scann kann von Hand angehalten oder neu starten- allerdings bin ich selbst von dem sehr geringen Akku-Verbrauch des Dienstes überrascht! Ich habe die App über Nacht laufen lassen und noch nicht mal 3% Akku benötigt.

Da die App Bluetooth verwendet, benötigt sie das Android Recht "Eure Position zu ermitteln" (*seuftz*) - natürlich macht sie das nicht - aber da theoretisch die Möglichkeit bestünde, gibt es diese Warnung vom Betriebssystem.

### Nutzen für Euch
Wer also in eine Traube von Menschen steht und sich fragt, ob wenigstens einer mit Eurer Corona-Warn-App (zumindest theoretisch) Daten austauschen könnte, dann könnt Ihr dies jetzt sehen!

Die App ermittelt nur, wie viel Beacons in Eurer Umgebung gestartet sind (das eigene Telefon zählt **nicht** mit) - ob die Corona-Warn-App ein anderes Gerät in seine interne Liste von möglichen Kontakten aufnimmt oder nicht liegt aber an vielen weiteren Faktoren!

Mit der App könnt Ihr also sehen, wie viele Mitmenschen um Euch herum den ExposureNotification Dienst ihres Mobiltelefones aktiviert haben (meiner _ganz persönlichen Meinung nach_ also Verantwortungsbewusst und solidarisch sind). Dies geht natürlich auch mit jeder anderen BluetoothLE Scanner App wie z.B. [RaMBLE](https://play.google.com/store/apps/details?id=com.contextis.android.BLEScanner&hl=en) und dem Filtern auf die UUID 0xFD6F.

### Möglicher Missbrauch
Diese App ermöglicht in Theorie die Kontrolle von Menschen, ob Sie ein 'Exposurenotification Dienst' Signal (wie von Corona-Warn-App verwendet) senden z.B. in Vereinzelungsanlagen oder bei Zugangskontrollen.

_Wenn_ ich z.B. als Arbeitgeber eine solche __unzulässige Kontrolle__ meiner Mitarbeiter anstrebe, dann würde ich direkt BluetoothLE-Beacon Scanner Hardware (mit einer viel höheren inernen Scan-Frequenz) verbauen und den Zugang meinen Vorstellungen nach steuern, anstatt einen Mitarbeiter mit der **UUID 0xFD6F Tracer App** in der Hand vor die Eingangstür zu stellen (Und schon wieder habe ich einen potentiellen Arbeitsplatz in Deutschland vernichtet).

### Moral & Ethik
Das ExposureNotification Framework von Apple & Google in der Welt - So wie Krebs, Waffen, und Donald Trump - Für einige Dinge hat sich die Gesellschaft auf Regeln geeinigt (regional unterschiedlich), wie damit im allgemeinen umzugehen ist. Diese App schafft Transparenz - nicht mehr und nicht weniger. _I didn't pull the trigger_

[//]: # (Vorab - Natürlich birgt ein _nicht vorhandener_ 'Exposurenotification Dienst' **keine** potentielle Gefahr einer Körperverletzung!)
[//]: # (Wenn mir jemand heute in Gütersloh einen Baseballschläger swingend entgegenkommt, dann treffe ich ganz alleine die Entscheidung [basierend auf meiner persönlichen Einstellung] ob und wie ich diesem Mitmenschen offen und unvoreingenommen begegne [oder es ggf. doch vermeide]. Wenn mir jemand mit einem Stiletto in der Hand entgegen kommt, habe ich weniger Möglichkeiten mein eigenes Verhalten der aktuellen Situation anzupassen [weshalb es mir durchaus Sinn ergibt, das solche Messer hierzulande Verboten sind].)
[//]: # ("_Ja - aber das ist doch was völlig anders_" - I don't think so!)    
   
---
## Corona Skeptiker
Für die Verschwörungstheoretiker unter Euch: Für Euch ist die App auch super! Checkt, wie viel Verräter es in Euren eigenen Reihen gibt!

---
#### Meine ganz persönliche Meinung
Ich habe jetzt so einiges lesen dürfen... Menschen die zu wissen glauben was ich mir möglicherweise Wünschte oder gar was ich Denke - bzw. die darüber spekulieren was ich reflektiere, impliziere und möglicherweise in Kauf nehme.

Für mich ganz persönlich überwiegen die Vorteile des Exposurenotification Frameworks die potentiellen möglichen Nachteile - dort wo es die Gesellschaft nicht schafft sinnvolle (und der Mehrheit vermittelbare) Regeln für den Umgang mit einer Technologie zu finden, ist die Politik gefodert entsprechende Gesetze zu verabschieden.
  
[//]: # (Schon so einige male habe ich mich in den letzten Monaten dabei ertappt, dass ich Denke, dass ich mit wünschte "_Corona mache doch bitte Unfruchtbar/Impotent_")
