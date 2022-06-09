package com.sbitbd.alhelalattendance.download;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Dashboard;
import com.sbitbd.alhelalattendance.MainActivity;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.settings.settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class download extends AppCompatActivity {

    private config config = new config();
    private static int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        check();
    }

    private void check() {
        try {
            AllTask allTask = new AllTask();
            if (config.checkData(download.this, "SELECT * FROM class"))
                status =1;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("3", "SELECT id as 'one',class_name as 'two' FROM `add_class` " +
                            "ORDER BY id ASC", config.TWO_DIMENSION);
                else
                    dialog();
            }
            if (config.checkData(download.this, "SELECT * FROM section"))
                status=2;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("4", "SELECT id AS 'one',class_id AS 'two',group_id AS 'three'," +
                            "section_name AS 'four' from add_section ORDER BY id ASC", config.FOUR_DIMENSION);
                else
                    dialog();
            }
            if (config.checkData(download.this, "SELECT * FROM period"))
                status=3;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("5", "SELECT id AS 'one',class_id AS 'two',period_name AS 'three'," +
                            "subject_name AS 'four' from add_period ORDER BY id ASC", config.FOUR_DIMENSION);
                else
                    dialog();
            }
            if (config.checkData(download.this, "SELECT * FROM teacher"))
                status=4;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("2", "SELECT teachers_id AS 'one',teachers_name AS 'two',mobile_no AS 'three' " +
                            "from teachers_information order by index_no ASC", config.FOUR_DIMENSION);
                else
                    dialog();
            }
            if (config.checkData(download.this, "SELECT * FROM student"))
                status=5;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("1", "select student_personal_info.id,student_name,father_name,mother_name," +
                            "class_id,class_roll,group_id,section_id,student_guardian_information.guardian_contact as 'contact_no' " +
                            "from student_personal_info inner join running_student_info on " +
                            "student_personal_info.id = running_student_info.student_id inner join " +
                            "student_guardian_information on student_personal_info.id = " +
                            "student_guardian_information.id ORDER BY class_roll ASC", config.STUDENT_URL);
                else
                    dialog();
            }
            if (config.checkData(download.this, "SELECT * FROM teacher_priority"))
                status=6;
            else {
                if (config.isOnline(download.this))
                    allTask.doInBackground("6", "select user as 'one', class as 'two',`group` " +
                            "as 'three',section as 'four',subjectName as 'five' from subject_priority_att ", config.FIVE_DIMENSION);
                else
                    dialog();
            }
            // check status
            if (status == 6) {
                startActivity(new Intent(download.this, Dashboard.class));
                finish();
            }
        } catch (Exception e) {
        }
    }

    private void dialog() {
        try {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(download.this);
            dialogBuilder.setTitle("Internet Error!");
            dialogBuilder.setMessage("No Internet Connection!");

            dialogBuilder.setNegativeButton("cancel", (dialog, which) -> {
                System.exit(1);
                dialog.cancel();
            });
            dialogBuilder.setPositiveButton("retry", (dialog, which) -> {
                check();
            });
            dialogBuilder.show();
        } catch (Exception e) {
        }
    }

//    private void download_class() {
//        try {
//            String sql = "SELECT id as 'one',class_name as 'two' FROM `add_class` ORDER BY id ASC";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            store_class(response.trim());
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(download.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(config.QUERY, sql);
//                    return params;
//                }
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(download.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//        }
//    }
//
//
//
//    protected void download_section() {
//        try {
//            String sql = "SELECT id AS 'one',class_id AS 'two',group_id AS 'three',section_name AS 'four' from add_section ORDER BY id ASC";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.FOUR_DIMENSION,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            store_section(response.trim());
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(download.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(config.QUERY, sql);
//                    return params;
//                }
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(download.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//        }
//    }
//
//
//
//    protected void download_period() {
//        try {
//            String sql = "SELECT id AS 'one',class_id AS 'two',period_name AS 'three',subject_name AS 'four' from add_period ORDER BY id ASC";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.FOUR_DIMENSION,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            store_period(response.trim());
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(download.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(config.QUERY, sql);
//                    return params;
//                }
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(download.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//        }
//    }
//
//
//
//    protected void download_teacher() {
//        try {
//            String sql = "SELECT teachers_id AS 'one',teachers_name AS 'two',mobile_no AS 'three' " +
//                    "from teachers_information ORDER BY teachers_id ASC";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.FOUR_DIMENSION,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            store_teacher(response.trim());
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(download.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(config.QUERY, sql);
//                    return params;
//                }
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(download.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//        }
//    }
//
//
//
//    protected void download_student() {
//        try {
//            String sql = "select student_personal_info.id,student_name,father_name,mother_name," +
//                    "class_id,class_roll,group_id,section_id,student_guardian_information.guardian_contact as 'contact_no' " +
//                    "from student_personal_info inner join running_student_info on " +
//                    "student_personal_info.id = running_student_info.student_id inner join " +
//                    "student_guardian_information on student_personal_info.id = " +
//                    "student_guardian_information.id ORDER BY class_roll ASC";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.STUDENT_URL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            store_student(response.trim());
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(download.this, error.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(config.QUERY, sql);
//                    return params;
//                }
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(download.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//        }
//    }

    private class AllTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, strings[2],
                        response -> {
                            switch (strings[0]) {
                                case "1":
                                    store_student(response.trim());
                                    break;
                                case "2":
                                    store_teacher(response.trim());
                                    break;
                                case "3":
                                    store_class(response.trim());
                                    break;
                                case "4":
                                    store_section(response.trim());
                                    break;
                                case "5":
                                    store_period(response.trim());
                                    break;
                                default:
                                    store_teacher_priority(response.trim());

                            }
                        }, error -> {
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(download.this);
                            dialogBuilder.setTitle("Internet Error!");
                            dialogBuilder.setMessage(error.toString());
                            dialogBuilder.setCancelable(false);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.cancel();
                                cancel(true);
                            });
                            dialogBuilder.show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(config.QUERY, strings[1]);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(download.this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            } catch (Exception e) {
            }
            return null;
        }


        private void store_student(String response) {

            new Thread(() -> {
                try {
                    String id = "";
                    String name = "";
                    String phone = "";
                    String father_name = "";
                    String mother_name = "";
                    String class_id = "";
                    String section_id = "";
                    String group_id = "";
                    String roll = "";
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray(config.RESULT);
                    for (int i = 0; i <= result.length(); i++) {
                        try {
                            JSONObject collegeData = result.getJSONObject(i);
                            phone = collegeData.getString(config.PHONE);
                            id = collegeData.getString(config.ID);
                            name = collegeData.getString(config.STUDENT_NAME);
                            father_name = collegeData.getString(config.FATHER);
                            mother_name = collegeData.getString(config.MOTHER);
                            class_id = collegeData.getString(config.CLASS_ID);
                            section_id = collegeData.getString(config.SECTION_ID);
                            group_id = collegeData.getString(config.GROUP_ID);
                            roll = collegeData.getString(config.CLASS_ROLL);
                            config.add_student(download.this, id, name, phone, class_id, section_id, group_id,
                                    roll, father_name, mother_name);
                        } catch (Exception e) {
                        }
                    }
                    status++;
                    if (status == 6) {
                        startActivity(new Intent(download.this, Dashboard.class));
                        finish();
                    }
                } catch (Exception e) {
                }
            }).start();

        }

        private void store_teacher(String response) {
            String id = "";
            String name = "";
            String phone = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray result = jsonObject.getJSONArray(config.RESULT);
                for (int i = 0; i <= result.length(); i++) {
                    try {
                        JSONObject collegeData = result.getJSONObject(i);
                        phone = collegeData.getString(config.THREE);
                        id = collegeData.getString(config.ONE);
                        name = collegeData.getString(config.TWO);
                        config.add_teacher(download.this, id, name, phone);
                    } catch (Exception e) {
                    }
                }
                status++;
                if (status == 6) {
                    startActivity(new Intent(download.this, Dashboard.class));
                    finish();
                }
            } catch (Exception e) {
            }
        }

        private void store_class(String response) {
            String id = "";
            String name = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray result = jsonObject.getJSONArray(config.RESULT);
                for (int i = 0; i <= result.length(); i++) {
                    try {
                        JSONObject collegeData = result.getJSONObject(i);
                        name = collegeData.getString(config.TWO);
                        id = collegeData.getString(config.ONE);
                        config.add_class(download.this, id, name);
                    } catch (Exception e) {
                    }
                }

                status++;
                if (status == 6) {
                    startActivity(new Intent(download.this, Dashboard.class));
                    finish();
                }
            } catch (Exception e) {
            }
        }

        private void store_section(String response) {
            String id = "";
            String name = "";
            String class_id = "";
            String group_id = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray result = jsonObject.getJSONArray(config.RESULT);
                for (int i = 0; i <= result.length(); i++) {
                    try {
                        JSONObject collegeData = result.getJSONObject(i);
                        name = collegeData.getString(config.FOUR);
                        id = collegeData.getString(config.ONE);
                        class_id = collegeData.getString(config.TWO);
                        group_id = collegeData.getString(config.THREE);
                        config.add_section(download.this, id, name, class_id, group_id);
                    } catch (Exception e) {
                    }
                }

                status++;
                if (status == 6) {
                    startActivity(new Intent(download.this, Dashboard.class));
                    finish();
                }
            } catch (Exception e) {
            }
        }

        private void store_period(String response) {
            String id = "";
            String name = "";
            String class_id = "";
            String subject_name = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray result = jsonObject.getJSONArray(config.RESULT);
                for (int i = 0; i <= result.length(); i++) {
                    try {
                        JSONObject collegeData = result.getJSONObject(i);
                        name = collegeData.getString(config.THREE);
                        id = collegeData.getString(config.ONE);
                        class_id = collegeData.getString(config.TWO);
                        subject_name = collegeData.getString(config.FOUR);
                        config.add_period(download.this, id, name, class_id, subject_name);
                    } catch (Exception e) {
                    }
                }

                status++;
                if (status == 6) {
                    startActivity(new Intent(download.this, Dashboard.class));
                    finish();
                }
            } catch (Exception e) {
            }
        }

        private void store_teacher_priority(String response) {
            database sqlite_db = new database(download.this);
            new Thread(() -> {
                String user = "";
                String group = "";
                String class_id = "";
                String section = "";
                String subject = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray(config.RESULT);
                    for (int i = 0; i <= result.length(); i++) {
                        try {
                            JSONObject collegeData = result.getJSONObject(i);
                            user = collegeData.getString(config.ONE);
                            class_id = collegeData.getString(config.TWO);
                            group = collegeData.getString(config.THREE);
                            section = collegeData.getString(config.FOUR);
                            subject = collegeData.getString(config.FIVE);
//                                config.add_sub_reg(download.this, id, class_id,group,subject);

                            try {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("teacher_id", user);
                                contentValues.put("class_id", class_id);
                                contentValues.put("group_id", group);
                                contentValues.put("section_id", section);
                                contentValues.put("subject_name", subject);
                                boolean ch = sqlite_db.DataOperation(contentValues, "insert", "teacher_priority", null);
                                if (!ch) {
                                    Toast.makeText(download.this, "Failed to add teacher priority", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                            }

                        } catch (Exception e) {
                        }
                    }
                    status++;
                    if (status == 6) {
                        try {
                            startActivity(new Intent(download.this, Dashboard.class));
                            finish();
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                } finally {
                    try {
                        sqlite_db.close();
                    } catch (Exception e) {
                    }
                }
            }).start();

        }
    }
}