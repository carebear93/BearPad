package com.example.kris.bearpad;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class activity0 extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MainActivity";
    private int id=1;

    EditText EditText1;
    EditText Title;
    ImageView imageView;

    private boolean newNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // If the user opens up a saved note
        // This catches the intent from NotesAdapter
        // Catches the string!!

        Title = (EditText) findViewById(R.id.Title);
        EditText1 = (EditText) findViewById(R.id.EditText1);
        imageView = (ImageView)findViewById(R.id.imageView);

        // Button to save Note
        // Saves file as .txt as Note1
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
                Toast.makeText(activity0.this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO Camera Bug FIX!
        // Camera Button
        // Calls camera through intent
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (Throwable ex){
                        // Error occurred while creating the File
                        Log.i(TAG, "IOException Failed to Create File");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        try {//                        Uri photoURI = FileProvider.getUriForFile(getApplication().getApplicationContext(),"com.example.android.fileprovider", photoFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile.getAbsolutePath());
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (Throwable ex)
                        {
                            String error = ex.toString();}
                    }
                }
            }
        });

        // TODO Gallery Bug FIX!
        // Gallery Button in the bottom action bar
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("clickedValue")){
            final String clickedValue = intent.getStringExtra("clickedValue").toString();
            if (clickedValue != null) {
                // Load note from phone and display title and text
                newNote = false;
                String currentNote = Open();
                String Current =  Open();
                List<NotesBuilder> toAdd = new ArrayList<>();
                toAdd = new Gson().fromJson(Current, new TypeToken<List <NotesBuilder>>(){}.getType());
                // Loop through notes to find the right ID.
                // Could have used lambda but not available on lowest API version.
                NotesBuilder note = new NotesBuilder();
                for (NotesBuilder value : toAdd) {
                    if (value.id == Integer.parseInt(clickedValue)){
                        note = value;
                        id=Integer.parseInt(clickedValue);
                    }
                }
                Title.setText(note.getTitle(), TextView.BufferType.EDITABLE);
                EditText1.setText(note.getContent(), TextView.BufferType.EDITABLE);
            }
        }
        else {
            newNote = true;
        }




//        EditText1.setText(Open("Note1.txt"));
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Intent intent = getIntent();
//        String clickedValue = intent.getStringExtra("clickedValue").toString();
//    }

    private File createImageFile() {
        File image;
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getFilesDir();
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
            return image;
        } catch (Throwable e) {
            String error = e.toString();
        }
        return new File("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case  REQUEST_IMAGE_CAPTURE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Cannot run application because camera service permission have not been granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity0, menu);
        return true;
    }


    //******************************************************************************************************************************
    // Save Notes Function
    /*
        Opens the current noteList as string
        Turns the string into the list of noteBuilders
        Make a new noteBuilder from their user
        Add this to current list
        TURN TO JSON!!
    */
    public void Save() {
        // Saving a new note
      if (newNote){
          // Generate random ID Number
          int min = 1;
          int max = 1000000;
          Random r = new Random();
          int i1 = r.nextInt(max - min + 1) + min;

          String Current =  Open();
          List<NotesBuilder> toAdd = new ArrayList<>();
          toAdd = new Gson().fromJson(Current, new TypeToken<List <NotesBuilder>>(){}.getType());
          NotesBuilder plank = new NotesBuilder(Title.getText().toString(),EditText1.getText().toString());
          plank.id=i1;
          toAdd.add(plank);
          try {
              OutputStreamWriter out =
                      new OutputStreamWriter(openFileOutput("noteList.txt", 0));
              String stringToAdd = new Gson().toJson(toAdd);
              out.write(stringToAdd);
              out.close();
          }
          catch (Throwable t) {
              Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
          }
      }
      // Update current note!
      else {
          String Current =  Open();
          List<NotesBuilder> toAdd = new ArrayList<>();
          toAdd = new Gson().fromJson(Current, new TypeToken<List <NotesBuilder>>(){}.getType());
          for (NotesBuilder value : toAdd) {
              if (value.id == id ){
                  value.setTitle(Title.getText().toString());
                  value.setContent(EditText1.getText().toString());
              }
          }
          try {
              OutputStreamWriter out =
                      new OutputStreamWriter(openFileOutput("noteList.txt", 0));
              String stringToAdd = new Gson().toJson(toAdd);
              out.write(stringToAdd);
              out.close();
          }
          catch (Throwable t) {
              Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
          }
      }
    }
//****************************************************************************************************************************



    //****************************************************************************************************************************
    // Open Method
    /*
        This checks if notesList exists on the phone.
        If it exists it will return as string.
        Else it will create the list.
    */
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
//****************************************************************************************************************************




    public boolean FileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    // Gallery & Camera button result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If Camera button pressed (fab2)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Find why image does not load
            }
        }

//        // If Gallery button is pressed (fab3)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                // Log.d(TAG, String.valueOf(bitmap));
//
//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    //TODO Add Speech to Text
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notes) {
            Intent myIntent = new Intent(activity0.this, NoteSelect.class);
            activity0.this.startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
