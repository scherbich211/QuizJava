package com.example.quizjava.ui.login;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.MainActivity;
import com.example.quizjava.R;
import com.example.quizjava.ui.singUp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login_btn;
    private TextView forgot_password, sign_up_btn;
    private FirebaseAuth mAuth;

    private Dialog progress_dialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        forgot_password = findViewById(R.id.forgot_password);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        progress_dialog = new Dialog(LoginActivity.this);
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        mAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    login();
                }
            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateData() {

        if(email.getText().toString().isEmpty()){
            email.setError("Enter email id");
            return false;
        }
        if(password.getText().toString().isEmpty()){
            password.setError("Enter password");
            return false;
        }
        return true;
    };


    private void login() {
        progress_dialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            DBQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    progress_dialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onFailure() {
                                    progress_dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "SMTH went wrong on login",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progress_dialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}