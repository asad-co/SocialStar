package com.assadcoorp.socialstar.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.assadcoorp.socialstar.DataTypes.PostDataType;
import com.assadcoorp.socialstar.DataTypes.StoryDataType;
import com.assadcoorp.socialstar.Adapters.PostAdapter;
import com.assadcoorp.socialstar.Adapters.StoryAdapter;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.DataTypes.UserStoryDataType;
import com.assadcoorp.socialstar.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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
import java.util.Date;

public class HomeFragment extends Fragment {

    ShimmerRecyclerView post_rv, story_rv;
    ArrayList<StoryDataType> story_list;
    ArrayList<PostDataType> post_list = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;
    CardView add_story;
    ActivityResultLauncher<String> gallery_launcher;
    FirebaseStorage storage;
    ImageView add_story_img, profile_toolbar_pic;
    ProgressDialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        story_rv = view.findViewById(R.id.story_rv_id);
        story_rv.showShimmerAdapter();
        post_rv = view.findViewById(R.id.post_rv_id);
        post_rv.showShimmerAdapter();

        add_story = view.findViewById(R.id.home_add_story_cardview_id);
        add_story_img = view.findViewById(R.id.home_addstory_img_id);
        profile_toolbar_pic = view.findViewById(R.id.toolbar_profile_pic_img_id);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        getprofile();

        story_list = new ArrayList<>();
        StoryAdapter story_adapter = new StoryAdapter(story_list, getContext());
        LinearLayoutManager story_linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        story_rv.setLayoutManager(story_linearLayoutManager);
        story_rv.setNestedScrollingEnabled(false);

        database.getReference()
                .child("Stories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        story_list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            StoryDataType story = new StoryDataType();
                            story.setStoryby(dataSnapshot.getKey());
                            story.setStoryat(dataSnapshot.child("postedby").getValue(Long.class));
                            ArrayList<UserStoryDataType> stories = new ArrayList<>();
                            for (DataSnapshot snapshot1 : dataSnapshot.child("userstories").getChildren()) {
                                UserStoryDataType userstories = snapshot1.getValue(UserStoryDataType.class);
                                stories.add(userstories);
                            }
                            story.setStories(stories);
                            story_list.add(story);
                        }
                        story_rv.setAdapter(story_adapter);
                        story_rv.hideShimmerAdapter();
                        story_adapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        PostAdapter post_adapter = new PostAdapter(post_list, getContext());
        LinearLayoutManager post_linearLayoutManager = new LinearLayoutManager(getContext());
        post_rv.setLayoutManager(post_linearLayoutManager);
        post_rv.addItemDecoration(new DividerItemDecoration(post_rv.getContext(), DividerItemDecoration.VERTICAL));
        post_rv.setNestedScrollingEnabled(false);
        database.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
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

        add_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery_launcher.launch("image/*");
            }
        });
        gallery_launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    dialog.show();
                    add_story_img.setImageURI(result);
                    long time = new Date().getTime();
                    final StorageReference reference = storage.getReference()
                            .child("strories")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(time + "");
                    reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StoryDataType story = new StoryDataType();
                                    story.setStoryby(FirebaseAuth.getInstance().getUid());
                                    story.setStoryat(time);
                                    //Toast.makeText(getContext(), "hey bye", Toast.LENGTH_SHORT).show();
                                    database.getReference()
                                            .child("Stories").child(FirebaseAuth.getInstance().getUid())
                                            .child("postedby").setValue(story.getStoryat())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //Toast.makeText(getContext(), "hey day", Toast.LENGTH_SHORT).show();
                                                    UserStoryDataType userstories = new UserStoryDataType(uri.toString(), story.getStoryat());
                                                    database.getReference().child("Stories").child(FirebaseAuth.getInstance().getUid())
                                                            .child("userstories").child(time + "")
                                                            .setValue(userstories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
            }
        });
        return view;
    }

    private void getprofile() {
        //setting up display
        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDataType user_profile = snapshot.getValue(UserDataType.class);
                            Picasso.get().load(user_profile.getProfilephoto())
                                    .placeholder(R.drawable.profile_icon)
                                    .into(profile_toolbar_pic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}