package com.example.locatnotes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locatnotes.database.DatabaseHelper
import com.example.locatnotes.database.MainAdapter
import com.example.locatnotes.databinding.ActivityMainBinding
import com.example.locatnotes.models.LocatNotesModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var locatNotesList : ArrayList<LocatNotesModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        binding.fabAdd.setOnClickListener {
            startActivityForResult(Intent(this, AddNewLocatActivity::class.java), UPDATE_RV_REQUEST_CODE)
        }

        getListFromDatabase()

    }

    fun getListFromDatabase() {
        locatNotesList = DatabaseHelper(this).getLocatNotes()

        if(locatNotesList.size > 0) {
            binding.rvLocations.layoutManager = LinearLayoutManager(this)
            val mainAdapter = MainAdapter(this,locatNotesList)
            binding.rvLocations.setHasFixedSize(true)
            binding.rvLocations.adapter = mainAdapter

            mainAdapter.setOnClickListener(object: MainAdapter.OnClickListener {
                override fun onClick(position: Int, model: LocatNotesModel) {
                    val intent = Intent(this@MainActivity, LocatNoteDetailsActivity::class.java)
                    intent.putExtra("EXTRA_PLACE_DETAILS", model)
                    startActivity(intent)
                }
            })
        }
    }

    companion object {
        private const val UPDATE_RV_REQUEST_CODE = 11
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && requestCode == UPDATE_RV_REQUEST_CODE) {
            getListFromDatabase()
        }
    }
}