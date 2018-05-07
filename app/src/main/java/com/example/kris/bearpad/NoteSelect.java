package com.example.kris.bearpad;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class NoteSelect extends AppCompatActivity {
    private AdapterView.OnItemClickListener mListener;

    private List<NotesBuilder> notesList = new ArrayList<>();
    private NotesAdapter nAdapter;
    private RecyclerView notesRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareNotes();
        setContentView(R.layout.activity_note_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toast.makeText(NoteSelect.this, "Tap to open Note, Long Tap to delete note.", Toast.LENGTH_LONG).show();

        // Search Bar is actually just Edit Text widget
        EditText search = findViewById(R.id.searchView);

        // Listener for search filter
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Button to open new Note
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteSelect.this, activity0.class));
            }
        });

        // Link to recyclerView
        notesRecycler = (RecyclerView) findViewById(R.id.notes);

        // Load Note list
        nAdapter = new NotesAdapter(notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notesRecycler.setLayoutManager(mLayoutManager);
        notesRecycler.setItemAnimator(new DefaultItemAnimator());
        notesRecycler.setAdapter(nAdapter);
    }

    // Filter for Search View
    private void filter(String text) {
        ArrayList<NotesBuilder> filteredList = new ArrayList<>();

        for (NotesBuilder item : notesList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        nAdapter.filterList(filteredList);
    }

    // When activity is revisited updates the recycle view
    @Override
    public void onRestart() {
        super.onRestart();
        prepareNotes();

        notesRecycler = (RecyclerView) findViewById(R.id.notes);

        // Load Note list
        nAdapter = new NotesAdapter(notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notesRecycler.setLayoutManager(mLayoutManager);
        notesRecycler.setItemAnimator(new DefaultItemAnimator());
        notesRecycler.setAdapter(nAdapter);
    }
    private void prepareNotes () {
        notesList.clear();
        String Current = Open();
        List<NotesBuilder> toAdd = new ArrayList<>();
        toAdd = new Gson().fromJson(Current, new TypeToken<List<NotesBuilder>>() {
        }.getType());
        notesList.addAll(toAdd);
    }

    // Open File by String Name
    public String Open() {
        String content = "";
        if (FileExists("noteList.txt")) {
            try {
                InputStream in = openFileInput("noteList.txt");
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else {
            try {
                OutputStreamWriter out =
                        new OutputStreamWriter(openFileOutput("noteList.txt", 0));
                List<NotesBuilder> toAdd = new ArrayList<>();
                String stringToAdd = new Gson().toJson(toAdd);
                out.write(stringToAdd);
                out.close();
                content = stringToAdd;
            }
            catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }

    public boolean FileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
//TODO Add Search

}