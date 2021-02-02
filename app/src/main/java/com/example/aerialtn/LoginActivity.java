package com.example.aerialtn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aerialtn.Prevelant.Prevelant;
import com.example.aerialtn.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText Inputid, Inputpassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbId = "Users";
    private CheckBox ChkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_button_login_page);
        Inputid = (EditText) findViewById(R.id.login_id);
        Inputpassword = (EditText) findViewById(R.id.login_pass);
        loadingBar = new ProgressDialog(this);
        ChkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();

            }
        });

    }

    private void LoginUser() {

        String id = Inputid.getText().toString();
        String password = Inputpassword.getText().toString();
        
        if (TextUtils.isEmpty(id))
        {
            Toast.makeText(this, "Write your ID", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Write your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Connecting");
            loadingBar.setMessage("Checking Credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(id, password);

        }

    }

    private void AllowAccessToAccount(String id, String password)
    {
        if (ChkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevelant.UserIdKey, id);
            Paper.book().write(Prevelant.UserPwKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbId).child(id).exists())
                {
                    Users usersdata = dataSnapshot.child(parentDbId) .child(id) .getValue(Users.class);

                    if (usersdata.getId().equals(id))
                    {
                        if (usersdata.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
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