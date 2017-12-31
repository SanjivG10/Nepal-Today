package com.techniary.nepaltoday;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.Channel;

public class ChangeQuoteActivity extends AppCompatActivity {

    private TextInputLayout mTextInputLayout;
    private Button mButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_quote);

        String quote=getIntent().getStringExtra("quote");


    mTextInputLayout = (TextInputLayout) findViewById(R.id.quote_user_CHANGEQUOTE);
    mTextInputLayout.getEditText().setText(quote);
    mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.ChangeQuoteToolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle(" Update Your Bio ");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    mButton= (Button) findViewById(R.id.change_quote_button_CHANGEQUOTEACTIVITY);

    mAuth = FirebaseAuth.getInstance();
    String cur_user = mAuth.getCurrentUser().getUid();
    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(cur_user).child("Bio");

    mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String quote = mTextInputLayout.getEditText().getText().toString();
            if(!TextUtils.isEmpty(quote)) {
                mDatabaseReference.setValue(quote).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(ChangeQuoteActivity.this,PersonalAccountActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(ChangeQuoteActivity.this," Error Updating ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    });
    }
}
