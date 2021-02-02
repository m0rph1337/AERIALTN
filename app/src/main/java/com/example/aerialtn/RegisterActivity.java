package com.example.aerialtn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

     Button RegisterButton;
     EditText Inputid, Inputpassword;
     ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterButton = (Button) findViewById(R.id.register_button_register_page);
        Inputid = (EditText) findViewById(R.id.register_ID);
        Inputpassword = (EditText) findViewById(R.id.register_password);
        loadingBar = new ProgressDialog(this);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccount();
            }
        });

    }

    private void CreateAccount() {

        String id = Inputid.getText().toString();
        String password = Inputpassword.getText().toString();

        if (TextUtils.isEmpty(id))
        {
            Toast.makeText(this, "Please specify an ID", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Create a password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateID(id, password);
        }
    }

    private void ValidateID(final String id, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(id).exists()))
                {
                    HashMap<String, Object> userdatamap = new HashMap<>();
                    userdatamap.put("id", id);
                    userdatamap.put("password", password);

                    RootRef.child("Users") .child(id) .updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Account Created, Please login", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please login or create a new account", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}