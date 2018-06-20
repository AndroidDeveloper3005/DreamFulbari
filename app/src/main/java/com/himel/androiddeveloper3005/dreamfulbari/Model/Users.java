package com.himel.androiddeveloper3005.dreamfulbari.Model;

/**
 * Created by AkshayeJH on 15/12/17.
 */

public class Users {

    public String name, image, phone,bloodGroup,status;

    public Users(){

    }

    public Users(String name, String image, String phone, String bloodGroup, String status) {
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.status = status;
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

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
