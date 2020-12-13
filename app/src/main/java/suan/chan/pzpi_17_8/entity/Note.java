package suan.chan.pzpi_17_8.entity;

import android.graphics.Bitmap;

import java.util.Date;

public class Note {
    private Long id;
    private String title;
    private String description;
    private String text;
    private PriorityType priority;
    private Date editTime;
    private Bitmap image;

    public Note(){
        id = Long.valueOf(0);
        title = "";
        description = "";
        text = "";
        priority = PriorityType.Third;
        editTime = new Date(System.currentTimeMillis());
    }

    public Note(Long id, String title, String description, PriorityType priority, Date editTime){

        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.editTime = editTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PriorityType getPriority() {
        return priority;
    }

    public void setPriority(PriorityType priority) {
        this.priority = priority;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
