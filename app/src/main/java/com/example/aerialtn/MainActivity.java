package com.example.aerialtn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.aerialtn.Prevelant.Prevelant;
import com.example.aerialtn.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

     private Button CreateAccButton, LoginButton;
     private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CreateAccButton = (Button) findViewById(R.id.button_register);
        LoginButton = (Button) findViewById(R.id.button_login);
        loadingBar = new ProgressDialog(this);




        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);



            }
        });

        CreateAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        String UserIdKey = Paper.book().read(Prevelant.UserIdKey);
        String UserPwKey = Paper.book().read(Prevelant.UserPwKey);

        if (UserIdKey != "" && UserPwKey != "")
        {
            if (!TextUtils.isEmpty(UserIdKey) && !TextUtils.isEmpty(UserPwKey))
            {
                AllowAccess(UserIdKey, UserPwKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Wait");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

        
    }

    private void AllowAccess(final String id, final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(id).exists())
                {
                    Users usersdata = dataSnapshot.child("Users") .child(id) .getValue(Users.class);

                    if (usersdata.getId().equals(id))
                    {
                        if (usersdata.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {

                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}