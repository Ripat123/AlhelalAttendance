package com.sbitbd.alhelalacademy.registration;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.verification.verify;

import java.util.HashMap;
import java.util.Map;

public class controller {
    protected void check_teacher(Context context, String teacher_id, String phone,
                            String password, ProgressBar progressBar) {
//        progressDialog = ProgressDialog.show(context, "", "Proccessing...", false, false);
        try {
            String sql = "SELECT teacher_id as 'id' FROM user WHERE teacher_id = '" + teacher_id + "' and admin_status = '1'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.trim().equals("")) {
                                Toast.makeText(context, "Already Registered", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
//                                check_phone(context,teacher_id,phone,password,progressBar);
                                insertuserData(context,teacher_id,phone,password,progressBar);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
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
    }
    protected void check_phone(Context context, String teacher_id, String phone,
                                 String password, ProgressBar progressBar) {
//        progressDialog = ProgressDialog.show(context, "", "Proccessing...", false, false);
        try {
            String sql = "SELECT teacher_id FROM user WHERE phone = '" + phone + "'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.trim().equals("")) {
                                Toast.makeText(context, "Phone number Already taken!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                insertuserData(context,teacher_id,phone,password,progressBar);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
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
    }
    public void insertuserData(Context context, String teacher_id, String phone,
                               String password, ProgressBar progressBar) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.REG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Toast.makeText(seller_reg.this,response,Toast.LENGTH_LONG).show();
                            if (response.trim().equals("1")) {
//                                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
//                                dialogBuilder.setIcon(R.drawable.ic_check);
//                                dialogBuilder.setTitle("Registration Successful");
//                                dialogBuilder.setMessage("Please wait for Approve.");
//                                dialogBuilder.setCancelable(false);
//                                dialogBuilder.setPositiveButton("OK",(dialog, which) -> {
//                                    Intent intent = new Intent(context, MainActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    context.startActivity(intent);
//                                });
//                                dialogBuilder.show();

                                Intent intent = new Intent(context, verify.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("id",teacher_id);
                                intent.putExtra("phone",phone);
                                intent.putExtra("password",password);
                                context.startActivity(intent);

                            } else {
                                Toast.makeText(context, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.ID, teacher_id);
                    params.put(config.PHONE, phone);
                    params.put(config.PASS, password);
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
