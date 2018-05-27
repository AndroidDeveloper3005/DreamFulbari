package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class BlogPost implements Serializable {


    private String title;
    private String description;
    private String imageUri;
    private String username;
    private String userImage;
    private String date;
    private String time;

    public BlogPost() {
    }


    public BlogPost(String title, String description, String imageUri, String username, String userImage, String date, String time) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.username = username;
        this.userImage = userImage;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
