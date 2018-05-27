package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String username;
    private String userimage;
    private String comment;
    private String date;
    private String time;

    public Comment() {

    }

    public Comment(String username, String userimage, String comment, String date, String time) {
        this.username = username;
        this.userimage = userimage;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
