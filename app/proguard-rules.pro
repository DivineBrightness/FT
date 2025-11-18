# Add project specific ProGuard rules here.

# Keep data classes for serialization
-keep class com.yijing.divination.data.model.** { *; }
-keep class com.yijing.divination.data.local.database.entity.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Serializers
-keep,includedescriptorclasses class com.yijing.divination.**$$serializer { *; }
-keepclassmembers class com.yijing.divination.** {
    *** Companion;
}
-keepclasseswithmembers class com.yijing.divination.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt
-dontwarn com.google.errorprone.annotations.*
