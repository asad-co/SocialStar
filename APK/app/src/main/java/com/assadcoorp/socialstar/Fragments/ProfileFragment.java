package com.assadcoorp.socialstar.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assadcoorp.socialstar.Activities.AdvancedSettings;
import com.assadcoorp.socialstar.Activities.Fnf;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.Activities.UpdateProfile;
import com.assadcoorp.socialstar.DataTypes.FollowerDataType;
import com.assadcoorp.socialstar.Adapters.FollowersAdapter;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.LogIN;
import com.assadcoorp.socialstar.Activities.Posts;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    RecyclerView followers_rv;
    ArrayList<FollowerDataType> followers_list;
    ImageView cover_changing_img, cover_photo, profile_changing_img;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    TextView username, profession, about,followers,friends,posts;
    CircleImageView profile_pic;
    androidx.appcompat.widget.Toolbar profile_toolbar;
    CardView friends_cv,post_cv;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        setHasOptionsMenu(true);

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        followers_rv = view.findViewById(R.id.followers_rv_id);
        cover_changing_img = view.findViewById(R.id.profile_coverphoto_change_img_id);
        cover_photo = view.findViewById(R.id.profile_coverphoto_img_id);
        username = view.findViewById(R.id.profile_user_name_txt_id);
        profession = view.findViewById(R.id.profile_user_profession_txt_id);
        about = view.findViewById(R.id.profile_about_txt_id);
        profile_changing_img = view.findViewById(R.id.profile_profile_pic_change_img_id);
        profile_pic=view.findViewById(R.id.profile_profile_pic_img_id);
        followers=view.findViewById(R.id.profile_followers_num_txt_id);
        post_cv=view.findViewById(R.id.profile_post_cv_id);
        friends_cv=view.findViewById(R.id.profile_friends_cv_id);
        friends=view.findViewById(R.id.profile_friends_num_txt_id);
        posts=view.findViewById(R.id.profile_posts_num_txt_id);

        //setting up toolbar
        profile_toolbar= (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.myprofile_toolbar_id);
        ((AppCompatActivity)getActivity()).setSupportActionBar(profile_toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Profile");
        profile_toolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(), R.drawable.settings_24_icon));

        loadprofile(view);

        //setting up display
        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDataType user_profile = snapshot.getValue(UserDataType.class);
                            Picasso.get().load(user_profile.getCoverphoto()).placeholder(R.drawable.rain_drops_bg)
                                    .into(cover_photo);
                            Picasso.get().load(user_profile.getProfilephoto())
                                    .placeholder(R.drawable.profile_icon)
                                    .into(profile_pic);
                            followers.setText(user_profile.getFollowerCount()+"");
                            friends.setText(user_profile.getFriendCount()+"");
                            posts.setText(user_profile.getPostCount()+"");
                            if(username.getText().equals(null)){
                                username.setText(user_profile.getName());
                            }
                            if(profession.getText().equals(null)){
                                profession.setText(user_profile.getProfession());
                            }
                            if(about.getText().equals(null)){
                                about.setText(user_profile.getBio());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        followers_list = new ArrayList<>();
        FollowersAdapter followersAdapter = new FollowersAdapter(followers_list, getContext());
        LinearLayoutManager friends_linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        followers_rv.setLayoutManager(friends_linearLayoutManager);
        followers_rv.setAdapter(followersAdapter);
        database.getReference().child("Users")
                        .child(auth.getUid())
                                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followers_list.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            FollowerDataType follow=dataSnapshot.getValue(FollowerDataType.class);
                            followers_list.add(follow);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        cover_changing_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        profile_changing_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        post_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpost();
            }
        });

        friends_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsnfollowers("friends");
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            try {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    cover_photo.setImageURI(uri);

                    final StorageReference reference = storage.getReference().child("cover_photo").
                            child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Cover Photo Changed Successfully", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Users").child(auth.getUid())
                                            .child("coverphoto").setValue(uri.toString());

                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==1){
            try{
            if (data.getData() != null) {
                Uri uri = data.getData();
                profile_pic.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("profile_pic").
                        child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover Photo Changed Successfully", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("Users").child(auth.getUid())
                                        .child("profilephoto").setValue(uri.toString());

                            }
                        });
                    }
                });
            }
        }catch (Exception e) {
                e.printStackTrace();}
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_profile_settings_tabs,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_signout_menuitem_id:
                auth.signOut();
                startActivity(new Intent(getActivity(), LogIN.class));
                break;

            case R.id.profile_edit_menuitem_id:
                startActivity(new Intent(getActivity(), UpdateProfile.class));
                break;

            case R.id.profile_advanced_setting_menuitem_id:
                startActivity(new Intent(getActivity(), AdvancedSettings.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showpost() {
        Intent intent=new Intent(getContext(), Posts.class);
        intent.putExtra("userId",FirebaseAuth.getInstance().getUid());
        startActivity(intent);
    }

    private void friendsnfollowers(String type){
        Intent intent=new Intent(getActivity(), Fnf.class);
        intent.putExtra("userId",FirebaseAuth.getInstance().getUid());
        intent.putExtra("type",type);
        startActivity(intent);
    }

    private void loadprofile(View view)
    {
        String name,prof,stat;
        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("Current User Details",MODE_PRIVATE);
        name=sharedPreferences.getString("Name","");
        prof=sharedPreferences.getString("Profession","");
        stat=sharedPreferences.getString("Status","");
        username.setText(name);
        profession.setText(prof);
        about.setText(stat);

    }


}