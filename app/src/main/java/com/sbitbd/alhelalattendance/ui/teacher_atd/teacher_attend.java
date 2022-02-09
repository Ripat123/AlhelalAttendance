package com.sbitbd.alhelalattendance.ui.teacher_atd;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Adapter.teacher_atd_adapter;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class teacher_attend extends Fragment {

    private View root;
    private config config = new config();
    private RecyclerView teacher_atd_rec;
    private teacher_atd_adapter teacher_atd_adapter,teacher_atd_adapter1;
    private MaterialCardView start_card, end_card;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_teacher_attend, container, false);
        initview();
        return root;
    }

    private void initview() {
        try {
            teacher_atd_rec = root.findViewById(R.id.teacher_atd_rec);
            start_card = root.findViewById(R.id.start_card);
            end_card = root.findViewById(R.id.end_card);
            teacher_atd_adapter = new teacher_atd_adapter(root.getContext().getApplicationContext(),0);
            teacher_atd_adapter1 = new teacher_atd_adapter(root.getContext().getApplicationContext(),1);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 1);
            teacher_atd_rec.setLayoutManager(manager);
            get_leave(root.getContext().getApplicationContext(), "SELECT " +
                    "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                    "" + config.attend_date() + "' and attendance = '0'",teacher_atd_adapter);
            start_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start_card.setCardBackgroundColor(getResources().getColor(R.color.green));
                    end_card.setCardBackgroundColor(getResources().getColor(R.color.main_color));
                    get_leave(root.getContext().getApplicationContext(), "SELECT " +
                            "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                            "" + config.attend_date() + "' and attendance = '0'",teacher_atd_adapter);
                }
            });
            end_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start_card.setCardBackgroundColor(getResources().getColor(R.color.main_color));
                    end_card.setCardBackgroundColor(getResources().getColor(R.color.green));
                    get_leave(root.getContext().getApplicationContext(), "SELECT " +
                            "teacher_id AS 'one' from teacher_attend WHERE attend_date = '" +
                            "" + config.attend_date() + "' and attendance = '1' and end_time is null",teacher_atd_adapter1);
                }
            });

        } catch (Exception e) {
        }
    }

    private void get_leave(Context context, String sql,teacher_atd_adapter teacher_atd_adapter) {
        try {
            progressDialog = ProgressDialog.show(root.getContext(),"","Loading...",false,false);
            teacher_atd_adapter.Clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.TWO_DIMENSION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (!response.trim().equals("")) {
                                store_section(response.trim(),teacher_atd_adapter);
                            }else
                                Toast.makeText(context, "Attendance Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
        teacher_atd_rec.setAdapter(teacher_atd_adapter);
    }

    private void store_section(String response,teacher_atd_adapter teacher_atd_adapter) {
        String id = "";
        teacher_atd_adapter.Clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(config.RESULT);
            for (int i = 0; i <= result.length(); i++) {
                try {
                    JSONObject collegeData = result.getJSONObject(i);
                    id = collegeData.getString(config.ONE);
                    initadd("SELECT * FROM teacher WHERE id = '" + id + "'",teacher_atd_adapter);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private void initadd(String sql, teacher_atd_adapter teacher_atd_adapter) {
        attend_model attend_model;
        database sqliteDB = new database(root.getContext().getApplicationContext());
        try {
            Cursor cursor = sqliteDB.getUerData(sql);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    attend_model = new attend_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")), config.get_Time(),
                            cursor.getString(cursor.getColumnIndexOrThrow("id")) + ".jpg");
                    teacher_atd_adapter.adduser(attend_model);

                }
            }
            teacher_atd_rec.setAdapter(teacher_atd_adapter);
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }
}