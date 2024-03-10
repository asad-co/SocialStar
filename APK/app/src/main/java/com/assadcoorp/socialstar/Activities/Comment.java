package com.assadcoorp.socialstar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assadcoorp.socialstar.Adapters.CommentAdapter;
import com.assadcoorp.socialstar.DataTypes.CommentDataType;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.PostDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class Comment extends AppCompatActivity {
    ImageView like,dislike,share,send,user_pic,post_img;
    TextView like_t,dislike_t,comment_t,share_t,user_name,user_profession,post_descrip;
    EditText comment_edit;
    RecyclerView comments_rv;
    String post_ID,postedby;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<CommentDataType> comments_list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        like=findViewById(R.id.comment_like_img_id);
        dislike=findViewById(R.id.comment_dislike_img_id);
        share=findViewById(R.id.comment_share_img_id);
        send=findViewById(R.id.comment_commentsend_img_id);
        user_pic=findViewById(R.id.comment_profile_pic_img_id);
        post_img=findViewById(R.id.comment_postimage_img_id);
        like_t=findViewById(R.id.comment_like_txt_id);
        dislike_t=findViewById(R.id.comment_dislike_txt_id);
        comment_t=findViewById(R.id.comment_comment_txt_id);
        share_t=findViewById(R.id.comment_share_txt_id);
        user_name=findViewById(R.id.comment_user_postname_txt_id);
        user_profession=findViewById(R.id.comment_user_profession_txt_id);
        post_descrip=findViewById(R.id.comment_postdescrip_txt_id);
        comment_edit=findViewById(R.id.comment_entercomment_edit_id);
        comments_rv=findViewById(R.id.comments_rv_id);
        toolbar=(androidx.appcompat.widget.Toolbar)findViewById(R.id.comment_toolbar_id);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        Intent intent=getIntent();
        post_ID=intent.getStringExtra("postid");
        postedby=intent.getStringExtra("postedbyID");




        database.getReference().child("Posts")
                .child(post_ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostDataType post=snapshot.getValue(PostDataType.class);
                        if(post.getPostimage()!=null){
                        Picasso.get().load(post.getPostimage()).placeholder(R.drawable.rain_drops_bg)
                                .into(post_img);}
                        else{
                            post_img.setVisibility(View.GONE);
                        }
                        post_descrip.setText(String.valueOf(post.getPostdescription()));
                        comment_t.setText(String.valueOf(post.getComment()));
                        like_t.setText(String.valueOf(post.getPostlike()));
                        dislike_t.setText(String.valueOf(post.getPostdislike()));
                        share_t.setText(String.valueOf(post.getShare()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users")
                .child(postedby).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user_6=snapshot.getValue(UserDataType.class);
                        Picasso.get().load(user_6.getProfilephoto()).placeholder(R.drawable.profile_icon)
                                .into(user_pic);
                        user_name.setText(user_6.getName());
                        user_profession.setText(user_6.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!comment_edit.getText().toString().isEmpty()) {

                    CommentDataType comment = new CommentDataType();
                    comment.setComment(comment_edit.getText().toString());
                    String time=String.valueOf(new Date().getTime());
                    comment.setCommentedat(new Date().getTime());
                    comment.setCommentedby(FirebaseAuth.getInstance().getUid());
                    comment.setCommentlike(0);
                    comment.setCommentdislike(0);
                    comment.setCommentID(time);
                    database.getReference().child("Posts")
                            .child(post_ID).child("usercomments")
                            .child(time)
                            .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                boolean send_flag=true;
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference().child("Posts")
                                            .child(post_ID).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    PostDataType post=snapshot.getValue(PostDataType.class);
                                                    if(send_flag){
                                                    send_flag=false;
                                                    database.getReference().child("Posts")
                                                            .child(post_ID).child("comment")
                                                            .setValue(post.getComment() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getReference().child("Users")
                                                                            .child(postedby).child("posts")
                                                                            .child(post_ID).child("usercomments")
                                                                            .child(time)
                                                                            .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    database.getReference().child("Users")
                                                                                            .child(postedby).child("posts")
                                                                                            .child(post_ID).child("comment")
                                                                                            .setValue(post.getComment() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Toast.makeText(Comment.this, "Commented", Toast.LENGTH_SHORT).show();
                                                                                                    comment_edit.setText("");

                                                                                                    if(postedby.equals(FirebaseAuth.getInstance().getUid())){
                                                                                                    NotificationDataType notification=new NotificationDataType();
                                                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                    notification.setNotificationAt(new Date().getTime());
                                                                                                    notification.setPostID(post_ID);
                                                                                                    notification.setPostedBy(postedby);
                                                                                                    notification.setType("comment");
                                                                                                    notification.setCheckOpen(false);
                                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                                            .child("Notification").child(postedby)
                                                                                                            .push().setValue(notification);}

                                                                                                }
                                                                                            });
                                                                                }
                                                                            });

                                                                }
                                                            });

                                                }}

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });
                }
            }
        });
        comments_list=new ArrayList<>();
        CommentAdapter comment_adapter=new CommentAdapter(this,comments_list,post_ID,postedby);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        comments_rv.setLayoutManager(linearLayoutManager);
        comments_rv.setAdapter(comment_adapter);

        database.getReference().child("Posts").child(post_ID)
                .child("usercomments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments_list.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            CommentDataType comment_list_data=dataSnapshot.getValue(CommentDataType.class);
                            comments_list.add(comment_list_data);
                        }
                        comment_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //taking access to folder
                FirebaseDatabase.getInstance().getReference()
                                .child("Posts").child(post_ID)
                                .addValueEventListener(new ValueEventListener() {
                                    boolean like_flag=true;
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //check if already liked
                                        PostDataType post=snapshot.getValue(PostDataType.class);
                                        if(like_flag){
                                            like_flag=false;
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Posts").child(post_ID)
                                                .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        if(snapshot.exists()){
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Posts").child(post_ID)
                                                                    .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(Comment.this, String.valueOf(post.getPostlike()), Toast.LENGTH_SHORT).show();
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("Posts").child(post_ID)
                                                                                    .child("postlike").setValue(post.getPostlike()-1)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            //also adding samething in users directory of post
                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                    .child("Users").child(postedby)
                                                                                                    .child("posts").child(post_ID)
                                                                                                    .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                                    .child("Users").child(postedby)
                                                                                                                    .child("posts").child(post.getPostID())
                                                                                                                    .child("postlike").setValue(post.getPostlike()-1)
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void unused) {
                                                                                                                            like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                                            //holder.like_t.setText(String.valueOf(post.getPostlike()));

                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                        else{
                                                            //like button increase in like
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Posts").child(post_ID)
                                                                    .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("Posts").child(post_ID)
                                                                                    .child("postlike").setValue(post.getPostlike() + 1)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {

                                                                                            //samedoing in user catagory
                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                    .child("Users").child(postedby)
                                                                                                    .child("posts").child(post_ID)
                                                                                                    .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                                    .child("Users").child(postedby)
                                                                                                                    .child("posts").child(post_ID)
                                                                                                                    .child("postlike").setValue(post.getPostlike()+1)
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void unused) {
                                                                                                                            like.setImageResource(R.drawable.thumb_up_active_icon);
                                                                                                                            dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                                            //holder.like_t.setText(String.valueOf(post.getPostlike()));
                                                                                                                            if(postedby.equals(FirebaseAuth.getInstance().getUid())){
                                                                                                                                NotificationDataType notification=new NotificationDataType();
                                                                                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                                                notification.setNotificationAt(new Date().getTime());
                                                                                                                                notification.setPostID(post_ID);
                                                                                                                                notification.setPostedBy(postedby);
                                                                                                                                notification.setType("comment");
                                                                                                                                notification.setCheckOpen(false);
                                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                                        .child("Notification").child(postedby)
                                                                                                                                        .push().setValue(notification);}

                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                            //upper code ends here

                                                            //dislike ending
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Posts").child(post_ID)
                                                                    .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.exists()){
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Posts").child(post_ID)
                                                                                        .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                        .child("Posts").child(post_ID)
                                                                                                        .child("postdislike").setValue(post.getPostdislike() - 1)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void unused) {
                                                                                                                //user catagory work
                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                        .child("Users").child(postedby)
                                                                                                                        .child("posts").child(post_ID)
                                                                                                                        .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void unused) {
                                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                                        .child("Users").child(postedby)
                                                                                                                                        .child("posts").child(post_ID)
                                                                                                                                        .child("postdislike").setValue(post.getPostdislike() - 1)
                                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onSuccess(Void unused) {
                                                                                                                                                dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                                                                //holder.dis_t.setText(String.valueOf(post.getPostdislike()));

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

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                            //upper code ends here
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }}

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               FirebaseDatabase.getInstance().getReference()
                       .child("Posts").child(post_ID)
                       .addValueEventListener(new ValueEventListener() {
                           boolean dislike_flag=true;
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               PostDataType post=snapshot.getValue(PostDataType.class);
                               if(dislike_flag){
                                   dislike_flag=false;
                               //check if already disliked
                               FirebaseDatabase.getInstance().getReference()
                                       .child("Posts").child(post_ID)
                                       .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {

                                               if(snapshot.exists()){
                                                   FirebaseDatabase.getInstance().getReference()
                                                           .child("Posts").child(post_ID)
                                                           .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                           .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                               @Override
                                                               public void onSuccess(Void unused) {
                                                                   FirebaseDatabase.getInstance().getReference()
                                                                           .child("Posts").child(post_ID)
                                                                           .child("postdislike").setValue(post.getPostdislike()-1)
                                                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                               @Override
                                                                               public void onSuccess(Void unused) {
                                                                                   //user catagory work
                                                                                   FirebaseDatabase.getInstance().getReference()
                                                                                           .child("Users").child(postedby)
                                                                                           .child("posts").child(post_ID)
                                                                                           .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                           .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                               @Override
                                                                                               public void onSuccess(Void unused) {
                                                                                                   FirebaseDatabase.getInstance().getReference()
                                                                                                           .child("Users").child(postedby)
                                                                                                           .child("posts").child(post_ID)
                                                                                                           .child("postdislike").setValue(post.getPostdislike()-1)
                                                                                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                               @Override
                                                                                                               public void onSuccess(Void unused) {
                                                                                                                   dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                                   //holder.dis_t.setText(String.valueOf(post.getPostdislike()));

                                                                                                               }
                                                                                                           });
                                                                                               }
                                                                                           });
                                                                               }
                                                                           });
                                                               }
                                                           });
                                               }
                                               else{
                                                   //dislike button increase in dislike
                                                   FirebaseDatabase.getInstance().getReference()
                                                           .child("Posts").child(post_ID)
                                                           .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                           .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                               @Override
                                                               public void onSuccess(Void unused) {
                                                                   FirebaseDatabase.getInstance().getReference()
                                                                           .child("Posts").child(post_ID)
                                                                           .child("postdislike").setValue(post.getPostdislike() + 1)
                                                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                               @Override
                                                                               public void onSuccess(Void unused) {

                                                                                   //user catagory work
                                                                                   database.getReference()
                                                                                           .child("Users").child(postedby)
                                                                                           .child("posts").child(post_ID)
                                                                                           .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                           .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                               @Override
                                                                                               public void onSuccess(Void unused) {
                                                                                                   FirebaseDatabase.getInstance().getReference()
                                                                                                           .child("Users").child(postedby)
                                                                                                           .child("posts").child(post_ID)
                                                                                                           .child("postdislike").setValue(post.getPostdislike() + 1)
                                                                                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                               @Override
                                                                                                               public void onSuccess(Void unused) {
                                                                                                                   dislike.setImageResource(R.drawable.thumb_down_active);
                                                                                                                   like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                                   //holder.dis_t.setText(String.valueOf(post.getPostdislike()));
                                                                                                                   if(postedby.equals(FirebaseAuth.getInstance().getUid())){
                                                                                                                       NotificationDataType notification=new NotificationDataType();
                                                                                                                       notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                                       notification.setNotificationAt(new Date().getTime());
                                                                                                                       notification.setPostID(post_ID);
                                                                                                                       notification.setPostedBy(postedby);
                                                                                                                       notification.setType("dislikePost");
                                                                                                                       notification.setCheckOpen(false);
                                                                                                                       FirebaseDatabase.getInstance().getReference()
                                                                                                                               .child("Notification").child(postedby)
                                                                                                                               .push().setValue(notification);}

                                                                                                               }
                                                                                                           });
                                                                                               }
                                                                                           });
                                                                               }
                                                                           });
                                                               }
                                                           });
                                                   //code ends here for dislike

                                                   //dislike button also cause the current like to be removed
                                                   FirebaseDatabase.getInstance().getReference()
                                                           .child("Posts").child(post_ID)
                                                           .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                   if(snapshot.exists()){
                                                                       FirebaseDatabase.getInstance().getReference()
                                                                               .child("Posts").child(post_ID)
                                                                               .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                               .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                   @Override
                                                                                   public void onSuccess(Void unused) {
                                                                                       FirebaseDatabase.getInstance().getReference()
                                                                                               .child("Posts").child(post_ID)
                                                                                               .child("postlike").setValue(post.getPostlike() - 1)
                                                                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                   @Override
                                                                                                   public void onSuccess(Void unused) {

                                                                                                       //user catagory work
                                                                                                       FirebaseDatabase.getInstance().getReference()
                                                                                                               .child("Users").child(postedby)
                                                                                                               .child("posts").child(post_ID)
                                                                                                               .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                               .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                   @Override
                                                                                                                   public void onSuccess(Void unused) {
                                                                                                                       FirebaseDatabase.getInstance().getReference()
                                                                                                                               .child("Users").child(postedby)
                                                                                                                               .child("posts").child(post_ID)
                                                                                                                               .child("postlike").setValue(post.getPostlike() - 1)
                                                                                                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                   @Override
                                                                                                                                   public void onSuccess(Void unused) {
                                                                                                                                       like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                                                       //holder.like_t.setText(String.valueOf(post.getPostlike()));

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

                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError error) {

                                                               }
                                                           });
                                                   //code ends here for like effect from dislike

                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

                                           }
                                       });
                           }}

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });


            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertshare(post_ID,postedby);
            }
        });

        //setting up icon color
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(post_ID).child("userlikes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                           like.setImageResource(R.drawable.thumb_up_active_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(post_ID).child("userdislikes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dislike.setImageResource(R.drawable.thumb_down_active);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //visiting profile
       user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(postedby)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(Comment.this, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",postedby);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(postedby)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(Comment.this, Profile.class);
                                intent.putExtra("Name",user.getName())
                                       .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",postedby);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        like_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Comment.this,ShowReactions.class);
                intent.putExtra("reason","post")
                        .putExtra("reaction","like")
                        .putExtra("postID",post_ID)
                        .putExtra("postedbyID",postedby);
                startActivity(intent);


            }
        });
        dislike_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Comment.this,ShowReactions.class);
                intent.putExtra("reason","post")
                        .putExtra("reaction","dislike")
                        .putExtra("postID",post_ID)
                        .putExtra("postedbyID",postedby);
                startActivity(intent);
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

    public void sharepost(String post_ID,String posted_by){
        FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(post_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostDataType model=snapshot.getValue(PostDataType.class);
                        PostDataType share_post=new PostDataType();
                        String time= String.valueOf(new Date().getTime());
                        share_post.setShare(0);
                        share_post.setPostID(time);
                        share_post.setPostimage(String.valueOf(model.getPostimage()));
                        share_post.setPostedbyID(FirebaseAuth.getInstance().getUid());
                        share_post.setPostdescription(String.valueOf(Html.fromHtml("<b>"+user_name.getText()+" originally shared this post"+"</b>"+ model.getPostdescription())));
                        share_post.setPostedat(Long.parseLong(time));
                        share_post.setPostlike(0);
                        share_post.setPostdislike(0);
                        share_post.setComment(0);
                        share_post.setShare(0);
                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                .child(time).setValue(share_post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                .child("posts").child(time).setValue(share_post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        UserDataType user=snapshot.getValue(UserDataType.class);
                                                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                                .child("postCount").setValue(user.getPostCount()+1)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Posts").child(model.getPostID())
                                                                                                .child("share").setValue(model.getShare()+1)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child("Users").child(model.getPostedbyID())
                                                                                                                .child("posts").child(model.getPostID())
                                                                                                                .child("share").setValue(model.getShare()+1);
                                                                                                        Toast.makeText(getApplicationContext(), "Shared", Toast.LENGTH_SHORT).show();
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void alertshare(String post_ID,String posted_by){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to share this post?");

        // Set Alert Title
        builder.setTitle("Share Post");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            sharepost(post_ID,posted_by);

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
}