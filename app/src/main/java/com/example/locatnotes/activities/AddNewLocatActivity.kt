package com.example.locatnotes.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.locatnotes.databinding.ActivityAddNewLocatBinding
import com.karumi.dexter.Dexter
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import com.example.locatnotes.R
import com.example.locatnotes.database.DatabaseHelper
import com.example.locatnotes.models.LocatNotesModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class AddNewLocatActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityAddNewLocatBinding

    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var savedImageUri: Uri
    private var latitude = 0.0
    private var longitude = 0.0
    lateinit var notesModel: LocatNotesModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewLocatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        if(!Places.isInitialized()) {
            Places.initialize(this@AddNewLocatActivity,
                resources.getString(R.string.google_maps_api_key))
        }

        binding.etLocation.setOnClickListener {
            try {

                val fields = listOf(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                )

                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                    .build(this)

                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        binding.etDate.setOnClickListener(this)

        binding.tvAddImg.setOnClickListener(this)

        var isGoodToSave = false

        binding.btnSave.setOnClickListener {

            if(binding.etTitle.text.isNullOrEmpty()) {
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                isGoodToSave = false
            }
            else if(binding.etDesc.text.isNullOrEmpty()) {
                Toast.makeText(this, "Description cannot be empty!", Toast.LENGTH_SHORT).show()
                isGoodToSave = false
            }
            else if(binding.etDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Date cannot be empty!", Toast.LENGTH_SHORT).show()
                isGoodToSave = false
            }
            else if(binding.etLocation.text.isNullOrEmpty()) {
                Toast.makeText(this, "Location cannot be empty!", Toast.LENGTH_SHORT).show()
                isGoodToSave = false
            }
            else if(savedImageUri.equals("") || savedImageUri.equals(null)) {
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                isGoodToSave = false
            } else {
                isGoodToSave = true
                val title = binding.etTitle.text.toString()
                val desc = binding.etDesc.text.toString()
                val savedUri = savedImageUri.toString()
                val location = binding.etLocation.text.toString()
                val date = binding.etDate.text.toString()

                if(isGoodToSave) {
                    notesModel = LocatNotesModel(
                        0,
                        title,
                        desc,
                        savedUri,
                        location,
                        date,
                        latitude,
                        longitude
                    )

                    val successLong = DatabaseHelper(this).addLocatNotes(notesModel)

                    if(successLong != -1L) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Could not save LocatNote!", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.etDate -> {
                DatePickerDialog(this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                updateDate()
            }
            R.id.tvAddImg -> {
                val picDialog = AlertDialog.Builder(this)
                picDialog.setTitle("Select Action")
                val picDialogItems = arrayOf("Select photo from Gallery","Take a picture from Camera")
                picDialog.setItems(picDialogItems) {
                    dialog, which ->
                    when(which) {
                        0 -> openGalleryForImage()
                        1 -> openCameraForImage()
                    }
                }
                picDialog.show()
            }
        }
    }

    private fun openCameraForImage() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.CAMERA,
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()) {
                    val intImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intImg, CAMERA_INTENT)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun openGalleryForImage() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()) {
                    val intImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intImg, GALLERY_INTENT)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("It looks like you have disabled the permissions, it can be enabled under Settings.")
            .setPositiveButton("Go to Settings") {
                _ , _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this,"Activity not found.",Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateDate() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val int = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(int,3)
        } else {
            Toast.makeText(this,"Permission has not been granted. Please go to settings and provide camera permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT) {
            val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
            binding.appCompatImageButton.setImageBitmap(thumbNail)
            savedImageUri = saveImageToGallery(thumbNail)
        }

        if(resultCode == Activity.RESULT_OK && requestCode == CAMERA_INTENT) {
            if(data != null) {
                val contentUri = data.data
                try {
                    val selectedImageBitmap = data.extras!!.get("data") as Bitmap
                    binding.appCompatImageButton.setImageBitmap(selectedImageBitmap)
                    savedImageUri = saveImageToGallery(selectedImageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this,e.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }


        }

        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.etLocation.setText(place.address)
            latitude = place.latLng!!.latitude
            longitude = place.latLng!!.longitude
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) : Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpeg")

        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            Toast.makeText(this,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }

        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY_INTENT = 69
        private const val CAMERA_INTENT = 99
        private const val IMAGE_DIRECTORY = "LocatNotes"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 7
    }
}