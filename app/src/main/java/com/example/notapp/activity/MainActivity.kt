package com.example.notapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notapp.R
import com.example.notapp.adapter.NoteAdapter
import com.example.notapp.database.NoteDatabase
import com.example.notapp.database.SharedPref
import com.example.notapp.databinding.ActivityMainBinding
import com.example.notapp.model.Note

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDatabase: NoteDatabase
    private lateinit var sharedPref: SharedPref
    private var noteList: MutableList<Note> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.notes)

        sharedPref = SharedPref(this)
        noteDatabase = NoteDatabase(this)
        noteAdapter = NoteAdapter()
        noteList = noteDatabase.getAllNotes()
        setupRv()
        binding.fab.setOnClickListener {
            fabClick(binding.root)
        }

        noteAdapter.onItemClicked = {
            val bundle = bundleOf("note" to it)
            val intent = Intent(this,SecondActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        noteAdapter.onItemLongClicked = {note, pos ->
            AlertDialog.Builder(this).apply {
                setTitle("Delete note")
                setMessage("Do you want to delete ${note.title}?")
                setPositiveButton("Yes"){di,_ ->
                    di.dismiss()
                    noteDatabase.deleteNote(note.id!!)
                    noteAdapter.notifyItemRemoved(pos)
                    noteList.removeAt(pos)
                }
                setNeutralButton("Cancel",null)
            }.create().show()
        }
        binding.appCompatEditText.addTextChangedListener {
            val list:MutableList<Note> = mutableListOf()
            for (note in noteList){
                if (note.title!!.lowercase()
                        .contains(it.toString().lowercase()) || note.description!!.lowercase()
                        .contains(it.toString().lowercase())){
                    list.add(note)
                }
            }
            noteAdapter.filterNotes(list)
        }
    }
    private fun setupRv() = binding.recyclerView.apply {
        noteAdapter.noteList = noteList
        layoutManager = if (sharedPref.getManager()) LinearLayoutManager(this@MainActivity)
        else StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        adapter = noteAdapter
        binding.empty.isVisible = noteAdapter.noteList.isEmpty()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.linear -> {
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                sharedPref.saveManager(true)
            }
            R.id.stagger -> {
                binding.recyclerView.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                sharedPref.saveManager(false)
            }
            R.id.deleteAll -> {
                if (noteList.isNotEmpty()) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Delete notes")
                        setMessage("Are you sure do you want to delete all notes?")
                        setPositiveButton("Yes") { di, _ ->
                            di.dismiss()
                            noteList.clear()
                            noteDatabase.deleteAllNotes()
                            setupRv()
                        }
                        setNeutralButton("Cancel", null)
                    }.create().show()
                }
            }
        }
        return true
    }
    fun fabClick(view: View){
        startActivity(Intent(this,SecondActivity::class.java))
    }
}