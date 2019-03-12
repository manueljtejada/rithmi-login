package com.example.rithmilogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Sign In";

    private EditText mEmailInput;
    private EditText mPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);

        /* Initialize FirebaseAuth */
        mAuth = FirebaseAuth.getInstance();

        /* Views */
        mEmailInput = findViewById(R.id.emailInput);
        mPasswordInput = findViewById(R.id.passwordInput);

        /* Buttons */
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
    }

    private void login(String email, String password) {
        if (!validateForm()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Successfully signed in user" + user);
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.error_authentication, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error signing in");
                    }
                }
            });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailInput.setError(getBaseContext().getString(R.string.error_empty_field));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailInput.setError(getBaseContext().getString(R.string.error_invalid_email));
            valid = false;
        } else {
            mEmailInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordInput.setError(getBaseContext().getString(R.string.error_empty_field));
            valid = false;
        } else {
            mPasswordInput.setError(null);
        }

        return valid;

    }

    @Override
    public void onClick(View v) {
        login(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
    }
}
