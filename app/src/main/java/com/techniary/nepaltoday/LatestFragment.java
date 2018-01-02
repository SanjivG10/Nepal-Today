package com.techniary.nepaltoday;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class LatestFragment extends Fragment {
    private RecyclerView latestFragmentRecyclerView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;


    public LatestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_latest,container,false);
        latestFragmentRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewForLatestFragment);
        latestFragmentRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        latestFragmentRecyclerView.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabaseReference.keepSynced(true);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostViewHolder>(
                        Posts.class, R.layout.each_post_layout, PostViewHolder.class, mDatabaseReference
                ) {

                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {
                        Context c = getActivity();
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setCaption(model.getCaption());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setImage(model.getImage(),c);
                        viewHolder.setCurrentUserImage(model.getUserPhoto(),c);
                    }

                };
        latestFragmentRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View theRecyclingView;
        public PostViewHolder(View itemView) {
            super(itemView);
            theRecyclingView = itemView;
        }

        public void setUsername(String username)
        {
            TextView mUsername= (TextView) theRecyclingView.findViewById(R.id.userWhoPostedUserName);
            mUsername.setText(username);
        }


        public void setCaption(String caption) {
            TextView mUsername= (TextView) theRecyclingView.findViewById(R.id.captionForPic);
            mUsername.setText(caption);
        }

        public void setImage(final String image, final Context c) {
            final ImageView imageView = (ImageView) theRecyclingView.findViewById(R.id.userUploadedImageView);

            Picasso.with(c).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                Picasso.with(c).load(image).into(imageView);
                }
            });
        }

        public void setTime(String time) {
            TextView time_of_posting = (TextView) theRecyclingView.findViewById(R.id.timeOfPosting_each_post_layout);
            time_of_posting.setText(time);
        }

        public void setCurrentUserImage(final String userPhoto, final Context c) {
            final CircleImageView imageView = (CircleImageView) theRecyclingView.findViewById(R.id.userWhoPostedCircleImageView);

            Picasso.with(c).load(userPhoto).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(c).load(userPhoto).into(imageView);
                }
            });
        }
    }

}
