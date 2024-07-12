package com.lutech.paintV3

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lutech.paintV3.UI.Activity.MainActivity
import com.lutech.paintV3.UI.Activity.NoteDetailActivity
import java.io.File

class NoteWidgetProvider : AppWidgetProvider() {
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
//        lateinit var file: File

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_note)

            // Pending intent to launch NoteDetailActivity
            val intent = Intent(context, NoteDetailActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context, appWidgetId, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

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
            views.setOnClickPendingIntent(R.id.btnAdd, addPendingIntent)

            // Update widget content
            val sharedPrefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)
            val title = sharedPrefs.getString("widget_title_$appWidgetId", "No Title")
            val content = sharedPrefs.getString("widget_content_$appWidgetId", "No Content")
            val date = sharedPrefs.getString("widget_date_$appWidgetId", "No date")
            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_content, content)
            views.setTextViewText(R.id.widget_date, date)

            //setImageViewFromFile(context, file, views, R.id.widget_image)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateNoteData(
            context: Context,
            appWidgetId: Int,
            title: String,
            content: String,
            date: String
        ) {
            val sharedPrefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)
            sharedPrefs.edit().apply {
                putString("widget_title_$appWidgetId", title)
                putString("widget_content_$appWidgetId", content)
                putString("widget_date_$appWidgetId", date)
                apply()
            }

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, NoteWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in appWidgetIds) {
                if (id == appWidgetId) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }

//        private fun setImageViewFromFile(
//            context: Context,
//            mFile: File,
//            views: RemoteViews,
//            imageViewId: Int
//        ) {
//            if (mFile.exists()) {
//                val uri: Uri = FileProvider.getUriForFile(
//                    context,
//                    "com.lutech.paintV3.fileprovider",
//                    mFile
//                )
//                Glide.with(context)
//                    .load(uri)
//                    .into(object : CustomTarget<Drawable>() {
//                        override fun onResourceReady(
//                            resource: Drawable,
//                            transition: Transition<in Drawable>?
//                        ) {
//                            views.setImageViewBitmap(imageViewId, (resource as BitmapDrawable).bitmap)
//                            AppWidgetManager.getInstance(context).updateAppWidget(
//                                ComponentName(context, NoteWidgetProvider::class.java),
//                                views
//                            )
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                            // Handle placeholder if needed
//                        }
//                    })
//            } else {
//                // Handle the case where the file doesn't exist
//            }
//        }
    }

}

