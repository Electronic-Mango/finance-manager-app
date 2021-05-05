# Finance Manager Android app

## Description
This project contains a simple finance manager Android app written as a student assignment.
It is my first ever app and my first time programming in Kotlin.

All data is stored in internal SQLite DB (using
[Android Room](https://developer.android.com/training/data-storage/room)) so this app can actually
technically be used on a daily basis (even if it's quite basic).

App also allows external access to data via a custom
[`android.content.ContentProvider`](https://developer.android.com/reference/android/content/ContentProvider).
Only reading is possible, any modification of data is not allowed. Providing it was one of the
requirements of the assignment.

## Issues
Unfortunately due to time constraints it's not perfect regarding localization - only Polish language
is available and some strings/values are baked in Kotlin code rather than resources.

Included balance graph view is also very barebones - one of the requirements was not using any
external library, so entire thing is done by drawing on
[`android.graphics.Canvas`](https://developer.android.com/reference/android/graphics/Canvas) in
custom [`android.view.View`](https://developer.android.com/reference/android/view/View).
That and (again) time constraints did not allowed me to make it more polished.

Kotlin Android Extensions usage should be migrated to Jetpack view binding.

Unit tests for the project are missing (again due to time constraints).