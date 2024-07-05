package com.lutech.myapplication

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

class NoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val ACTION_UPDATE_NOTE = "com.example.notepad.ACTION_UPDATE_NOTE"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            val noteTitle = prefs.getString("title_$appWidgetId", "No Title")
            val noteContent = prefs.getString("content_$appWidgetId", "No Content")

            val views = RemoteViews(context.packageName, R.layout.widget_note)
            views.setTextViewText(R.id.widget_note_title, noteTitle)
            views.setTextViewText(R.id.widget_note_content, noteContent)

            val intent = Intent(context, NoteWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_NOTE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            )
            views.setOnClickPendingIntent(R.id.widget_note_layout, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
