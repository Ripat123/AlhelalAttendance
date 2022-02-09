package com.sbitbd.alhelalattendance.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.ui.update_attendance.update_attendance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class teacher_atd_adapter extends RecyclerView.Adapter<teacher_atd_adapter.userHolder> {
    Context context;
    List<attend_model> attend_models;
    int check;

    public teacher_atd_adapter(Context context, int check) {
        this.context = context;
        attend_models = new ArrayList<>();
        this.check = check;
    }

    @NonNull
    @Override
    public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_atd_card, null);
        userHolder userHolder = new userHolder(inflat);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull userHolder holder, int position) {
        attend_model attend_model = attend_models.get(position);
        holder.bind(attend_model);
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
    public void removeUser(attend_model cart_model){
        int position = getPosition(cart_model);

        if(position!=-1){
            removeUser(position);
        }
    }

    public void removeUser(int position){
        attend_models.remove(position);
        notifyItemRemoved(position);
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

    class userHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView roll, name,time_txt;
        MaterialCardView start, end;
        Context context1;
        config config = new config();
        ProgressDialog progressDialog;

        public userHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.std_profile);
            roll = itemView.findViewById(R.id.roll_t);
            name = itemView.findViewById(R.id.name);
            end = itemView.findViewById(R.id.present);
            start = itemView.findViewById(R.id.comment_card);
            time_txt = itemView.findViewById(R.id.time_txt);
            context1 = itemView.getContext();
        }

        public void bind(attend_model attend_model) {
            try {
                Picasso.get().load(config.STUDENT_IMG + attend_model.getId() + ".jpg").transform(new RoundedCornersTransformation(100, 0))
                        .fit().centerCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(imageView);
                roll.setText(attend_model.getRoll());
                name.setText(attend_model.getName());
                if (check == 0 || check == 2) {
                    end.setVisibility(View.GONE);
                    time_txt.setText(R.string.start);
                }
                else if (check == 3)
                    start.setVisibility(View.GONE);
                else
                    time_txt.setText(R.string.update);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (config.isOnline(context1)) {
                            if (check == 2) {
                                progressDialog = ProgressDialog.show(context1, "", "Loading...", false, false);
                                config.teacherGap_check(context1, "select id from teacher_gap_time " +
                                        "where attend_date = '" + config.attend_date() + "' and teacher_id = " +
                                        "'" + attend_model.getId() + "' and start_time is not null and end_time is null", attend_model.getId(), progressDialog);
                            } else {
                                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1);
                                dialogBuilder.setTitle("Update");
                                dialogBuilder.setMessage(attend_model.getRoll());
                                dialogBuilder.setIcon(R.drawable.ic_baseline_update_24);
                                dialogBuilder.setCancelable(false);
                                dialogBuilder.setPositiveButton("Present", (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context1, "", "Loading...", false, false);
                                    config.teacherLiveUpdate(context1, "UPDATE teacher_attend SET attendance ='1'," +
                                            "start_time='" + config.get_Time() + "' where attend_date = '" + config
                                            .attend_date() + "' and teacher_id = '" + attend_model.getId() + "'", progressDialog);
                                    removeUser(attend_model);
                                });
                                dialogBuilder.setNegativeButton("Leave", (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context1, "", "Loading...", false, false);
                                    config.teacherLiveUpdate(context1, "UPDATE teacher_attend SET attendance ='2'," +
                                            "start_time='" + config.get_Time() + "' where attend_date = '" + config
                                            .attend_date() + "' and teacher_id = '" + attend_model.getId() + "'", progressDialog);
                                    removeUser(attend_model);
                                });
                                dialogBuilder.setNeutralButton("Absent", (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context1, "", "Loading...", false, false);
                                    config.teacherLiveUpdate(context1, "UPDATE teacher_attend SET attendance ='0'," +
                                            "start_time='" + config.get_Time() + "' where attend_date = '" + config
                                            .attend_date() + "' and teacher_id = '" + attend_model.getId() + "'", progressDialog);
                                });
                                dialogBuilder.show();
                            }
                        }else
                            config.regularSnak(v,"No Internet Connection!");
                    }
                });

                end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (config.isOnline(context1)) {
                            progressDialog = ProgressDialog.show(context1, "", "Loading...", false, false);
                            if (check == 3) {
                                config.teacherLiveUpdate(context1, "UPDATE teacher_gap_time SET end_time " +
                                        "= current_time WHERE attend_date = '" + config.attend_date() + "' and teacher_id = " +
                                        "'" + attend_model.getId() + "' and start_time is not null and end_time is null", progressDialog);
                            } else {
                                config.teacherLiveUpdate(context1, "UPDATE teacher_attend SET " +
                                        "end_time='" + config.get_Time() + "' where attend_date = '" + config
                                        .attend_date() + "' and teacher_id = '" + attend_model.getId() + "'", progressDialog);
                            }
                            removeUser(attend_model);
                        }else
                            config.regularSnak(v,"No Internet Connection!");
                    }
                });
            } catch (Exception e) {
            }
        }
    }
}
