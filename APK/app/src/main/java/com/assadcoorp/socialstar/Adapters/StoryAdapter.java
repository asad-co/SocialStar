package com.assadcoorp.socialstar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.assadcoorp.socialstar.DataTypes.StoryDataType;
import com.assadcoorp.socialstar.DataTypes.UserDataType;
import com.assadcoorp.socialstar.DataTypes.UserStoryDataType;
import com.assadcoorp.socialstar.R;
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {
    ArrayList<StoryDataType> list;
    Context context;

    public StoryAdapter(ArrayList<StoryDataType> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_story, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        StoryDataType model = list.get(position);
        if (model.getStories().size() > 0) {
            UserStoryDataType last_stories = model.getStories().get(model.getStories().size() - 1);
            Picasso.get().load(last_stories.getImage())
                    .into(holder.story_image);
            holder.ring_image.setPortionsCount(model.getStories().size());
            FirebaseDatabase.getInstance().getReference().child("Users").child(model.getStoryby())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDataType user = snapshot.getValue(UserDataType.class);
                            Picasso.get().load(user.getProfilephoto())
                                    .placeholder(R.drawable.profile_icon)
                                    .into(holder.profile_pic);
                            holder.name.setText(user.getName());
                            holder.story_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStoryDataType stories : model.getStories()) {
                                        myStories.add(new MyStory(
                                                stories.getImage()
                                        ));
                                    }
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getName()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(user.getProfilephoto()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();
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
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView story_image, profile_pic;
        CircularStatusView ring_image;
        TextView name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            story_image = itemView.findViewById(R.id.friend_story_img_id);
            profile_pic = itemView.findViewById(R.id.story_profile_pic_img_id);

            ring_image = itemView.findViewById(R.id.story_profile_pic_ring_id);
            name = itemView.findViewById(R.id.friend_name_txt_id);
        }
    }
}
