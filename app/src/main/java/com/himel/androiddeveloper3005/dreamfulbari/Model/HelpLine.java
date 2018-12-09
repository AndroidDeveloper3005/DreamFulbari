package com.himel.androiddeveloper3005.dreamfulbari.Model;

import java.io.Serializable;

public class HelpLine implements Serializable {
    private String ambulance;
    private String fireservice;
    private String chairman;
    private String uno;
    private String police;

    public HelpLine(String ambulance, String fireservice, String chairman, String uno, String police) {
        this.ambulance = ambulance;
        this.fireservice = fireservice;
        this.chairman = chairman;
        this.uno = uno;
        this.police = police;
    }

    public HelpLine(String fireservice, String chairman, String uno, String police) {
        this.fireservice = fireservice;
        this.chairman = chairman;
        this.uno = uno;
        this.police = police;
    }

    public String getAmbulance() {
        return ambulance;
    }

    public void setAmbulance(String ambulance) {
        this.ambulance = ambulance;
    }

    public String getFireservice() {
        return fireservice;
    }

    public void setFireservice(String fireservice) {
        this.fireservice = fireservice;
    }

    public String getChairman() {
        return chairman;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
    }

    public String getPolice() {
        return police;
    }

    public void setPolice(String police) {
        this.police = police;
    }
}
