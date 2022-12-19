package com.sbitbd.alhelalacademy.ui.offline_attend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.sbitbd.alhelalacademy.Adapter.offline_adapter;
import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.ui.home.HomeViewModel;

public class offline_attend extends Fragment {

    private View root;
    private MaterialCardView not_found_card;
    private RecyclerView recyclerView;
    private offline_adapter offline_adapter;
    private HomeViewModel homeViewModel = new HomeViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_offline_attend, container, false);
        initview();
        return root;
    }

    private void initview(){
        try {
            not_found_card = root.findViewById(R.id.not_found_card);
            recyclerView = root.findViewById(R.id.offline_rec);

            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            GridLayoutManager manager = new GridLayoutManager(root.getContext().getApplicationContext(), 1);
            recyclerView.setLayoutManager(manager);
            offline_adapter = new offline_adapter(root.getContext().getApplicationContext());
            homeViewModel.get_offline_attend(root.getContext().getApplicationContext(),offline_adapter);
            recyclerView.setAdapter(offline_adapter);
            if (offline_adapter.getItemCount() == 0)
                not_found_card.setVisibility(View.VISIBLE);
        }catch (Exception e){
        }
    }
}