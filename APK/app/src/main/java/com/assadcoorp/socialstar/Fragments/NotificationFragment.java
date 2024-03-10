package com.assadcoorp.socialstar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.ViewPagerAdapter;
import com.assadcoorp.socialstar.R;
import com.google.android.material.tabs.TabLayout;

public class NotificationFragment extends Fragment {
    ViewPager notification_viewer;
    TabLayout tabs;

    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        notification_viewer=view.findViewById(R.id.notification_viewpager_id);
        tabs=view.findViewById(R.id.notification_tablayout_id);
        notification_viewer.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(notification_viewer);
        return view;
    }
}