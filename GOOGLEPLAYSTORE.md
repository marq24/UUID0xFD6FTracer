# To be in GooglePlayStore or not to be in GooglePlayStore ![Google Play](/misc/playstore/google-play-badge_en.png)
## Frustration
I made the big mistake to upload UUID 0xFD6F Tracer app to GooglePlay and made the even bigger mistake to provide in the English app description some technical background information. In that section I have mentioned the 'ExposureNotification Framework'. This keywords triggered an instant ban of the app via GooglePlay (initially only the German app description was available - which was probably fine - or simply not understood by the scan-algorithm).

So the app was locked in Google's PlayStore - which implies that I as developer can not even access the app any longer in the backend of the so called Google Play Console (in order to alter anything) - so once your app is locked there is no way for you as developer to alter e.g. the app description. With other words: Google decide that you have no access right any longer to things you have created. This implies that you even does not have a single chance the read your initial given app description to get an idea, what might have lead to the ban.

## Chronology
### 24.06.2020, 05:52 [Google]
App was banned - with the information that:
 
_If you are not a government entity or public health organization (or your app is not commissioned or approved by one of these entities): Make appropriate changes to your app, and be sure to remove references and/or keywords related to COVID-19._

_Make appropriate changes to your app, and be sure to remove references and/or keywords related to COVID-19._

_Double check that your app is compliant with all other Developer Program Policies._ 

_Sign in to your Play Console and submit the policy compliant app using a new package name and a new app name._

### 24.06.2020, 07:39 [marq24/Google]
I have filed an appeal, 'explaining, that my app is not a coronavirus app nor it's using the ExposureNotification Framework and got an instant confirmation

_Dear Google Play Developer,_

_Thanks for contacting Google Play. Your appeal has been submitted successfully and will be reviewed by a **specialist**. The ticket number for your appeal is referenced in the subject of this message. Currently, we are experiencing a high volumes so there may be a delay in our responses. Thank you for your patience and understanding while we work on getting a response back to you as soon as possible._  

_Please refrain from sending duplicate appeals as this will not reduce response time._  

_Thanks,_
_The Google Play Team_

### 30.06.2020, 05:04 [Google]
I received a reply - (6 days later) - telling me exactly the same that was initially provided at the _24.06.2020, 05:52_ - no background information what have lead to Google's judgement to categorize 'UUID 0xFD6F Tracer' as _app that currently contains references of COVID-19 and tracing_. 

### 30.06.2020, 10:11 [marq24]
_Unfortunately I am not able to get any new information from your reply - this was/is just the text I already received with the LOCK notification - I just can continue speculate - since you have decided to ma app to 'LOCK' state in GooglePlayConsole I am not able to see/check/verify - nor I am able to correct anything that might be wrong with the app. Therefore I kindly ask to move the app to the state "REMOVED" - which would allow me to see the current app description. It's correct that I probably typed the word "coronavirus" in the English App description - but it would have been no issue at all to alter this so that the app would be compliant with Section 8.3. But to be honest - after almost one week I can not remember every single word that I have typed in the app description in order to check, what would have caused such a drastic action (LOCK) and add a penalty to my developer account._

_The App does not use the 'Exposure Notification API' in any form - the app makes uses of the stock Bluetooth-APIs available since LOLLIPOP - scan the Environment for BluetoothLE Beacons with a specific ID - the APP is OpenSource any can be reviewed any time https://github.com/marq24/UUID0xFD6FTracer/ - Yes - it is correct that I have one class in the sources that had the name 'Covid19Beacon' - I am uncertain if this java class name was already enough to be banned - nevertheless the class is already renamed to a more reasonable name (to reflect the core function - UUIDFD6FBeacon) and could be updated (if I would had access)._

_Renaming the App and give it a new package.id would not solve the issue for me right now - since I do not have enough information what was/is the root cause for the lock - again - I can of course read in your reply - "your app currently contains references of COVID-19 and tracing" - Is it only the app description? (which I can not verify right now) - No big deal - allow me to alter the description and all is fine - Is it the one class name? - No big deal - allow me to upload an updated APK - all is fine - Is it the fact, that I have explained in the app description that the Exposure Notification Framework creates a BluetoothLE Beacon with a specific ID that can be scanned by any BluetoothLE Receiver?! - this is a fact that I can remove from the description - but honestly - this is nothing special about my app - there are plenty of tools who can do it. Please allow me to alter the App Store Entry - allow me to upload a new APK - then you can double check IF this app will violate against 8.3 or any other GooglePlay Policy. If it's none of the above (description text, class name), then please provide more information, why Google believes that an app that is using stock Lollipop android - and only androidx.* dependencies (and no 'play-services' at all) is not welcome in GooglePlayStore._

_I hope I do not have to wait additional 6 days to receive a reply with some new information._

### 01.07.2020, 10:29 [Google]
_Thanks for your reply._

_In your app's description claims the feature such as "Google's & Apple's ExposureNotification Framework - **this Framework ist use by some Corona App's in order to exchange information between mobile phones.**" and "**to be warned if anybody in this group will report to be infected later.**"._

_We have confirmed our initial decision and your app will not be reinstated as long as valid proof of document is submitted._

_If you are not a government entity or public health organization (or your app is not commissioned or approved by one of these entities): Make appropriate changes to your app, and be sure to remove references and/or keywords related to COVID-19 using a new package name and a new app name._

_Thanks for your understanding and continued support of Google Play._

### 01.07.2020, 10:53 [marq24]
_thanks for the fast response - so basically you ban the app cause in the app description I made the error to explain the functionality & purpose of Google's ExposureNotification Framework? Seriously?_

_Where do I claim, that "**my app  will warned (you) if anybody in this group will report to be infected later**" ?! - You are taking these sentences of the app description totally out of any context - Where do I claim that my app use any feature provided by the ExposureNotification Framework ?!_

_"...Google's & Apple's ExposureNotification Framework - **this Framework** is use by some Corona App's in order to exchange information between mobile phones."_

_'**This Framework**" is obviously **your** Framework... It would be no deal to remove that last section from the app description "this Framework is use by some Corona App's in order to exchange information between mobile phones." - on order to avoid any misunderstandings (and do not get FALSE positive hits when users search for Corona-App's) - Do you have difficulties to get the app description removed from your search index - is this the root cause of the ban - at least that would be an explanation (even not really understandable)..._ 

_it's true that english is not my mother though - but I am sorry - this is going totally the wrong way. You want to help people to get correct/valid information about COVID-19 - totally fair. But I really don't understand why you misinterpret the app description drastically while you have all options to verify that the App does not make any use of  ExposureNotification Framework. App Description "could" be altered in 3min._

### 02.07.2020, 04:22 [Google]
_Thanks for your reply._

_As much as I'd like to help, I’m not able to provide any more detail or a better answer to your question. In our previous email, I made sure to include all the information available to me._

_You should be able to find more about your issue here: **The requirements for coronavirus disease 2019 (COVID-19) apps**, Section 8.3 of the Developer Distribution Agreement, the Enforcement policy, and our Help Center article_ 

_Thanks for your understanding and continued support of Google Play._

## Conclution
So what should I do? - it seams to be impossible to get out of this endless loop - and this explains why there is no UUID 0xFD6F Tracer in GooglePlay
