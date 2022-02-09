package com.sbitbd.alhelalattendance.update_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
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
import com.sbitbd.alhelalattendance.Adapter.absent_adapter;
import com.sbitbd.alhelalattendance.Adapter.present_adapter;
import com.sbitbd.alhelalattendance.Adapter.update_atd_adapter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.databinding.ActivityUpdateViewBinding;
import com.sbitbd.alhelalattendance.present_view.present_view;
import com.sbitbd.alhelalattendance.ui.update_attendance.update_attendance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class update_view extends AppCompatActivity {

    private ActivityUpdateViewBinding activity_update_view;
    private ImageView back;
    private RecyclerView present_rec;
    private String class_id, section, period;
    private ProgressDialog loadingProgressBar;
    private TextView total;
    private config config = new config();
    private update_atd_adapter update_atd_adapter;
    private attend_model attend_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_update_view = ActivityUpdateViewBinding.inflate(getLayoutInflater());
        setContentView(activity_update_view.getRoot());
        initView();
    }

    private void initView() {
        try {
            back = activity_update_view.presentBack;
            present_rec = activity_update_view.presentRec;
            total = activity_update_view.total;
            update_atd_adapter = new update_atd_adapter(update_view.this);
            GridLayoutManager manager = new GridLayoutManager(update_view.this, 1);
            present_rec.setLayoutManager(manager);

            class_id = getIntent().getStringExtra("class_id");
            section = getIntent().getStringExtra("section_id");
            period = getIntent().getStringExtra("period_id");

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
            initData();
        } catch (Exception e) {
        }
    }

    private void initData() {
        try {
            loadingProgressBar = ProgressDialog.show(update_view.this, "", "Loading...", false, false);
            user_model user_model = config.User_info(update_view.this);
            String sql = "select attendance.student_id" +
                    " as 'one',attendance.id as 'two' from attendance inner join running_student_info on" +
                    " attendance.student_id=running_student_info.student_id where attendance.class_id = '" + class_id + "'" +
                    "    and attendance.period_id = '" + period + "' and attendance.section_id = '" + section + "' and" +
                    " attendance.attend_date = '" + config.attend_date() + "' and attendance.attendance = '0' order by running_student_info.class_roll asc";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingProgressBar.dismiss();
                            if (response != null && !response.trim().equals("") && !response.trim().equals("{\"result\":[]}")) {
                                set_fun(response.trim(), update_view.this, update_atd_adapter, total);

                            } else {
                                Toast.makeText(update_view.this, "Not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(present_view.this, "Failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(update_view.this, error.toString(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.QUERY, sql);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(update_view.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {e.printStackTrace();
        }
        present_rec.setAdapter(update_atd_adapter);
    }

    private void set_fun(String response, Context context, update_atd_adapter present_adapter, TextView total) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                try {
                    JSONObject collegeData = result.getJSONObject(i);
                    get_student_data(context, present_adapter, collegeData
                            .getString(config.ONE), collegeData.getString(config.TWO), total);

                } catch (Exception e) {e.printStackTrace();
                }
            }

        } catch (Exception e) {e.printStackTrace();
        }
    }

    private void get_student_data(Context context, update_atd_adapter class_adapter
            , String id, String atd_id, TextView total) {
        database sqliteDB = new database(context);
        try {
            String sql = "select student.roll,student.student_name from student where student.id = '" + id + "'";
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    attend_model = new attend_model(id
                            , cursor.getString(cursor.getColumnIndexOrThrow("roll")),
                            cursor.getString(cursor.getColumnIndexOrThrow("student_name")), atd_id,
                            id + ".jpg");

                    class_adapter.adduser(attend_model);
                    total.setText(Integer.toString(class_adapter.getItemCount()));
                }
            }
        } catch (Exception e) {e.printStackTrace();
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }
}