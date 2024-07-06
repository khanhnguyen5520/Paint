package com.lutech.paintV3

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var dateTextView:TextView
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        val buttonUpdate: Button = findViewById(R.id.buttonUpdate)
        dateTextView = findViewById(R.id.tvDate)


        // Get the appWidgetId from the intent
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        // Load the current note data
        loadNoteData()

        // Set click listener for the update button
        buttonUpdate.setOnClickListener {
            updateNote()
        }
    }

    private fun loadNoteData() {
        val sharedPrefs = getSharedPreferences("widget_data", Context.MODE_PRIVATE)
        val title = sharedPrefs.getString("widget_title_$appWidgetId", "")
        val content = sharedPrefs.getString("widget_content_$appWidgetId", "")
        val date = sharedPrefs.getString("widget_date_$appWidgetId","")
        editTextTitle.setText(title)
        editTextContent.setText(content)
        dateTextView.text = date

    }

    private fun updateNote() {
        val newTitle = editTextTitle.text.toString()
        val newContent = editTextContent.text.toString()
        val newDate = dateTextView.text.toString()

        // Save the updated note data
        NoteWidgetProvider.updateNoteData(this, appWidgetId, newTitle, newContent, newDate)

        // Return the result to the app widget manager
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}
