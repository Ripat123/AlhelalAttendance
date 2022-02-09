package com.sbitbd.alhelalattendance.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbitbd.alhelalattendance.Adapter.absent_adapter;
import com.sbitbd.alhelalattendance.Adapter.attend_adapter;
import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Adapter.offline_adapter;
import com.sbitbd.alhelalattendance.Adapter.present_adapter;
import com.sbitbd.alhelalattendance.Adapter.teacher_adpter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Dashboard;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.Model.update_attend_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.download.download;
import com.sbitbd.alhelalattendance.teacher_page.teacher_page;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private class_model class_model;
    private attend_model attend_model;
    private config config = new config();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void get_class(Context context, class_adapter class_adapter) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM class where id in (select class_id " +
                    "  from teacher_priority where teacher_id = '"+config.User_info(context).getId()+"')");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new class_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
                    class_adapter.adduser(class_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_teacher(Context context, teacher_adpter class_adapter,String text) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM teacher where name LIKE '%"+text+"%'");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),"","");
                    class_adapter.adduser(attend_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_student(Context context, teacher_adpter class_adapter,String id,String section,String text) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM student where class_id = '"+id+"' and section_id = '"+section+"' " +
                    "and (student.roll LIKE '%"+text+"%' OR student.student_name LIKE '%"+text+"%') ORDER BY roll ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"1",
                            cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                    class_adapter.adduser(attend_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_present_student(Context context, present_adapter class_adapter, String class_id,
                                    String period, String date, String section,String text) {
        database sqliteDB = new database(context);
        try {
            String sql = "select student.id,student.roll,student.student_name " +
                    "from attendance inner join student on attendance.student_id = " +
                    "student.id where attendance.class_id = '"+class_id+"' " +
                    "and attendance.period_id = '"+period+"' and attendance.section_id = '"+section+"' and " +
                    "attendance.attend_date = '"+date+"' and attendance.attendance = '1' and (student.roll " +
                    " LIKE '%"+text+"%' OR student.student_name LIKE '%"+text+"%')";
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"1",
                            cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                    class_adapter.adduser(attend_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_leave_student(Context context, present_adapter class_adapter, String class_id,
                                    String period, String date, String section,String text) {
        database sqliteDB = new database(context);
        try {
            String sql = "select student.id,student.roll,student.student_name " +
                    "from attendance inner join student on attendance.student_id = " +
                    "student.id where attendance.class_id = '"+class_id+"' " +
                    "and attendance.period_id = '"+period+"' and attendance.section_id = '"+section+"' and " +
                    "attendance.attend_date = '"+date+"' and attendance.attendance = '2' and (student.roll " +
                    " LIKE '%"+text+"%' OR student.student_name LIKE '%"+text+"%')";
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"1",
                            cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                    class_adapter.adduser(attend_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_absent_student(Context context, absent_adapter class_adapter, String class_id,
                                   String period, String date, String section,String text) {
        database sqliteDB = new database(context);
        try {
            String sql = "select student.id,student.phone,student.roll,student.student_name " +
                    "from attendance inner join student on attendance.student_id = " +
                    "student.id where attendance.class_id = '"+class_id+"' " +
                    "and attendance.period_id = '"+period+"' and attendance.section_id = '"+section+"' and " +
                    "attendance.attend_date = '"+date+"' and attendance.attendance = '0' and (student.roll " +
                    "LIKE '%"+text+"%' OR student.student_name LIKE '%"+text+"%')";
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                    class_adapter.adduser(attend_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_attend_student(Context context, attend_adapter class_adapter, String class_id,
                                   String section_id, TextView textView) {
        database sqliteDB = new database(context);
        try {
            deleteTemp(context);
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM student where class_id = " +
                    "'"+class_id+"' and section_id = '"+section_id+"' ORDER BY roll ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"1",
                            cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                    class_adapter.adduser(attend_model);
                    addTemp(context,attend_model);
                }
            }
            textView.setText(Integer.toString(class_adapter.getItemCount()));
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void addTemp(Context context,attend_model attend_model){
        try {
            config.add_attend_temp(context,attend_model.getId(),attend_model.getAttend_status(),"");
        }catch (Exception e){
        }
    }
    public void deleteTemp(Context context){
        try {
            config.deletetemp(context);
        }catch (Exception e){
        }
    }
    public void deleteteacher_Temp(Context context){
        try {
            config.deleteteacher_temp(context);
        }catch (Exception e){
        }
    }
    public void addteacher_Temp(Context context,attend_model attend_model){
        try {
            config.add_teacher_attend_temp(context,attend_model.getId(),attend_model.getAttend_status(),"",config.get_Time());
        }catch (Exception e){
        }
    }

    public void get_offline_attend(Context context, offline_adapter class_adapter) {
        database sqliteDB = new database(context);
        update_attend_model user_model;
        try {
            deleteTemp(context);
            Cursor cursor = sqliteDB.getUerData("select attendance.attend_date,attendance.time," +
                    "attendance.class_id,attendance.section_id,attendance.period_id," +
                    "class.class_name,section.section_name,period.period_name from attendance inner " +
                    "join class on attendance.class_id = class.id inner join section on " +
                    "attendance.section_id = section.id inner join period on attendance.period_id = " +
                    "period.id where upload_status = '0' group by attend_date," +
                    "class_name,section_name,period_name");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    user_model = new update_attend_model(cursor.getString(cursor.getColumnIndexOrThrow("section_name"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("attend_date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("period_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("section_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("period_id")));
                    class_adapter.adduser(user_model);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void get_student_from_online(Context context,String sql,present_adapter class_adapter,absent_adapter
            absent_adapter, ProgressDialog progressDialog,int check,TextView total) {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (!response.trim().equals("")) {
                                set_fun(response.trim(),context,class_adapter,absent_adapter,check,total);
                            }else
                                Toast.makeText(context, "Attendance Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void set_fun(String response,Context context,present_adapter present_adapter,absent_adapter
            absent_adapter,int check,TextView total) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                try {
                    JSONObject collegeData = result.getJSONObject(i);
                    get_student_data(context,present_adapter,absent_adapter,collegeData
                            .getString(config.ONE),check,total);

                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private void get_student_data(Context context, present_adapter class_adapter,absent_adapter
            absent_adapter,String id, int check,TextView total) {
        database sqliteDB = new database(context);
        try {
            String sql = "select student.roll,student.student_name from student where student.id = '"+id+"'";
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    attend_model = new attend_model(id
                            ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"1",
                            id+".jpg");
                    if (check == 0) {
                        absent_adapter.adduser(attend_model);
                        total.setText(Integer.toString(absent_adapter.getItemCount()));
                    }
                    else {
                        class_adapter.adduser(attend_model);
                        total.setText(Integer.toString(class_adapter.getItemCount()));
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }
}