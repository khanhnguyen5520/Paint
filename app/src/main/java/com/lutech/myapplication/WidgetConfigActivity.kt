package com.lutech.myapplication

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class WidgetConfigActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        setResult(RESULT_CANCELED)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val titleEditText = findViewById<EditText>(R.id.edit_widget_title)
        val contentEditText = findViewById<EditText>(R.id.edit_widget_content)
        val addButton = findViewById<Button>(R.id.add_widget_button)

        addButton.setOnClickListener {
            val prefs = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE).edit()
            prefs.putString("title_$appWidgetId", titleEditText.text.toString())
            prefs.putString("content_$appWidgetId", contentEditText.text.toString())
            prefs.apply()

            val appWidgetManager = AppWidgetManager.getInstance(this)
            NoteWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}

