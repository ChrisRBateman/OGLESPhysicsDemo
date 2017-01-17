# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Producing useful obfuscated stack traces
-printmapping out.map
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keepattributes EnclosingMethod

-keep class com.google.fpl.liquidfun.*
-keep enum com.google.fpl.liquidfun.*
-keep interface com.google.fpl.liquidfun.*
-keepclassmembers class com.google.fpl.liquidfun.liquidfunJNI {
    native <methods>;
    public *;
    private *;
}
