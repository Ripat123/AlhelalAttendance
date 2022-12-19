package com.sbitbd.alhelalacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Model.attend_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.student_details;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class attend_adapter extends RecyclerView.Adapter<attend_adapter.userHolder> {

    Context context;
    List<attend_model> attend_models;
    final int check;

    public attend_adapter(Context context, int check) {
        this.context = context;
        attend_models = new ArrayList<>();
        this.check = check;
    }

    @NonNull
    @Override
    public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.present_card, null);
        userHolder userHolder = new userHolder(inflat);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull userHolder holder, int position) {
        attend_model attend_model = attend_models.get(position);
        holder.bind(attend_model);
//        animate(holder);
    }

    @Override
    public int getItemCount() {
        return attend_models.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anti_bounce);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
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
        TextView roll, name, attend_status;
        MaterialCardView attend_card, main_card, comment_card;
        Context context1;
        config config = new config();

        public userHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.std_profile);
            roll = itemView.findViewById(R.id.roll);
            name = itemView.findViewById(R.id.name);
            attend_status = itemView.findViewById(R.id.attend_status);
            attend_card = itemView.findViewById(R.id.present);
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
                if (attend_model.getAttend_status().equals("1")) {
                    attend_card.getBackground().setTint(Color.GREEN);
                    attend_status.setText("P");
//                    updateUser(new attend_model(attend_model.getId(), attend_model.getRoll(), attend_model.getName(), "1", ""));
                } else if (attend_model.getAttend_status().equals("2")) {
                    attend_card.getBackground().setTint(Color.CYAN);
                    attend_status.setText("L");

                } else {
                    attend_card.getBackground().setTint(Color.RED);
                    attend_status.setText("A");
//                    updateUser(new attend_model(attend_model.getId(), attend_model.getRoll(), attend_model.getName(), "0", ""));
                }

                attend_card.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            if (attend_model.getAttend_status().equals("0") || attend_model.getAttend_status().equals("2")) {
                                attend_card.getBackground().setTint(Color.GREEN);
                                attend_status.setText("P");
                                updateUser(new attend_model(attend_model.getId(), attend_model.getRoll(), attend_model.getName(), "1", attend_model.getImage()));

                                if (check == 1) {
                                    config.Live_update_status(context1, attend_model.getImage(), "1");
                                    config.Live_update_comment(context1, attend_model.getImage(), "");
                                    config.Live_update_time(context1, attend_model.getImage());
                                } else {
                                    config.update_status(context1, attend_model.getId(), "1");
                                    config.update_comment(context1, attend_model.getId(), "");
                                }
                            } else {
                                attend_card.getBackground().setTint(Color.RED);
                                attend_status.setText("A");
                                updateUser(new attend_model(attend_model.getId(), attend_model.getRoll(), attend_model.getName(), "0", attend_model.getImage()));

                                if (check == 1) {
                                    config.Live_update_status(context1, attend_model.getImage(), "0");
                                    config.Live_update_comment(context1, attend_model.getImage(), "");
                                    config.Live_update_time(context1, attend_model.getImage());
                                } else {
                                    config.update_status(context1, attend_model.getId(), "0");
                                    config.update_comment(context1, attend_model.getId(), "");
                                }
                            }
                        } catch (Exception e) {
                        }


                    }
                });
                attend_card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        attend_card.getBackground().setTint(Color.CYAN);
                        attend_status.setText("L");
                        updateUser(new attend_model(attend_model.getId(), attend_model.getRoll(), attend_model.getName(), "2", attend_model.getImage()));
                        if (check == 1) {
                            config.Live_update_status(context1, attend_model.getImage(), "2");
                            config.Live_update_comment(context1, attend_model.getImage(), "");
                            config.Live_update_time(context1, attend_model.getImage());
                        }
                        else {
                            config.update_status(context1, attend_model.getId(), "2");
                            config.update_comment(context1, attend_model.getId(), "");
                        }

                        View view = LayoutInflater.from(context1).inflate(R.layout.textfield3, null);
                        EditText comment = view.findViewById(R.id.comment);
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                        dialogBuilder.setTitle("Add Comment(Optional)");
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setView(view);
                        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                            dialog.cancel();
                        });
                        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                            if (check == 1)
                                config.Live_update_comment(context1, attend_model.getImage(), comment.getText().toString().trim());
                            else
                                config.update_comment(context1, attend_model.getId(), comment.getText().toString().trim());
                        });
                        dialogBuilder.show();
                        return true;
                    }
                });

                comment_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = LayoutInflater.from(context1).inflate(R.layout.textfield3, null);
                        EditText comment = view.findViewById(R.id.comment);
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                        dialogBuilder.setTitle("Add Comment");
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setView(view);
                        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                        });
                        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                            if (check == 1)
                                config.Live_update_comment(context1, attend_model.getImage(), comment.getText().toString().trim());
                            else
                                config.update_comment(context1, attend_model.getId(), comment.getText().toString().trim());
                        });
                        dialogBuilder.show();
                    }
                });
                main_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, attend_model.getRoll() + "\n" + attend_model.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                main_card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context1, student_details.class);
                        intent.putExtra("roll", attend_model.getRoll());
                        intent.putExtra("name", attend_model.getName());
                        intent.putExtra("id", attend_model.getId());
                        intent.putExtra("image", attend_model.getId() + ".jpg");
                        context1.startActivity(intent);
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }

    }
}
