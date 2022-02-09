package com.sbitbd.alhelalattendance.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class database extends SQLiteOpenHelper {
    SQLiteDatabase DB = this.getWritableDatabase();
    QueryProcess query = new QueryProcess();
    private final static String DBNAME = "attendanceDB";
    private final static int VERSION = 1;
    private final static String user = "create Table user(id VARCHAR,teacher_id INTEGER,admin_status VARCHAR," +
            "phone VARCHAR,password VARCHAR,save INTEGER)";
    private final static String user_upgrade = "drop Table if exists user";
    private final static String teacher = "create Table teacher(id INTEGER,name VARCHAR," +
            "phone VARCHAR)";
    private final static String teacher_upgrade = "drop Table if exists teacher";
    private final static String all_class = "create Table class(id INTEGER,class_name VARCHAR)";
    private final static String all_class_upgrade = "drop Table if exists class";
    private final static String section = "create Table section(id INTEGER,section_name VARCHAR,class_id VARCHAR,group_id VARCHAR)";
    private final static String section_upgrade = "drop Table if exists section";
    private final static String period = "create Table period(id INTEGER,class_id VARCHAR,period_name VARCHAR,subject_name VARCHAR)";
    private final static String period_upgrade = "drop Table if exists period";
    private final static String group = "create Table all_group(id INTEGER,group_name VARCHAR,class_id VARCHAR)";
    private final static String group_upgrade = "drop Table if exists all_group";
    private final static String student = "create Table student(id BIGINT,student_name VARCHAR," +
            "roll INTEGER,phone VARCHAR,image VARCHAR,class_id VARCHAR,section_id VARCHAR,group_id VARCHAR," +
            "father_name VARCHAR,mother_name VARCHAR)";
    private final static String student_upgrade = "drop Table if exists student";
    private final static String attendance = "create Table attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,time VARCHAR,date INTEGER," +
            "month INTEGER,year INTEGER,attend_date DATE,class_id INTEGER,section_id INTEGER,period_id INTEGER,student_id BIGINT" +
            ",attendance INTEGER,comment VARCHAR,teacher_id INTEGER,upload_status INTEGER)";
    private final static String attendance_upgrade = "drop Table if exists attendance";
    private final static String attendance_temp = "create Table attendance_temp(student_id BIGINT,attendance INTEGER,comment VARCHAR)";
    private final static String attendance_temp_upgrade = "drop Table if exists attendance_temp";
    private final static String teacher_attendance_temp = "create Table teacher_attendance_temp(teacher_id BIGINT," +
            "attendance INTEGER,comment VARCHAR,start_time VARCHAR)";
    private final static String teacher_attendance_temp_upgrade = "drop Table if exists teacher_attendance_temp";
    private final static String teacher_attendance = "create Table teacher_attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,start_time VARCHAR,end_time VARCHAR," +
            "attend_date DATE,teacher_id BIGINT" +
            ",attendance INTEGER,comment VARCHAR,admin_id INTEGER,upload_status INTEGER)";
    private final static String teacher_attendance_upgrade = "drop Table if exists teacher_attendance";
    private final static String teacher_priority = "create Table teacher_priority(id INTEGER,class_id " +
            "VARCHAR,section_id VARCHAR,group_id VARCHAR,teacher_id VARCHAR,subject_name VARCHAR,subject_part VARCHAR)";
    private final static String teacher_priority_upgrade = "drop Table if exists teacher_priority";

    public database(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(user);
            db.execSQL(teacher);
            db.execSQL(all_class);
            db.execSQL(section);
            db.execSQL(period);
            db.execSQL(student);
            db.execSQL(group);
            db.execSQL(attendance);
            db.execSQL(attendance_temp);
            db.execSQL(teacher_attendance_temp);
            db.execSQL(teacher_attendance);
            db.execSQL(teacher_priority);
        }catch (Exception e){
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(user_upgrade);
        db.execSQL(teacher_upgrade);
        db.execSQL(all_class_upgrade);
        db.execSQL(section_upgrade);
        db.execSQL(period_upgrade);
        db.execSQL(student_upgrade);
        db.execSQL(group_upgrade);
        db.execSQL(attendance_upgrade);
        db.execSQL(attendance_temp_upgrade);
        db.execSQL(teacher_attendance_temp_upgrade);
        db.execSQL(teacher_attendance_upgrade);
        db.execSQL(teacher_priority_upgrade);
        onCreate(db);
    }

    public Boolean DataOperation(ContentValues contentValues, String choose, String table, String where){
        if(choose.equals("insert")){
            return query.insert(DB,contentValues,table);
        }else if(choose.equals("update")){
            return query.update(DB,contentValues,table,where);
        }else if(choose.equals("delete")){
            return query.delete(DB,table,where);
        }else
            return false;
    }
    public Cursor getUerData(String sql){
        return query.getData(DB,sql);
    }
}
