package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.NotificationAdapter;
import com.assadcoorp.socialstar.Adapters.SearchAdapter;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.SearchDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fnf extends AppCompatActivity {
    ShimmerRecyclerView fnf_rv;
    ArrayList<UserDataType> user_list=new ArrayList<>();
    FirebaseAuth search_auth;
    FirebaseDatabase search_database;
    Toolbar toolbar;
    String type,userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnf);
        search_auth=FirebaseAuth.getInstance();
        search_database=FirebaseDatabase.getInstance();
        fnf_rv=findViewById(R.id.fnf_rv_id);
        toolbar=findViewById(R.id.toolbar_fnf_id);
        fnf_rv.showShimmerAdapter();
        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        userID=intent.getStringExtra("userId");
        String cap = type.substring(0, 1).toUpperCase() + type.substring(1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(cap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SearchAdapter searchAdapter=new SearchAdapter(getApplicationContext(),user_list);
        LinearLayoutManager fnf_linearlayoutmanager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        fnf_rv.setLayoutManager(fnf_linearlayoutmanager);
        fnf_rv.setAdapter(searchAdapter);

        search_database.getReference()
                .child("Users").child(userID)
                .child(type)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user_list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            String hisUid = "" + dataSnapshot.getRef().getKey();
                            getUsers(hisUid);
                        }


                        fnf_rv.setAdapter(searchAdapter);
                        fnf_rv.hideShimmerAdapter();
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
    private void getUsers(String hisUid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("userID").equalTo(hisUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UserDataType model = ds.getValue(UserDataType.class);
                    user_list.add(model);
                }
                SearchAdapter adapterUsers = new SearchAdapter(getApplicationContext(), user_list);
                fnf_rv.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}