package com.sbitbd.alhelalattendance.class_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

public class class_view extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView back;
//    private class_model class_model;
    private class_adapter class_adapter;
    private HomeViewModel homeViewModel =new HomeViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_view);
        initview();
    }
    private void initview(){
        try {
            recyclerView = findViewById(R.id.class_rec);
            back = findViewById(R.id.class_back);

            back.setOnClickListener(v -> {
                onBackPressed();
                finish();
            });

            GridLayoutManager manager = new GridLayoutManager(class_view.this, 2);
            recyclerView.setLayoutManager(manager);
            String check = getIntent().getStringExtra("check");
            if (check != null && check.equals("2")) {
                class_adapter = new class_adapter(class_view.this, 2);

                homeViewModel.get_class(class_view.this,class_adapter);
                recyclerView.setAdapter(class_adapter);
            }else if (check != null && check.equals("3")){
                class_adapter = new class_adapter(class_view.this, 3);
                homeViewModel.get_class(class_view.this,class_adapter);

                recyclerView.setAdapter(class_adapter);
            }else if (check != null && check.equals("1")){
                class_adapter = new class_adapter(class_view.this, 1);
                homeViewModel.get_class(class_view.this,class_adapter);

                recyclerView.setAdapter(class_adapter);
            }else if (check != null && check.equals("4")){
                //leave student
                class_adapter = new class_adapter(class_view.this, 5);
                homeViewModel.get_class(class_view.this,class_adapter);

                recyclerView.setAdapter(class_adapter);
            }
        }catch (Exception e){
        }
    }
}