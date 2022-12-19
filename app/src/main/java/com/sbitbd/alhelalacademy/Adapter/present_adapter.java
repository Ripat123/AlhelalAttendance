package com.sbitbd.alhelalacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Model.attend_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.student_details;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class present_adapter extends RecyclerView.Adapter<present_adapter.userHolder>{
    Context context;
    List<attend_model> attend_models;

    public present_adapter(Context context) {
        this.context = context;
        attend_models = new ArrayList<>();
    }

    @NonNull
    @Override
    public present_adapter.userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.present_st_card, null);
        userHolder userHolder = new userHolder(inflat);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull present_adapter.userHolder holder, int position) {
        attend_model attend_model = attend_models.get(position);
        holder.bind(attend_model);
//        animate(holder);
    }

    @Override
    public int getItemCount() {
        return attend_models.size();
    }

//    public void animate(RecyclerView.ViewHolder viewHolder) {
//        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anti_bounce);
//        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
//    }
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
        TextView roll, name, attend_status;
        MaterialCardView main_card;
        Context context1;
        ConstraintLayout constraintLayout;
        config config = new config();

        public userHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.std_profile);
            roll = itemView.findViewById(R.id.roll);
            name = itemView.findViewById(R.id.name);
            attend_status = itemView.findViewById(R.id.period_t);
//            attend_card = itemView.findViewById(R.id.present);
            main_card = itemView.findViewById(R.id.main_card);
            constraintLayout = itemView.findViewById(R.id.period_l);
            context1 = itemView.getContext();
        }

        public void bind(attend_model attend_model) {
            try {
                Picasso.get().load(config.STUDENT_IMG+attend_model.getImage()).transform(new RoundedCornersTransformation(30, 0))
                        .fit().centerCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(imageView);
                roll.setText(attend_model.getRoll());
                name.setText(attend_model.getName());
                if (attend_model.getAttend_status().equals("1")){
                    constraintLayout.setBackgroundColor(Color.GREEN);
                    attend_status.setText("P");
                }
                else if (attend_model.getAttend_status().equals("0")){
                    constraintLayout.setBackgroundColor(Color.GRAY);
                    attend_status.setText("N");
                }
                else if (attend_model.getAttend_status().equals("2")){
                    constraintLayout.setBackgroundColor(Color.RED);
                    attend_status.setText("A");
                }


                main_card.setOnClickListener(v -> {
                    Intent intent = new Intent(context1, student_details.class);
                    intent.putExtra("roll",attend_model.getRoll());
                    intent.putExtra("name",attend_model.getName());
                    intent.putExtra("image",attend_model.getImage());
                    intent.putExtra("id",attend_model.getId());
                    context1.startActivity(intent);
                });

            } catch (Exception e) {
            }
        }
    }
}
