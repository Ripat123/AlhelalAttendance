package com.sbitbd.alhelalattendance.teacher_attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalattendance.Adapter.attend_adapter;
import com.sbitbd.alhelalattendance.Adapter.teacher_attend_adapter;
import com.sbitbd.alhelalattendance.Adapter.teacher_leave_adapter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.attend_task_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.attend_form.attend;
import com.sbitbd.alhelalattendance.settings.settings;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class teacher_attend extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    //    attend_model attend_model;
    teacher_attend_adapter attend_adapter;
    private teacher_leave_adapter teacher_leave_adapter;
    TextView total;
    private int status;
    private List<attend_model> attend_models = new ArrayList<>();

    private com.sbitbd.alhelalattendance.Config.config config = new config();
    private MaterialButton submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attend);
        initview();
    }

    private void initview() {
        try {
            back = findViewById(R.id.attend_back);
            recyclerView = findViewById(R.id.tcr_rec);
            total = findViewById(R.id.total_st);
            submit = findViewById(R.id.submit);
            status = getIntent().getIntExtra("status", 1);


            back.setOnClickListener(v -> {
                onBackPressed();
                finish();
            });
            if (status == 1) {
                // teacher start attend
                GridLayoutManager manager = new GridLayoutManager(teacher_attend.this, 1);
                recyclerView.setLayoutManager(manager);
                attend_adapter = new teacher_attend_adapter(teacher_attend.this, 0, status);
                Attend_Start attend_start = new Attend_Start();
                attend_start.execute();
            } else if (status == 2) {
                GridLayoutManager manager = new GridLayoutManager(teacher_attend.this, 1);
                recyclerView.setLayoutManager(manager);
                teacher_leave_adapter = new teacher_leave_adapter(teacher_attend.this);
                get_leave(teacher_attend.this, config.attend_date());
            }
            submit.setOnClickListener(v -> {
                if (config.isOnline(teacher_attend.this)) {
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(teacher_attend.this);
                    dialogBuilder.setTitle("Teacher Attendance");
                    dialogBuilder.setMessage("Are you sure, you want to submit all Attendance?");
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
                    dialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                        submit();
                    });
                    dialogBuilder.show();
                } else
                    config.regularSnak(v, "You must connect with internet.");
            });
        } catch (Exception e) {
        }
    }

    private void submit() {
        if (status == 1)
            config.add_teacher_attendance(teacher_attend.this);
        else if (status == 2)
            configLeave();
    }

    private void updateteacher_json(JSONObject jsonObject, Context context) {
        ProgressDialog progressDialog = ProgressDialog.show(context, "", "Loading...", false, false);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADD_ONLINE,
                    response -> {
                        progressDialog.dismiss();
                        if (response.trim().equals("")) {
                            Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();
                            finish();
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

    private void get_leave(Context context, String attend_date) {
        try {
            String sql = "SELECT teacher_id AS 'one' from teacher_attend WHERE attend_date = " +
                    "'" + attend_date + "' and attendance = '1'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    response -> {
                        if (!response.trim().equals("")) {
                            store_section(response.trim());
                        } else {
                            Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show();
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
        recyclerView.setAdapter(teacher_leave_adapter);
    }

    private void store_section(String response) {
        String id = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                try {
                    JSONObject collegeData = result.getJSONObject(i);
                    id = collegeData.getString(config.ONE);
                    initadd("SELECT * FROM teacher WHERE id = '" + id + "'");
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
        total.setText(Integer.toString(teacher_leave_adapter.getItemCount()));
    }

    private void initadd(String sql) {
        attend_model attend_model;
        database sqliteDB = new database(teacher_attend.this);
        try {
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")), config.get_Time(),
                            cursor.getString(cursor.getColumnIndexOrThrow("id")) + ".jpg");
                    teacher_leave_adapter.adduser(attend_model);

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

    private void configLeave() {
        JSONObject jsonObject = new JSONObject();
        try {
            attend_models = teacher_leave_adapter.getList();
            for (int i = 0; i < attend_models.size(); i++) {
                jsonObject.put("" + i, "UPDATE teacher_attend SET end_time = '" + attend_models
                        .get(i).getAttend_status() + "' WHERE teacher_id='" + attend_models.get(i).getId() + "'" +
                        " AND attend_date = '" + config.attend_date() + "'");
            }
            if (config.isOnline(teacher_attend.this)) {
                updateteacher_json(jsonObject, teacher_attend.this);
            } else
                Toast.makeText(teacher_attend.this, "No Internet", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    private class Attend_Start extends AsyncTask<attend_task_model, String, teacher_attend_adapter> {
        ProgressDialog progressDialog;
        HomeViewModel homeViewModel = new HomeViewModel();
        attend_model attend_model;

        @Override
        protected teacher_attend_adapter doInBackground(attend_task_model... attend_task_models) {
//            homeViewModel.get_attend_student(attend.this,attend_adapter,class_id,section_id,total);

            database sqliteDB = new database(teacher_attend.this);
            try {
                homeViewModel.deleteteacher_Temp(teacher_attend.this);
                Cursor cursor = sqliteDB.getUerData("SELECT * FROM teacher");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                                , cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("phone")), "0",
                                cursor.getString(cursor.getColumnIndexOrThrow("id")) + ".jpg");
                        attend_adapter.adduser(attend_model);
                        homeViewModel.addteacher_Temp(teacher_attend.this, attend_model);

                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
            return attend_adapter;
        }

        @Override
        protected void onPostExecute(teacher_attend_adapter attend_adapter) {
            progressDialog.dismiss();
            recyclerView.setAdapter(attend_adapter);
            total.setText(Integer.toString(attend_adapter.getItemCount()));
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(teacher_attend.this, "", "Loading...", false, false);
        }
    }
}