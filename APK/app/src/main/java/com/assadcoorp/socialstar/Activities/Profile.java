package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assadcoorp.socialstar.DataTypes.FollowerDataType;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.RequestDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class Profile extends AppCompatActivity {
    String name, profession, userID;
    TextView user_name, user_profession, user_bio, user_friends, user_followers, user_posts, friendtxt, followtxt;
    CardView post, chat, follow, add_F, friends, followers;
    ImageView user_profile, user_cover, friendimg, followimg;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Toolbar toolbar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        name = intent.getStringExtra("Name");
        profession = intent.getStringExtra("Profession");
        userID = intent.getStringExtra("UserID");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        //findviewbyid
        user_name = findViewById(R.id.selected_profile_user_name_txt_id);
        user_profession = findViewById(R.id.selected_profile_user_profession_txt_id);
        user_bio = findViewById(R.id.selected_profile_about_txt_id);
        user_friends = findViewById(R.id.selected_profile_friends_num_txt_id);
        user_followers = findViewById(R.id.selected_profile_followers_num_txt_id);
        user_posts = findViewById(R.id.selected_profile_posts_num_txt_id);
        post = findViewById(R.id.selected_profile_posts_cv_id);
        chat = findViewById(R.id.selected_profile_chat_cv_id);
        follow = findViewById(R.id.selected_profile_follow_cv_id);
        add_F = findViewById(R.id.selected_profile_addfriends_cv_id);
        friends = findViewById(R.id.selected_profile_friends_cv_id);
        followers = findViewById(R.id.selected_profile_followers_cv_id);
        user_profile = findViewById(R.id.selected_profile_profile_pic_img_id);
        user_cover = findViewById(R.id.selected_profile_coverphoto_img_id);
        friendimg = findViewById(R.id.selected_friend_cv_img_id);
        friendtxt = findViewById(R.id.selected_friend_cv_txt_id);
        followimg = findViewById(R.id.selected_follow_cv_img_id);
        followtxt = findViewById(R.id.selected_follow_cv_txt_id);
        linearLayout=findViewById(R.id.linear_layout);
        toolbar=findViewById(R.id.toolbar_selected);
        //setting up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //settingupdisplay
        update_userinfo();
        if(FirebaseAuth.getInstance().getUid().equals(userID)){
            add_F.setVisibility(View.GONE);
            follow.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
        else{
        update_friendcv();
        update_followcv();
        }

        //click listeners
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpost();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followuser();
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsnfollowers("followers");
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsnfollowers("friends");
            }
        });
        add_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addfriend();
            }
        });


    }

    private void followuser() {

        FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType model=snapshot.getValue(UserDataType.class);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(userID)
                                        .child("followers")
                                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(userID)
                                                            .child("followers")
                                                            .child(FirebaseAuth.getInstance().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users")
                                                                            .child(userID)
                                                                            .child("followerCount")
                                                                            .setValue(model.getFollowerCount() - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    update_followcv();
                                                                                    Toast.makeText(getApplicationContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                } else {
                                                    FollowerDataType follow = new FollowerDataType();
                                                    follow.setFollowedby(FirebaseAuth.getInstance().getUid());
                                                    follow.setTime(new Date().getTime());
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(userID)
                                                            .child("followers")
                                                            .child(FirebaseAuth.getInstance().getUid())
                                                            .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    if (model.getFollowerCount() > 0) {

                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Users")
                                                                                .child(userID)
                                                                                .child("followerCount")
                                                                                .setValue(model.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @SuppressLint("NewApi")
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        update_followcv();
                                                                                        Toast.makeText(getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
                                                                                        NotificationDataType notification = new NotificationDataType();
                                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                        notification.setNotificationAt(new Date().getTime());
                                                                                        notification.setType("follow");
                                                                                        notification.setCheckOpen(false);

                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Notification").child(userID)
                                                                                                .push().setValue(notification);


                                                                                    }
                                                                                });
                                                                    } else {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Users")
                                                                                .child(userID)
                                                                                .child("followerCount")
                                                                                .setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @SuppressLint("NewApi")
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        update_followcv();
                                                                                        Toast.makeText(getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
                                                                                        NotificationDataType notification = new NotificationDataType();
                                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                        notification.setNotificationAt(new Date().getTime());
                                                                                        notification.setType("follow");
                                                                                        notification.setCheckOpen(false);

                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Notification").child(userID)
                                                                                                .push().setValue(notification);
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


    }

    private  void  addfriend(){
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID)
                .child("friends")
                .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    alertunfriend();

                                }
                                else{
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users").child(userID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    UserDataType model=snapshot.getValue(UserDataType.class);
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Requests")
                                                            .child(userID).child(FirebaseAuth.getInstance().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Requests")
                                                                                .child(userID).child(FirebaseAuth.getInstance().getUid())
                                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        update_friendcv();
                                                                                        Toast.makeText(getApplicationContext(), "Request Unsent", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                    else {
                                                                        RequestDataType request = new RequestDataType();
                                                                        request.setSentAt(new Date().getTime());
                                                                        request.setSentbyID(FirebaseAuth.getInstance().getUid());
                                                                        request.setSenttoID(userID);

                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Requests")
                                                                                .child(userID).child(FirebaseAuth.getInstance().getUid())
                                                                                .setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        update_friendcv();
                                                                                        Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




    }

    private void alertunfriend(){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to unfirend this user?");

        // Set Alert Title
        builder.setTitle("Unfriend");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will perform action
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userID)
                    .child("friends")
                    .child(FirebaseAuth.getInstance().getUid())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("friends")
                                    .child(userID).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    update_friendcv();
                                                    Toast.makeText(Profile.this, "Unfriend", Toast.LENGTH_SHORT).show();

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users").child(userID)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    UserDataType user_friend=snapshot.getValue(UserDataType.class);
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(userID).child("friendCount")
                                                                            .setValue(user_friend.getFriendCount()-1)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(FirebaseAuth.getInstance().getUid())
                                                                                            .child("friendCount").setValue(user_friend.getFriendCount()-1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    NotificationDataType notification = new NotificationDataType();
                                                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                    notification.setNotificationAt(new Date().getTime());
                                                                                                    notification.setType("unfriend");
                                                                                                    notification.setCheckOpen(false);

                                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                                            .child("Notification").child(userID)
                                                                                                            .push().setValue(notification);
                                                                                                }
                                                                                            });
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });


                                                }
                                            });


                        }
                    });
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });



        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void showpost() {
        Intent intent=new Intent(Profile.this,Posts.class);
        intent.putExtra("userId",userID);
        startActivity(intent);
    }

    private void friendsnfollowers(String type){
        Intent intent=new Intent(Profile.this,Fnf.class);
        intent.putExtra("userId",userID);
        intent.putExtra("type",type);
        startActivity(intent);
    }

    public void update_userinfo() {
        user_name.setText(name);
        user_profession.setText(profession);
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user = snapshot.getValue(UserDataType.class);
                        Picasso.get().load(user.getProfilephoto())
                                .placeholder(R.drawable.profile_icon)
                                .into(user_profile);
                        Picasso.get().load(user.getCoverphoto())
                                .placeholder(R.drawable.rain_drops_bg)
                                .into(user_cover);
                        user_friends.setText(user.getFriendCount()+"");
                        user_followers.setText(user.getFollowerCount()+"");
                        user_posts.setText(user.getPostCount()+"");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void update_friendcv() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userID)
                .child("friends")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            friendimg.setImageResource(R.drawable.added_icon);
                            friendtxt.setText("Friend Added");
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Requests")
                                    .child(userID)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("NewApi")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                friendtxt.setText("Request Sent");
                                                friendimg.setImageResource(R.drawable.add_friend_icon);
                                                friendimg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                                                        PorterDuff.Mode.SRC_IN);
                                                friendimg.setBackgroundResource(R.color.grey);
                                            } else {
                                                friendtxt.setText("Add Friend");
                                                friendimg.setImageResource(R.drawable.add_friend_icon);
                                                friendimg.setBackgroundResource(R.color.light_blue);
                                                friendimg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                                                        PorterDuff.Mode.SRC_IN);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void update_followcv() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userID)
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"NewApi", "ResourceAsColor"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            followtxt.setText("Followed");
                            followimg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                                    android.graphics.PorterDuff.Mode.SRC_IN);
                            followimg.setBackgroundResource(R.color.grey);
                        } else {
                            followtxt.setText("Follow");
                            followimg.setBackgroundResource(R.color.light_blue);
                            followimg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                                    android.graphics.PorterDuff.Mode.SRC_IN);
                        }
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