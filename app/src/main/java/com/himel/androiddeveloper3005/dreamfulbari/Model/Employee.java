package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String organization;
    private String image;
    private String address;
    private String currentLoc;
    private String gender;
    private String bloodgroup;
    private String profession;

    public Employee() {
    }

    public Employee(String name, String email, String phone, String organization, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.image = image;
    }

    public Employee(String name, String email, String phone, String organization, String image, String address, String currentLoc, String gender, String bloodgroup, String profession) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.image = image;
        this.address = address;
        this.currentLoc = currentLoc;
        this.gender = gender;
        this.bloodgroup = bloodgroup;
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentLoc() {
        return currentLoc;
    }

    public void setCurrentLoc(String currentLoc) {
        this.currentLoc = currentLoc;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
