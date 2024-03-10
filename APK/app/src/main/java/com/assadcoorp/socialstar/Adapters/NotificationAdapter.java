package com.assadcoorp.socialstar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.Activities.Comment;
import com.assadcoorp.socialstar.DataTypes.NotificationDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.Activities.Profile;
import com.assadcoorp.socialstar.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{
    ArrayList<NotificationDataType> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationDataType> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_notification,parent,false);
        return new viewHolder(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        NotificationDataType model=list.get(position);
        String time_ago = TimeAgo.using(model.getNotificationAt());
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType user=snapshot.getValue(UserDataType.class);
                        holder.timelapsed.setText(time_ago);
                        Picasso.get().load(user.getProfilephoto())
                                .placeholder(R.drawable.profile_icon)
                                .into(holder.profile);
                        if(model.getType().equals("likePost")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" liked your post"));
                        }
                        if(model.getType().equals("dislikePost")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" dislike your post"));
                        }
                        if(model.getType().equals("comment")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" commented on your post"));

                        }
                        if(model.getType().equals("follow")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" started following you"));
                        }
                        if(model.getType().equals("unfriend")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" unfriend you"));
                        }
                        if(model.getType().equals("likeComment")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" liked your comment"));
                        }
                        if(model.getType().equals("dislikeComment")){
                            holder.message.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" disliked your comment"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //database code ends here
        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Notification")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(model.getNotificationID())
                        .child("checkOpen").setValue(true);
                if(!model.getType().equals("follow")&&!model.getType().equals("unfriend")){
                Intent intent=new Intent(context, Comment.class);
                intent.putExtra("postid",String.valueOf(model.getPostID()))
                        .putExtra("postedbyID",model.getPostedBy())
                        .putExtra("Position",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);}
                else if(model.getType().equals("follow")||model.getType().equals("unfriend")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(model.getNotificationBy())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserDataType user=snapshot.getValue(UserDataType.class);
                                    Intent intent=new Intent(context, Profile.class);
                                    intent.putExtra("Name",user.getName())
                                            .putExtra("Profession",user.getProfession())
                                            .putExtra("UserID",model.getNotificationBy());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }
            }
        });
        if(model.isCheckOpen()){
            holder.block.setCardBackgroundColor(Color.parseColor("#EAEAEA"));
        }
        else if(!model.isCheckOpen()){
            holder.block.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //visiting profile
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(model.getNotificationBy())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType user=snapshot.getValue(UserDataType.class);
                                Intent intent=new Intent(context, Profile.class);
                                intent.putExtra("Name",user.getName())
                                        .putExtra("Profession",user.getProfession())
                                        .putExtra("UserID",model.getNotificationBy());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView message,timelapsed;
        CardView block;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.notification_profile_pic_img_id);
            message=itemView.findViewById(R.id.notification_message_txt_id);
            timelapsed=itemView.findViewById(R.id.notification_timelapsed_txt_id);
            block=itemView.findViewById(R.id.notification_cardview_id);
        }
    }
}
