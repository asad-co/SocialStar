package com.assadcoorp.socialstar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.Activities.Comment;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.PostDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    ArrayList<PostDataType> list;
    Context context;

    public PostAdapter(ArrayList<PostDataType> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post, parent, false);
        return new viewHolder(view);
    }
   
    
    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostDataType model = list.get(position);
        
        
        if (model.getPostimage() != null) {
            Picasso.get().load(model.getPostimage()).placeholder(R.drawable.rain_drops_bg)
                    .into(holder.post);
        } else {
            holder.post.setVisibility(View.GONE);
        }
        if (!model.getPostdescription().isEmpty()) {
            holder.descrip.setText(model.getPostdescription());
        } else {
            holder.descrip.setVisibility(View.GONE);
        }
        holder.like_t.setText(String.valueOf(model.getPostlike()));
        holder.dis_t.setText(String.valueOf(model.getPostdislike()));
        holder.com_t.setText(String.valueOf(model.getComment()));
        holder.share_t.setText(String.valueOf(model.getShare()));
        //setting up icon color
        holder.comment.setImageResource(R.drawable.comment_icon);
        holder.share.setImageResource(R.drawable.share_icon);
        FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(model.getPostID()).child("userlikes")
                        .child(FirebaseAuth.getInstance().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            holder.like.setImageResource(R.drawable.thumb_up_active_icon);
                                        }
                                        else {
                                            holder.like.setImageResource(R.drawable.thumb_up_icon);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(model.getPostID()).child("userdislikes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.dislike.setImageResource(R.drawable.thumb_down_active);
                        }
                        else {
                            holder.dislike.setImageResource(R.drawable.thumb_down_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedbyID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user = snapshot.getValue(UserDataType.class);
                        Picasso.get().load(user.getProfilephoto()).placeholder(R.drawable.profile_icon)
                                .into(holder.profile);
                        holder.name.setText(user.getName());
                        holder.about.setText(user.getProfession());
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
                        .child("Posts").child(model.getPostID())
                        .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts").child(model.getPostID())
                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(model.getPostID())
                                                            .child("postlike").setValue(model.getPostlike()-1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    //also adding samething in users directory of post
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(model.getPostedbyID())
                                                                            .child("posts").child(model.getPostID())
                                                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(model.getPostedbyID())
                                                                                            .child("posts").child(model.getPostID())
                                                                                            .child("postlike").setValue(model.getPostlike()-1)
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
                                            .child("Posts").child(model.getPostID())
                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts").child(model.getPostID())
                                                            .child("postlike").setValue(model.getPostlike() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    //samedoing in user catagory
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Users").child(model.getPostedbyID())
                                                                            .child("posts").child(model.getPostID())
                                                                            .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("Users").child(model.getPostedbyID())
                                                                                            .child("posts").child(model.getPostID())
                                                                                            .child("postlike").setValue(model.getPostlike()+1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    holder.like.setImageResource(R.drawable.thumb_up_active_icon);
                                                                                                    holder.dislike.setImageResource(R.drawable.thumb_down_icon);
                                                                                                    //holder.like_t.setText(String.valueOf(model.getPostlike()));
                                                                                                    if(!model.getPostedbyID().equals(FirebaseAuth.getInstance().getUid())){
                                                                                                        NotificationDataType notification=new NotificationDataType();
                                                                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                        notification.setNotificationAt(new Date().getTime());
                                                                                                        notification.setPostID(model.getPostID());
                                                                                                        notification.setPostedBy(model.getPostedbyID());
                                                                                                        notification.setType("likePost");
                                                                                                        notification.setCheckOpen(false);
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child("Notification").child(model.getPostedbyID())
                                                                                                                .push().setValue(notification);
                                                                                                    }
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
                                            .child("Posts").child(model.getPostID())
                                            .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Posts").child(model.getPostID())
                                                                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("Posts").child(model.getPostID())
                                                                                .child("postdislike").setValue(model.getPostdislike() - 1)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        //user catagory work
                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("Users").child(model.getPostedbyID())
                                                                                                .child("posts").child(model.getPostID())
                                                                                                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {
                                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                                .child("Users").child(model.getPostedbyID())
                                                                                                                .child("posts").child(model.getPostID())
                                                                                                                .child("postdislike").setValue(model.getPostdislike() - 1)
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
                                .child("Posts").child(model.getPostID())
                                .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Posts").child(model.getPostID())
                                                    .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Posts").child(model.getPostID())
                                                                    .child("postdislike").setValue(model.getPostdislike()-1)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            //user catagory work
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("Users").child(model.getPostedbyID())
                                                                                    .child("posts").child(model.getPostID())
                                                                                    .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                    .child("Users").child(model.getPostedbyID())
                                                                                                    .child("posts").child(model.getPostID())
                                                                                                    .child("postdislike").setValue(model.getPostdislike()-1)
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
                                                    .child("Posts").child(model.getPostID())
                                                    .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Posts").child(model.getPostID())
                                                                    .child("postdislike").setValue(model.getPostdislike() + 1)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            //user catagory work
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("Users").child(model.getPostedbyID())
                                                                                    .child("posts").child(model.getPostID())
                                                                                    .child("userdislikes").child(FirebaseAuth.getInstance().getUid())
                                                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                    .child("Users").child(model.getPostedbyID())
                                                                                                    .child("posts").child(model.getPostID())
                                                                                                    .child("postdislike").setValue(model.getPostdislike() + 1)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            holder.dislike.setImageResource(R.drawable.thumb_down_active);
                                                                                                            holder.like.setImageResource(R.drawable.thumb_up_icon);
                                                                                                            //holder.dis_t.setText(String.valueOf(model.getPostdislike()));
                                                                                                            if(!model.getPostedbyID().equals(FirebaseAuth.getInstance().getUid())){
                                                                                                            NotificationDataType notification=new NotificationDataType();
                                                                                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                            notification.setNotificationAt(new Date().getTime());
                                                                                                            notification.setPostID(model.getPostID());
                                                                                                            notification.setPostedBy(model.getPostedbyID());
                                                                                                            notification.setType("dislikePost");
                                                                                                            notification.setCheckOpen(false);
                                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                                    .child("Notification").child(model.getPostedbyID())
                                                                                                                    .push().setValue(notification);
                                                                                                        }
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
                                                    .child("Posts").child(model.getPostID())
                                                    .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()){
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Posts").child(model.getPostID())
                                                                        .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Posts").child(model.getPostID())
                                                                                        .child("postlike").setValue(model.getPostlike() - 1)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                                //user catagory work
                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                        .child("Users").child(model.getPostedbyID())
                                                                                                        .child("posts").child(model.getPostID())
                                                                                                        .child("userlikes").child(FirebaseAuth.getInstance().getUid())
                                                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void unused) {
                                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                                        .child("Users").child(model.getPostedbyID())
                                                                                                                        .child("posts").child(model.getPostID())
                                                                                                                        .child("postlike").setValue(model.getPostlike() - 1)
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
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,Comment.class);
                intent.putExtra("postid",String.valueOf(model.getPostID()))
                        .putExtra("postedbyID",model.getPostedbyID())
                        .putExtra("Position",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        //visiting profile

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getPostedbyID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                        .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getPostedbyID());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(model.getPostedbyID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDataType user=snapshot.getValue(UserDataType.class);
                            Intent intent=new Intent(context, Profile.class);
                            intent.putExtra("Name",user.getName())
                                    .putExtra("Profession",user.getProfession())
                                    .putExtra("UserID",model.getPostedbyID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertshare(holder,model);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile, menu, post, like, dislike, comment, share;
        TextView name, about, like_t, dis_t, com_t, share_t, descrip;
        

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.post_profile_pic_img_id);
            menu = itemView.findViewById(R.id.post_menu_img_id);
            post = itemView.findViewById(R.id.post_img_id);

            like = itemView.findViewById(R.id.like_img_id);
            dislike = itemView.findViewById(R.id.dislike_img_id);
            comment = itemView.findViewById(R.id.comment_img_id);
            share = itemView.findViewById(R.id.share_img_id);

            name = itemView.findViewById(R.id.post_username_txt_id);
            about = itemView.findViewById(R.id.post_about_txt_id);
            like_t = itemView.findViewById(R.id.like_txt_id);
            dis_t = itemView.findViewById(R.id.dislike_txt_id);
            com_t = itemView.findViewById(R.id.comment_txt_id);
            share_t = itemView.findViewById(R.id.share_txt_id);
            descrip = itemView.findViewById(R.id.post_descrip_txt_id);
        }
    }

    public void sharepost(viewHolder holder,PostDataType model){
        PostDataType share_post=new PostDataType();
        String time= String.valueOf(new Date().getTime());
        share_post.setShare(0);
        share_post.setPostID(time+" "+FirebaseAuth.getInstance().getUid());
        if(!model.getPostimage().equals(null)){
        share_post.setPostimage(String.valueOf(model.getPostimage()));}
        else{
            share_post.setPostimage(null);
        }
        share_post.setPostedbyID(FirebaseAuth.getInstance().getUid());
        share_post.setPostdescription(String.valueOf(Html.fromHtml("<b>"+holder.name.getText().toString()+" originally shared this post "+"</b>"+ model.getPostdescription().toString())));
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
                                                                                        Toast.makeText(context, "Shared", Toast.LENGTH_SHORT).show();
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

    private void alertshare(viewHolder holder,PostDataType model){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to share this post?");

        // Set Alert Title
        builder.setTitle("Share Post");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            sharepost(holder,model);

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

