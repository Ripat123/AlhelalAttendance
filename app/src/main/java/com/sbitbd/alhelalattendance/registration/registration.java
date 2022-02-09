package com.sbitbd.alhelalattendance.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Dashboard;
import com.sbitbd.alhelalattendance.MainActivity;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.Model.four_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.download.download;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registration extends AppCompatActivity {

    private Button reg,login;
    private EditText phone,password,confirm_pass;
    private AutoCompleteTextView autoCompleteTextView;
    private ProgressBar progressBar;
    private config config = new config();
    private controller controller = new controller();
    private List<four_model> teacherList = new ArrayList<>();
    private List<String> teacherName = new ArrayList<>();
    private String teacher_id="";
    private four_model class_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initview();
    }
    private void initview(){
        try {
            reg = findViewById(R.id.login);
            login = findViewById(R.id.reg);
            phone = findViewById(R.id.phone_r);
            password = findViewById(R.id.pass);
            confirm_pass = findViewById(R.id.con_pass);
            autoCompleteTextView = findViewById(R.id.teacher_s);
            progressBar = findViewById(R.id.progress);

            showTeacher();
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    class_model = teacherList.get(position);
                    teacher_id = class_model.getOne();
                    phone.setText(class_model.getThree());
                }
            });
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reg();
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(registration.this, MainActivity.class));
                    finish();
                }
            });
        }catch (Exception e){
        }
    }

    private void reg(){
        try {
            if (phone.getText().toString().trim().equals("")) {
                phone.setError("Empty Phone Number");
                Toast.makeText(registration.this, "Empty Phone Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (autoCompleteTextView.getText().toString().trim().equals("")) {
                autoCompleteTextView.setError("Select Teacher");
                Toast.makeText(registration.this, "Select Teacher", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().trim().equals("")) {
                password.setError("Empty Password");
                Toast.makeText(registration.this, "Empty Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().length() < 5) {
                password.setError("Password must be >= 5 character");
                Toast.makeText(registration.this, "Password must be >= 5 character", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.getText().toString().equals(confirm_pass.getText().toString())) {
                confirm_pass.setError("Password didn't matched");
                Toast.makeText(registration.this, "Password didn't matched", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            controller.check_teacher(registration.this,teacher_id,phone.getText().toString().trim(),
                    password.getText().toString().trim(),progressBar);
        }catch (Exception e){
        }
    }

    private void showTeacher(){
        try {
            String sql = "SELECT teachers_id AS 'one',teachers_name AS 'two',mobile_no AS 'three' " +
                    "from teachers_information ORDER BY teachers_id ASC";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.FOUR_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showTeacherList(response);
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(registration.this,
                                    R.layout.item_name,teacherName);
                            autoCompleteTextView.setAdapter(dataAdapter);
                            if (!config.checkData(registration.this,"SELECT * FROM teacher"))
                                store_teacher(response);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(registration.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(registration.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
        }
    }

    private void showTeacherList(String response){
        String id = "";
        String name = "",phone = "";
        four_model cat_models;
        try {
            teacherList.clear();
            teacherName.clear();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                JSONObject collegeData = result.getJSONObject(i);
                name = collegeData.getString(config.TWO);
                id = collegeData.getString(config.ONE);
                phone = collegeData.getString(config.THREE);
                cat_models = new four_model(id,name,phone,"");
                teacherList.add(cat_models);
                teacherName.add(name);
            }
        }catch (Exception e){
        }
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
                    config.add_teacher(registration.this,id,name,phone);
                }catch (Exception e){
                }
            }

        } catch (Exception e) {
        }
    }
}