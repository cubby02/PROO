package com.cubbysulotions.proo.LoginSignupScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cubbysulotions.proo.MainActivity;
import com.cubbysulotions.proo.Models.Users;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static android.content.ContentValues.TAG;


public class LoginFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_login, container, false);
    }

    private EditText email, password;
    private Button btnBack, btnLogin;
    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            email = view.findViewById(R.id.txtEmailLogin);
            password = view.findViewById(R.id.txtPasswordLogin);
            btnBack = view.findViewById(R.id.btnBackLogin);
            btnLogin = view.findViewById(R.id.btnLogin);
            navController = Navigation.findNavController(view);

            //Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            /*
            if (mAuth.getCurrentUser() != null){
                getActivity().finish();
                return;
            } */

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.action_loginFragment_to_welcomeScreenFragment);
                }
            });
        } catch (Exception e){
            toast("Something went wrong, Please try again");
            Log.e("Login Error", "exception", e);
        }
    }

    private void login() {
        try {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();

            if (emailText.isEmpty() || passText.isEmpty()){
                email.setError("Required");
                password.setError("Required");
            } else {

                mAuth.signInWithEmailAndPassword(emailText, passText)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                    if (Objects.equals(task.getException().getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")){
                                        toast("Email does not exist");
                                    } else if(Objects.equals(task.getException().getMessage(), "The password is invalid or the user does not have a password.")){
                                        toast("Wrong password");
                                    } else if(Objects.equals(task.getException().getMessage(),
                                            "We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]")){
                                        toast("Multiple attempts, please try again later");
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e){
            toast("Login Error, Please try again");
            Log.e("Login Error", "exception", e);
        }
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}