package com.sbitbd.alhelalacademy.Config;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.sbitbd.alhelalacademy.Dashboard;
import com.sbitbd.alhelalacademy.Model.user_model;
import com.sbitbd.alhelalacademy.download.download;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class config {
    public static final String LOGIN = "https://alhelalacademy.edu.bd/android/login.php";
    public static final String ADMIN_VERIFY = "https://alhelalacademy.edu.bd/android/admin_verify.php";
    public static final String SEND_OTP = "https://alhelalacademy.edu.bd/android/send_otp.php";
    public static final String REG = "https://alhelalacademy.edu.bd/android/registration.php";
    public static final String GET_ID = "https://alhelalacademy.edu.bd/android/get_ID.php";
    public static final String INSERT = "https://alhelalacademy.edu.bd/android/insert.php";
    public static final String FORGET = "https://alhelalacademy.edu.bd/android/forget_password.php";
    public static final String RESET_PASS = "https://alhelalacademy.edu.bd/android/reset_pass.php";
    public static final String TWO_DIMENSION = "https://alhelalacademy.edu.bd/android/two_dms.php";
    public static final String FOUR_DIMENSION = "https://alhelalacademy.edu.bd/android/four_dms.php";
    public static final String FIVE_DIMENSION = "https://alhelalacademy.edu.bd/android/five_dms.php";
    public static final String SEVEN_DIMENSION = "https://alhelalacademy.edu.bd/android/seven_dms.php";
    public static final String STUDENT_URL = "https://alhelalacademy.edu.bd/android/student_data.php";
    public static final String ADD_ONLINE = "https://alhelalacademy.edu.bd/android/Add_Attendance.php";
    public static final String SEND_SMS = "https://alhelalacademy.edu.bd/android/send_sms.php";
    public static final String STUDENT_IMG = "https://alhelalacademy.edu.bd/other_img/";
    public static final String QUERY = "query";
    public static final String RESULT = "result";
    public static final String PASS = "pass";
    public static final String STUDENT_NAME = "student_name";
    public static final String ID = "id";
    public static final String PHONE = "phone";
    //    public static final String NAME = "name";
    public static final String ONE = "one";
    public static final String TWO = "two";
    public static final String THREE = "three";
    public static final String FOUR = "four";
    public static final String FIVE = "five";
    public static final String SIX = "six";
    public static final String SEVEN = "seven";
    public static final String FATHER = "father_name";
    public static final String MOTHER = "mother_name";
    public static final String CLASS_ID = "class_id";
    public static final String CLASS_ROLL = "class_roll";
    public static final String GROUP_ID = "group_id";
    public static final String SECTION_ID = "section_id";
    public static final String SMS = "sms";
//    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public void regularSnak(View fab, String msg) {
        Snackbar snackbar = Snackbar.make(fab, msg, Snackbar.LENGTH_LONG);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackbar.show();
    }

    public Boolean checkUser(Context context) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM user");
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    return true;
                }
            }
        } catch (Exception e) {
        } finally {

            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public Boolean checkData(Context context, String sql) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    return true;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static String channelName(String myUid, String peerUid) {
        return myUid + peerUid;
    }

    public user_model User_info(Context context) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM user");
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    return new user_model(cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("password")),
                            cursor.getString(cursor.getColumnIndexOrThrow("save")),
                            cursor.getString(cursor.getColumnIndexOrThrow("admin_status")),
                            cursor.getString(cursor.getColumnIndexOrThrow("teacher_id")));
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void updateUser(Context context, String guestID, String status, String phone, String password, String save) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("teacher_id", guestID);
//            contentValues.put("admin_status", status);
            contentValues.put("phone", phone);
            contentValues.put("password", password);
            contentValues.put("save", save);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "user", "id = '1'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void updateAdmin_status(Context context, String status) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("admin_status", status);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "user", "id = '1'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void insertuser(Context context, String status, String phone, String password, String guestID, String save) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "1");
            contentValues.put("teacher_id", guestID);
//            contentValues.put("admin_status", status);
            contentValues.put("phone", phone);
            contentValues.put("password", password);
            contentValues.put("save", save);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "user", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to create user", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void deleteuser(Context context) {
        database sqliteDB = new database(context);
        try {
            boolean check = sqliteDB.DataOperation(null, "delete", "user",
                    "id = '1'");
            if (!check) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_class(Context context, String id, String name) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("class_name", name);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "class", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to add class", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_section(Context context, String id, String name, String class_id, String group_id) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("section_name", name);
            contentValues.put("group_id", group_id);
            contentValues.put("class_id", class_id);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "section", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to add class", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_period(Context context, String id, String name, String class_id, String subject_name) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("class_id", class_id);
            contentValues.put("period_name", name);
            contentValues.put("subject_name", subject_name);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "period", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to add period", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_teacher(Context context, String id, String name, String phone) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("name", name);
            contentValues.put("phone", phone);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "teacher", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to add teacher", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_student(Context context, String id, String name, String phone, String class_id,
                            String section_id, String group_id, String class_roll, String father, String mother) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("student_name", name);
            contentValues.put("phone", phone);
            contentValues.put("roll", class_roll);
            contentValues.put("class_id", class_id);
            contentValues.put("section_id", section_id);
            contentValues.put("group_id", group_id);
            contentValues.put("father_name", father);
            contentValues.put("mother_name", mother);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "student", null);
            if (ch) {
            } else
                Toast.makeText(context, "Failed to add student", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public String getData(Context context, String sql, String column_name) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(column_name));
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void add_attend_temp(Context context, String id, String status, String comment) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("student_id", id);
            contentValues.put("attendance", status);
            contentValues.put("comment", comment);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "attendance_temp", null);
            if (!ch)
                Toast.makeText(context, "Failed to add temp", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void deletetemp(Context context) {
        database sqliteDB = new database(context);
        try {
            boolean check = sqliteDB.DataOperation(null, "delete", "attendance_temp",
                    null);
            if (!check) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void deleteteacher_temp(Context context) {
        database sqliteDB = new database(context);
        try {
            boolean check = sqliteDB.DataOperation(null, "delete", "teacher_attendance_temp",
                    null);
            if (!check) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_teacher_attend_temp(Context context, String id, String status, String comment, String start) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("teacher_id", id);
            contentValues.put("attendance", status);
            contentValues.put("comment", comment);
            contentValues.put("start_time", start);
            boolean ch = sqlite_db.DataOperation(contentValues, "insert", "teacher_attendance_temp", null);
            if (!ch)
                Toast.makeText(context, "Failed to add temp", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void all_delete(Context context, String table) {
        database sqliteDB = new database(context);
        try {
            boolean check = sqliteDB.DataOperation(null, "delete", table,
                    null);
            if (!check) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void add_attendance(Context context, String class_id, String section_id, String period_id) {
        database sqlite_db = new database(context);
        config config = new config();
        String time, date, month, year, attend_date;
        time = config.get_Time();
        date = config.get_Date();
        month = config.get_month();
        year = config.get_year();
        attend_date = config.attend_date();
        JSONObject jsonObject = new JSONObject();

        new Thread(() -> {
            try {
                user_model user_model = User_info(context);
                int i = 0;
                Cursor cursor = sqlite_db.getUerData("SELECT * FROM attendance_temp");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("time", time);
                        contentValues.put("date", date);
                        contentValues.put("month", month);
                        contentValues.put("year", year);
                        contentValues.put("attend_date", attend_date);
                        contentValues.put("class_id", class_id);
                        contentValues.put("section_id", section_id);
                        contentValues.put("period_id", period_id);
                        contentValues.put("student_id", cursor.getString(cursor.getColumnIndexOrThrow("student_id")));
                        contentValues.put("attendance", cursor.getString(cursor.getColumnIndexOrThrow("attendance")));
                        contentValues.put("comment", cursor.getString(cursor.getColumnIndexOrThrow("comment")));
                        contentValues.put("teacher_id", user_model.getId());
                        contentValues.put("upload_status", "0");
                        boolean ch = sqlite_db.DataOperation(contentValues, "insert", "attendance", null);
                        if (ch) {
                            jsonObject.put("" + i, "INSERT INTO attendance(time,date,month,year," +
                                    "attend_date,class_id,section_id,period_id,student_id,attendance," +
                                    "comment,teacher_id) VALUES('" + time + "','" + date + "','" + month + "'" +
                                    ",'" + year + "','" + attend_date + "','" + class_id + "','" + section_id + "','" + period_id + "'" +
                                    ",'" + cursor.getString(cursor.getColumnIndexOrThrow("student_id")) + "'," +
                                    "'" + cursor.getString(cursor.getColumnIndexOrThrow("attendance")) + "'," +
                                    "'" + cursor.getString(cursor.getColumnIndexOrThrow("comment")) + "'," +
                                    "'" + user_model.getId() + "')");
                            i++;
                        } else
                            Toast.makeText(context, "Failed to add attendance", Toast.LENGTH_SHORT).show();
                    }
                    if (isOnline(context))
                        add_json(jsonObject, context, attend_date, class_id, section_id, period_id);
                    else
                        Toast.makeText(context, "Attendance submitted in offline!", Toast.LENGTH_SHORT).show();
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

    public void add_teacher_attendance(Context context) {
        database sqlite_db = new database(context);
        config config = new config();
        String attend_date;
        attend_date = config.attend_date();
        JSONObject jsonObject = new JSONObject();

        try {
            user_model user_model = User_info(context);
            int i = 0;
            Cursor cursor = sqlite_db.getUerData("SELECT * FROM teacher_attendance_temp");
            if (cursor.getCount() > 0) {
                ContentValues contentValues = new ContentValues();
                while (cursor.moveToNext()) {


                    contentValues.put("attend_date", attend_date);
                    contentValues.put("teacher_id", cursor.getString(cursor.getColumnIndexOrThrow("teacher_id")));
                    contentValues.put("attendance", cursor.getString(cursor.getColumnIndexOrThrow("attendance")));
                    contentValues.put("comment", cursor.getString(cursor.getColumnIndexOrThrow("comment")));
                    contentValues.put("start_time", cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                    contentValues.put("admin_id", user_model.getId());
                    boolean ch = sqlite_db.DataOperation(contentValues, "insert", "teacher_attendance", null);
                    if (!ch)
                        Toast.makeText(context, "Failed to add attendance", Toast.LENGTH_SHORT).show();

                    jsonObject.put("" + i, "INSERT INTO teacher_attend(start_time,attend_date,teacher_id,attendance," +
                            "comment,admin_id) VALUES('" + cursor.getString(cursor.getColumnIndexOrThrow("start_time")) + "'," +
                            "'" + attend_date + "','" + cursor.getString(cursor.getColumnIndexOrThrow("teacher_id")) + "'," +
                            "'" + cursor.getString(cursor.getColumnIndexOrThrow("attendance")) + "'," +
                            "'" + cursor.getString(cursor.getColumnIndexOrThrow("comment")) + "'," +
                            "'" + user_model.getId() + "')");
                    i++;

                }
                if (isOnline(context))
                    teacherATD_check(context,jsonObject);
                else
                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
        }
    }

    private void add_json(JSONObject jsonObject, Context context, String attend_date, String class_id,
                          String section_id, String period_id) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADD_ONLINE,
                    response -> {
                        if (response.trim().equals("")) {
                            Toast.makeText(context, "Attendance Uploaded Successful!", Toast.LENGTH_LONG).show();
                            update_online_status(context, attend_date, class_id, section_id, period_id);
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

    private void addteacher_json(JSONObject jsonObject, Context context,ProgressDialog progressDialog) {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADD_ONLINE,
                    response -> {
                        progressDialog.dismiss();
                        if (response.trim().equals("")) {

                            Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, Dashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, response.trim(), Toast.LENGTH_LONG).show();
                        }
                    }, error -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }) {
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

//    private void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = context.getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }

    public void update_online_status(Context context, String attend_date, String class_id, String
            section_id, String period_id) {
        database sqlite_db = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("upload_status", "1");
            boolean ch = sqlite_db.DataOperation(contentValues, "update", "attendance"
                    , "attend_date = '" + attend_date + "' and class_id = '" + class_id + "' " +
                            "and section_id='" + section_id + "' and period_id = '" + period_id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
        } finally {
            try {
                sqlite_db.close();
            } catch (Exception e) {
            }
        }
    }

    public void update_status(Context context, String id, String status) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("attendance", status);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "attendance_temp"
                    , "student_id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void update_teacher_status(Context context, String id, String status) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("attendance", status);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "teacher_attendance_temp"
                    , "teacher_id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void Live_update_status(Context context, String id, String status) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("attendance", status);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "attendance"
                    , "id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void update_comment(Context context, String id, String comment) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("comment", comment);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "attendance_temp"
                    , "student_id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void update_teacher_comment(Context context, String id, String comment) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("comment", comment);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "teacher_attendance_temp"
                    , "teacher_id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void Live_update_comment(Context context, String id, String comment) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("comment", comment);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "attendance"
                    , "id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void Live_update_time(Context context, String id) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("time", get_Time());
            contentValues.put("date", get_Date());
            contentValues.put("month", get_month());
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "attendance"
                    , "id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public String get_Time() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public String get_Date() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public String get_month() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MM");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public String get_year() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public String attend_date() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void teacher_update_start(Context context, String id, String time) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("start_time", time);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "teacher_attendance_temp"
                    , "teacher_id = '" + id + "'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    public void teacher_update_end(Context context, String id, String time) {
        database sqliteDB = new database(context);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("end_time", time);
            boolean ch = sqliteDB.DataOperation(contentValues, "update", "teacher_attendance"
                    , "teacher_id = '" + id + "' and attend_date ='"+attend_date()+"'");
            if (!ch)
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    private void teacherATD_check(Context context,JSONObject jsonObject) {

        try {
            ProgressDialog progressDialog = ProgressDialog.show(context,"","Loading...",false,false);
            String sql = "SELECT * FROM teacher_attend where attend_date = '"+attend_date()+"'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    response -> {

                        if (response.trim().equals("")) {
                            addteacher_json(jsonObject,context,progressDialog);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Teacher Attendance Already Started!", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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

    public void teacherLiveUpdate(Context context,String sql,ProgressDialog progressDialog) {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.INSERT,
                    response -> {
                        progressDialog.dismiss();
                        if (response.trim().equals("")) {
                            Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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

    public void teacherGap_check(Context context,String sql,String id,ProgressDialog progressDialog) {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    response -> {

                        if (response.trim().equals("")) {
                            teacherLiveUpdate(context,"INSERT INTO teacher_gap_time (teacher_id," +
                                    "start_time,attend_date)VALUES('"+id+"',current_time,'"+attend_date()+"')",progressDialog);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Gap time Already Started!", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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

    public void teacher_admin_check(Context context,String sql) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADMIN_VERIFY,
                    response -> {
                        if (!response.trim().equals("")) {
                            updateAdmin_status(context,response.trim());
                            Intent intent =new Intent(context, download.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Admin Checking Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
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

}
