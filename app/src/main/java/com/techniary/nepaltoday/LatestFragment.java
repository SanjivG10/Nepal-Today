package com.techniary.nepaltoday;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.login.LoginException;

import de.hdodenhof.circleimageview.CircleImageView;


public class LatestFragment extends Fragment {
    private RecyclerView latestFragmentRecyclerView;
    private static DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private static Context context;
    private static boolean firstTime;
    private static String totalVotes;
    private static String uniqueKey;
    private static Boolean userReactState;
    private static int positionOfAdapter;
    private static FirebaseRecyclerAdapter mRecyclerAdapter;
    private static String currentUserID;

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
        userReactState = false;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        positionOfAdapter = 0;

        latestFragmentRecyclerView.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabaseReference.keepSynced(true);
        currentUserID = mAuth.getCurrentUser().getUid();
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
                    protected void populateViewHolder(final PostViewHolder viewHolder,final Posts model, int position) {
                        Context c = getActivity();
                        context = c;
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setCaption(model.getCaption());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setImage(model.getImage(),c);
                        viewHolder.setCurrentUserImage(model.getUserPhoto(),c);
                        viewHolder.imageViewIfUserClicked(model.getCurrentUserReaction(),c);
                        totalVotes = model.getTotalReactions();
                        uniqueKey = model.getUnique();
                    }


                };
        mRecyclerAdapter = firebaseRecyclerAdapter;
        latestFragmentRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View theRecyclingView;
        private Button loveButton ;

        public PostViewHolder(View itemView) {
            super(itemView);
            theRecyclingView = itemView;
            loveButton = (Button) theRecyclingView.findViewById(R.id.loveButton);
            loveButton.setOnClickListener(this);

        }



        @Override
        public void onClick(View view) {
            if(view.getId()==loveButton.getId())
            {
                positionOfAdapter = getAdapterPosition();
                uniqueKey = mRecyclerAdapter.getRef(positionOfAdapter).getKey();

                if(firstTime==true)
                {

                    loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button_red,0,0,0);
                    firstTime = false;
                    int vote= Integer.valueOf(totalVotes);
                    if(vote<0)
                    {
                        vote = 0;
                    }
                    vote++;
                    totalVotes = String.valueOf(vote);
                    Map<String, Object> reactions = new HashMap<>();
                    reactions.put("TotalReactions",totalVotes);
                    reactions.put("CurrentUserReaction","reacted");

                    mDatabaseReference.child(uniqueKey).updateChildren(reactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                            HashMap<String,String> reactingUser = new HashMap<>();
                            reactingUser.put(currentUserID,"1");
                            mDatabaseReference.child(uniqueKey).child("ReactingUser").setValue(reactingUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                    }
                                    else
                                    {
                                        Toast.makeText(context," Some Error Occured ",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            }
                            else
                            {
                                Toast.makeText(context," Some Error Occured ",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
                else
                {
                    loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button,0,0,0);
                    firstTime = true;

                    int vote= Integer.valueOf(totalVotes);
                    if(vote<0)
                    {
                        vote = 0;
                    }
                    if(vote>0) {
                        vote--;
                    }
                    if(vote==0)
                    {
                        totalVotes="0";
                    }
                    totalVotes = String.valueOf(vote);
                    Map<String, Object> reactions = new HashMap<>();
                    reactions.put("TotalReactions",totalVotes);
                    reactions.put("CurrentUserReaction","notreacted");

                    mDatabaseReference.child(uniqueKey).updateChildren(reactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mDatabaseReference.child(uniqueKey).child("ReactingUser").child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {

                                        }
                                        else
                                        {
                                            Toast.makeText(context," Some Error Occured ",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(context," Some Error Occured ",Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                }

            }
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


        public void imageViewIfUserClicked(String currentUserReaction, final Context c) {

            if(currentUserReaction.equals("notreacted")) {

                loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button,0,0,0);
            }
            else if (currentUserReaction.equals("reacted"))
            {
                loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button_red,0,0,0);
            }
            else
            {
                loveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_button,0,0,0);
            }

        }
    }


}
