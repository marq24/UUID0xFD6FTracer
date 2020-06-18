# [AppLogo](/app/src/main/res/mipmap-hdpi/ic_launcher_round.png) UUID 0xFD6F Tracer !
## English
Main purpose of the app is to scann your current enviroment for active devices running an application that make use of Google's & Apple's exposurenotification Framework - this Framework ist use by some Corona App's in order to exchange information between mobile phones.

So this app will show you how many other devices are currently visible to your running Corona-App (like the Corona-Warn-App from the RKI) - So when you are at a place with some people you can judge how confident you could be concerning the possibility to be warned if anybody in this group will report to be infected later.

Join OPEN Beta @ Google Play (open on your mobile device)
https://play.google.com/apps/testing/com.emacberry.uuid0xfd6ftracer

## Deutsch
Ich habe eine Funktion in der Corona-Warn-App des RKI's vermisst. Ich weiß, ich habe sie installiert & aktiviert...
Aber wie sieht es denn in meinem aktuellen Umfeld aus? Haben die Menschen um mich herum ebenfalls die App am Start?

Für Android habe ich deswegen eine kleine App geschrieben, mit der man angezeigt bekommt wie viele unterschiedliche Geräte um Euch herum ein Corona-Warn-App "Begegnungs-Erkennungs-Signal" senden.

Bis das ganze über den GooglePlayStore verfügbar ist, dauert leider noch ein paar Tage (ich warte auf die Freigabe).

Solange könnt Ihr Euch das APK einfach direkt von GitHub holen (mein erstes echtes OpenSourceProjekt) und als SideLoad installieren.

Download: https://github.com/…/downlo…/0.9.0.3/UUID0xFD6F_v0.9.0.3.apk

Technisch: Der gemeinsam von Google und Apple entwickelte Standard über den die möglichen Kontakt-Informationen ausgetauscht werden, basiert auf dem Start eines BluetoothLE Beacons mit der UUID 0xFD6F auf dem Mobiltelefon.

Die App scannt die Umgebung permanent nach Beacons mit dieser UUID und zeigt euch die Summe der gefunden "Sender" sowohl als System-Benachrichtigung - als auch in der App selbst an. Die UI ist hässlich, aber für das Anzeigen einer Ziffer ausreichend.

Der Scann kann von Hand angehalten & gestartet werden - allerdings bin ich selbst von dem sehr geringen Akku-Verbrauch des Dienstes überrascht!

Da die App Bluetooth verwendet, benötigt sie das Android Recht "Eure Position zu ermitteln" (*seuftz*) - natürlich macht sie das nicht - aber da theoretisch die Möglichkeit bestünde....

Also wer in eine Traube von Menschen steht und sich fragt, ob wenigstens einer mit Eurer Corona-Warn-App (zumindest theoretisch) Daten austauschen könnte, dann könnt Ihr dies jetzt sehen!

Die App ermittelt nur, wie viel Beacons in Eurer Umgebung gestartet sind (und zählt sich selbst auch mit) - ob die Corona-Warn-App ein anderes Gerät in seine Liste von möglichen Kontakten aufnimmt oder nicht liegt aber an vielen weiteren Faktoren!

Mit der App könnt Ihr also sehen, wie viele Mitmenschen um Euch herum verantwortungsvoll und solidarisch ist.

Teilen erwünscht...

Und für die Verschwörungstheoretiker unter Euch: Für Euch ist die App auch super! Checkt, wie viel Verräter es in Euren eigenen Reihen gibt!
