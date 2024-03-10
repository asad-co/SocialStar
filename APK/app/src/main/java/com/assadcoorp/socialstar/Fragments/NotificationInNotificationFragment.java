package com.assadcoorp.socialstar.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.NotificationAdapter;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationInNotificationFragment extends Fragment {
    ShimmerRecyclerView notifications_rv;
    ArrayList<NotificationDataType> notifications_list;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public NotificationInNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification_in_notification, container, false);
        notifications_rv=view.findViewById(R.id.notifications_rv_id);
        notifications_rv.showShimmerAdapter();
        notifications_list=new ArrayList<>();
        NotificationAdapter notificationAdapter=new NotificationAdapter(notifications_list,getContext());
        LinearLayoutManager notifications_linearlayoutmanager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notifications_rv.setLayoutManager(notifications_linearlayoutmanager);
        notifications_rv.setAdapter(notificationAdapter);

        database.getReference()
                .child("Notification").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications_list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            NotificationDataType notification=dataSnapshot.getValue(NotificationDataType.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            notifications_list.add(notification);

                        }
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return view;
    }
}