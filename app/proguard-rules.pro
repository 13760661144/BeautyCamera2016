# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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

-keep class cn.image.** { *; }

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#人脸检测
-keep class com.uls.multifacetrackerlib.**{*;}



#--以下为换脸轻应用SDK相关混淆--start
#---------- 反射 ----------
-dontwarn java.lang.invoke.**
-keep class java.lang.invoke.**{*;}
#---------- 注解 ----------
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class javax.annotation.**{*;}
-dontwarn javax.annotation.**
#---------- OkHttp3 ----------
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
#---------- Retrofit ----------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
#---------- RxJava RxAndroid ----------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# so相关的
-keep class com.adnonstop.faceswaplibrary.FaceSwap{ *;}
-keep class com.uls.multifacetrackerlib.**{*;}
#----end

#七牛播放器
-keep class com.pili.pldroid.player.** { *; }
-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}