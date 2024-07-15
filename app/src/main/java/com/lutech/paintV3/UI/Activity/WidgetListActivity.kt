package com.lutech.paintV3.UI.Activity

// WidgetListActivity.kt
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lutech.paintV3.NoteWidgetProvider
import com.lutech.paintV3.R
import com.lutech.paintV3.WidgetListAdapter

class WidgetListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_list)

        val listView: ListView = findViewById(R.id.widget_list_view)

        val widgetNames = arrayOf("Widget 1", "Widget 2", "Widget 3")
        val adapter = WidgetListAdapter(this, widgetNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val widgetName = widgetNames[position]
            addWidgetToHomeScreen(widgetName)
        }
    }

    private fun addWidgetToHomeScreen(widgetName: String) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val myProvider = ComponentName(this, NoteWidgetProvider::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported) {
            val pinnedWidgetCallbackIntent = Intent(this, NoteWidgetProvider::class.java)
            val successCallback = PendingIntent.getBroadcast(
                this, 0, pinnedWidgetCallbackIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
            Toast.makeText(this, "$widgetName added to home screen", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pinning widgets is not supported on your device", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}


