# Keep view binding classes
-keep class **.databinding.* { *; }
-keep class **.viewbinding.* { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Room database, DAO, and entities
-keep class androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep interface * implements androidx.room.Dao { *; }
-keep class **_Impl { *; }

# Keep model enums used via name/valueOf
-keepclassmembers enum * { *; }

# Keep custom views used in XML
-keep class com.example.smartexpensetracker.ui.report.view.BarChartView { *; }
