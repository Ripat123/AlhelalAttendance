package com.sbitbd.alhelalattendance.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.student_details;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class teacher_adpter extends RecyclerView.Adapter<teacher_adpter.viewHolder>{

    Context context;
    List<attend_model> attend_models;
    int check;

    public teacher_adpter(Context context,int check) {
        this.context = context;
        attend_models = new ArrayList<>();
        this.check = check;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_card, null);
        viewHolder viewHolder = new viewHolder(inflat);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
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

    class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name,phone;
        MaterialCardView present,mainCard;
        Context context1;

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.tc_profile);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.number_t);
            present = itemView.findViewById(R.id.present);
            mainCard = itemView.findViewById(R.id.main_card);
            context1 = itemView.getContext();
        }
        public void bind(attend_model attend_model){
            try {
                if (check == 0) {
                    Picasso.get().load(config.STUDENT_IMG+attend_model.getImage()).transform(new RoundedCornersTransformation(100, 0))
                            .fit().centerCrop()
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(imageView);
                }else {
                    Picasso.get().load(config.STUDENT_IMG+attend_model.getId()+".jpg").transform(new RoundedCornersTransformation(100, 0))
                            .fit().centerCrop()
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(imageView);

                }
                phone.setText(attend_model.getRoll());
                name.setText(attend_model.getName());
                mainCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (check == 0){
                            Intent intent = new Intent(context1, student_details.class);
                            intent.putExtra("roll",attend_model.getRoll());
                            intent.putExtra("name",attend_model.getName());
                            intent.putExtra("image",attend_model.getImage());
                            intent.putExtra("id",attend_model.getId());
                            context1.startActivity(intent);
                        }
                    }
                });
                present.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phone;
                        if (check == 0){
                            phone = get_phone("SELECT * FROM student where id = '"+attend_model.getId()+"'");
                            if (phone != null && !phone.equals("")) {
                                if (phone.length() == 10){
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + "0"+phone));
                                    context1.startActivity(intent);
                                    present.getBackground().setTint(Color.GREEN);
                                }else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + phone));
                                    context1.startActivity(intent);
                                    present.getBackground().setTint(Color.GREEN);
                                }
                            }
                        }else {
                            phone = get_phone("SELECT * FROM teacher where id = '"+attend_model.getId()+"'");
                            if (phone.length() == 10){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + "0"+phone));
                                context1.startActivity(intent);
                                present.getBackground().setTint(Color.GREEN);
                            }else {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));
                                context1.startActivity(intent);
                                present.getBackground().setTint(Color.GREEN);
                            }
                        }

                    }
                });
            }catch (Exception e){
            }
        }
        private String get_phone(String sql){
            database sqliteDB = new database(context);
            try {
                Cursor cursor = sqliteDB.getUerData(sql);
                if (cursor.getCount() > 0) {
                    if (cursor.moveToNext()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
            return null;
        }
    }
}
