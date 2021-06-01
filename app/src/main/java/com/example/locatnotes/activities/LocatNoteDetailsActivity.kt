package com.example.locatnotes.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.locatnotes.R
import com.example.locatnotes.databinding.ActivityLocatNoteDetailsBinding
import com.example.locatnotes.models.LocatNotesModel

class LocatNoteDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityLocatNoteDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocatNoteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var locatNotesModel : LocatNotesModel? = null

        if(intent.hasExtra("EXTRA_PLACE_DETAILS")) {
            locatNotesModel = intent.getParcelableExtra("EXTRA_PLACE_DETAILS") as LocatNotesModel?
        }

        if(locatNotesModel != null) {
            setSupportActionBar(binding.toolbarPlaceDetails)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = locatNotesModel.title

            binding.toolbarPlaceDetails.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.ivPlaceImage.setImageURI(Uri.parse(locatNotesModel.imageUri))
            binding.tvDescription.text = locatNotesModel.description
            binding.tvLocation.text = locatNotesModel.location
        }
    }
}