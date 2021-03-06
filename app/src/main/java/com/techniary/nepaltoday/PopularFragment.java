package com.techniary.nepaltoday;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PopularFragment extends Fragment {
    private RecyclerView latestFragmentRecyclerView;
    private FirebaseAuth mAuth;
    private static DatabaseReference mDatabaseReference;
    private static String currentUserID;
    private static Context context;
    private static FirebaseRecyclerAdapter mRecyclerAdapter;
    private Query q;
    private static DatabaseReference userDatabaseReference;



    public PopularFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_popular, container, false);
        latestFragmentRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewForPopularFragment);
        latestFragmentRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        latestFragmentRecyclerView.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Posts");

        q = mDatabaseReference.orderByChild("TotalReactions");
        userDatabaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseReference.keepSynced(true);
        currentUserID = mAuth.getCurrentUser().getUid();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Posts,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(
                Posts.class, R.layout.each_post_layout , PostViewHolder.class, q
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {
                Context c = getActivity();
                viewHolder.setUsername(model.getCurrentUserID());
                viewHolder.setCaption(model.getCaption());
                viewHolder.setTime(model.getTime());
                viewHolder.setImage(model.getImage(), c);
                viewHolder.setCurrentUserImage(model.getCurrentUserID(), c);
                viewHolder.imageViewIfUserClicked(model.getCurrentUserReaction(), c);

            }
        };

        latestFragmentRecyclerView.setAdapter(firebaseRecyclerAdapter);
        mRecyclerAdapter = firebaseRecyclerAdapter;

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View theRecyclingView;
        private Button loveButton;
        private RelativeLayout likingLinearLayout;

        public PostViewHolder(View itemView) {
            super(itemView);

            theRecyclingView = itemView;
            loveButton = (Button) theRecyclingView.findViewById(R.id.loveButton);
            likingLinearLayout = (RelativeLayout) theRecyclingView.findViewById(R.id.LikingAndCommentingSection);
            likingLinearLayout.setVisibility(View.GONE);
        }


        public void setUsername(String userPostedID)
        {
            final TextView mUsername= (TextView) theRecyclingView.findViewById(R.id.userWhoPostedUserName);
            userDatabaseReference.child(userPostedID).child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUsername.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setCaption(String caption) {
            TextView mUsername = (TextView) theRecyclingView.findViewById(R.id.captionForPic);
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

        public void setCurrentUserImage(final String userPostedID, final Context c) {
            final CircleImageView imageView = (CircleImageView) theRecyclingView.findViewById(R.id.userWhoPostedCircleImageView);

            userDatabaseReference.child(userPostedID).child("profile_picture").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    Picasso.with(c).load(dataSnapshot.getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(c).load(dataSnapshot.getValue().toString()).into(imageView);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }




        public void imageViewIfUserClicked(String howCurrentUserReacts, final Context c) {

            String key = mRecyclerAdapter.getRef(getAdapterPosition()).getKey();
            mDatabaseReference.child(key).child("ReactingUser").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(currentUserID)) {
                        loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button_red, 0, 0, 0);

                    } else {
                        loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button, 0, 0, 0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}

