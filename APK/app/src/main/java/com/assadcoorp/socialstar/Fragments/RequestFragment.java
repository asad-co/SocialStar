package com.assadcoorp.socialstar.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assadcoorp.socialstar.Adapters.RequestAdapter;
import com.assadcoorp.socialstar.Adapters.RequestAdapter;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.RequestDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestFragment extends Fragment {

    ShimmerRecyclerView request_rv;
    ArrayList<RequestDataType> requests_list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public RequestFragment() {
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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request, container, false);
        request_rv=view.findViewById(R.id.request_rv_id);
        request_rv.showShimmerAdapter();
        requests_list=new ArrayList<>();
        RequestAdapter RequestAdapter=new RequestAdapter(requests_list,getContext());
        LinearLayoutManager requests_linearlayoutmanager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        request_rv.setLayoutManager(requests_linearlayoutmanager);
        request_rv.setAdapter(RequestAdapter);

        database.getReference()
                .child("Requests").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests_list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            RequestDataType request=dataSnapshot.getValue(RequestDataType.class);
                            request.setRequestID(dataSnapshot.getKey());
                            requests_list.add(request);

                        }
                        RequestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return  view;
    }
}