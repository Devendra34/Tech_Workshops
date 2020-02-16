package com.example.techworkshops.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.techworkshops.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class LogInFragment extends Fragment implements View.OnClickListener {

    private EditText mEmailId;
    private EditText mPassword;
    private Button mLogin;
    private TextView goToSignUp;
    private View view;
    private AlertDialog spotsBox;

    private NavController navController;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log_in, container, false);
        mEmailId = view.findViewById(R.id.signin_email_id);
        mPassword = view.findViewById(R.id.signin_password);
        mLogin = view.findViewById(R.id.login_button);
        goToSignUp = view.findViewById(R.id.login_to_signup);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spotsBox = new SpotsDialog(getContext());
        navController = Navigation.findNavController(view);
        mLogin.setOnClickListener(this);
        goToSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                login();
                break;
            case R.id.login_to_signup:
                navController.navigate(R.id.nav_sign_up);
                break;
        }
    }

    private void login() {
        String emailId = mEmailId.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (!checkAllFields(emailId, password)) {
            return;
        }
        spotsBox.show();
        firebaseAuth.signInWithEmailAndPassword(emailId,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        spotsBox.cancel();
                        afterSignIn(true,"");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spotsBox.cancel();
                        afterSignIn(false,e.getMessage());
                    }
                });
    }


    private void afterSignIn(boolean isSuccess, String errorMessage){
        if (isSuccess) {
            navController.navigate(R.id.nav_dashboard);
        } else {
            makeSnackBar(errorMessage);
        }
    }

    private boolean checkAllFields(String emailId, String password) {
        if (TextUtils.isEmpty(emailId)) {
            makeSnackBar("Please Enter Email Id");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            makeSnackBar("Please Enter your Password");
            return false;
        }
        return true;
    }

    private void makeSnackBar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}