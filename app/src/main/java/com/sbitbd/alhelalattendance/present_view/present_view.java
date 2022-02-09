package com.sbitbd.alhelalattendance.present_view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.sbitbd.alhelalattendance.Adapter.absent_adapter;
import com.sbitbd.alhelalattendance.Adapter.attend_adapter;
import com.sbitbd.alhelalattendance.Adapter.present_adapter;
import com.sbitbd.alhelalattendance.Adapter.teacher_adpter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.MainActivity;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.attend_task_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.attend_form.attend;
import com.sbitbd.alhelalattendance.teacher_page.teacher_page;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class present_view extends AppCompatActivity {

    private RecyclerView recyclerView;
    private present_adapter present_adapter;
    private attend_model attend_model;
    private ImageView back, refresh;
    private absent_adapter absent_adapter;
    private teacher_adpter teacher_adpter;
    private attend_adapter attend_adapter;
    private TextView total, title_t, bottom_title;
    private HomeViewModel homeViewModel = new HomeViewModel();
    private String id, class_id, section_id, period_id, date;
    private config config = new config();
    private ProgressDialog loadingProgressBar;
    private EditText search;
    private Button date_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_view);
        initview();
    }

    private void initview() {
        try {
            back = findViewById(R.id.present_back);
            refresh = findViewById(R.id.refresh);
            total = findViewById(R.id.total);
            title_t = findViewById(R.id.title_t);
            bottom_title = findViewById(R.id.bottom_title);
            search = findViewById(R.id.search);
            date_btn = findViewById(R.id.date_btn);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });

            String check = getIntent().getStringExtra("check");
            id = getIntent().getStringExtra("id");
            class_id = getIntent().getStringExtra("class_id");
            section_id = getIntent().getStringExtra("section_id");
            period_id = getIntent().getStringExtra("period_id");
            date = config.attend_date();

            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

            materialDateBuilder.setTitleText("SELECT A DATE");
            materialDateBuilder.setTheme(R.style.RoundShapeCalenderTheme);

            final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
            date_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                }
            });

            recyclerView = findViewById(R.id.present_rec);
            GridLayoutManager manager = new GridLayoutManager(present_view.this, 1);
            recyclerView.setLayoutManager(manager);
            if (check != null && check.equals("1")) {
                //absent student
                title_t.setText(R.string.absent_student);
                absent_adapter = new absent_adapter(present_view.this);
                homeViewModel.get_absent_student(present_view.this, absent_adapter, class_id,
                        period_id, date, section_id,"");

                recyclerView.setAdapter(absent_adapter);
                total.setText(Integer.toString(absent_adapter.getItemCount()));

            } else if (check != null && check.equals("2")) {
                //all teacher
                date_btn.setVisibility(View.GONE);
                title_t.setText(R.string.view_all_teacher);
                bottom_title.setText(R.string.total_teacher);
                refresh.setVisibility(View.VISIBLE);
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                teacher_adpter = new teacher_adpter(present_view.this, 1);
                homeViewModel.get_teacher(present_view.this, teacher_adpter,"");

                recyclerView.setAdapter(teacher_adpter);
                total.setText(Integer.toString(teacher_adpter.getItemCount()));
            } else if (check != null && check.equals("3")) {
                //all student
                date_btn.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                title_t.setText(R.string.view_all_students);
//                refresh.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
                teacher_adpter = new teacher_adpter(present_view.this, 0);
                homeViewModel.get_student(present_view.this, teacher_adpter, id,section_id,"");

                recyclerView.setAdapter(teacher_adpter);
                total.setText(Integer.toString(teacher_adpter.getItemCount()));
            } else if (check != null && check.equals("4")) {
                //leave student
                title_t.setText(R.string.student_leave);
                present_adapter = new present_adapter(present_view.this);
                homeViewModel.get_leave_student(present_view.this, present_adapter, class_id,
                        period_id, date, section_id,"");
                recyclerView.setAdapter(present_adapter);
                total.setText(Integer.toString(present_adapter.getItemCount()));

            } else if (check != null && check.equals("5")) {
                //update attendance
                date_btn.setVisibility(View.GONE);
                title_t.setText(R.string.attendance);
                bottom_title.setText(R.string.total_student);
                refresh.setVisibility(View.GONE);

                Attend_Start attend_start = new Attend_Start();
                attend_start.execute(new attend_task_model(attend_adapter, class_id, ""));

            }
//            else if (check != null && check.equals("6")) {
//                //update online attendance
//                title_t.setText(R.string.attendance);
//                bottom_title.setText(R.string.total_student);
//                refresh.setVisibility(View.GONE);
//                getData("");
////                Attend_Start attend_start = new Attend_Start();
////                attend_start.execute(new attend_task_model(attend_adapter,class_id,id));
//
//            }
            else {
                //present student
                present_adapter = new present_adapter(present_view.this);
                homeViewModel.get_present_student(present_view.this, present_adapter, class_id,
                        period_id, date, section_id,"");
                recyclerView.setAdapter(present_adapter);
                total.setText(Integer.toString(present_adapter.getItemCount()));
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
                    Date date1 = new Date(selection + offsetFromUTC);
                    date = simpleFormat.format(date1);
                    search.setEnabled(false);
                    total.setText("0");
                    date_btn.setText(getResources().getString(R.string.view_by_date)+" ("+date+")");
                    if (check != null && check.equals("1")) {
                        //absent student
                        loadingProgressBar = ProgressDialog.show(present_view.this,"","Loading...",false,false);
                        title_t.setText(R.string.absent_student);
                        absent_adapter = new absent_adapter(present_view.this);
                        homeViewModel.get_student_from_online(present_view.this,"select attendance.student_id" +
                                " as 'one' from attendance where attendance.class_id = '"+class_id+"'" +
                                "    and attendance.period_id = '"+period_id+"' and attendance.section_id = '"+section_id+"' and" +
                                "    attendance.attend_date = '"+date+"' and attendance.attendance = '0'",
                                present_adapter,absent_adapter,loadingProgressBar,0,total);

                        recyclerView.setAdapter(absent_adapter);

                    } else if (check != null && check.equals("4")) {
                        //leave student
                        loadingProgressBar = ProgressDialog.show(present_view.this,"","Loading...",false,false);
                        title_t.setText(R.string.student_leave);
                        present_adapter = new present_adapter(present_view.this);
                        homeViewModel.get_student_from_online(present_view.this,"select attendance.student_id" +
                                        " as 'one' from attendance where attendance.class_id = '"+class_id+"'" +
                                        "    and attendance.period_id = '"+period_id+"' and attendance.section_id = '"+section_id+"' and" +
                                        "    attendance.attend_date = '"+date+"' and attendance.attendance = '2'",
                                present_adapter,absent_adapter,loadingProgressBar,1,total);
                        recyclerView.setAdapter(present_adapter);

                    }
                    else {
                        //present student
                        loadingProgressBar = ProgressDialog.show(present_view.this,"","Loading...",false,false);
                        present_adapter = new present_adapter(present_view.this);
                        homeViewModel.get_student_from_online(present_view.this,"select attendance.student_id" +
                                        " as 'one' from attendance where attendance.class_id = '"+class_id+"'" +
                                        "    and attendance.period_id = '"+period_id+"' and attendance.section_id = '"+section_id+"' and" +
                                        "    attendance.attend_date = '"+date+"' and attendance.attendance = '1'",
                                present_adapter,absent_adapter,loadingProgressBar,1,total);
                        recyclerView.setAdapter(present_adapter);
                    }
                }
            });

            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        View view = getCurrentFocus();
                        hideKeyboardFrom(present_view.this, view);
                        search(search.getText().toString().trim(),check);
                        return true;
                    }
                    return false;
                }
            });

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    search(search.getText().toString().trim(),check);
                }
            };
            search.addTextChangedListener(textWatcher);
        } catch (Exception e) {
        }
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void search(String text,String check) {
        try {
            if (check != null && check.equals("1")) {
                //absent student
                absent_adapter.Clear();
                homeViewModel.get_absent_student(present_view.this, absent_adapter, class_id,
                        period_id, date, section_id,text);

                recyclerView.setAdapter(absent_adapter);

            } else if (check != null && check.equals("2")) {
                //all teacher
                teacher_adpter.Clear();
                homeViewModel.get_teacher(present_view.this, teacher_adpter,text);

                recyclerView.setAdapter(teacher_adpter);
            } else if (check != null && check.equals("3")) {
                //all student
                teacher_adpter.Clear();
                homeViewModel.get_student(present_view.this, teacher_adpter, id,section_id,text);

                recyclerView.setAdapter(teacher_adpter);
            } else if (check != null && check.equals("4")) {
                //leave student
                present_adapter.Clear();
                homeViewModel.get_leave_student(present_view.this, present_adapter, class_id,
                        period_id, date, section_id,text);
                recyclerView.setAdapter(present_adapter);

            } else if (check != null && check.equals("5")) {
                //update attendance

                Attend_Start attend_start = new Attend_Start();
                attend_start.execute(new attend_task_model(attend_adapter, class_id, text));

            }
//            else if (check != null && check.equals("6")) {
//                //update online attendance
//                getData(text);
////                Attend_Start attend_start = new Attend_Start();
////                attend_start.execute(new attend_task_model(attend_adapter,class_id,id));
//
//            }
            else {
                //present student
                present_adapter.Clear();
                homeViewModel.get_present_student(present_view.this, present_adapter, class_id,
                        period_id, date, section_id,text);
                recyclerView.setAdapter(present_adapter);
            }
        } catch (Exception e) {
        }
    }



    private class Attend_Start extends AsyncTask<attend_task_model, String, attend_adapter> {
        ProgressDialog progressDialog;
        attend_model attend_model;
        attend_adapter attend_adapter = new attend_adapter(present_view.this, 1);

        @Override
        protected attend_adapter doInBackground(attend_task_model... attend_task_models) {
//            homeViewModel.get_attend_student(attend.this,attend_adapter,class_id,section_id,total);

            database sqliteDB = new database(present_view.this);

            try {
                String sql = "select attendance.id as 'aid',student.id,student.roll,student.student_name," +
                        "attendance.attendance from attendance inner join student on attendance.student_id = student.id " +
                        "where attendance.attend_date = '" + id + "' and attendance.class_id = '" + class_id + "' and attendance.section_id = '" + section_id + "'" +
                        " and attendance.period_id = '" + period_id + "' and attendance.upload_status = '0' and (student.roll " +
                        " LIKE '%"+attend_task_models[0].getSection_id()+"%' OR student.student_name LIKE '%"+attend_task_models[0].getSection_id()+"%') ORDER BY student.roll ASC";

                Cursor cursor = sqliteDB.getUerData(sql);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                                , cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                                cursor.getString(cursor.getColumnIndexOrThrow("student_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("attendance")),
                                cursor.getString(cursor.getColumnIndexOrThrow("aid")));
                        attend_adapter.adduser(attend_model);

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
        protected void onPostExecute(attend_adapter attend_adapter) {
            try {
                progressDialog.dismiss();
                recyclerView.setAdapter(attend_adapter);
                total.setText(Integer.toString(attend_adapter.getItemCount()));
            } catch (Exception e) {
            }

        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(present_view.this, "", "Loading...", false, false);
        }
    }
}