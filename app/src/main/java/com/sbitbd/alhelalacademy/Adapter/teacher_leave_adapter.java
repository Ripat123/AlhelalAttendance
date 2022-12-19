package com.sbitbd.alhelalacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Model.attend_model;
import com.sbitbd.alhelalacademy.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class teacher_leave_adapter extends RecyclerView.Adapter<teacher_leave_adapter.userHolder> {
    Context context;
    List<attend_model> attend_models;

    public teacher_leave_adapter(Context context) {
        this.context = context;
        attend_models = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public userHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_leave, null);
        userHolder userHolder = new userHolder(inflat);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull userHolder holder, int position) {
        attend_model attend_model = attend_models.get(position);
        holder.bind(attend_model);
    }

    @Override
    public int getItemCount() {
        return attend_models.size();
    }
    public List<attend_model> getList(){
        return attend_models;
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

    class userHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView roll, name, attend_status, time;
        MaterialCardView main_card, comment_card;
        Context context1;
        config config = new config();
        private int mHour, mMinute;

        public userHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.std_profile);
            roll = itemView.findViewById(R.id.roll_t);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time_txt);
            attend_status = itemView.findViewById(R.id.attend_status);
            main_card = itemView.findViewById(R.id.main_card);
            comment_card = itemView.findViewById(R.id.comment_card);
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
                time.setText(attend_model.getAttend_status());


//                comment_card.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final Calendar c = Calendar.getInstance();
//                        mHour = c.get(Calendar.HOUR_OF_DAY);
//                        mMinute = c.get(Calendar.MINUTE);
//
//                        // Launch Time Picker Dialog
//                        TimePickerDialog timePickerDialog = new TimePickerDialog(context1, R.style.RoundShapeTheme,
//                                new TimePickerDialog.OnTimeSetListener() {
//
//                                    @Override
//                                    public void onTimeSet(TimePicker view, int hourOfDay,
//                                                          int minute) {
//                                        time.setText(hourOfDay + ":" + minute);
//                                        updateUser(new attend_model(attend_model.getId(),attend_model.getRoll(),attend_model.getName(),hourOfDay + ":" + minute,""));
//                                        config.teacher_update_end(context1, attend_model.getId(), hourOfDay + ":" + minute);
//                                    }
//                                }, mHour, mMinute, false);
//                        timePickerDialog.show();
//
//                    }
//                });
                main_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, attend_model.getRoll() + "\n" + attend_model.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
//                main_card.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        Intent intent = new Intent(context1, student_details.class);
//                        intent.putExtra("roll", attend_model.getRoll());
//                        intent.putExtra("name", attend_model.getName());
//                        intent.putExtra("id", attend_model.getId());
//                        intent.putExtra("image", attend_model.getId() + ".jpg");
//                        context1.startActivity(intent);
//                        return true;
//                    }
//                });
            } catch (Exception e) {
            }
        }
    }
}
