package com.sbitbd.alhelalacademy.absent_view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Config.database;
import com.sbitbd.alhelalacademy.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class absent_view extends AppCompatActivity {

    private ImageView info_back,top_image;
    private TextView roll, name, number,father,mother,sid;
    private String id,image;
    private MaterialCardView phone;
    private EditText msg_num,msg;
    private config config = new config();
    private Button send_btn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_view);
        initview();
    }
    private void initview(){
        try {
            info_back = findViewById(R.id.info_back);
            roll = findViewById(R.id.det_roll);
            name = findViewById(R.id.d_name);
            top_image = findViewById(R.id.imageView10);
            phone = findViewById(R.id.phone);
            number = findViewById(R.id.number);
            father = findViewById(R.id.d_fname);
            mother = findViewById(R.id.d_mname);
            msg_num = findViewById(R.id.num);
            sid = findViewById(R.id.id);
            send_btn = findViewById(R.id.send_msg);
            msg = findViewById(R.id.msg);

            id = getIntent().getStringExtra("id");
            image = getIntent().getStringExtra("image");
            roll.setText(getIntent().getStringExtra("roll"));
            name.setText(getIntent().getStringExtra("name"));
            sid.setText(id);
            phone.setOnClickListener(v -> {
                if (!number.getText().toString().trim().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number.getText().toString().trim()));
                    startActivity(intent);
                }
            });
            info_back.setOnClickListener(v -> {
                onBackPressed();
                finish();
            });
            send_btn.setOnClickListener(view -> send_sms(msg_num.getText().toString().trim(),msg.getText().toString().trim(),view));
            get_student_details(absent_view.this,id);
            Picasso.get().load(config.STUDENT_IMG + image).transform(new RoundedCornersTransformation(10, 0))
                    .fit().centerInside()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(top_image);
        }catch (Exception e){
        }
    }

    private void get_student_details(Context context, String id) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM student where id = " +
                    "'" + id + "'");
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    father.setText(cursor.getString(cursor.getColumnIndexOrThrow("father_name")));
                    mother.setText(cursor.getString(cursor.getColumnIndexOrThrow("mother_name")));
                    if (cursor.getString(cursor.getColumnIndexOrThrow("phone")).length() == 10) {
                        number.setText("0" + cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                        msg_num.setText("0" + cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    }
                    else {
                        number.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                        msg_num.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    }
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

    private void send_sms(String phone,String sms,View v) {
        try {
            progressDialog = ProgressDialog.show(absent_view.this,"","Sending...",false,false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.SEND_SMS,
                    response -> {
                        progressDialog.cancel();
                        config.regularSnak(v,"Message sent.");
                    }, error -> {
                        progressDialog.cancel();
                        Toast.makeText(absent_view.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.PHONE, phone);
                    params.put(config.SMS, sms);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(absent_view.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
        }
    }
}