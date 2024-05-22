package com.example.drawingapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var drawingView: DrawingView
    private lateinit var brushButton: ImageButton
    private lateinit var purpleButton: ImageButton
    private lateinit var redButton: ImageButton
    private lateinit var greenButton: ImageButton
    private lateinit var orangeButton: ImageButton
    private lateinit var blueButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var colorPickerButton: ImageButton
    private lateinit var galleryButton: ImageButton

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted && permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        brushButton = findViewById(R.id.brush_button)
        purpleButton = findViewById(R.id.purple_button)
        greenButton = findViewById(R.id.green_button)
        redButton = findViewById(R.id.red_button)
        orangeButton = findViewById(R.id.orange_button)
        blueButton = findViewById(R.id.blue_button)
        undoButton = findViewById(R.id.undo_button)
        colorPickerButton = findViewById(R.id.color_picker_button)
        galleryButton = findViewById(R.id.button_gallery)

        drawingView = findViewById(R.id.drawing_view)
        drawingView.changeBrushSize((23.toFloat()))

        brushButton.setOnClickListener {
            showBrushChooserDialog()
        }

        purpleButton.setOnClickListener(this)
        greenButton.setOnClickListener(this)
        redButton.setOnClickListener(this)
        orangeButton.setOnClickListener(this)
        blueButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        colorPickerButton.setOnClickListener(this)
        galleryButton.setOnClickListener(this)

    }

    private fun showBrushChooserDialog() {
        val brushDialog = Dialog(this@MainActivity)
        brushDialog.setContentView(R.layout.dialog_brush)
        val seekBarProgress = brushDialog.findViewById<SeekBar>(R.id.dialog_seek_bar)
        val showProgressTv = brushDialog.findViewById<TextView>(R.id.dialog_text_view_progress)

        seekBarProgress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, p1: Int, p2: Boolean) {
                drawingView.changeBrushSize(seekBar.progress.toFloat())

                showProgressTv.text = seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        brushDialog.show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.purple_button -> {
                drawingView.setColor("#D14EF6")
            }
            R.id.red_button -> {
                drawingView.setColor("#F37684")
            }
            R.id.orange_button -> {
                drawingView.setColor("#EFB041")
            }
            R.id.green_button -> {
                drawingView.setColor("#2DC40B")
            }
            R.id.blue_button -> {
                drawingView.setColor("#2F6FF1")
            }
            R.id.undo_button -> {
                drawingView.undoPath()
            }
            R.id.color_picker_button -> {
                showColorPickerDialog()
            }
            R.id.button_gallery -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission()
                } else {
                    // get image
                }
            }
        }
    }

    private fun showColorPickerDialog() {
        val dialog = AmbilWarnaDialog(this, Color.GREEN, object: OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                drawingView.setColor(color)
            }

        })
        dialog.show()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
            ){
                showRationaleDialog()
        } else {
            requestPermission.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Storage permission")
            .setMessage("We need this permission in order to access the internal storage")
            .setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                requestPermission.launch(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
                dialog.dismiss()
            }
        builder.create().show()
    }
}