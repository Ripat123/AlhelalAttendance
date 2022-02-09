package com.sbitbd.alhelalattendance.Model;

public class attend_model {
    String id,roll,name,attend_status,image;

    public attend_model(String id, String roll, String name, String attend_status,String image) {
        this.id = id;
        this.roll = roll;
        this.name = name;
        this.attend_status = attend_status;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttend_status() {
        return attend_status;
    }

    public void setAttend_status(String attend_status) {
        this.attend_status = attend_status;
    }
}
