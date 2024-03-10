package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.assadcoorp.socialstar.Adapters.PostAdapter;
import com.assadcoorp.socialstar.DataTypes.PostDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Posts extends AppCompatActivity {

    String userID;
    ShimmerRecyclerView post_rv;
    Toolbar toolbar;
    FirebaseAuth auth;
    ArrayList<PostDataType> post_list = new ArrayList<>();
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        post_rv=findViewById(R.id.selected_user_post_rv_id);
        post_rv.showShimmerAdapter();
        toolbar=findViewById(R.id.post_toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        Intent intent=getIntent();
        userID=intent.getStringExtra("userId");






        PostAdapter post_adapter = new PostAdapter(post_list, getApplicationContext());
        LinearLayoutManager post_linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        post_rv.setLayoutManager(post_linearLayoutManager);
        post_rv.addItemDecoration(new DividerItemDecoration(post_rv.getContext(), DividerItemDecoration.VERTICAL));
        post_rv.setNestedScrollingEnabled(false);
        database.getReference().child("Users").child(userID).child("posts")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post_list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostDataType post = dataSnapshot.getValue(PostDataType.class);
                    post_list.add(post);
                }
                post_rv.setAdapter(post_adapter);
                post_rv.hideShimmerAdapter();
                post_adapter.notifyDataSetChanged();
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
}