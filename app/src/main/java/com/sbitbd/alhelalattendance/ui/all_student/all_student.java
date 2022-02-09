package com.sbitbd.alhelalattendance.ui.all_student;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.class_view.class_view;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

public class all_student extends Fragment {

    private View root;
    private RecyclerView recyclerView;
//    private class_model class_model;
    private class_adapter class_adapter;
    private HomeViewModel homeViewModel = new HomeViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_all_student, container, false);
        initview();
        return root;
    }
    private void initview(){
        try {
            recyclerView = root.findViewById(R.id.all_class_rec);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 2);
            recyclerView.setLayoutManager(manager);
            class_adapter = new class_adapter(root.getContext().getApplicationContext(),3);
            homeViewModel.get_class(root.getContext().getApplicationContext(),class_adapter);

            recyclerView.setAdapter(class_adapter);
        }catch (Exception e){
        }
    }
}