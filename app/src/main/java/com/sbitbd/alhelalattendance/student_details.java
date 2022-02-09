package com.sbitbd.alhelalattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class student_details extends AppCompatActivity {
    private ImageView back, top_image;
    private TextView roll, name, number,father,mother,sid;
    private String image, id;
    private MaterialCardView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        initview();
    }

    private void initview() {
        back = findViewById(R.id.info_back);
        roll = findViewById(R.id.det_roll);
        name = findViewById(R.id.d_name);
        top_image = findViewById(R.id.imageView10);
        phone = findViewById(R.id.phone);
        number = findViewById(R.id.number);
        father = findViewById(R.id.d_fname);
        mother = findViewById(R.id.d_mname);
        sid = findViewById(R.id.id);

        roll.setText(getIntent().getStringExtra("roll"));
        name.setText(getIntent().getStringExtra("name"));
        image = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");
        sid.setText(id);
        get_student_details(student_details.this,id);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number.getText().toString().trim().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number.getText().toString().trim()));
                    startActivity(intent);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Picasso.get().load(config.STUDENT_IMG + image).transform(new RoundedCornersTransformation(10, 0))
                .fit().centerInside()
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(top_image);
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
                    if (cursor.getString(cursor.getColumnIndexOrThrow("phone")).length() == 10)
                        number.setText("0"+cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    else
                        number.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
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
}