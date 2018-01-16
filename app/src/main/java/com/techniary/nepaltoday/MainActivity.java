package com.techniary.nepaltoday;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsViewPagerAdapter tabsViewPagerAdapter;
    private DatabaseReference mDatabaseReference;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNagivationView;
    private TextView userName;
    private CircleImageView userPhoto;
    private String musername;
    private String muserphoto;
    private Menu menu_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Nepal Today");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNagivationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = mNagivationView.getHeaderView(0);
        menu_nav = mNagivationView.getMenu();
        userName = (TextView) navHeader.findViewById(R.id.userNameheaderLayout);
        userPhoto = (CircleImageView) navHeader.findViewById(R.id.userProfile_headerLayout);

        tabsViewPagerAdapter = new TabsViewPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.main_activity_viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.main_activity_tab_layout);
        mViewPager.setAdapter(tabsViewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();
        MenuItem my_account = menu_nav.findItem(R.id.AccountForNavigation);
        MenuItem logout = menu_nav.findItem(R.id.logout_navigation);
        MenuItem bbcNews = menu_nav.findItem(R.id.BBCNews);
        MenuItem youtubeTrend = menu_nav.findItem(R.id.youtubeTrending);
        Log.e("SOMERROR","HATEHIS");
        if (mAuth.getCurrentUser() != null) {
            String current_user = mAuth.getCurrentUser().getUid();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);


            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("Username").getValue().toString() != null) {
                        userName.setText(dataSnapshot.child("Username").getValue().toString());
                    }
                    if (dataSnapshot.child("profile_picture").getValue().toString() != null) {
                        muserphoto = dataSnapshot.child("profile_picture").getValue().toString();
                    }

                    Picasso.with(getApplicationContext()).load(muserphoto).networkPolicy(NetworkPolicy.OFFLINE).into(userPhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(muserphoto).into(userPhoto);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        mDatabaseReference.keepSynced(true);
    }
    else if(mAuth.getCurrentUser()==null)
        {
          Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }


        my_account.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this,PersonalAccountActivity.class);
                startActivity(intent);
                return true;
            }
        });

        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                logOutAndGoToSignIn();
                return true;
            }
        });

        bbcNews.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent= new Intent(MainActivity.this,NewsActivity.class);
                startActivity(intent);

                return true;
            }
        });

        youtubeTrend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this,YoutubeVideoActivity.class);
                startActivity(intent);

                return true;
            }
        });


    }
    private void logOutAndGoToSignIn() {

        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();


        }


     mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Username").getValue().toString().equals("default") || dataSnapshot.child("profile_picture").getValue().toString().equals("default"))
                {
                    Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



    }
















    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.my_account_info_menu:
                Intent intent = new Intent(MainActivity.this,PersonalAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_menu_button:
                logOutAndGoToSignIn();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}
*/
