package com.sbitbd.alhelalattendance.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.agora.Constants;
import com.sbitbd.alhelalattendance.present_view.present_view;
import com.sbitbd.alhelalattendance.student_details;
import com.sbitbd.alhelalattendance.teacher_page.teacher_page;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class teacher_adpter extends RecyclerView.Adapter<teacher_adpter.viewHolder>{

    Context context;
    List<attend_model> attend_models;
    int check;
    present_view activity;
    com.sbitbd.alhelalattendance.teacher_page.teacher_page teacher_page;
    config config = new config();

    public teacher_adpter(Context context,int check,present_view activity) {
        this.context = context;
        attend_models = new ArrayList<>();
        this.check = check;
        this.activity = activity;
    }

    public teacher_adpter(Context context,int check,teacher_page teacher_page) {
        this.context = context;
        attend_models = new ArrayList<>();
        this.check = check;
        this.teacher_page = teacher_page;
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
                        if (check == 0) {
                            phone = get_phone("SELECT * FROM student where id = '" + attend_model.getId() + "'");
                            if (phone != null && !phone.equals("")) {
                                if (phone.length() == 10) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + "0" + phone));
                                    context1.startActivity(intent);
                                    present.getBackground().setTint(Color.GREEN);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + phone));
                                    context1.startActivity(intent);
                                    present.getBackground().setTint(Color.GREEN);
                                }
                            }
                        } else {
                            phone = get_phone("SELECT * FROM teacher where id = '" + attend_model.getId() + "'");
                            show_call_dialog(attend_model.getName(), phone, config.STUDENT_IMG + attend_model.getId() + ".jpg");
                        }

                    }
                });
            }catch (Exception e){
            }
        }



        private void show_call_dialog(String name, String phone, String img_link) {
            try {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context1, R.style.CustomBottomSheetDialog);
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.setContentView(R.layout.call_dialog);
                MaterialCardView online_call_card = bottomSheetDialog.findViewById(R.id.online_call_card);
                MaterialCardView offline_call_card = bottomSheetDialog.findViewById(R.id.offline_call_card);
                MaterialCardView online_video_call_card = bottomSheetDialog.findViewById(R.id.online_video_call_card);
                CircleImageView circle_img = bottomSheetDialog.findViewById(R.id.circle_img);
                TextView t_name = bottomSheetDialog.findViewById(R.id.t_name);

                t_name.setText(name);
                Picasso.get().load(img_link)
                        .fit().centerCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(circle_img);
                online_call_card.setOnClickListener(view -> {
                    startCall(phone,0);
                    bottomSheetDialog.dismiss();
                });
                online_video_call_card.setOnClickListener(view -> {
                    startCall(phone,1);
                    bottomSheetDialog.dismiss();
                });
                offline_call_card.setOnClickListener(view -> {
                    if (phone.length() == 10) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + "0" + phone));
                        context1.startActivity(intent);
                        present.getBackground().setTint(Color.GREEN);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        context1.startActivity(intent);
                        present.getBackground().setTint(Color.GREEN);
                    }
                    bottomSheetDialog.dismiss();
                });
                bottomSheetDialog.show();
            } catch (Exception e) {
            }
        }

        private void startCall(String number, int status) {
            if (number == null || number.equals("")) {
                Toast.makeText(context1,
                        "Number not found!",
                        Toast.LENGTH_SHORT).show();
            }

            String uid = config.User_info(context1).getPhone();
            if (uid.contains("+"))
                uid = uid.replace("+", "");
            String channel = config.channelName(uid, number);
//                                String channel = activity.getString(R.string.app_name);
            activity.gotoCallingInterface(number, channel, Constants.ROLE_CALLER, status);

//            Set<String> peerSet = new HashSet<>();
//            peerSet.add(number);
//
//            activity.rtmClient().queryPeersOnlineStatus(peerSet,
//                    new ResultCallback<Map<String, Boolean>>() {
//                        @Override
//                        public void onSuccess(Map<String, Boolean> statusMap) {
//                            Boolean bOnline = statusMap.get(number);
//                            if (bOnline != null && bOnline) {
//                                String uid = config.User_info(context1).getPhone();
//                                if (uid.contains("+"))
//                                    uid = uid.replace("+", "");
//                                String channel = config.channelName(uid, number);
////                                String channel = activity.getString(R.string.app_name);
//                                activity.gotoCallingInterface(number, channel, Constants.ROLE_CALLER, status);
//                            } else {
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context1,
//                                                number + " is offline!",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(ErrorInfo errorInfo) {
//                            Log.d("ttt", errorInfo.getErrorDescription());
//                        }
//                    });

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
