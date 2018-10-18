package com.unimelb.projectinsta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimelb.projectinsta.model.UserPojo;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private AutoCompleteTextView usernameView;
    private FirebaseAuth authentication;
    private View progressView;
    private View registerFormView;
    private View registerInfoView;
    private AutoCompleteTextView realNameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authentication = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        // Set up the register form.
        emailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        usernameView = (AutoCompleteTextView) findViewById(R.id.register_userName);
        passwordView = (EditText) findViewById(R.id.register_password);
        realNameView = (AutoCompleteTextView) findViewById(R.id.register_realName);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_register_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                String username = usernameView.getText().toString();
                String realName = realNameView.getText().toString();
                register(email,username,realName, password);
            }
        });
        registerFormView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);

        TextView goToLogin = findViewById(R.id.goTo_login);
        goToLogin.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToLogin();
                    }
                }
        );
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void register(final String email, final String username,final String realName, final String password) {

        if (!isEmailValid(email) || !isPasswordValid(password)) {
            return;
        }
        showProgress(true);

        authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debug"," on complete create user");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("debug", "createUserWithEmail:success");
                            FirebaseUser user = authentication.getCurrentUser();
                            if(user.getUid() != null) {
                                UserPojo currentUser = new UserPojo(user.getUid(),
                                        username,
                                        realName,
                                        email,
                                        password
                                );
                                FirebaseFirestore instadb = FirebaseFirestore.getInstance();
                                instadb.collection("users").document(user.getUid()).set(currentUser);
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("debug", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // testing Signed in by adding buttons and text fields

            Toast.makeText(RegisterActivity.this,"logged in",Toast.LENGTH_SHORT).show();
            //if user details found and logged in, start Intent and send user details to main screen activity
            Intent mainActivity = new Intent(this,MainActivity.class);
            mainActivity.putExtra("userDetail",user);
            startActivity(mainActivity);
        } else {
            // Signed out
            registerInfoView = findViewById(R.id.register_info);
            Toast.makeText(RegisterActivity.this,"error logging in",Toast.LENGTH_SHORT).show();
            findViewById(R.id.email_register_in_button).setVisibility(View.VISIBLE);
            registerInfoView.setVisibility(View.VISIBLE);
            findViewById(R.id.email).setVisibility(View.VISIBLE);
            findViewById(R.id.password).setVisibility(View.VISIBLE);
        }
    }

    private void goToLogin() {
        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }
}
