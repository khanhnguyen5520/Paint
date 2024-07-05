package com.lutech.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.google.android.material.button.MaterialButton
import com.lutech.myapplication.databinding.ActivityMainBinding
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
    private var isExpanded = false
    private lateinit var seekBar: SeekBar

    private lateinit var currentPhotoPath: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = findViewById(R.id.paint_view)

        binding.btnPen.setOnClickListener {
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 200
            }
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 150
            }
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 150
            }
            drawingView.setEraser(false)
            showColorPenPopup(256)
        }

        binding.btnMarker.setOnClickListener {
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 200
            }
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 150
            }
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 150
            }
            showColorPenPopup(128)
        }

        binding.btnErase.setOnClickListener {
            binding.btnErase.layoutParams = binding.btnErase.layoutParams.apply {
                height = 200
            }
            binding.btnPen.layoutParams = binding.btnPen.layoutParams.apply {
                height = 150
            }
            binding.btnMarker.layoutParams = binding.btnMarker.layoutParams.apply {
                height = 150
            }
            showEraserPopup()
        }

        binding.btnUndo.setOnClickListener {
            drawingView.undo()
        }

        binding.btnRedo.setOnClickListener {
            drawingView.redo()
        }

        binding.btnAddImage.setOnClickListener {
            showBottomSheet()
        }
        toggleBar()

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
        currentPopup!!.showAtLocation(binding.root, Gravity.BOTTOM, 0, 400)

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

        val btnBlack = popupView.findViewById<ImageButton>(R.id.btnBlack)
        btnBlack.setOnClickListener {
            drawingView.setColor(Color.BLACK, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            }

        }
        val btnRed = popupView.findViewById<ImageButton>(R.id.btnRed)
        btnRed.setOnClickListener {
            drawingView.setColor(Color.RED, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            }
        }

        val btnOrange = popupView.findViewById<ImageButton>(R.id.btnOrange)
        btnOrange.setOnClickListener {
            drawingView.setColor(R.color.orange, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
            }
        }

        val btnYellow = popupView.findViewById<ImageButton>(R.id.btnYellow)
        btnYellow.setOnClickListener {
            drawingView.setColor(Color.YELLOW, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            }
        }
        val btnGreen = popupView.findViewById<ImageButton>(R.id.btnGreen)
        btnGreen.setOnClickListener {
            drawingView.setColor(Color.GREEN, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            }
        }
        val btnBlue = popupView.findViewById<ImageButton>(R.id.btnBlue)
        btnBlue.setOnClickListener {
            drawingView.setColor(Color.BLUE, alpha)
            if (alpha == 256) {
                binding.btnPen.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            } else {
                binding.btnMarker.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            }
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

        binding.toggleBar.setOnClickListener {
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
        }
        binding.btnLine.setOnClickListener {
            drawingView.setMode("LINE")
        }
    }

    private fun collapseToolBar() {
        binding.btnRectangle.visibility = View.GONE
        binding.btnDraw.visibility = View.GONE
        binding.btnCircle.visibility = View.GONE
        binding.btnLine.visibility = View.GONE
        binding.toggleButton.setImageResource(R.drawable.ic_expand_more)
        isExpanded = false
    }

    private fun expandToolBar() {
        binding.btnRectangle.visibility = View.VISIBLE
        binding.btnDraw.visibility = View.VISIBLE
        binding.btnCircle.visibility = View.VISIBLE
        binding.btnLine.visibility = View.VISIBLE
        binding.toggleButton.setImageResource(R.drawable.ic_expand_less)
        isExpanded = true
    }

    private fun showBottomSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet)

        dialog.findViewById<LinearLayout>(R.id.takePhoto).setOnClickListener {
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
            dialog.dismiss()
        }

        dialog.findViewById<LinearLayout>(R.id.takeGallery).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_GALLERY)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
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
                        "com.lutech.myapplication.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
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
}





