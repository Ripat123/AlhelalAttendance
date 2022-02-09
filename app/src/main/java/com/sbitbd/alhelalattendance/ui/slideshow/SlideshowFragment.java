package com.sbitbd.alhelalattendance.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private RecyclerView recyclerView;
//    private class_model class_model;
    private class_adapter class_adapter;
    private HomeViewModel homeViewModel = new HomeViewModel();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initview(root);
        return root;
    }
    private void initview(View root){
        try {
            recyclerView = root.findViewById(R.id.absent_class_rec);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 2);
            recyclerView.setLayoutManager(manager);
            class_adapter = new class_adapter(root.getContext().getApplicationContext(),2);
            homeViewModel.get_class(root.getContext().getApplicationContext(),class_adapter);

            recyclerView.setAdapter(class_adapter);
        }catch (Exception e){
        }
    }
}