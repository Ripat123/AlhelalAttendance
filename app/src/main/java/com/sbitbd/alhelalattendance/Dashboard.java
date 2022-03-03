package com.sbitbd.alhelalattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.Config.database;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.Model.four_model;
import com.sbitbd.alhelalattendance.Model.user_model;
import com.sbitbd.alhelalattendance.activity.BaseCallActivity;
import com.sbitbd.alhelalattendance.attend_form.attend;
import com.sbitbd.alhelalattendance.network_listener.ApplicationListener;
import com.sbitbd.alhelalattendance.network_listener.ConnectivityReceiver;
import com.sbitbd.alhelalattendance.settings.settings;
import com.sbitbd.alhelalattendance.ui.update_attendance.update_attendance;
import com.sbitbd.alhelalattendance.website.website;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;

public class Dashboard extends BaseCallActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MenuItem web,online,teacher,teacher_gap;
    private config config = new config();
    private List<class_model> class_list = new ArrayList<>();
    private List<String> class_name = new ArrayList<>();
    private List<four_model> section_list = new ArrayList<>();
    private List<String> section_name = new ArrayList<>();
    private List<four_model> period_list = new ArrayList<>();
    private List<String> period_name = new ArrayList<>();
    private String class_id, section_id, period_id;
    private dashboard_controller dashboard_controller = new dashboard_controller();
    private static final int PERMISSION_REQ_FORWARD = 1 << 4;

    // Permission request when we want to stay in
    // current activity even if all permissions are granted.
    private static final int PERMISSION_REQ_STAY = 1 << 3;

    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(Dashboard.this).inflate(R.layout.textfield4, null);
                AutoCompleteTextView class_s = view1.findViewById(R.id.class_s);
                AutoCompleteTextView section = view1.findViewById(R.id.section_s);
                AutoCompleteTextView period = view1.findViewById(R.id.period_s);

                get_class(Dashboard.this);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Dashboard.this,
                        R.layout.item_name, class_name);
                class_s.setAdapter(dataAdapter);
                class_s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        class_model class_model = class_list.get(position);
                        class_id = class_model.getId();
                        get_section(Dashboard.this, class_model.getId());
                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Dashboard.this,
                                R.layout.item_name, section_name);
                        section.setAdapter(dataAdapter1);
                        section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                four_model four_model = section_list.get(position);
                                section_id = four_model.getOne();
                                get_period(Dashboard.this, four_model.getOne());
                                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(Dashboard.this,
                                        R.layout.item_name, period_name);
                                period.setAdapter(dataAdapter2);
                                period.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        four_model four_model1 = period_list.get(position);
                                        period_id = four_model1.getOne();
                                    }
                                });
                            }
                        });
                    }
                });


                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this,R.style.RoundShapeTheme);
                dialogBuilder.setTitle("Selection Required");
                dialogBuilder.setView(view1);
                dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.cancel();
                });
                dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                    if (class_s.getText().toString().equals("")) {
                        Toast.makeText(Dashboard.this, "Please Select a Class", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (section.getText().toString().equals("")) {
                        Toast.makeText(Dashboard.this, "Please Select a Section", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (period.getText().toString().equals("")) {
                        Toast.makeText(Dashboard.this, "Please Select a Period", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    initAttend(view);
                });
                dialogBuilder.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_all_student, R.id.nav_website,
                R.id.nav_offline,R.id.nav_online,R.id.nav_teacher_gap,R.id.nav_teacher)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        web = navigationView.getMenu().findItem(R.id.nav_website);
        online = navigationView.getMenu().findItem(R.id.nav_online);
        teacher = navigationView.getMenu().findItem(R.id.nav_teacher);
        teacher_gap = navigationView.getMenu().findItem(R.id.nav_teacher_gap);
        web.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(Dashboard.this, website.class));
                return true;
            }
        });
        online.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(Dashboard.this, update_attendance.class));
                return true;
            }
        });
        user_model user_model = config.User_info(Dashboard.this);
        if (user_model.getStatus() == null || user_model.getStatus().equals("0") || user_model.getStatus().equals("")){
            teacher.setVisible(false);
            teacher_gap.setVisible(false);
        }
        checkPermissions();
//        check_offline_data_upload();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(Dashboard.this, settings.class));
                return true;
            }
        });
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this,R.style.RoundShapeTheme);
                dialogBuilder.setTitle("Confirmation");
                dialogBuilder.setMessage("Are you Sure you want to Log Out?");

                dialogBuilder.setNegativeButton("No", (dialog, which) -> {

                    dialog.cancel();
                });
                dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                    config.deleteuser(Dashboard.this);
                    startActivity(new Intent(Dashboard.this, MainActivity.class));
                    finish();
                });
                dialogBuilder.show();
                return true;
            }
        });
        menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this,R.style.RoundShapeTheme);
                dialogBuilder.setTitle("Confirmation");
                dialogBuilder.setMessage("Are you Sure you want to exit?");

                dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.cancel();
                });
                dialogBuilder.setPositiveButton("exit", (dialog, which) -> {
                    System.exit(1);
                });
                dialogBuilder.show();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void get_class(Context context) {
        database sqliteDB = new database(context);
        class_model class_model;
        try {
            class_list.clear();
            class_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM class where id in (select class_id " +
                    "  from teacher_priority where teacher_id = '"+config.User_info(context).getId()+"')");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    class_model = new class_model(cursor.getString(cursor.getColumnIndexOrThrow("id"))
                            , cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
                    class_list.add(class_model);
                    class_name.add(cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
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

    public void get_section(Context context, String id) {
        database sqliteDB = new database(context);
        four_model class_model;
        try {
            section_list.clear();
            section_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM section where class_id = '" + class_id + "' " +
                    " and id in (select section_id from teacher_priority where " +
                    "teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_id+"')");
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

    public void get_period(Context context, String id) {
        database sqliteDB = new database(context);
        four_model class_model;
        try {
            period_list.clear();
            period_name.clear();
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM period where class_id = '"+class_id+"'" +
                    " and id in (select subject_name from teacher_priority " +
                    "where teacher_id = '"+config.User_info(context).getId()+"' and class_id = '"+class_id+"')");
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

    private void initAttend(View v){
        database sqliteDB = new database(Dashboard.this);
        try {
            Cursor cursor = sqliteDB.getUerData("SELECT * FROM attendance where attend_date=" +
                    "'"+config.attend_date()+"' and class_id='"+class_id+"' and section_id='"+section_id+"'" +
                    " and period_id='"+period_id+"'");
            if (cursor.getCount() > 0) {
                config.regularSnak(v,"Attendance already taken!");
            }
            else {
                Intent intent = new Intent(Dashboard.this, attend.class);
                intent.putExtra("class_id", class_id);
                intent.putExtra("section_id", section_id);
                intent.putExtra("period_id", period_id);
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

    private void checkPermissions() {
        if (!permissionArrayGranted(null)) {
            requestPermissions(PERMISSION_REQ_STAY);
        }
    }

    private boolean permissionArrayGranted(@Nullable String[] permissions) {
        String[] permissionArray = permissions;
        if (permissionArray == null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//                permissionArray = PERMISSIONS1;
//            else
            permissionArray = PERMISSIONS;
        }

        boolean granted = true;
        for (String per : permissionArray) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    private void requestPermissions(int request) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//            ActivityCompat.requestPermissions(this, PERMISSIONS1, request);
//        else
        ActivityCompat.requestPermissions(this, PERMISSIONS, request);
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_FORWARD ||
                requestCode == PERMISSION_REQ_STAY) {
            boolean granted = permissionArrayGranted(permissions);
            if (!granted) {
                toastNeedPermissions();
            }
        }
    }
    private void toastNeedPermissions() {
        Toast.makeText(this, R.string.need_necessary_permissions, Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        check_offline_data_upload();
//    }

//    private void check_offline_data_upload(){
//        try {
//            if (config.isOnline(Dashboard.this)){
//                dashboard_controller.get_offline_attend(Dashboard.this);
//            }
//        }catch (Exception e){
//        }
//    }


    @Override
    public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

    }
}