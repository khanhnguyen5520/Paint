package com.lutech.paintV3.UI.Activity

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.lutech.paintV3.NoteWidgetProvider
import com.lutech.paintV3.R
import com.lutech.paintV3.databinding.ActivityNoteDetailBinding
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the appWidgetId from the intent
        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        // Load the current note data
        loadNoteData()

        // Set click listener for the update button
        binding.btnUpdate.setOnClickListener {
            updateNote()
        }
    }

    private fun loadNoteData() {
        val sharedPrefs = getSharedPreferences("widget_data", Context.MODE_PRIVATE)
        val title = sharedPrefs.getString("widget_title_$appWidgetId", "")
        val content = sharedPrefs.getString("widget_content_$appWidgetId", "")
        val date = sharedPrefs.getString("widget_date_$appWidgetId", "")
        binding.edtTitle.setText(title)
        binding.edtContent.setText(content)
        binding.tvDate.text = date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNote() {
        val newTitle = binding.edtTitle.text.toString()
        val newContent = binding.edtContent.text.toString()
        val newDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

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
