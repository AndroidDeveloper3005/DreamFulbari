package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

/**
 * Created by himel on 15/12/17.
 */

public class Users implements Serializable {

    private String name, image, phone,bloodgroup,donner_status,location;

    public Users() {
    }

    public Users(String name, String image, String phone, String bloodgroup, String donner_status, String location) {
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.bloodgroup = bloodgroup;
        this.donner_status = donner_status;
        this.location = location;
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
