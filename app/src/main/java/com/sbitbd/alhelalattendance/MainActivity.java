package com.sbitbd.alhelalattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.download.download;
import com.sbitbd.alhelalattendance.forget.forget_code;
import com.sbitbd.alhelalattendance.network_listener.ConnectivityReceiver;
import com.sbitbd.alhelalattendance.registration.registration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button login, reg, forgot;
    private EditText phone_field, password_field;
    private MaterialCheckBox materialCheckBox;
    private config config = new config();
    private ProgressBar loadingProgressBar;
    private static user_model user_model;
    private String forget_phone, teacher_id;
    private ProgressDialog progressDialog;
    private List<String> teacher_name = new ArrayList<>();
    private List<class_model> teacher_list = new ArrayList<>();
    private class_model class_model;
    private String save = "0";
    private dashboard_controller dashboard_controller = new dashboard_controller();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initview();
    }

    private void initview() {
        try {
            login = findViewById(R.id.login);
            reg = findViewById(R.id.reg);
            forgot = findViewById(R.id.forgot);
            phone_field = findViewById(R.id.phone);
            password_field = findViewById(R.id.pass);
            materialCheckBox = findViewById(R.id.materialCheckBox);
            loadingProgressBar = findViewById(R.id.progress_l);
            check_offline_data_upload();
            save_state();

            materialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        save = "1";
                    else
                        save = "0";
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (phone_field.getText().toString().trim().equals("")) {
                        phone_field.setError("Empty Phone!");
                        Toast.makeText(MainActivity.this, "Empty Phone!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password_field.getText().toString().trim().equals("")) {
                        password_field.setError("Empty Password!");
                        Toast.makeText(MainActivity.this, "Empty Password!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    if (config.checkUser(MainActivity.this)) {
                        if (user_model.getPhone().equals(phone_field.getText().toString().trim()) &&
                                user_model.getPassword().equals(password_field.getText().toString().trim())) {
                            if (materialCheckBox.isChecked()) {
                                config.updateUser(MainActivity.this, user_model.getId(),
                                        user_model.getStatus(), user_model.getPhone(),
                                        user_model.getPassword(), "1");
                            }
                            startActivity(new Intent(MainActivity.this, download.class));
                            finish();
                        } else {
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("User didn't matched");
                            dialogBuilder.setMessage("Are you want to try with internet?");
                            dialogBuilder.setCancelable(false);
                            dialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                                if (config.isOnline(MainActivity.this)) {
                                    login(phone_field.getText().toString().trim(), password_field.getText().toString().trim());
                                } else {
                                    loadingProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "No Internet!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.cancel();
                                loadingProgressBar.setVisibility(View.GONE);
                            });
                            dialogBuilder.show();
                        }
                    } else {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this,R.style.RoundShapeTheme);
                        dialogBuilder.setTitle("User not found!");
                        dialogBuilder.setMessage("Are you want to try with internet?");
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                            if (config.isOnline(MainActivity.this)) {
                                login(phone_field.getText().toString().trim(), password_field.getText().toString().trim());
                            } else {
                                Toast.makeText(MainActivity.this, "No Internet!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                            loadingProgressBar.setVisibility(View.GONE);
                        });
                        dialogBuilder.show();
                    }


//                    startActivity(new Intent(MainActivity.this,Dashboard.class));
//                    finish();
                }
            });
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, registration.class));
                    finish();
                }
            });
            forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (config.isOnline(MainActivity.this)) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.forgot_phone, null);
                        EditText phoneE = view.findViewById(R.id.phone_for);
                        AutoCompleteTextView teacher = view.findViewById(R.id.teacher_f);
                        showTeacher(teacher);
                        teacher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                class_model = teacher_list.get(position);
                                teacher_id = class_model.getId();
                            }
                        });
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this,R.style.RoundShapeTheme);
                        dialogBuilder.setTitle("Forget Password");
                        dialogBuilder.setMessage("Select teacher and enter your phone number for a verification code");
                        dialogBuilder.setView(view);
                        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                            forget_phone = "";
                            dialog.cancel();
                        });
                        dialogBuilder.setPositiveButton("Reset", (dialog, which) -> {
                            if (phoneE.getText().toString().equals("")) {
                                phoneE.setError("Empty Number");
                                Toast.makeText(MainActivity.this, "Empty Number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (phoneE.getText().toString().length() < 11) {
                                phoneE.setError("Phone Number must be 11 characters");
                                Toast.makeText(MainActivity.this, "Phone Number must be 11 characters", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (teacher.getText().toString().trim().equals("")) {
                                teacher.setError("Select Teacher");
                                Toast.makeText(MainActivity.this, "Select Teacher", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            forget_phone = phoneE.getText().toString().trim();
                            getCode(forget_phone, teacher_id);
                        });
                        dialogBuilder.show();
                    } else
                        Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
        }
    }

    private void showTeacher(AutoCompleteTextView autoCompleteTextView) {
        try {
            String sql = "SELECT teachers_id as 'one',teachers_name as 'two' FROM `teachers_information`";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showTeacherList(response.trim());
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
                                    R.layout.item_name, teacher_name);
                            autoCompleteTextView.setAdapter(dataAdapter);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void showTeacherList(String response) {
        String id = "";
        String name = "";
        class_model cat_models;
        try {
            teacher_list.clear();
            teacher_name.clear();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                JSONObject collegeData = result.getJSONObject(i);
                name = collegeData.getString(config.TWO);
                id = collegeData.getString(config.ONE);
                cat_models = new class_model(id, name);
                teacher_list.add(cat_models);
                teacher_name.add(name);
            }
        } catch (Exception e) {
        }
    }

    private void getCode(String phone, String teacher) {
        try {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.FORGET,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (!response.equals("1")) {
                                Intent intent = new Intent(MainActivity.this, forget_code.class);
                                intent.putExtra("phone", phone);
                                intent.putExtra("id", teacher_id);
                                startActivity(intent);
                            } else
                                Toast.makeText(MainActivity.this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.PHONE, phone);
                    params.put(config.ID, teacher);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    public void login(String username, String password) {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingProgressBar.setVisibility(View.GONE);
                            if (response != null && !response.trim().equals("") && !response.trim().equals("{\"result\":[]}")) {
                                showJson(response.trim());
                            } else {
                                Toast.makeText(MainActivity.this, "not a verified user!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, username);
                    params.put(config.PASS, password);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void showJson(String response) {
        String admin = "", id = "", phone = "", password = "";
        try {
            phone = phone_field.getText().toString().trim();
            password = password_field.getText().toString().trim();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            JSONObject collegeData = result.getJSONObject(0);
            admin = collegeData.getString(config.STUDENT_NAME);
            id = collegeData.getString(config.ID);
//                phone = collegeData.getString(config.PHONE);
//                password = collegeData.getString(config.PASS);
            if (config.checkUser(MainActivity.this)) {
                config.updateUser(MainActivity.this, id, admin, phone, password, save);
            } else {
                config.insertuser(MainActivity.this, admin, phone, password, id, save);
            }
            config.teacher_admin_check(MainActivity.this,id);


        } catch (Exception e) {
        }
    }

    private void save_state() {
        try {
            user_model = config.User_info(MainActivity.this);
            if (user_model.getSave().equals("1")) {
                startActivity(new Intent(MainActivity.this, download.class));
                finish();
            }
        } catch (Exception e) {
        }
    }

    private void check_offline_data_upload(){
        try {
            if (config.isOnline(MainActivity.this)){
                dashboard_controller.get_offline_attend(MainActivity.this);
            }
        }catch (Exception e){
        }
    }


}