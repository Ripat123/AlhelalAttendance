package com.sbitbd.alhelalacademy.ui.update_attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalacademy.Adapter.attend_adapter;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Config.database;
import com.sbitbd.alhelalacademy.Model.attend_model;
import com.sbitbd.alhelalacademy.Model.class_model;
import com.sbitbd.alhelalacademy.Model.four_model;
import com.sbitbd.alhelalacademy.Model.user_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.update_view.update_view;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class update_attendance extends AppCompatActivity {

    private ImageView imageView;
    private AutoCompleteTextView class_s, section, period, roll_s;
    private config config = new config();
    private List<class_model> class_list = new ArrayList<>();
    private List<String> class_name = new ArrayList<>();
    private List<four_model> section_list = new ArrayList<>();
    private List<String> section_name = new ArrayList<>();
    private List<four_model> period_list = new ArrayList<>();
    private List<String> period_name = new ArrayList<>();
    private List<four_model> roll_list = new ArrayList<>();
    private List<String> roll_name = new ArrayList<>();
    private String class_id, section_id, period_id, student_id,attendance_id, time="",attendance=""
            ,comment="",off_attend_id;
    private Button go,multi_btn;
    private ProgressDialog loadingProgressBar;
    private four_model student_info;
    private attend_adapter attend_adapter;
    private attend_model attend_model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initview();
    }

    private void initview() {
        try {
            imageView = findViewById(R.id.imageView17);
            class_s = findViewById(R.id.class_s);
            section = findViewById(R.id.section_s);
            period = findViewById(R.id.period_s);
            roll_s = findViewById(R.id.roll_s);
            go = findViewById(R.id.go_btn);
            multi_btn = findViewById(R.id.multi_btn);

            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (class_s.getText().toString().equals("")) {
                        Toast.makeText(update_attendance.this, "Please Select a Class", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (section.getText().toString().equals("")) {
                        Toast.makeText(update_attendance.this, "Please Select a Section", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (period.getText().toString().equals("")) {
                        Toast.makeText(update_attendance.this, "Please Select a Period", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (roll_s.getText().toString().equals("")) {
                        Toast.makeText(update_attendance.this, "Please Select a Roll", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Intent intent = new Intent(update_attendance.this, present_view.class);
//                    intent.putExtra("class_id", class_id);
//                    intent.putExtra("section_id", section_id);
//                    intent.putExtra("period_id", period_id);
//                    intent.putExtra("check","6");
//                    startActivity(intent);
                    getData();
                }
            });

            multi_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(update_attendance.this, update_view.class);
                    intent.putExtra("class_id",class_id);
                    intent.putExtra("section_id",section_id);
                    intent.putExtra("period_id",period_id);
                    startActivity(intent);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });

            get_class(update_attendance.this);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(update_attendance.this,
                    R.layout.item_name, class_name);
            class_s.setAdapter(dataAdapter);
            class_s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    class_model class_model = class_list.get(position);
                    class_id = class_model.getId();
                    get_section(update_attendance.this, class_model.getId());
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(update_attendance.this,
                            R.layout.item_name, section_name);
                    section.setText("");
                    section.setAdapter(dataAdapter1);
                    section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            four_model four_model = section_list.get(position);
                            section_id = four_model.getOne();
                            get_period(update_attendance.this, four_model.getOne());
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(update_attendance.this,
                                    R.layout.item_name, period_name);
                            period.setText("");
                            period.setAdapter(dataAdapter2);
                            period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model1 = period_list.get(position);
                                    period_id = four_model1.getOne();
                                }
                            });

                            get_roll(update_attendance.this, class_id, section_id);
                            ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(update_attendance.this,
                                    R.layout.item_name, roll_name);
                            roll_s.setText("");
                            roll_s.setAdapter(dataAdapter3);
                            roll_s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    student_info = getPosition(roll_s.getText().toString().trim());
                                    if (student_info != null) {
                                        student_id = student_info.getOne();
                                    }else {
                                        roll_s.setText("");
                                        student_id = "";
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } catch (Exception e) {
        }
    }

    private four_model getPosition(String pro_model){
        try {
            for (four_model x:roll_list){
                if(x.getThree().equals(pro_model)){
                    return x;
                }
            }
        }catch (Exception e){
        }
        return null;
    }

    private void get_class(Context context) {
        database sqliteDB = new database(context);
        class_model class_model;
        try {
            class_list.clear();
            class_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM class where id in (select class_id " +
                    "  from teacher_priority where teacher_id = '"+config.User_info(context).getId()+"')");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new class_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
                    class_list.add(class_model);
                    class_name.add(cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
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

    private void get_section(Context context, String id) {
        database sqliteDB = new database(context);
        four_model class_model;
        try {
            section_list.clear();
            section_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM section where class_id = '" + class_id + "' " +
                    " and id in (select section_id from teacher_priority where " +
                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_id+"')");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new four_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("section_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("group_id")));
                    section_list.add(class_model);
                    section_name.add(cursor.getString(cursor.getColumnIndexOrThrow("section_name")));
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

    private void get_roll(Context context, String class_id, String section_id) {
        database sqliteDB = new database(context);
        four_model class_model;
        try {
            section_list.clear();
            section_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM student where class_id = '" + class_id + "' " +
                    "and section_id = '" + section_id + "'");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new four_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("student_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("roll")), "");
                    roll_list.add(class_model);
                    roll_name.add(cursor.getString(cursor.getColumnIndexOrThrow("roll")));
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

    private void get_period(Context context, String id) {
        database sqliteDB = new database(context);
        four_model class_model;
        try {
            period_list.clear();
            period_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM period where class_id = '"+class_id+"'" +
                    " and id in (select subject_name from teacher_priority " +
                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_id+"')");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new four_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("period_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("subject_name")));
                    period_list.add(class_model);
                    period_name.add(cursor.getString(cursor.getColumnIndexOrThrow("period_name")));
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

    private void getData() {
        try {
            loadingProgressBar = ProgressDialog.show(update_attendance.this, "", "Loading...", false, false);
            user_model user_model = config.User_info(update_attendance.this);
            String sql = "select attendance.id as 'one',attendance.attendance as 'two' from attendance " +
                    " where attendance.attend_date = '" + config.attend_date() + "' and attendance.class_id " +
                    " = '" + class_id + "' and attendance.section_id = '" + section_id + "'" +
                    " and attendance.period_id = '" + period_id + "' and attendance.teacher_id = '" + user_model.getId() + "' and attendance.student_id = '" + student_id + "'";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingProgressBar.dismiss();
                            if (response != null && !response.trim().equals("") && !response.trim().equals("{\"result\":[]}")) {
                                initDialog(response);
                            } else {
                                Toast.makeText(update_attendance.this, "Not found!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(present_view.this, "Failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(update_attendance.this, error.toString(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(update_attendance.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void initDialog(String response) {
        attend_adapter = new attend_adapter(update_attendance.this, 1);

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            JSONObject collegeData = result.getJSONObject(0);
            View view1 = LayoutInflater.from(update_attendance.this).inflate(R.layout.recycle, null);
            RecyclerView recyclerView = view1.findViewById(R.id.rec_lay);
            GridLayoutManager manager = new GridLayoutManager(update_attendance.this, 1);
            recyclerView.setLayoutManager(manager);

            String sql1 = "select * from attendance where attendance.attend_date = '" + config.attend_date() + "' and attendance.class_id " +
                    "  = '" + class_id + "' and attendance.section_id = '" + section_id + "'" +
                    " and attendance.period_id = '" + period_id + "' and attendance.student_id = '" + student_id + "'";
            database sqliteDB = new database(update_attendance.this);
            Cursor cursor = sqliteDB.getUerData(sql1);

            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
//                    time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
//                    attendance = cursor.getString(cursor.getColumnIndexOrThrow("attendance"));
//                    comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                    off_attend_id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                }
            }

            attendance_id = collegeData.getString(config.ONE);
            attend_model = new attend_model(student_id
                    , roll_s.getText().toString().trim(),
                    student_info.getTwo(),
                    collegeData.getString(config.TWO),
                    off_attend_id);
            attend_adapter.adduser(attend_model);
            recyclerView.setAdapter(attend_adapter);

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(update_attendance.this);
            dialogBuilder.setTitle("Update");
            dialogBuilder.setIcon(R.drawable.ic_baseline_update_24);
            dialogBuilder.setView(view1);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                update_attend(attendance_id);
            });
            dialogBuilder.show();
        } catch (Exception e) {
        }
    }

    private void update_attend(String attend_id) {

        try {
            loadingProgressBar = ProgressDialog.show(update_attendance.this, "", "Loading...", false, false);
            String sql1 = "select * from attendance where attendance.attend_date = '" + config.attend_date() + "' and attendance.class_id " +
                    "  = '" + class_id + "' and attendance.section_id = '" + section_id + "'" +
                    " and attendance.period_id = '" + period_id + "' and attendance.student_id = '" + student_id + "'";
            database sqliteDB = new database(update_attendance.this);
            Cursor cursor = sqliteDB.getUerData(sql1);

            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
//                    time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                    attendance = cursor.getString(cursor.getColumnIndexOrThrow("attendance"));
                    comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                    off_attend_id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                }
            }
            time = config.get_Time();
            String sql = "UPDATE attendance SET time='"+time+"',attendance='"+attendance+"',comment='"+comment+"' " +
                    " where attendance.id = '" + attend_id + "'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.INSERT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingProgressBar.dismiss();

                            if (!response.trim().equals("")) {
                                Toast.makeText(update_attendance.this, "failed!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(update_attendance.this, "Updated!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(present_view.this, "Failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(update_attendance.this, error.toString(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(update_attendance.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

}