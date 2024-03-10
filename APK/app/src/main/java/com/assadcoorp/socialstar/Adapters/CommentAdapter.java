package com.assadcoorp.socialstar.Adapters;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.Activities.Comment;
import com.assadcoorp.socialstar.Activities.ShowReactions;
import com.assadcoorp.socialstar.DataTypes.CommentDataType;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{
    private final String postID,postedby;
    Context context;
    ArrayList<CommentDataType> list;


    public CommentAdapter(Context context, ArrayList<CommentDataType> list, String postID,String postedby) {
        this.context = context;
        this.list = list;
        this.postID=postID;
        this.postedby=postedby;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_comment,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentDataType model=list.get(position);
        String time_ago = TimeAgo.using(model.getCommentedat());
        holder.comment.setText(model.getComment());
        holder.time.setText(time_ago);
        holder.like_t.setText(String.valueOf(model.getCommentlike()));
        holder.dislike_t.setText(String.valueOf(model.getCommentdislike()));

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getCommentedby()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user=snapshot.getValue(UserDataType.class);
                        Picasso.get().load(user.getProfilephoto())
                                .placeholder(R.drawable.profile_icon)
                                .into(holder.profile);
                        holder.name.setText(user.getName());
                        holder.profession.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(postID).child("usercomments").child(model.getCommentID())
                .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.like.setImageResource(R.drawable.thumb_up_active_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(postID).child("usercomments").child(model.getCommentID())
                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.dislike.setImageResource(R.drawable.thumb_down_active);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if already liked
                FirebaseDatabase.getInstance().getReference()
                        .child("Posts").child(postID)
                        .child("usercomments").child(model.getCommentID())
                        .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(postID)
                                                            .child("usercomments").child(model.getCommentID())
                                                            .child("commentlike").setValue(model.getCommentlike() - 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    //also adding samething in users directory of post
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(postedby)
                                                                            .child("posts").child(postID)
                                                                            .child("usercomments").child(model.getCommentID())
                                                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(postedby)
                                                                                            .child("posts").child(postID)
                                                                                            .child("usercomments").child(model.getCommentID())
                                                                                            .child("commentlike").setValue(model.getCommentlike() - 1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    holder.like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                    //holder.like_t.setText(String.valueOf(model.getPostlike()));

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
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(postID)
                                                            .child("usercomments").child(model.getCommentID())
                                                            .child("commentlike").setValue(model.getCommentlike() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    //samedoing in user catagory
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(postedby)
                                                                            .child("posts").child(postID)
                                                                            .child("usercomments").child(model.getCommentID())
                                                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(postedby)
                                                                                            .child("posts").child(postID)
                                                                                            .child("usercomments").child(model.getCommentID())
                                                                                            .child("commentlike").setValue(model.getCommentlike() + 1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    holder.like.setImageResource(R.drawable.thumb_up_active_icon);
                                                                                                    holder.dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                    //holder.like_t.setText(String.valueOf(model.getPostlike()));


                                                                                                    if(postedby.equals(FirebaseAuth.getInstance().getUid())){
                                                                                                        NotificationDataType notification=new NotificationDataType();
                                                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                        notification.setNotificationAt(new Date().getTime());
                                                                                                        notification.setPostID(postID);
                                                                                                        notification.setPostedBy(postedby);
                                                                                                        notification.setType("likePost");
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
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Posts").child(postID)
                                                                .child("usercomments").child(model.getCommentID())
                                                                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Posts").child(postID)
                                                                                .child("usercomments").child(model.getCommentID())
                                                                                .child("commentdislike").setValue(model.getCommentdislike() - 1)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        //user catagory work
                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Users").child(postedby)
                                                                                                .child("posts").child(postID)
                                                                                                .child("usercomments").child(model.getCommentID())
                                                                                                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child("Users").child(postedby)
                                                                                                                .child("posts").child(postID)
                                                                                                                .child("usercomments").child(model.getCommentID())
                                                                                                                .child("commentdislike").setValue(model.getCommentdislike() - 1)
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void unused) {
                                                                                                                        holder.dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                                        //holder.dis_t.setText(String.valueOf(model.getPostdislike()));

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

            }
        });
        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if already disliked
                FirebaseDatabase.getInstance().getReference()
                        .child("Posts").child(postID)
                        .child("usercomments").child(model.getCommentID())
                        .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(postID)
                                                            .child("usercomments").child(model.getCommentID())
                                                            .child("commentdislike").setValue(model.getCommentdislike()-1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    //user catagory work
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(postedby)
                                                                            .child("posts").child(postID)
                                                                            .child("usercomments").child(model.getCommentID())
                                                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(postedby)
                                                                                            .child("posts").child(postID)
                                                                                            .child("usercomments").child(model.getCommentID())
                                                                                            .child("commentdislike").setValue(model.getCommentdislike()-1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    holder.dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                    //holder.dis_t.setText(String.valueOf(model.getPostdislike()));

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
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(postID)
                                                            .child("usercomments").child(model.getCommentID())
                                                            .child("commentdislike").setValue(model.getCommentdislike() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    //user catagory work
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(postedby)
                                                                            .child("posts").child(postID)
                                                                            .child("usercomments").child(model.getCommentID())
                                                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(postedby)
                                                                                            .child("posts").child(postID)
                                                                                            .child("usercomments").child(model.getCommentID())
                                                                                            .child("commentdislike").setValue(model.getCommentdislike() + 1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    holder.dislike.setImageResource(R.drawable.thumb_down_active);
                                                                                                    holder.like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                    //holder.dis_t.setText(String.valueOf(model.getPostdislike()));
                                                                                                    if(postedby.equals(FirebaseAuth.getInstance().getUid())){
                                                                                                        NotificationDataType notification=new NotificationDataType();
                                                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                        notification.setNotificationAt(new Date().getTime());
                                                                                                        notification.setPostID(postID);
                                                                                                        notification.setPostedBy(postedby);
                                                                                                        notification.setType("dislikeComment");
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
                                            .child("Posts").child(postID)
                                            .child("usercomments").child(model.getCommentID())
                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Posts").child(postID)
                                                                .child("usercomments").child(model.getCommentID())
                                                                .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Posts").child(postID)
                                                                                .child("usercomments").child(model.getCommentID())
                                                                                .child("commentlike").setValue(model.getCommentlike() - 1)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        //user catagory work
                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Users").child(postedby)
                                                                                                .child("posts").child(postID)
                                                                                                .child("usercomments").child(model.getCommentID())
                                                                                                .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child("Users").child(postedby)
                                                                                                                .child("posts").child(postID)
                                                                                                                .child("usercomments").child(model.getCommentID())
                                                                                                                .child("commentlike").setValue(model.getCommentlike() - 1)
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void unused) {
                                                                                                                        holder.like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                                        //holder.like_t.setText(String.valueOf(model.getPostlike()));

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


            }
        });
        //visiting profile
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getCommentedby())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                        .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getCommentedby());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getCommentedby())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                        .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getCommentedby());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });


        holder.like_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ShowReactions.class);
                intent.putExtra("reason","comment")
                        .putExtra("reaction","like")
                        .putExtra("postID",postID)
                        .putExtra("postedbyID",postedby)
                        .putExtra("commentbyID",model.getCommentedby())
                        .putExtra("commentID",model.getCommentID());
                context.startActivity(intent);


            }
        });
        holder.dislike_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ShowReactions.class);
                intent.putExtra("reason","comment")
                        .putExtra("reaction","dislike")
                        .putExtra("postID",postID)
                        .putExtra("postedbyID",postedby)
                        .putExtra("commentbyID",model.getCommentedby())
                        .putExtra("commentID",model.getCommentID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile,like,dislike;
        TextView name,profession,comment,time,like_t,dislike_t;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profession=itemView.findViewById(R.id.comment_comment_profession_txt_id);
            name=itemView.findViewById(R.id.comment_comment_name_txt_id);
            comment=itemView.findViewById(R.id.comment_comment_usercomment_txt_id);
            profile=itemView.findViewById(R.id.comment_comment_profile_pic_img_id);
            time=itemView.findViewById(R.id.comment_comment_time_txt_id);
            like=itemView.findViewById(R.id.comment_comment_like_img_id);
            dislike=itemView.findViewById(R.id.comment_comment_dislike_img_id);
            like_t=itemView.findViewById(R.id.comment_comment_like_txt_id);
            dislike_t=itemView.findViewById(R.id.comment_comment_dislike_txt_id);
        }
    }
}
