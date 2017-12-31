package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UsersPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView mCircleImageView;
    private TextView userName;
    private EditText mEditText;
    private ImageButton mImageButton;
    private ImageView mImageView;
    private Button postButton;
    private String userID;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private static final int GALLERY_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_post);


        mToolbar = (Toolbar) findViewById(R.id.ToolbarForUserPost);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(" Please wait, while we process your image ");
        mProgressDialog.setMessage(" Processing image, please wait for a while ");
        mProgressDialog.setCanceledOnTouchOutside(false);

        mCircleImageView = (CircleImageView) findViewById(R.id.user_circleImageView_UsersPostActivity);
        userName = (TextView) findViewById(R.id.userName_UsersPostActivity);
        mEditText = (EditText) findViewById(R.id.editText_UsersPostActivity);
        mImageButton = (ImageButton) findViewById(R.id.add_image_Users_Post_Activity);
        mImageView = (ImageView) findViewById(R.id.UsersPostActivity_imageView);
        postButton = (Button) findViewById(R.id.postButton_UsersPostActivity);


        mFirebaseAuth = FirebaseAuth.getInstance();
        userID = mFirebaseAuth.getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference().child("User_images").child(userID);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(userID);


        postButton.setTextColor(Color.DKGRAY);
        postButton.setEnabled(false);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(TextUtils.isEmpty(mEditText.getText().toString()))) {
                    postButton.setTextColor(Color.parseColor("#FFFFFF"));
                    postButton.setEnabled(true);
                } else {
                    postButton.setTextColor(Color.DKGRAY);
                    postButton.setEnabled(false);

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!(TextUtils.isEmpty(mEditText.getText().toString()))) {
                    postButton.setTextColor(Color.parseColor("#FFFFFF"));
                    postButton.setEnabled(true);
                } else {
                    postButton.setTextColor(Color.DKGRAY);
                    postButton.setEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(TextUtils.isEmpty(mEditText.getText().toString()))) {
                    postButton.setTextColor(Color.parseColor("#FFFFFF"));
                    postButton.setEnabled(true);
                } else {
                    postButton.setTextColor(Color.DKGRAY);
                    postButton.setEnabled(false);

                }
            }
        });


        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            Picasso.with(getApplicationContext()).load(imageUri).resize(width,height).centerCrop().into(mImageView);

        }
    }


}
