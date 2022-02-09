package com.sbitbd.alhelalattendance.verification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.download.download;

import java.util.HashMap;
import java.util.Map;

public class verify extends AppCompatActivity {
    private EditText verify_t;
    private Button verify_btn;
    private TextInputLayout email_lay;
    private String id, phone, pass;
    private ProgressDialog progressDialog;
    private config config = new config();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        initView();
    }

    private void initView() {
        try {
            verify_btn = findViewById(R.id.virify);
            verify_t = findViewById(R.id.verify_t);
            email_lay = findViewById(R.id.email_lay);

            id = getIntent().getStringExtra("id");
            phone = getIntent().getStringExtra("phone");
            pass = getIntent().getStringExtra("password");

            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (verify_t.getText().toString().trim().equals("") || verify_t.getText().toString()
                            .trim().length() < 4 || verify_t.getText().toString().trim().length() > 4) {
                        email_lay.setError("Invalid Verification code");
                        return;
                    }
                    checkOTP();
                }
            });
            send();
        } catch (Exception e) {
        }
    }

    private void checkOTP() {
        progressDialog = ProgressDialog.show(verify.this, "", "Checking...", false, false);
        try {
            String sql = "SELECT teacher_id as 'id' FROM user WHERE teacher_id = '" + id + "' and code = " +
                    "'" + verify_t.getText().toString().trim() + "' and phone = '" + phone + "'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (response.trim().equals("")) {
                                Toast.makeText(verify.this, "Invalid Verification code!", Toast.LENGTH_SHORT).show();
                            } else {
                                update(progressDialog);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(verify.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(verify.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void send() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.SEND_OTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("1")) {
                                Toast.makeText(verify.this, "Verification not sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(verify.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.PHONE, phone);
                    params.put(config.ID, id);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(verify.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }

    private void update(ProgressDialog progressDialog) {
        try {
            String sql = "UPDATE user SET admin_status = '1' WHERE teacher_id = '" + id + "' and code = " +
                    "'" + verify_t.getText().toString().trim() + "' and phone = '" + phone + "'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.INSERT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (!response.trim().equals("")) {
                                Toast.makeText(verify.this, "Update failed!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (config.checkUser(verify.this)) {
                                    config.updateUser(verify.this, id, "1", phone, "", "1");
                                } else {
                                    config.insertuser(verify.this, "1", phone, "", id, "1");
                                }
                                config.teacher_admin_check(verify.this,id);

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(verify.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(verify.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }
}