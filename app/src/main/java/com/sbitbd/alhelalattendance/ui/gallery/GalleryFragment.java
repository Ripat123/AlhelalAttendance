package com.sbitbd.alhelalattendance.ui.gallery;

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

import com.sbitbd.alhelalattendance.Adapter.attend_adapter;
import com.sbitbd.alhelalattendance.Adapter.class_adapter;
import com.sbitbd.alhelalattendance.Adapter.present_adapter;
import com.sbitbd.alhelalattendance.Model.attend_model;
import com.sbitbd.alhelalattendance.Model.class_model;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.attend_form.attend;
import com.sbitbd.alhelalattendance.ui.home.HomeViewModel;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private RecyclerView recyclerView;
//    private class_model class_model;
    private class_adapter class_adapter;
    private HomeViewModel homeViewModel = new HomeViewModel();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initveiw(root);
        return root;
    }
    private void initveiw(View root){
        try {
            recyclerView = root.findViewById(R.id.present_class_rec);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 2);
            recyclerView.setLayoutManager(manager);
            class_adapter = new class_adapter(root.getContext().getApplicationContext(),1);
            homeViewModel.get_class(root.getContext().getApplicationContext(),class_adapter);

            recyclerView.setAdapter(class_adapter);
        }catch (Exception e){
        }
    }
}