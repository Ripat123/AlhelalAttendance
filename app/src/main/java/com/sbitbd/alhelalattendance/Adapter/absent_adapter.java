package com.sbitbd.alhelalattendance.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.absent_view.absent_view;
import com.sbitbd.alhelalattendance.present_view.present_view;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class absent_adapter extends RecyclerView.Adapter<absent_adapter.viewHolder> {
    Context context;
    List<attend_model> attend_models;
    ProgressDialog progressDialog;

    public absent_adapter(Context context) {
        this.context = context;
        this.attend_models = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.absent_card, null);
        viewHolder viewHolder = new viewHolder(inflat);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        attend_model class_model = attend_models.get(position);
        holder.bind(class_model);
    }

    @Override
    public int getItemCount() {
        return attend_models.size();
    }

    public void Clear() {
        attend_models.clear();
        notifyDataSetChanged();
    }

    public void adduser(attend_model pro_model) {
        try {
            attend_models.add(pro_model);
            int position = attend_models.indexOf(pro_model);
            notifyItemInserted(position);
        } catch (Exception e) {
        }
    }

    public void updateUser(attend_model pro_model) {
        try {
            int position = getPosition(pro_model);
            if (position != -1) {
                attend_models.set(position, pro_model);
                notifyItemChanged(position);
            }
        } catch (Exception e) {
        }
    }

    public int getPosition(attend_model pro_model) {
        try {
            for (attend_model x : attend_models) {
                if (x.getId().equals(pro_model.getId()) && x.getRoll().equals(pro_model.getRoll())) {
                    return attend_models.indexOf(x);
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    class viewHolder extends RecyclerView.ViewHolder {
        MaterialCardView msg, fine, main_card;
        ImageView std_profile;
        TextView roll, name;
        Context context1;
        config config = new config();
        String phone = "";

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.message_card);
            fine = itemView.findViewById(R.id.fine_card);
            main_card = itemView.findViewById(R.id.absent_main_card);
            std_profile = itemView.findViewById(R.id.std_profile);
            roll = itemView.findViewById(R.id.roll);
            name = itemView.findViewById(R.id.name);
            this.context1 = itemView.getContext();
        }

        public void bind(attend_model attend_model) {
            try {
                Picasso.get().load(config.STUDENT_IMG + attend_model.getImage()).transform(new RoundedCornersTransformation(100, 0))
                        .fit().centerCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(std_profile);
                roll.setText(attend_model.getRoll());
                name.setText(attend_model.getName());
                msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (attend_model.getAttend_status() == null || attend_model.getAttend_status().equals(""))
                            config.regularSnak(v, "Phone Number not found!");
                        else {

                            if (attend_model.getAttend_status().length() == 10)
                                phone = "0" + attend_model.getAttend_status();
                            else
                                phone = attend_model.getAttend_status();
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Auto Message");
                            dialogBuilder.setMessage("Do you want to send a Message to " + phone + "?");
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                send_sms(phone,attend_model.getName()+" আজ স্কুলে অনুপস্থিত।");

                            });
                            dialogBuilder.show();
                        }
                    }
                });
                fine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                main_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context1, absent_view.class);
                        intent.putExtra("id",attend_model.getId());
                        intent.putExtra("image",attend_model.getImage());
                        intent.putExtra("roll",attend_model.getRoll());
                        intent.putExtra("name",attend_model.getName());
                        context1.startActivity(intent);
                    }
                });
            } catch (Exception e) {
            }
        }

        private void send_sms(String phone,String sms) {
            try {
                progressDialog = ProgressDialog.show(context1,"","Sending...",false,false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, config.SEND_SMS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.cancel();
                                Toast.makeText(context1, "Message sent.", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        Toast.makeText(context1, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(config.PHONE, phone);
                        params.put(config.SMS, sms);
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
