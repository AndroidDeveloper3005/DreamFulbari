package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class User  implements Serializable {
    private String id,name, image,phone,bloodgroup,donner_status,location,status;

    public User(String id, String name, String image, String phone, String bloodgroup, String donner_status, String location, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.bloodgroup = bloodgroup;
        this.donner_status = donner_status;
        this.location = location;
        this.status = status;
    }

    public User(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getDonner_status() {
        return donner_status;
    }

    public void setDonner_status(String donner_status) {
        this.donner_status = donner_status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
