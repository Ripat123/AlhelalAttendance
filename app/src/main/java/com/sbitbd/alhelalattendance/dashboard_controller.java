package com.sbitbd.alhelalattendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbitbd.alhelalattendance.Adapter.offline_adapter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.five_model;
import com.sbitbd.alhelalattendance.Model.update_attend_model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dashboard_controller {
    private List<five_model> offline_date_list = new ArrayList<>();
    private Context context;
    private int count=0,i=0;

    protected void get_offline_attend(Context context) {
        database sqliteDB = new database(context);
        this.context= context;
        five_model user_model;
        try {
            Cursor cursor = sqliteDB.getUerData("select attendance.attend_date,attendance.time," +
                    "attendance.class_id,attendance.section_id,attendance.period_id from attendance " +
                    " where upload_status = '0' group by attend_date," +
                    "class_id,section_id,period_id");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    user_model = new five_model(
                            cursor.getString(cursor.getColumnIndexOrThrow("attend_date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("section_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("period_id")));
                    offline_date_list.add(user_model);
                }
            }
            count = offline_date_list.size();
            if (count > 0) {
                AllTask allTask = new AllTask();
                allTask.execute(offline_date_list.get(i).getOne(), offline_date_list.get(i).getTwo(), offline_date_list.get(i).getThree(),
                        offline_date_list.get(i).getFour(), offline_date_list.get(i).getFive());
                i++;
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    private class AllTask extends AsyncTask<String,String, JSONObject> {

        JSONObject jsonObject = new JSONObject();
        String date,time,class_id,section_id,period_id;

        @Override
        protected JSONObject doInBackground(String... strings) {
            database sqliteDB = new database(context);
            this.date = strings[0];
            this.time = strings[1];
            this.class_id = strings[2];
            this.section_id = strings[3];
            this.period_id = strings[4];
            try {
                int i=0;
                Cursor cursor = sqliteDB.getUerData("SELECT * FROM attendance where attend_date=" +
                        "'"+strings[0]+"' and class_id='"+strings[2]+"' and section_id='"+strings[3]+"'" +
                        " and period_id='"+strings[4]+"' and upload_status = '0'");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()){
                        jsonObject.put("" + i, "INSERT INTO attendance(time,date,month,year," +
                                "attend_date,class_id,section_id,period_id,student_id,attendance," +
                                "comment,teacher_id) VALUES('" + cursor.getString(cursor.getColumnIndexOrThrow("time")) + "','" + cursor.getString(cursor.
                                getColumnIndexOrThrow("date")) + "','" + cursor.getString(cursor.
                                getColumnIndexOrThrow("month")) + "'" +
                                ",'" + cursor.getString(cursor.getColumnIndexOrThrow("year")) + "','" + strings[0] + "'" +
                                ",'" + strings[2] + "','" + strings[3] + "','" + strings[4] + "'" +
                                ",'" + cursor.getString(cursor.getColumnIndexOrThrow("student_id")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("attendance")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("comment")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("teacher_id")) + "')");
                        i++;
                    }
                }
                else {
                    Toast.makeText(context,"Not found!",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null){
                Toast.makeText(context,"Empty data!",Toast.LENGTH_SHORT).show();
            }else
                add_json(jsonObject,date,class_id,section_id,period_id);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context,"Uploading...",Toast.LENGTH_SHORT).show();
        }

        private void add_json(JSONObject jsonObject, String attend_date, String class_id,
                              String section_id, String period_id) {
            config config =new config();
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADD_ONLINE,
                        response -> {

                            if (response.trim().equals("")) {
                                Toast.makeText(context, "Attendance Uploaded Successful!", Toast.LENGTH_LONG).show();
                                config.update_online_status(context, attend_date, class_id, section_id, period_id);
                                if (count >= i) {
                                    try {
                                        AllTask allTask = new AllTask();
                                        allTask.execute(offline_date_list.get(i).getOne(), offline_date_list.get(i).getTwo(), offline_date_list.get(i).getThree(),
                                                offline_date_list.get(i).getFour(), offline_date_list.get(i).getFive());
                                        i++;
                                    }catch (Exception e){
                                    }
                                }
                            } else {
                                Toast.makeText(context, response.trim(), Toast.LENGTH_LONG).show();
                            }
                        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(config.QUERY, jsonObject.toString());
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
    }

}
