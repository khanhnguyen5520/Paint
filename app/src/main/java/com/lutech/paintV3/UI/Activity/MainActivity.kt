package com.lutech.paintV3.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.lutech.paintV3.DrawingView
import com.lutech.paintV3.NoteWidgetProvider
import com.lutech.paintV3.R
import com.lutech.paintV3.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var binding: ActivityMainBinding
    private val PICK_IMAGE_GALLERY = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_CAMERA_PERMISSION = 3
    private var currentPopup: PopupWindow? = null
    private var isExpanded = true
    private lateinit var seekBar: SeekBar

    private lateinit var currentPhotoPath: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = findViewById(R.id.paint_view)

        bottomBar()
        toolBar()
        toggleBar()

        val buttonAddWidget: Button = findViewById(R.id.add_widget_button)
        buttonAddWidget.setOnClickListener {
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
            }
        }
    }

    private fun bottomBar() {
        binding.btnPen.setOnClickListener {
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 150
            }
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 100
            }
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 100
            }
            drawingView.setEraser(false)
            showColorPenPopup(256)
        }

        binding.btnMarker.setOnClickListener {
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 150
            }
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 100
            }
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 100
            }
            showColorPenPopup(128)
        }

        binding.btnErase.setOnClickListener {
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 150
            }
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 100
            }
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 100
            }
            showEraserPopup()
        }
    }

    private fun toolBar() {
        binding.btnUndo.setOnClickListener {
            drawingView.undo()
        }

        binding.btnRedo.setOnClickListener {
            drawingView.redo()
        }

        binding.btnAddImage.setOnClickListener {
            showBottomSheet()
        }


        binding.btnSave.setOnClickListener {
            drawingView.saveCanvasToFile()
        }
    }

    private fun showColorPenPopup(alpha: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_size_color, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        currentPopup = PopupWindow(popupView, width, height, true)
        currentPopup!!.elevation = 5f

        // show the popup window
        currentPopup!!.showAtLocation(binding.root, Gravity.BOTTOM, 0, 300)
        val wavyLine = popupView.findViewById<ImageView>(R.id.wavyLine)
        val seekBar = popupView.findViewById<SeekBar>(R.id.seekbarSize)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val btnColor = popupView.findViewById<ImageView>(R.id.btnColor)
        btnColor.setOnClickListener {
            openColorPicker(alpha)
        }

        popupView.findViewById<ImageButton>(R.id.btnBlack).setOnClickListener {
            drawingView.setColor(Color.BLACK, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            }
            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN
            )

        }

        popupView.findViewById<ImageButton>(R.id.btnRed).setOnClickListener {
            drawingView.setColor(Color.RED, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            }

            wavyLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))

            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN
            )
        }

        popupView.findViewById<ImageButton>(R.id.btnOrange).setOnClickListener {
            drawingView.setColor(R.color.orange, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
            }
            wavyLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))

            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.orange), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.orange), PorterDuff.Mode.SRC_IN
            )
        }

        popupView.findViewById<ImageButton>(R.id.btnYellow).setOnClickListener {
            drawingView.setColor(Color.YELLOW, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            }

            wavyLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))

            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.yellow), PorterDuff.Mode.SRC_IN
            )
        }

        popupView.findViewById<ImageButton>(R.id.btnGreen).setOnClickListener {
            drawingView.setColor(Color.GREEN, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            }
            wavyLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN
            )
        }

        popupView.findViewById<ImageButton>(R.id.btnBlue).setOnClickListener {
            drawingView.setColor(Color.BLUE, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            }
            wavyLine.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            seekBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_IN
            )
            seekBar.thumb.setColorFilter(
                ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun openColorPicker(alpha: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_color, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.elevation = 20F

        // show the popup window
        popupWindow.showAtLocation(binding.root, Gravity.BOTTOM, 0, 400)

        val colorPickerView = popupView.findViewById<ColorPickerView>(R.id.colorPickerView)
        val brightnessSlideBar = popupView.findViewById<BrightnessSlideBar>(R.id.brightnessSlide)

        colorPickerView.setColorListener(object : ColorEnvelopeListener {
            @SuppressLint("ResourceType")
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                drawingView.setColor(envelope!!.color, alpha)
            }
        })
        colorPickerView.attachBrightnessSlider(brightnessSlideBar)
    }

    private fun showEraserPopup() {
        drawingView.setEraser(true)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_eraser, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        currentPopup = PopupWindow(popupView, width, height, focusable)
        currentPopup!!.elevation = 5f

        currentPopup!!.showAtLocation(binding.root, Gravity.BOTTOM, 0, 400)

        seekBar = popupView.findViewById(R.id.seekBar)
        val circleView = popupView.findViewById<View>(R.id.circleView)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateCircleSize(circleView, progress)
                drawingView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        val btnDelete = popupView.findViewById<MaterialButton>(R.id.deleteAll)
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
            currentPopup!!.dismiss()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val deleteLog = AlertDialog.Builder(this)
        deleteLog.setTitle("Xoá tất cả bản vẽ?")
        deleteLog.setPositiveButton("XÁC NHẬN") { dialog, _ ->
            drawingView.deleteAll()
            dialog.dismiss()
        }
        deleteLog.setNegativeButton("HUỶ BỎ") { dialog, _ ->
            dialog.dismiss()
        }
        deleteLog.show()
    }

    private fun updateCircleSize(circleView: View, progress: Int) {
        val minSize = 1
        val maxSize = 80
        val newSize = minSize + (maxSize - minSize) * progress / seekBar.max

        val layoutParams = circleView.layoutParams
        layoutParams.width = newSize
        layoutParams.height = newSize
        circleView.layoutParams = layoutParams
    }

    private fun toggleBar() {

        binding.toggleButton.setOnClickListener {
            if (isExpanded) {
                collapseToolBar()
            } else {
                expandToolBar()
            }
        }
        binding.btnRectangle.setOnClickListener {
            drawingView.setMode("RECTANGLE")
        }

        binding.btnDraw.setOnClickListener {
            drawingView.setMode("NORMAL")
        }
        binding.btnCircle.setOnClickListener {
            drawingView.setMode("CIRCLE")
            binding.btnCircle.setBackgroundColor(Color.parseColor("#d8e8f5"))
        }
        binding.btnLine.setOnClickListener {
            drawingView.setMode("LINE")
        }
    }

    private fun collapseToolBar() {
        binding.toggleBar.visibility = View.GONE
        binding.toggleButton.setImageResource(R.drawable.ic_expand_more)
        isExpanded = !isExpanded
    }

    private fun expandToolBar() {
        binding.toggleBar.visibility = View.VISIBLE
        binding.toggleButton.setImageResource(R.drawable.ic_expand_less)
        isExpanded = !isExpanded
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_photo, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        val btnGallery = bottomSheetView.findViewById<LinearLayout>(R.id.btnGallery)
        btnGallery.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        val btnCamera = bottomSheetView.findViewById<LinearLayout>(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
            bottomSheetDialog.dismiss()
        }

        val btnCancel = bottomSheetView.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_GALLERY)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.lutech.paintV3.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to take pictures",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val options = BitmapFactory.Options()
            options.inSampleSize = 3
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            drawingView.loadBitmap(bitmap!!)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 3
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
            drawingView.loadBitmap(bitmap)
        }
    }
}