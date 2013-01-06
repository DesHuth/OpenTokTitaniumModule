OpenTokTitaniumModule
=====================

There are instructions in the example folder for the use of this module.

The dist directory contains a compiled module using the source contained in
this project. To use this just extract the zip file to the module directory of
your Titanium project.

In order for the app to work you will also probably need to create a custom AndroidManifest.xml
file and add some permissions.  The easiest way to do this is to build your project and copy the generated AndroidManifest.xml from the build directory to platform/android

Open the AndroidManifest.xml file and add any of the following lines that are not present.

	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.ACCESS_MOCK_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<uses-permission android:name="android.permission.CAMERA"/>
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.RECORD_AUDIO" />
	
	<uses-feature android:name="android.hardware.camera" />
	<uses-feature android:name="android.hardware.camera.autofocus" />


I do not claim to be a competent native Android programmer (that's what Titanium is for!)
so the module may well not be the best or most efficient way go about using the OpenTok
Android libraries, if you want to tinker with the source I would suggest the following: 

Realistically the easiest way to create the module would be to create a
blank module using Titanium Studio and copy the contents of the
src/com/phc/opentok directory to the corresponding directory in your module project.
You will also need to copy the files lib directory and then add them to the build path
for the module.  This can be found in Titanium Studio by selecting The Project Menu
then clicking Properties, selct Java Build Path on the left menu, then click Libraries
at the top of the main dialog and Add External JARs.  Then browse to each of the jar
files copied to the lib directory. 
