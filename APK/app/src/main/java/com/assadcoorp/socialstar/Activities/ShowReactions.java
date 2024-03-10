package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.SearchAdapter;
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

public class ShowReactions extends AppCompatActivity {
    String reason,reaction,postID,postedbyID,commentbyID,commentID,folder_react;
    Toolbar toolbar;
    ShimmerRecyclerView react_rv;
    ArrayList<UserDataType> user_list=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reactions);
        react_rv=findViewById(R.id.reactions_rv_id);
        Intent intent=getIntent();
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        reason=intent.getStringExtra("reason");
        reaction=intent.getStringExtra("reaction");
        postID=intent.getStringExtra("postID");
        postedbyID=intent.getStringExtra("postedbyID");
        if(reason.equals("comment")){
            commentbyID=intent.getStringExtra("commentbyID");
            commentID=intent.getStringExtra("commentID");
        }
        toolbar=findViewById(R.id.toolbar_reactions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String cap = reason.substring(0, 1).toUpperCase() + reason.substring(1)+"'s "+reaction+"s";
        getSupportActionBar().setTitle(cap);

        SearchAdapter searchAdapter=new SearchAdapter(getApplicationContext(),user_list);
        LinearLayoutManager react_linearlayoutmanager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        react_rv.setLayoutManager(react_linearlayoutmanager);
        react_rv.setAdapter(searchAdapter);


        if(reason.equals("post")) {
            if(reaction.equals("like")){
                 folder_react="userlikes";
            }
            else{
                 folder_react="userdislikes";
            }

            database.getReference()
                    .child("Posts").child(postID)
                    .child(folder_react)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String hisUid = "" + dataSnapshot.getRef().getKey();
                                getUsers(hisUid);
                            }
                            react_rv.setAdapter(searchAdapter);
                            react_rv.hideShimmerAdapter();
                            searchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else if(reason.equals("comment")){
            if(reaction.equals("like")){
                folder_react="userlikes";
            }
            else{
                folder_react="userdislikes";
            }

            database.getReference()
                    .child("Posts").child(postID)
                    .child("usercomments").child(commentID)
                    .child(folder_react)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String hisUid = "" + dataSnapshot.getRef().getKey();
                                getUsers(hisUid);
                            }


                            react_rv.setAdapter(searchAdapter);
                            react_rv.hideShimmerAdapter();
                            searchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

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
                react_rv.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}