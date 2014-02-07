FitbitAndroidSample
===================

Sample project using Scribe to connect to the Fitbit API in Android. All you will need is:

* Your API key
* Your API secret key

This is kinda-sorta-working, but there are a few bugs:

* (The big bug) Every time you run it, you have to get another PIN. I'd have thought that Fitbit would return the same one each time, but it doesn't. This is probably a part of my hapless failure to understand how OAuth really works
* The authorisation window pops a new browser window if you've already authorised the app once before, which leaves you hitting the back button to go back to your app

Hope this is some use to someone - after the frustration I had getting this far I rather lost steam with what I intended doing, but perhaps you'll fare better...
