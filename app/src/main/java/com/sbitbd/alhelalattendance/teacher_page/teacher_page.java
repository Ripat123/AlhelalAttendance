package com.sbitbd.alhelalattendance.teacher_page;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.sbitbd.alhelalattendance.Adapter.teacher_adpter;
import com.sbitbd.alhelalattendance.Adapter.teacher_atd_adapter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.present_view.present_view;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class teacher_page extends AppCompatActivity {

    private ImageView back;
    private TextView total,title_t,bottom_title,date_t;
//    private String id;
    private RecyclerView recyclerView;
    private teacher_adpter teacher_adpter;
    private ProgressDialog progressDialog;
    private config config = new config();
    private MaterialCardView view_date_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_page);
        initveiw();
    }

    private void initveiw(){
        try {
            back = findViewById(R.id.present_back);
            total = findViewById(R.id.total);
            title_t = findViewById(R.id.title_t);
            bottom_title = findViewById(R.id.bottom_title);
            recyclerView = findViewById(R.id.present_rec_atd);
            view_date_card = findViewById(R.id.view_date_card);
            date_t = findViewById(R.id.date_t);

            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

            materialDateBuilder.setTitleText("SELECT A DATE");
            materialDateBuilder.setTheme(R.style.RoundShapeCalenderTheme);

            final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
            teacher_adpter = new teacher_adpter(teacher_page.this, 1);

            view_date_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


                }
            });


            String check = getIntent().getStringExtra("check");
//            id = getIntent().getStringExtra("id");

            GridLayoutManager manager = new GridLayoutManager(teacher_page.this, 1);
            recyclerView.setLayoutManager(manager);

            if (check.equals("1")){
                //present teacher
                title_t.setText(R.string.present_teacher);
                bottom_title.setText(R.string.total_teacher);

                get_leave(teacher_page.this,"SELECT " +
                        "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                        "" + config.attend_date() + "' and attendance='1'",teacher_adpter);

                recyclerView.setAdapter(teacher_adpter);

            }
            else if (check.equals("2")){
                //absent teacher
                title_t.setText(R.string.absent_teacher);
                bottom_title.setText(R.string.total_teacher);
                get_leave(teacher_page.this,"SELECT " +
                        "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                        "" + config.attend_date() + "' and attendance='0'",teacher_adpter);

                recyclerView.setAdapter(teacher_adpter);

            }
            else if (check.equals("3")){
                //leave teacher
                title_t.setText(R.string.teacher_leave1);
                bottom_title.setText(R.string.total_teacher);
                get_leave(teacher_page.this,"SELECT " +
                        "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                        "" + config.attend_date() + "' and attendance='2'",teacher_adpter);

                recyclerView.setAdapter(teacher_adpter);

            }

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onPositiveButtonClick(Long selection) {
                    TimeZone timeZoneUTC = TimeZone.getDefault();
                    // It will be negative, so that's the -1
                    int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                    // Create a date format, then a date object with our offset
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date date = new Date(selection + offsetFromUTC);
                    String selected = simpleFormat.format(date);

//                    get_leave(teacher_page.this,"SELECT " +
//                            "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
//                            "" + selected + "' and attendance='1'",teacher_adpter);
//
//                    recyclerView.setAdapter(teacher_adpter);

                    date_t.setText(getResources().getString(R.string.view_by_date)+" ("+selected+")");
                    if (check.equals("1")){
                        //present teacher
                        title_t.setText(R.string.present_teacher);
                        bottom_title.setText(R.string.total_teacher);

                        get_leave(teacher_page.this,"SELECT " +
                                "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                                "" + selected + "' and attendance='1'",teacher_adpter);

                        recyclerView.setAdapter(teacher_adpter);

                    }
                    else if (check.equals("2")){
                        //absent teacher
                        title_t.setText(R.string.absent_teacher);
                        bottom_title.setText(R.string.total_teacher);
                        get_leave(teacher_page.this,"SELECT " +
                                "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                                "" + selected + "' and attendance='0'",teacher_adpter);

                        recyclerView.setAdapter(teacher_adpter);

                    }
                    else if (check.equals("3")){
                        //leave teacher
                        title_t.setText(R.string.teacher_leave1);
                        bottom_title.setText(R.string.total_teacher);
                        get_leave(teacher_page.this,"SELECT " +
                                "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                                "" + selected + "' and attendance='2'",teacher_adpter);

                        recyclerView.setAdapter(teacher_adpter);

                    }
                }
            });
        }catch (Exception e){
        }
    }

    private void get_leave(Context context, String sql, teacher_adpter teacher_atd_adapter) {
        try {
            progressDialog = ProgressDialog.show(teacher_page.this,"","Loading...",false,false);
            teacher_atd_adapter.Clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (!response.trim().equals("")) {
                                store_section(response.trim(),teacher_atd_adapter);
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
        recyclerView.setAdapter(teacher_atd_adapter);
    }

    private void store_section(String response,teacher_adpter teacher_atd_adapter) {
        String id = "";
        teacher_atd_adapter.Clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                try {
                    JSONObject collegeData = result.getJSONObject(i);
                    id = collegeData.getString(config.ONE);
                    initadd("SELECT * FROM teacher WHERE id = '" + id + "'",teacher_atd_adapter);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private void initadd(String sql, teacher_adpter teacher_atd_adapter) {
        attend_model attend_model;
        database sqliteDB = new database(teacher_page.this);
        try {
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            ,cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),"","");
                    teacher_atd_adapter.adduser(attend_model);
                }
            }
            recyclerView.setAdapter(teacher_atd_adapter);
            total.setText(Integer.toString(teacher_adpter.getItemCount()));
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }
}