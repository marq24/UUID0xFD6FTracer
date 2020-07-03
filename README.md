# UUID 0xFD6F Tracer ![AppLogo](/app/src/main/res/mipmap-hdpi/ic_launcher_round.png)

---
## English
Main purpose of the app is to scan your current environment for active devices running an application that make use of Google's & Apple's ExposureNotification Framework.

[Download Latest UUID0xFD6FTracer.apk](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.1.1/UUID0xFD6F_v0.9.1.1.apk)

#### Why the app is not available in Google PlayStore yet?
To be honest - only Google knows - since more then a week I try to get this solved with the Google support - but looks like I am just doomed...

[If you like to read the details - just read here](/GOOGLEPLAYSTORE.md) 

---
## Deutsch
[//]: # (Ich habe eine Funktion in der Corona-Warn-App des RKI's vermisst. Ich weiß, **ich habe sie installiert & aktiviert** - aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben Menschen um mich herum ebenfalls die App am Start?)
Ich habe eine Funktion in der Warn-App des RKI's vermisst. Ich weiß, **ich habe sie installiert & aktiviert** - aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben Menschen um mich herum ebenfalls die App am Start?

[//]: # (Für Android habe ich deswegen eine kleine App geschrieben -natürlich kostenlos und ohne Werbung-, mit der man angezeigt bekommt wie viele unterschiedliche Geräte um Euch herum ein Corona-Warn-App "Begegnungs-Erkennungs-Signal" senden.)
Für Android habe ich deswegen eine kleine App geschrieben (natürlich kostenlos und ohne Werbung), mit der man angezeigt bekommt wie viele unterschiedliche Geräte um Euch herum ein "Begegnungs-Erkennungs-Signal" senden.

[Download des aktuellen UUID0xFD6FTracer.apk](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.1.1/UUID0xFD6F_v0.9.1.1.apk)

### Technisch
Der gemeinsam von Google und Apple entwickelte Standard über den die möglichen Kontakt-Informationen ausgetauscht werden, basiert darauf, dass auf dem Mobiltelefon ein BluetoothLE Beacon mit der **UUID 0xFD6F** gestartet wird.

Die App scannt die Umgebung permanent nach Beacons mit dieser UUID und zeigt Euch die Summe der gefunden "Sender" sowohl als System-Benachrichtigung - als auch in der App selbst an. Die UI ist hässlich, aber für das Anzeigen einer Ziffer sicherlich ausreichend. UI/UUX Hilfe ist erwünscht!

Den Scann kann von Hand angehalten oder neu starten- allerdings bin ich selbst von dem sehr geringen Akku-Verbrauch des Dienstes überrascht! Ich habe die App über Nacht laufen lassen und noch nicht mal 3% Akku benötigt.

Da die App Bluetooth verwendet, benötigt sie das Android Recht "Eure Position zu ermitteln" (*seuftz*) - natürlich macht sie das nicht - aber da theoretisch die Möglichkeit bestünde, gibt es diese Warnung vom Betriebssystem.

### Aufzeichnen? ![GPSLoggerII](/misc/docs/gpsl-icon.png)
Auf eine Möglichkeit zur Aufzeichnung der Beacon Signale habe ich zur Reduktion der Komplexität in dieser App verzichtet und diese Funktionalität statdessen in den über lange Jahre entwickleten GPSLogger II integriert.

[Details & Download zum GPSLogger II (kostenfrei & ohne Werbung)](/LOGGING.md)

### Nutzen für Euch
Wer also in eine Traube von Menschen steht und sich fragt, ob wenigstens einer mit Eurer Warn-App (zumindest theoretisch) Daten austauschen könnte, dann könnt Ihr dies jetzt sehen!

Die App ermittelt nur, wie viel Beacons in Eurer Umgebung gestartet sind (das eigene Telefon zählt **nicht** mit) - ob die Warn-App ein anderes Gerät in seine interne Liste von möglichen Kontakten aufnimmt oder nicht liegt aber an vielen weiteren Faktoren!

Mit der App könnt Ihr also sehen, wie viele Mitmenschen um Euch herum den ExposureNotification Dienst ihres Mobiltelefones aktiviert haben (meiner _ganz persönlichen Meinung nach_ also Verantwortungsbewusst und solidarisch sind). Dies geht natürlich auch mit jeder anderen BluetoothLE Scanner App wie z.B. [RaMBLE](https://play.google.com/store/apps/details?id=com.contextis.android.BLEScanner&hl=en) und dem Filtern auf die UUID 0xFD6F.

### Möglicher Missbrauch
Diese App ermöglicht in Theorie die Kontrolle von Menschen, ob Sie ein 'Exposurenotification Dienst' Signal (wie von Warn-App verwendet) senden z.B. in Vereinzelungsanlagen oder bei Zugangskontrollen.

_Wenn_ ich z.B. als Arbeitgeber eine solche __unzulässige Kontrolle__ meiner Mitarbeiter anstrebe, dann würde ich direkt BluetoothLE-Beacon Scanner Hardware (mit einer viel höheren inernen Scan-Frequenz) verbauen und den Zugang meinen Vorstellungen nach steuern, anstatt einen Mitarbeiter mit der **UUID 0xFD6F Tracer App** in der Hand vor die Eingangstür zu stellen (Und schon wieder habe ich einen potentiellen Arbeitsplatz in Deutschland vernichtet).

### Moral & Ethik
Das ExposureNotification Framework von Apple & Google in der Welt - So wie Krebs, Waffen, und Donald Trump - Für einige Dinge hat sich die Gesellschaft auf Regeln geeinigt (regional unterschiedlich), wie damit im allgemeinen umzugehen ist. Diese App schafft Transparenz - nicht mehr und nicht weniger. _I didn't pull the trigger_

[//]: # (Vorab - Natürlich birgt ein _nicht vorhandener_ 'Exposurenotification Dienst' **keine** potentielle Gefahr einer Körperverletzung!)
[//]: # (Wenn mir jemand heute in Gütersloh einen Baseballschläger swingend entgegenkommt, dann treffe ich ganz alleine die Entscheidung [basierend auf meiner persönlichen Einstellung] ob und wie ich diesem Mitmenschen offen und unvoreingenommen begegne [oder es ggf. doch vermeide]. Wenn mir jemand mit einem Stiletto in der Hand entgegen kommt, habe ich weniger Möglichkeiten mein eigenes Verhalten der aktuellen Situation anzupassen [weshalb es mir durchaus Sinn ergibt, das solche Messer hierzulande Verboten sind].)
[//]: # ("_Ja - aber das ist doch was völlig anders_" - I don't think so!)    
   
---
## Alluhutträger & Skeptiker
Für die Verschwörungstheoretiker unter Euch: Für Euch ist die App auch super! Checkt, wie viel Verräter es in Euren eigenen Reihen gibt!

---
#### Meine ganz persönliche Meinung
Ich habe jetzt so einiges lesen dürfen... Menschen die zu wissen glauben was ich mir möglicherweise Wünschte oder gar was ich Denke - bzw. die darüber spekulieren was ich reflektiere, impliziere und möglicherweise in Kauf nehme.

Für mich ganz persönlich überwiegen die Vorteile des Exposurenotification Frameworks die potentiellen möglichen Nachteile - dort wo es die Gesellschaft nicht schafft sinnvolle (und der Mehrheit vermittelbare) Regeln für den Umgang mit einer Technologie zu finden, ist die Politik gefordert entsprechende Gesetze zu verabschieden.
  
[//]: # (Schon so einige male habe ich mich in den letzten Monaten dabei ertappt, dass ich Denke, dass ich mit wünschte "_Corona mache doch bitte Unfruchtbar/Impotent_")
