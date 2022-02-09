package com.sbitbd.alhelalattendance.Model;

import com.sbitbd.alhelalattendance.Adapter.attend_adapter;

public class attend_task_model {
    attend_adapter attend_adapter;
    String class_id,section_id;

    public attend_task_model(attend_adapter attend_adapter,
                             String class_id, String section_id) {
        this.attend_adapter = attend_adapter;
        this.class_id = class_id;
        this.section_id = section_id;
    }

    public com.sbitbd.alhelalattendance.Adapter.attend_adapter getAttend_adapter() {
        return attend_adapter;
    }

    public void setAttend_adapter(com.sbitbd.alhelalattendance.Adapter.attend_adapter attend_adapter) {
        this.attend_adapter = attend_adapter;
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
}
