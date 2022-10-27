package com.example.quizjava.ui.singUp;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.MainActivity;
import com.example.quizjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email,password, confirm_password;
    private Button singUp_btn;
    private ImageView back_btn;

    private FirebaseAuth mAuth;

    private String emailStr, passwordStr, confirmPassStr, nameStr;

    private Dialog progress_dialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.userName);
        email = findViewById(R.id.emailID);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        singUp_btn = findViewById(R.id.signUp_btn);
        back_btn = findViewById(R.id.back_btn);

        progress_dialog = new Dialog(SignUpActivity.this);
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Registering user...");


        mAuth = FirebaseAuth.getInstance();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        singUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    signUpNewUser();
                }

            }
        });
    }

    private boolean validate(){
        nameStr = name.getText().toString().trim();
        passwordStr = password.getText().toString().trim();
        emailStr = email.getText().toString().trim();
        confirmPassStr = confirm_password.getText().toString().trim();
        if(nameStr.isEmpty()){
            name.setError("Enter name");
            return false;
        }
        if(emailStr.isEmpty()){
            email.setError("Enter email");
            return false;
        }
        if(passwordStr.isEmpty()){
            password.setError("Enter password");
            return false;
        }
        if(confirmPassStr.isEmpty()){
            confirm_password.setError("Enter confirm password");
            return false;
        }
        if(passwordStr.compareTo(confirmPassStr) != 0){
            Toast.makeText(SignUpActivity.this, "Passwords must match",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void signUpNewUser(){
        progress_dialog.show();
        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Sign Up Success", Toast.LENGTH_SHORT).show();

                            DBQuery.createUserData(emailStr,nameStr, new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    DBQuery.loadData(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progress_dialog.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            SignUpActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(SignUpActivity.this, "SMTH went wrong, try again1",
                                                    Toast.LENGTH_SHORT).show();
                                            progress_dialog.dismiss();
                                        }
                                    });
                                }
                                @Override
                                public void onFailure() {
                                    Toast.makeText(SignUpActivity.this, "SMTH went wrong, try again",
                                            Toast.LENGTH_SHORT).show();
                                    progress_dialog.dismiss();
                                }
                            });
                        } else {
                            progress_dialog.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}