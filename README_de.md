# UUID 0xFD6F Tracer ![AppLogo](./app/src/main/res/mipmap-hdpi/ic_launcher_round.png)

Ich habe eine Funktion in der Corona-Warn-App des RKI's vermisst. Ich weiß, **ich habe sie installiert & aktiviert** -
aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben Menschen um mich herum ebenfalls die App am Start?

Für Android habe ich deswegen eine kleine App geschrieben (natürlich kostenlos und ohne Werbung), mit der man angezeigt
bekommt wie viele unterschiedliche Geräte um Euch herum ein Corona-Warn-App "Begegnungs-Erkennungs-Signal" senden.

[Liste von Länderen deren Corona-App ebenfalls auf dem _Exposure Notification Framework_ (UUID 0xFD6F Beacaons) basiert
 und der UUID 0xFD6F Tracer ebenfalls funktioniert](./COUNTRIES.md)

## Installation
#### Über F-Droid
[![F-Droid appstore](./misc/fdroid/320px-Get_it_on_F-Droid.svg.png)](https://f-droid.org/app/com.emacberry.uuid0xfd6fscan)

https://f-droid.org/app/com.emacberry.uuid0xfd6fscan

#### Über den amazon appstore verfügbar
[![amazon appstore](./misc/amazon/amazon-appstore-badge-de-black.png)](https://www.amazon.de/gp/product/B08CY7JY1P)

https://www.amazon.de/gp/product/B08CY7JY1P

#### Installation auch als Sideload möglich (erfordert 'Installation von unsicheren Quellen zulassen')
[Download des aktuellen UUID0xFD6FTracer.apk direkt hier von GitHub](https://github.com/marq24/UUID0xFD6FTracer/releases/download/0.9.1.14/UUID0xFD6F_v0.9.1.14.apk)

##### Kein Download via GooglePlay?
Google hat ein zweites Mal entschieden die App aus dem Store zu entfernen, diesmal mit dem Hinweis darauf, dass der App
Title nicht ihren Richtlinien entspricht. Ich bin es leid zu versuchen mit den Bot's und unterbezahlten Support
Mitarbeitern zu diskutieren. Ich werde keinen dritten Versuch starten.
[Details kann man hier (in Englisch) nachlesen](/GOOGLEPLAYSTORE.md)

## Einstellungen / Einrichtung
Erläuterungen zu den Einstellungen und Optionen sind unter [SETTINGS.md](./SETTINGS.md) zu finden (auf Englisch).

## Technische Hintergrundinformationen
Der gemeinsam von Google und Apple entwickelte Standard [_Exposure Notification_] über den die möglichen
Kontakt-Informationen ausgetauscht werden, basiert darauf, dass auf dem Mobiltelefon ein BluetoothLE Beacon mit der
**UUID 0xFD6F** gestartet wird.

Die App scannt die Umgebung permanent nach Beacons mit dieser UUID und zeigt Euch die Summe der gefunden "Sender" sowohl
als System-Benachrichtigung - als auch in der App selbst an. Die UI ist nicht hübsch - jedoch ist sie aber für das
Anzeigen einer einzigen Ziffer sicherlich ausreichend. UI/UUX Unterstützung ist im Projekt willkommen!

Den Scann kann von Hand angehalten oder neu starten- allerdings bin ich selbst von dem sehr geringen Akku-Verbrauch des
Dienstes überrascht! Ich habe die App über Nacht laufen lassen und noch nicht mal 3% Akku benötigt.

Da die App Bluetooth verwendet, benötigt sie das Android Recht "Eure Position zu ermitteln" (*seuftz*) - natürlich macht
sie das nicht - aber da theoretisch die Möglichkeit bestünde, gibt es diese Warnung vom Betriebssystem.


## Aufzeichnen? ![GPSLoggerII](./misc/docs/gpsl-icon.png)
Auf eine Möglichkeit zur Aufzeichnung der Beacon Signale habe ich zur Reduktion der Komplexität in dieser App
verzichtet. Der über lange Jahre entwickelte GPSLogger II bietet die Funktionalität BluetoothLE Beacons, wie sie vom
_Exposure Notification_ Framework verwendet werden, aufzuzeichen.

[Details & Download vom GPSLogger II (kostenfrei & ohne Werbung)](/LOGGING_de.md)


## Nutzen für Euch
Wer also in eine Traube von Menschen steht und sich fragt, ob wenigstens einer mit Eurer Warn-App (zumindest
theoretisch) Daten austauschen könnte, dann könnt Ihr dies jetzt sehen!

Die App ermittelt nur, wie viel Beacons in Eurer Umgebung gestartet sind (das eigene Telefon zählt **nicht** mit) - ob
die Warn-App ein anderes Gerät in seine interne Liste von möglichen Kontakten aufnimmt oder nicht liegt aber an vielen
weiteren Faktoren!

Mit der App könnt Ihr also sehen, wie viele Mitmenschen um Euch herum den _Exposure Notification_ Dienst ihres
Mobiltelefones aktiviert haben (meiner _ganz persönlichen Meinung nach_ also Verantwortungsbewusst und solidarisch
sind). Dies geht natürlich auch mit jeder anderen BluetoothLE Scanner App wie z.B.
[RaMBLE](https://play.google.com/store/apps/details?id=com.contextis.android.BLEScanner&hl=en) und dem Filtern auf die
UUID 0xFD6F.


## Möglicher Missbrauch
Diese App ermöglicht in Theorie die Kontrolle von Menschen, ob Sie ein '_Exposure Notification_ Dienst' Signal (wie von
Warn-App verwendet) senden z.B. in Vereinzelungsanlagen oder bei Zugangskontrollen.

_Wenn_ ich z.B. als Arbeitgeber eine solche __unzulässige Kontrolle__ meiner Mitarbeiter anstrebe, dann würde ich direkt
BluetoothLE-Beacon Scanner Hardware (mit einer viel höheren inernen Scan-Frequenz) verbauen und den Zugang meinen
Vorstellungen nach steuern, anstatt einen Mitarbeiter mit der **UUID 0xFD6F Tracer App** in der Hand vor die Eingangstür
zu stellen (Und schon wieder habe ich einen potentiellen Arbeitsplatz in Deutschland vernichtet).


## Moral & Ethik
Das _Exposure Notification Framework_ von Apple & Google in der Welt - So wie Krebs, Waffen, und Donald Trump - Für
einige Dinge hat sich die Gesellschaft auf Regeln geeinigt (regional unterschiedlich), wie damit im allgemeinen
umzugehen ist. Diese App schafft Transparenz - nicht mehr und nicht weniger. _I didn't pull the trigger_

[//]: # (Vorab - Natürlich birgt ein _nicht vorhandener_ 'Exposurenotification Dienst' **keine** potentielle Gefahr einer Körperverletzung!)
[//]: # (Wenn mir jemand heute in Gütersloh einen Baseballschläger swingend entgegenkommt, dann treffe ich ganz alleine die Entscheidung [basierend auf meiner persönlichen Einstellung] ob und wie ich diesem Mitmenschen offen und unvoreingenommen begegne [oder es ggf. doch vermeide]. Wenn mir jemand mit einem Stiletto in der Hand entgegen kommt, habe ich weniger Möglichkeiten mein eigenes Verhalten der aktuellen Situation anzupassen [weshalb es mir durchaus Sinn ergibt, das solche Messer hierzulande Verboten sind].)
[//]: # ("_Ja - aber das ist doch was völlig anders_" - I don't think so!)    
   

## Meine ganz persönliche Meinung
Ich habe jetzt so einiges lesen dürfen... Menschen die zu wissen glauben was ich mir möglicherweise Wünschte oder gar
was ich Denke - bzw. die darüber spekulieren was ich reflektiere, impliziere und möglicherweise in Kauf nehme.

Für mich ganz persönlich überwiegen die Vorteile des _Exposure Notification Frameworks_ die potentiellen möglichen
Nachteile - dort wo es die Gesellschaft nicht schafft sinnvolle (und der Mehrheit vermittelbare) Regeln für den Umgang
mit einer Technologie zu finden, ist die Politik gefordert entsprechende Gesetze zu verabschieden.
  
[//]: # (Schon so einige male habe ich mich in den letzten Monaten dabei ertappt, dass ich Denke, dass ich mit wünschte "_Corona mache doch bitte Unfruchtbar/Impotent_")

---
## Alluhutträger & Skeptiker
Für die Verschwörungstheoretiker unter Euch: Für Euch ist die App auch super! Checkt, wie viel Verräter es in Euren
eigenen Reihen gibt!
