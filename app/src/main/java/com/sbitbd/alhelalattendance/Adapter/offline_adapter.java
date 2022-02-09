package com.sbitbd.alhelalattendance.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Dashboard;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.update_attend_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.attend_form.attend;
import com.sbitbd.alhelalattendance.present_view.present_view;
import com.sbitbd.alhelalattendance.settings.settings;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;
import com.sbitbd.alhelalattendance.ui.update_attendance.update_attendance;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class offline_adapter extends RecyclerView.Adapter<offline_adapter.viewHolder> {

    Context context, context1;
    List<update_attend_model> user_models;

    public offline_adapter(Context context) {
        this.context = context;
        this.user_models = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_item, null);
        viewHolder viewHolder = new viewHolder(inflat);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        update_attend_model class_model = user_models.get(position);
        holder.bind(class_model);
        animate(holder);
    }

    @Override
    public int getItemCount() {
        return user_models.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void Clear() {
        user_models.clear();
        notifyDataSetChanged();
    }

    public void adduser(update_attend_model pro_model) {
        try {
            user_models.add(pro_model);
            int position = user_models.indexOf(pro_model);
            notifyItemInserted(position);
        } catch (Exception e) {
        }
    }

    //    public void updateUser(update_attend_model pro_model) {
//        try {
//            int position = getPosition(pro_model);
//            if (position != -1) {
//                user_models.set(position, pro_model);
//                notifyItemChanged(position);
//            }
//        } catch (Exception e) {
//        }
//    }
    public void removeUser(update_attend_model cart_model) {
        int position = getPosition(cart_model);

        if (position != -1) {
            removeUser(position);
        }
    }

    public void removeUser(int position) {
        user_models.remove(position);
        notifyItemRemoved(position);
    }

    public int getPosition(update_attend_model pro_model) {
        try {
            for (update_attend_model x : user_models) {
                if (x.getClass_id().equals(pro_model.getClass_id()) && x.getSection_id().equals(pro_model
                        .getSection_id()) && x.getPeriod_id().equals(pro_model.getPeriod_id()) && x.getSave().equals(pro_model.getSave())) {
                    return user_models.indexOf(x);
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView date, time, class_name, section, period;
        MaterialCardView upload_card, offline_card;
        config config = new config();
        final View view;

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            class_name = itemView.findViewById(R.id.class_name);
            section = itemView.findViewById(R.id.section_t);
            period = itemView.findViewById(R.id.period_n);
            upload_card = itemView.findViewById(R.id.comment_card);
            offline_card = itemView.findViewById(R.id.offline_card);
            context1 = itemView.getContext();
            view = itemView;
        }

        public void bind(update_attend_model user_model) {
            try {
                date.setText(user_model.getSave());
                time.setText(user_model.getId());
                class_name.setText("Class: " + user_model.getPassword() + ";");
                section.setText("Section: " + user_model.getPhone());
                period.setText("Period: " + user_model.getStatus());
                upload_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (config.isOnline(context1)) {
                            AllTask allTask = new AllTask();
                            allTask.execute(user_model.getSave(), user_model.getId(), user_model.getClass_id(),
                                    user_model.getSection_id(), user_model.getPeriod_id());
                            removeUser(user_model);
                        } else {
                            config.regularSnak(v, "Currently your Internet Unavailable!");
                        }
                    }
                });
                offline_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context1, present_view.class);
                        intent.putExtra("check", "5");
                        intent.putExtra("id", user_model.getSave());
                        intent.putExtra("class_id", user_model.getClass_id());
                        intent.putExtra("section_id", user_model.getSection_id());
                        intent.putExtra("period_id", user_model.getPeriod_id());
                        context1.startActivity(intent);
                    }
                });
            } catch (Exception e) {
            }
        }


    }

    private class AllTask extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog;
        JSONObject jsonObject = new JSONObject();
        String date, time, class_id, section_id, period_id;

        @Override
        protected JSONObject doInBackground(String... strings) {
            database sqliteDB = new database(context1);
            this.date = strings[0];
            this.time = strings[1];
            this.class_id = strings[2];
            this.section_id = strings[3];
            this.period_id = strings[4];
            try {
                int i = 0;
                Cursor cursor = sqliteDB.getUerData("SELECT * FROM attendance where attend_date=" +
                        "'" + strings[0] + "' and class_id='" + strings[2] + "' and section_id='" + strings[3] + "'" +
                        " and period_id='" + strings[4] + "' and upload_status = '0'");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        jsonObject.put("" + i, "INSERT INTO attendance(time,date,month,year," +
                                "attend_date,class_id,section_id,period_id,student_id,attendance," +
                                "comment,teacher_id) VALUES('" + cursor.getString(cursor.getColumnIndexOrThrow("time")) + "','" + cursor.getString(cursor.
                                getColumnIndexOrThrow("date")) + "','" + cursor.getString(cursor.
                                getColumnIndexOrThrow("month")) + "'" +
                                ",'" + cursor.getString(cursor.getColumnIndexOrThrow("year")) + "','" + strings[0] + "'" +
                                ",'" + strings[2] + "','" + strings[3] + "','" + strings[4] + "'" +
                                ",'" + cursor.getString(cursor.getColumnIndexOrThrow("student_id")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("attendance")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("comment")) + "'," +
                                "'" + cursor.getString(cursor.getColumnIndexOrThrow("teacher_id")) + "')");
                        i++;
                    }
                } else {
                    Toast.makeText(context1, "Not found!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) {
                Toast.makeText(context1, "Empty data!", Toast.LENGTH_SHORT).show();
            } else
                add_json(jsonObject, date, class_id, section_id, period_id, progressDialog);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context1, "", "Uploading...", false, false);
        }

        private void add_json(JSONObject jsonObject, String attend_date, String class_id,
                              String section_id, String period_id, ProgressDialog progressDialog) {
            config config = new config();
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, config.ADD_ONLINE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                if (response.trim().equals("")) {
                                    Toast.makeText(context1, "Attendance Uploaded Successful!", Toast.LENGTH_LONG).show();
                                    config.update_online_status(context1, attend_date, class_id, section_id, period_id);

                                } else {
                                    Toast.makeText(context1, response.trim(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context1, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(config.QUERY, jsonObject.toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context1);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            } catch (Exception e) {
            }
        }
    }
}
