package com.example.kris.bearpad;

// This class builds the saved notes lists

public class NotesBuilder {
    //Hidden ID of Note.
    public int id;

    private String title,
            content;

    public NotesBuilder() {
    }

    public NotesBuilder(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle (String value) {
        this.title=value;
    }

    public void setContent (String value) {
        this.content=value;
    }

}