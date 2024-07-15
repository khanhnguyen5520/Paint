package com.lutech.paintV3

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.lutech.paintV3.UI.Activity.MainActivity

class WidgetBarProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_bar)

            // Pending intent to launch MainActivity when btnAdd is clicked
            val addIntent = Intent(context, MainActivity::class.java)
            val addPendingIntent = PendingIntent.getActivity(
                context, 0, addIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            views.setOnClickPendingIntent(R.id.draw, addPendingIntent)

            // Pending intent to trigger showBottomSheet function when addImage is clicked
            val bottomSheetIntent = Intent(context, BottomSheetReceiver::class.java).apply {
                action = MainActivity.ACTION_SHOW_BOTTOM_SHEET
            }
            val bottomSheetPendingIntent = PendingIntent.getBroadcast(
                context, 0, bottomSheetIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            views.setOnClickPendingIntent(R.id.addImage, bottomSheetPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
