package com.sbitbd.alhelalacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sbitbd.alhelalacademy.Config.config;
import com.sbitbd.alhelalacademy.Config.database;
import com.sbitbd.alhelalacademy.Model.class_model;
import com.sbitbd.alhelalacademy.Model.four_model;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.attend_form.attend;
import com.sbitbd.alhelalacademy.present_view.present_view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class class_adapter extends RecyclerView.Adapter<class_adapter.viewHolder> {

    Context context;
    List<class_model> class_models;
    int check;

    public class_adapter(Context context, int check) {
        this.context = context;
        class_models = new ArrayList<>();
        this.check = check;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflat = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_card, null);
        viewHolder viewHolder = new viewHolder(inflat);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        class_model class_model = class_models.get(position);
        holder.bind(class_model);
        animate(holder);
    }

    @Override
    public int getItemCount() {
        return class_models.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void Clear() {
        class_models.clear();
        notifyDataSetChanged();
    }

    public void adduser(class_model pro_model) {
        try {
            class_models.add(pro_model);
            int position = class_models.indexOf(pro_model);
            notifyItemInserted(position);
        } catch (Exception e) {
        }
    }

    public void updateUser(class_model pro_model) {
        try {
            int position = getPosition(pro_model);
            if (position != -1) {
                class_models.set(position, pro_model);
                notifyItemChanged(position);
            }
        } catch (Exception e) {
        }
    }

    public int getPosition(class_model pro_model) {
        try {
            for (class_model x : class_models) {
                if (x.getId().equals(pro_model.getId()) && x.getName().equals(pro_model.getName())) {
                    return class_models.indexOf(x);
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    class viewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_lay;
        MaterialCardView cardView;
        TextView textView;
        Context context1;
        config config = new config();
        private List<four_model> section_list = new ArrayList<>();
        private List<String> section_name = new ArrayList<>();
        private List<four_model> period_list = new ArrayList<>();
        private List<String> period_name = new ArrayList<>();
        private List<String> date_list = new ArrayList<>();
        String section_id, period_id;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            cl_lay = itemView.findViewById(R.id.cl_lay);
            cardView = itemView.findViewById(R.id.cl_card);
            textView = itemView.findViewById(R.id.class_name);
            context1 = itemView.getContext();
        }

        public void bind(class_model class_model) {
            try {

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (check == 1) {
                            // Present student
                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield2, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section2);
                            AutoCompleteTextView period = view.findViewById(R.id.period1);

                            get_section(context1, "SELECT * FROM section where class_id = '" + class_model.getId() + "' " +
                                    " and id in (select section_id from teacher_priority where " +
                                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, section_name);
                            section.setAdapter(dataAdapter1);
                            section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = section_list.get(position);
                                    section_id = four_model.getOne();
                                }
                            });
                            get_period(context1, "SELECT * FROM period where class_id = '"+class_model.getId()+"'" +
                                    " and id in (select subject_name from teacher_priority " +
                                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, period_name);
                            period.setAdapter(dataAdapter2);
                            period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = period_list.get(position);
                                    period_id = four_model.getOne();
                                }
                            });

                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Selection Required");

                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (period.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Period", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(context1, present_view.class);
                                intent.putExtra("class_id", class_model.getId());
                                intent.putExtra("section_id", section_id);
                                intent.putExtra("period_id", period_id);
                                context1.startActivity(intent);
                            });
                            dialogBuilder.show();


                        } else if (check == 2) {
                            // Absent
                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield2, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section2);
                            AutoCompleteTextView period = view.findViewById(R.id.period1);

                            get_section(context1, "SELECT * FROM section where class_id = '" + class_model.getId() + "' " +
                                    " and id in (select section_id from teacher_priority where " +
                                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, section_name);
                            section.setAdapter(dataAdapter1);
                            section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = section_list.get(position);
                                    section_id = four_model.getOne();
                                }
                            });
                            get_period(context1, "SELECT * FROM period where class_id = '"+class_model.getId()+"'" +
                                    " and id in (select subject_name from teacher_priority " +
                                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, period_name);
                            period.setAdapter(dataAdapter2);
                            period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = period_list.get(position);
                                    period_id = four_model.getOne();
                                }
                            });
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Selection Required");
                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (period.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Period", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(context1, present_view.class);
                                intent.putExtra("check", "1");
                                intent.putExtra("class_id", class_model.getId());
                                intent.putExtra("section_id", section_id);
                                intent.putExtra("period_id", period_id);
                                context1.startActivity(intent);
                            });
                            dialogBuilder.show();
                        } else if (check == 5) {
                            // leave student
                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield2, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section2);
                            AutoCompleteTextView period = view.findViewById(R.id.period1);

                            get_section(context1, "SELECT * FROM section where class_id = '" + class_model.getId() + "' " +
                                    " and id in (select section_id from teacher_priority where " +
                                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, section_name);
                            section.setAdapter(dataAdapter1);
                            section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = section_list.get(position);
                                    section_id = four_model.getOne();
                                }
                            });
                            get_period(context1, "SELECT * FROM period where class_id = '"+class_model.getId()+"'" +
                                    " and id in (select subject_name from teacher_priority " +
                                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, period_name);
                            period.setAdapter(dataAdapter2);
                            period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = period_list.get(position);
                                    period_id = four_model.getOne();
                                }
                            });

                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Selection Required");

                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (period.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Period", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(context1, present_view.class);
                                intent.putExtra("check", "4");
                                intent.putExtra("class_id", class_model.getId());
                                intent.putExtra("section_id", section_id);
                                intent.putExtra("period_id", period_id);
                                context1.startActivity(intent);
                            });
                            dialogBuilder.show();


                        } else if (check == 0) {
                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield1, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section1);
                            AutoCompleteTextView date = view.findViewById(R.id.time1);
                            AutoCompleteTextView period = view.findViewById(R.id.period_sl);
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1);
                            dialogBuilder.setTitle("Selection Required");

                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (date.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Date", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (period.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Period", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                context1.startActivity(new Intent(context1, attend.class));
                            });
                            dialogBuilder.show();
                        } else if (check == 3) {
                            //All student
                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield5, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section_s);
                            get_section(context1, "SELECT * FROM section where class_id = '" + class_model.getId() + "' " +
                                    " and id in (select section_id from teacher_priority where " +
                                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, section_name);
                            section.setAdapter(dataAdapter1);
                            section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = section_list.get(position);
                                    section_id = four_model.getOne();
                                }
                            });
                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Selection Required");

                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(context1, present_view.class);
                                intent.putExtra("check", "3");
                                intent.putExtra("id", class_model.getId());
                                intent.putExtra("section_id", section_id);
                                intent.putExtra("name", "All Students");
                                context1.startActivity(intent);
                            });
                            dialogBuilder.show();

                        } else if (check == 4) {
                            //Attendance

                            View view = LayoutInflater.from(context1).inflate(R.layout.textfield2, null);
                            AutoCompleteTextView section = view.findViewById(R.id.section2);
                            AutoCompleteTextView period = view.findViewById(R.id.period1);
                            get_section(context1, "SELECT * FROM section where class_id = '" + class_model.getId() + "' " +
                                    " and id in (select section_id from teacher_priority where " +
                                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, section_name);
                            section.setAdapter(dataAdapter1);
                            section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = section_list.get(position);
                                    section_id = four_model.getOne();
                                }
                            });

                            get_period(context1, "SELECT * FROM period where class_id = '"+class_model.getId()+"'" +
                                    " and id in (select subject_name from teacher_priority " +
                                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_model.getId()+"')");
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context1,
                                    R.layout.item_name, period_name);
                            period.setAdapter(dataAdapter2);
                            period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    four_model four_model = period_list.get(position);
                                    period_id = four_model.getOne();
                                }
                            });

                            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context1,R.style.RoundShapeTheme);
                            dialogBuilder.setTitle("Selection Required");

                            dialogBuilder.setView(view);
                            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                                dialog.cancel();
                            });
                            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                if (section.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Section", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (period.getText().toString().equals("")) {
                                    Toast.makeText(context1, "Please Select a Period", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                initAttend(v,class_model.getId(),section_id,period_id);

                            });
                            dialogBuilder.show();
                        }
                    }
                });
                textView.setText(class_model.getName());
                back(cl_lay);
            } catch (Exception e) {
            }
        }

        private void back(ConstraintLayout materialCardView) {
            try {
                Random r = new Random();
                int i1 = r.nextInt(10 - 1) + 1;
                switch (i1) {
                    case 1:
                    case 8:
                        materialCardView.setBackgroundResource(R.drawable.gradient1);
                        break;
                    case 2:
                    case 9:
                        materialCardView.setBackgroundResource(R.drawable.gradient2);
                        break;
                    case 3:
                    case 10:
                        materialCardView.setBackgroundResource(R.drawable.gradient3);
                        break;
                    case 4:
                        materialCardView.setBackgroundResource(R.drawable.gradient4);
                        break;
                    case 5:
                        materialCardView.setBackgroundResource(R.drawable.gradient5);
                        break;
                    case 6:
                        materialCardView.setBackgroundResource(R.drawable.gradient6);
                        break;
                    case 7:
                        materialCardView.setBackgroundResource(R.drawable.gradient7);
                        break;
                }
            } catch (Exception e) {
            }
        }

        public void get_section(Context context, String sql) {
            database sqliteDB = new database(context);
            four_model class_model;
            try {
                section_list.clear();
                section_name.clear();
                Cursor cursor = sqliteDB.getUerData(sql);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        class_model = new four_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                                , cursor.getString(cursor.getColumnIndexOrThrow("section_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("group_id")));
                        section_list.add(class_model);
                        section_name.add(cursor.getString(cursor.getColumnIndexOrThrow("section_name")));
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
        }

        public void get_period(Context context, String sql) {
            database sqliteDB = new database(context);
            four_model class_model;
            try {
                period_list.clear();
                period_name.clear();
                Cursor cursor = sqliteDB.getUerData(sql);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        class_model = new four_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                                , cursor.getString(cursor.getColumnIndexOrThrow("class_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("period_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("subject_name")));
                        period_list.add(class_model);
                        period_name.add(cursor.getString(cursor.getColumnIndexOrThrow("period_name")));
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
        }

        public void get_date(Context context, String sql) {
            database sqliteDB = new database(context);
            try {
                date_list.clear();
                date_list.add("Today");
                Cursor cursor = sqliteDB.getUerData(sql);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        date_list.add(cursor.getString(cursor.getColumnIndexOrThrow("attend_date")));
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
        }

        private void initAttend(View v,String class_id,String section_id,String period_id){
            database sqliteDB = new database(context1);
            try {
                Cursor cursor = sqliteDB.getUerData("SELECT * FROM attendance where attend_date=" +
                        "'"+config.attend_date()+"' and class_id='"+class_id+"' and section_id='"+section_id+"'" +
                        " and period_id='"+period_id+"'");
                if (cursor.getCount() > 0) {
                    config.regularSnak(v,"Attendance already taken!");
                }
                else {
                    Intent intent = new Intent(context1, attend.class);
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("section_id", section_id);
                    intent.putExtra("period_id", period_id);
                    context1.startActivity(intent);
                }
            } catch (Exception e) {
            } finally {
                try {
                    sqliteDB.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
