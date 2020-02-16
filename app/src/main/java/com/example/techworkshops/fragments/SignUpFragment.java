package com.example.techworkshops.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.techworkshops.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dmax.dialog.SpotsDialog;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText mName;
    private EditText mEmailId;
    private EditText mPassword;
    private EditText mConfirmPassowrd;
    private TextView goToLoginPage;
    private Button mSignUp;
    private View view;
    private AlertDialog spotsBox;

    private NavController navController;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SignUpFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mName = view.findViewById(R.id.signup_name);
        mEmailId = view.findViewById(R.id.signup_email_id);
        mPassword = view.findViewById(R.id.signup_password);
        mConfirmPassowrd = view.findViewById(R.id.signup_confirm_password);
        goToLoginPage = view.findViewById(R.id.signup_to_login_screen);
        mSignUp = view.findViewById(R.id.signup_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        goToLoginPage.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        spotsBox = new SpotsDialog(getContext());
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.signup_button:
                signup();
                break;
            case R.id.signup_to_login_screen:
                navController.navigate(R.id.nav_log_in);
                break;
        }
    }

    private void signup() {
        String name = mName.getText().toString().trim();
        final String emailId = mEmailId.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassowrd.getText().toString().trim();
        if (!allDetailsEntered(name, emailId, password, confirmPassword)) {
            return;
        }
        spotsBox.show();
        db.collection("students").document().set(new Student(name,emailId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.createUserWithEmailAndPassword(emailId,password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Log.d(TAG, "onSuccess: Account created");

                                        spotsBox.cancel();
                                        afterSignUp(true,"");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Account not created");
                                        spotsBox.cancel();
                                        afterSignUp(false,e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spotsBox.cancel();
                        afterSignUp(false,e.getMessage());
                    }
                });
    }

    private void afterSignUp(boolean isSignedUp, String message) {
        if (isSignedUp) {
            navController.navigate(R.id.nav_dashboard);
        } else {
            makeSnackBar(message);
        }
    }

    private boolean allDetailsEntered(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            makeSnackBar("Please Enter Name");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            makeSnackBar("Please Enter Email Id");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            makeSnackBar("Please set password with minimum length of 6");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            makeSnackBar("Please confirm with same password");
            return false;
        }
        return true;
    }

    private void makeSnackBar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
