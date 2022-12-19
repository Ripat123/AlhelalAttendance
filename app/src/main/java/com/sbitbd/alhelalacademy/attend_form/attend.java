package com.sbitbd.alhelalacademy.attend_form;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalacademy.Adapter.attend_adapter;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Config.database;
import com.sbitbd.alhelalacademy.Dashboard;
import com.sbitbd.alhelalacademy.Model.attend_model;
import com.sbitbd.alhelalacademy.Model.attend_task_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.ui.home.HomeViewModel;

public class attend extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
//    attend_model attend_model;
    attend_adapter attend_adapter;
    TextView total;

    private config config = new config();
    private MaterialButton submit;

    private String class_id,section_id,period_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        initview();

    }
    private void initview(){
        try {
            back = findViewById(R.id.attend_back);
            recyclerView = findViewById(R.id.std_rec);
            total = findViewById(R.id.total_st);
            submit = findViewById(R.id.submit);

            class_id = getIntent().getStringExtra("class_id");
            section_id = getIntent().getStringExtra("section_id");
            period_id = getIntent().getStringExtra("period_id");

            back.setOnClickListener(v -> {
                onBackPressed();
                finish();
            });
            submit.setOnClickListener(v -> {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(attend.this);
                dialogBuilder.setTitle("Student Attendance");
                dialogBuilder.setMessage("Are you sure, you want to submit all Attendance?");
                dialogBuilder.setCancelable(false);
                dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
                dialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                    submit();
                });
                dialogBuilder.show();

            });
            doInBackground();

        }catch (Exception e){
        }
    }

    private void submit(){
        try {
            Attend_Submit attend_submit = new Attend_Submit();
            attend_submit.execute();
        }catch (Exception e){
        }
    }

    private void doInBackground(){
        GridLayoutManager manager = new GridLayoutManager(attend.this, 1);
        recyclerView.setLayoutManager(manager);
        attend_adapter = new attend_adapter(attend.this,0);
        Attend_Start attend_start = new Attend_Start();
        attend_start.execute(new attend_task_model(attend_adapter,class_id,section_id));

    }

    private class Attend_Submit extends AsyncTask<attend_task_model,String,String>{
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(attend_task_model... attend_task_models) {
            config.add_attendance(attend.this,class_id,section_id,period_id);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(attend.this);
            dialogBuilder.setTitle("Successfully Submitted!");
            dialogBuilder.setIcon(R.drawable.ic_check);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("ok", (dialog, which) -> {
                startActivity(new Intent(attend.this, Dashboard.class));
                finish();
            });
            dialogBuilder.show();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(attend.this,"","Loading...",false,false);
        }
    }

    private class Attend_Start extends AsyncTask<attend_task_model,String,attend_adapter>{
        ProgressDialog progressDialog;
        HomeViewModel homeViewModel = new HomeViewModel();
        attend_model attend_model;

        @Override
        protected attend_adapter doInBackground(attend_task_model... attend_task_models) {
//            homeViewModel.get_attend_student(attend.this,attend_adapter,class_id,section_id,total);

            database sqliteDB = new database(attend.this);
            try {
                homeViewModel.deleteTemp(attend.this);
                Cursor cursor = sqliteDB.getUerData("SELECT * FROM student where class_id = " +
                        "'"+class_id+"' and section_id = '"+section_id+"' ORDER BY roll ASC");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                                ,cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                                cursor.getString(cursor.getColumnIndexOrThrow("student_name")),"0",
                                cursor.getString(cursor.getColumnIndexOrThrow("id"))+".jpg");
                        attend_adapter.adduser(attend_model);
                        homeViewModel.addTemp(attend.this,attend_model);

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
            progressDialog.dismiss();
            recyclerView.setAdapter(attend_adapter);
            total.setText(Integer.toString(attend_adapter.getItemCount()));
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(attend.this,"","Loading...",false,false);
        }
    }

}