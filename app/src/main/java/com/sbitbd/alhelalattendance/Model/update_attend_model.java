package com.sbitbd.alhelalattendance.Model;

public class update_attend_model {
    String phone,password,status,id,save,class_id,section_id,period_id;

    public update_attend_model(String phone, String password, String save,String status,String id,
                               String class_id, String section_id, String period_id) {
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.id = id;
        this.save = save;
        this.class_id = class_id;
        this.section_id = section_id;
        this.period_id = period_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getPeriod_id() {
        return period_id;
    }

    public void setPeriod_id(String period_id) {
        this.period_id = period_id;
    }
}
