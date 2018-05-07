package com.example.kris.bearpad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
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
import java.util.ListIterator;
import java.util.Random;

import static android.content.ContentValues.TAG;

// This class is an Adapter the utilizes the recycleView
// The list is build using an Array

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    SharedPreferences sharedpreference;
    public List<NotesBuilder> notesList = new ArrayList<>();
    private TextView mTextView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView title;
        public TextView noteID;

        public MyViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            noteID = (TextView) view.findViewById(R.id.noteID);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                // Open File onClickListener
                public void onClick(View v) {
                    Context context = v.getContext();
                    Log.d(TAG, "You Clicked = " + title);
                    Toast.makeText(v.getContext()," You pressed " + noteID.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(v.getContext(), activity0.class);
                    // Use Intent to send noteID to activity0
                    intent.putExtra("clickedValue",noteID.getText().toString());
                    context.startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Ask for user to confirm deletion of note.
                        }
                    });

                Toast.makeText(v.getContext()," You pressed " + noteID.getText().toString(), Toast.LENGTH_LONG).show();
                int id = Integer.parseInt(noteID.getText().toString());
                ListIterator<NotesBuilder> iter = notesList.listIterator();
                while(iter.hasNext()){
                    if(iter.next().id == id){
                        iter.remove();
                    }
                }
                Save (v.getContext());
                notifyDataSetChanged();
                return false;
                }

                // This is modified open method because context was unavailable
                // The context can only be gather from the View instance
                // So the code has been changed to allow the context to be passed from the view instance
                public String Open(Context context) {
                    String content = "";
                    if (FileExists("noteList.txt", context)) {
                        try {
                            InputStream in = context.openFileInput("noteList.txt");
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
                            Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        try {
                            OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput("noteList.txt", 0));
                            List<NotesBuilder> toAdd = new ArrayList<>();
                            String stringToAdd = new Gson().toJson(toAdd);
                            out.write(stringToAdd);
                            out.close();
                            content = stringToAdd;
                        }
                        catch (Throwable t) {
                            Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    return content;
                }

                public boolean FileExists(String fname, Context context){
                    File file = context.getFileStreamPath(fname);
                    return file.exists();
                }

                public void Save(Context context) {
                    // Update current note!
                    String Current = Open(context);
                    List<NotesBuilder> toAdd = new ArrayList<>();
                    toAdd = new Gson().fromJson(Current, new TypeToken<List<NotesBuilder>>() {
                    }.getType());
                    toAdd = notesList;
                    try {
                        OutputStreamWriter out =
                                new OutputStreamWriter(context.openFileOutput("noteList.txt", 0));
                        String stringToAdd = new Gson().toJson(toAdd);
                        out.write(stringToAdd);
                        out.close();
                    } catch (Throwable t) {
                        Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    // Populate View Holder

    public NotesAdapter(List<NotesBuilder> notesList) {
        this.notesList = notesList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotesBuilder note = notesList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.noteID.setText(String.valueOf(note.id));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
    public List<NotesBuilder> getNotesList() {
        return notesList;
    }

    // Filter List for Search
    public void filterList(ArrayList<NotesBuilder> filteredList) {
        notesList = filteredList;
        notifyDataSetChanged();
    }
}

