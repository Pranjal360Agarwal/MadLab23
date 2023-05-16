package com.example.keepnotes.model;

public class NoteModel {

    private String title, note_text, date_time, web_link, image_path, color;
    private Long id;

    public NoteModel(Long id, String title, String note_text, String date_time, String web_link, String image_path, String color) {
        this.id = id;
        this.title = title;
        this.note_text = note_text;
        this.date_time = date_time;
        this.web_link = web_link;
        this.image_path = image_path;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getWeb_link() {
        return web_link;
    }

    public void setWeb_link(String web_link) {
        this.web_link = web_link;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
