package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String user_name;
    private String user_image;
    private String comment;

    public Comment(String user_name, String user_image, String comment) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.comment = comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
