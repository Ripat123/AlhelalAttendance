package com.sbitbd.alhelalacademy.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalacademy.Adapter.class_adapter;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Config.database;
import com.sbitbd.alhelalacademy.Model.user_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.class_view.class_view;
import com.sbitbd.alhelalacademy.present_view.present_view;
import com.sbitbd.alhelalacademy.teacher_attendance.teacher_attend;
import com.sbitbd.alhelalacademy.teacher_page.teacher_page;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MaterialCardView all_card,teacher_card,absent_card,present_card,leave_card,teacher_come,
            teacher_leave,teacher_present,teacher_absent,teacher_leave_card;
//    private class_model class_model;
    private class_adapter class_adapter;
    private RecyclerView recyclerView;
    private TextView student_count,student_count2;
    private config config = new config();
    private ProgressDialog progressDialog;
    private ConstraintLayout teacher_panel;
//    private int mHour,mMinute;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initview(root);
        return root;
    }
    private void initview(View root){
        try {
            all_card = root.findViewById(R.id.std_card);
            teacher_card = root.findViewById(R.id.teacher_card);
            absent_card = root.findViewById(R.id.absent_card);
            present_card = root.findViewById(R.id.present_card);
            leave_card = root.findViewById(R.id.leave_card);
            recyclerView = root.findViewById(R.id.recyclerView);
            student_count = root.findViewById(R.id.student_count);
            student_count2 = root.findViewById(R.id.student_count2);
            teacher_come = root.findViewById(R.id.teacher_come);
            teacher_leave = root.findViewById(R.id.teacher_leave);
            teacher_present = root.findViewById(R.id.present_card_std);
            teacher_absent = root.findViewById(R.id.absent_card_std);
            teacher_leave_card = root.findViewById(R.id.leave_card_std);
            teacher_panel = root.findViewById(R.id.teacher_panel);

            user_model user_model = config.User_info(root.getContext().getApplicationContext());
            if (user_model.getStatus().equals("0") || user_model.getStatus().equals("")){
                teacher_panel.setVisibility(View.GONE);
            }

            all_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), class_view.class);
                    intent.putExtra("check","3");
                    startActivity(intent);
                }
            });
            teacher_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), present_view.class);
                    intent.putExtra("check","2");
                    startActivity(intent);
                }
            });
            absent_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), class_view.class);
                    intent.putExtra("check","2");
                    startActivity(intent);

                }
            });
            present_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), class_view.class);
                    intent.putExtra("check","1");
                    startActivity(intent);
//startService(root);
                }
            });
            leave_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), class_view.class);
                    intent.putExtra("check","4");
                    startActivity(intent);

                }
            });

            teacher_come.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkT(root.getContext().getApplicationContext(),view);
                }
            });
            teacher_leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getcheck(root.getContext().getApplicationContext(),config.attend_date(),view);
                }
            });
            teacher_leave.setVisibility(View.GONE);
            teacher_present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), teacher_page.class);
                    intent.putExtra("check","1");
                    startActivity(intent);
                }
            });
            teacher_absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), teacher_page.class);
                    intent.putExtra("check","2");
                    startActivity(intent);
                }
            });
            teacher_leave_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(root.getContext().getApplicationContext(), teacher_page.class);
                    intent.putExtra("check","3");
                    startActivity(intent);
                }
            });

            student_count.setText(config.getData(root.getContext().getApplicationContext(),
                    "SELECT count(student.id) as 'id' from student where student.class_id in " +
                            "(select class_id from teacher_priority where teacher_id = '"+config.User_info(root.getContext().getApplicationContext()).getId()+"') " +
                            "and student.section_id in (select section_id from teacher_priority where " +
                            "teacher_id = '"+config.User_info(root.getContext().getApplicationContext()).getId()+"')","id"));
//            student_count.setText("");
            student_count2.setText(config.getData(root.getContext().getApplicationContext(),
                    "SELECT count(id) as 'id' from teacher","id"));

            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 2);
            recyclerView.setLayoutManager(manager);
            class_adapter = new class_adapter(root.getContext().getApplicationContext(),4);
            homeViewModel.get_class(root.getContext().getApplicationContext(),class_adapter);


            recyclerView.setAdapter(class_adapter);
        }catch (Exception e){
        }
    }

    private void checkT(Context context,View View) {
        database sqliteDB = new database(context);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM teacher_attendance where attend_date " +
                    "= '"+config.attend_date()+"'");
            if (cursor.getCount() > 0) {
                config.regularSnak(View,"Attendance already taken!");
            }else {
                Intent intent = new Intent(context, teacher_attend.class);
                intent.putExtra("status",1);
                startActivity(intent);
            }
        } catch (Exception e) {
        } finally {
            try {
                sqliteDB.close();
            } catch (Exception e) {
            }
        }
    }

    private void getcheck(Context context, String attend_date,View v) {
        progressDialog = ProgressDialog.show(v.getContext(),"","Checking...",false,false);
        try {
            String sql = "SELECT end_time AS 'id' from teacher_attend WHERE attend_date = " +
                    "'" + attend_date + "' and attendance = '1'";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.GET_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            if (response.trim().equals("") || response.trim().equals("null")) {
                                Intent intent = new Intent(context, teacher_attend.class);
                                intent.putExtra("status",2);
                                startActivity(intent);
                            } else {
                                config.regularSnak(v,"Already Submitted!");
                            }
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
    }

//    public void startService(View root) {
//        Intent serviceIntent = new Intent(root.getContext().getApplicationContext(), attend_view_service.class);
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
//        ContextCompat.startForegroundService(root.getContext().getApplicationContext(), serviceIntent);
//    }
//    public void stopService(View root) {
//        Intent serviceIntent = new Intent(root.getContext().getApplicationContext(), attend_view_service.class);
//        stopService(serviceIntent);
//    }
}