package com.sbitbd.alhelalacademy.forget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.MainActivity;
import com.sbitbd.alhelalacademy.R;

import java.util.HashMap;
import java.util.Map;

public class forget_code extends AppCompatActivity {

    private Button verify,setpass;
    private EditText phone_for,pass,pass_con;
    config config = new config();
    private String phone;
    private ProgressDialog progressDialog;
    private String teacher_id;
    private ImageView ve_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_code);

        setpass = findViewById(R.id.set_passbtn);
        pass = findViewById(R.id.pass_v);
        pass_con = findViewById(R.id.conpass_v);
        phone = getIntent().getStringExtra("phone");
        teacher_id = getIntent().getStringExtra("id");
        verify = findViewById(R.id.verify_btn);
        phone_for = findViewById(R.id.phone_for_v);
        ve_back = findViewById(R.id.ve_back);

        ve_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matching(phone_for.getText().toString().trim(),phone);
            }
        });
        setpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getText().toString().equals("")) {
                    pass.setError("Empty Password");
                    Toast.makeText(forget_code.this, "Empty Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().length() < 5) {
                    pass.setError("Password must be >= 5 character");
                    Toast.makeText(forget_code.this, "Password must be >= 5 character", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.getText().toString().equals(pass_con.getText().toString())) {
                    pass_con.setError("Password didn't matched");
                    Toast.makeText(forget_code.this, "Password didn't matched", Toast.LENGTH_SHORT).show();
                    return;
                }
                Reset();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void Matching(String code,String phone){
        try {
            progressDialog = ProgressDialog.show(forget_code.this,"","Loading",false,false);
            String sql = "SELECT teacher_id AS 'id' FROM user WHERE phone = '"+phone+"' AND code = '"+code+"'";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if(!response.trim().equals("")) {
                                pass.setEnabled(true);
                                pass_con.setEnabled(true);
                                setpass.setEnabled(true);
//                                geustID = response;
                            }else
                                Toast.makeText(forget_code.this,"Invalid Verification Code" , Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(forget_code.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(forget_code.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){e.printStackTrace();
        }
    }

    private void Reset(){
        try {
            progressDialog = ProgressDialog.show(forget_code.this,"","Loading",false,false);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.RESET_PASS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if(response.trim().equals("1")) {
                                Toast.makeText(forget_code.this,"Success" , Toast.LENGTH_LONG).show();
                                startActivity(new Intent(forget_code.this, MainActivity.class));
                                finish();
                            }else
                                Toast.makeText(forget_code.this,"Unsuccessful please try again." , Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(forget_code.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, pass.getText().toString());
                    params.put(config.ID, phone);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(forget_code.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }catch (Exception e){
        }
    }
}