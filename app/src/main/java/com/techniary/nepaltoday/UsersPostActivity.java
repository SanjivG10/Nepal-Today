package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
    private ImageButton crossImageButton;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private ProgressDialog postProgressDailog;
    private Uri imageUriForSaving;
    private DatabaseReference gettingUsernameAndPhotoReference;
    private Bitmap currentUploadingImage;

    private String downloadUrlForImage;

    private static final int GALLERY_REQUEST_CODE = 200;
    private String mUsername;
    private String currentUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_post);


        mToolbar = (Toolbar) findViewById(R.id.ToolbarForUserPost);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        crossImageButton = (ImageButton) findViewById(R.id.cross_icon);
        crossImageButton.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(" Please wait, while we process your image ");
        mProgressDialog.setMessage(" Processing image, please wait for a while ");
        mProgressDialog.setCanceledOnTouchOutside(false);

        postProgressDailog = new ProgressDialog(this);
        postProgressDailog.setTitle(" Uploading");
        postProgressDailog.setMessage(" Please wait while we upload");
        postProgressDailog.setCanceledOnTouchOutside(false);

        imageUriForSaving = null;
        downloadUrlForImage = null;
        currentUserPhoto= null;
        currentUploadingImage = null;

        mCircleImageView = (CircleImageView) findViewById(R.id.user_circleImageView_UsersPostActivity);
        userName = (TextView) findViewById(R.id.userName_UsersPostActivity);
        mEditText = (EditText) findViewById(R.id.editText_UsersPostActivity);
        mImageButton = (ImageButton) findViewById(R.id.add_image_Users_Post_Activity);
        mImageView = (ImageView) findViewById(R.id.UsersPostActivity_imageView);
        postButton = (Button) findViewById(R.id.postButton_UsersPostActivity);


        mFirebaseAuth = FirebaseAuth.getInstance();
        userID = mFirebaseAuth.getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference().child("User_images").child(userID);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        final String current_logged_in_user = mFirebaseAuth.getCurrentUser().getUid();

        gettingUsernameAndPhotoReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_logged_in_user);
        gettingUsernameAndPhotoReference.keepSynced(true);

        gettingUsernameAndPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName_value = dataSnapshot.child("Username").getValue().toString();
                final String imageUrl = dataSnapshot.child("profile_picture").getValue().toString();
                currentUserPhoto = imageUrl;
                userName.setText(userName_value);
                mUsername = userName_value;

                Picasso.with(getApplicationContext()).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(mCircleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(imageUrl).into(mCircleImageView);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String current_user_id  = mFirebaseAuth.getCurrentUser().getUid();
                postProgressDailog.show();

                //current date
                Date currentDateAndTime = Calendar.getInstance().getTime();
                final String time = currentDateAndTime.toString();
                //current time
                Calendar calendar = Calendar.getInstance();
                final SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                final String postTime = mdformat.format(calendar.getTime());
                final String Caption = mEditText.getText().toString();


                if(currentUploadingImage!=null) {
                    StorageReference filePath = mStorageReference.child("User_images")
                            .child(time + ".jpg");


                    //uploadingBitmap
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    currentUploadingImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filePath.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            postProgressDailog.dismiss();
                            Toast.makeText(getApplicationContext()," Error in Saving Image ",Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            downloadUrlForImage = taskSnapshot.getDownloadUrl().toString();
                            saveDataWithPhoto(postTime,Caption,downloadUrlForImage);                        }
                    });


                }
                else
                {
                    saveDataWithoutPhoto(Caption,postTime);
                }

                //for saving images and posts









            }
        });

        crossImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setImageResource(android.R.color.transparent);
                crossImageButton.setVisibility(View.GONE);
                mEditText.setHint(" What's on your mind? ");
            }
        });



    }

    private void saveDataWithoutPhoto(String Caption, String postTime) {

        HashMap<String, String> user_posts = new HashMap<>();
        user_posts.put("Time",postTime);
        user_posts.put("Caption",Caption);
        user_posts.put("TotalReactions","0");
        user_posts.put("CurrentUserReaction","notreacted");
        user_posts.put("CurrentUserID",userID);
        user_posts.put("ReactingUser","null");
        final String uniqueId = mDatabaseReference.push().getKey();
        user_posts.put("Unique",uniqueId);
        mDatabaseReference.child(uniqueId).setValue(user_posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                                            postProgressDailog.dismiss();
                                            Intent intent = new Intent(UsersPostActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                }
                else
                {
                    postProgressDailog.dismiss();
                    Toast.makeText(getApplicationContext()," Error in Saving ",Toast.LENGTH_LONG).show();

                }
            }

        });
    }

    private void saveDataWithPhoto(String postTime, String Caption, String downloadUrlForImage) {
        HashMap<String, String> user_posts = new HashMap<>();
        user_posts.put("Time",postTime);
        user_posts.put("Image",downloadUrlForImage);
        user_posts.put("Caption",Caption);
        user_posts.put("TotalReactions","0");
        user_posts.put("CurrentUserID",userID);
        user_posts.put("CurrentUserReaction","notreacted");
        final String uniqueId = mDatabaseReference.push().getKey();
        user_posts.put("Unique",uniqueId);
        mDatabaseReference.child(uniqueId).setValue(user_posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {


                                            postProgressDailog.dismiss();
                                            Intent intent = new Intent(UsersPostActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                }
                else
                {
                    postProgressDailog.dismiss();
                    Toast.makeText(getApplicationContext()," Error in Saving ",Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            String url= getRealPathFromURI(getApplicationContext(),imageUri);
            File f = new File(url);

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(url);
                mImageView.setImageBitmap(bitmap);
                mEditText.setHint(" Enter your Caption for the Pic ");
                crossImageButton.setVisibility(View.VISIBLE);

                try {
                    Bitmap compressedImageBitmap = new Compressor(getApplicationContext()).setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75).compressToBitmap(f);
                    currentUploadingImage = compressedImageBitmap;
                }
                catch (IOException e)
                {
                    Toast.makeText(getApplicationContext()," Some Error Occured ",Toast.LENGTH_LONG).show();
                    currentUploadingImage= null;
                }


                currentUploadingImage = bitmap;

            }
            catch (OutOfMemoryError e)
            {
                Toast.makeText(getApplicationContext(), " File too Large ", Toast.LENGTH_LONG).show();
                if(mImageView.getDrawable()==null) {
                    mEditText.setHint(" What's on your mind?");
                    crossImageButton.setVisibility(View.GONE);
                }
            }

            //Picasso.with(getApplicationContext()).load(imageUri).into(mImageView);

        }
    }





    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
