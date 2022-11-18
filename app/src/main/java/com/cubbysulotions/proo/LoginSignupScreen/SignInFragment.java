package com.cubbysulotions.proo.LoginSignupScreen;

import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cubbysulotions.proo.Home.UserDetailsBottomSheet;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.Firebase.Users;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import hari.bounceview.BounceView;

import static android.content.ContentValues.TAG;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    private TextInputEditText fname, lname, email, password;
    private TextInputLayout fnameLayout, lnameLayout, emailLayout, passLayout, weeksLayout;
    private Button btnBack;
    private ImageButton btnSubmit;
    private FirebaseAuth mAuth;
    private NavController navController;
    private NavOptions navOptions;
    LoadingDialog loadingDialog;
    AutoCompleteTextView spinner;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            fname = view.findViewById(R.id.txtFname);
            lname = view.findViewById(R.id.txtLname);
            email = view.findViewById(R.id.txtEmail);
            password = view.findViewById(R.id.txtPassword);
            fnameLayout = view.findViewById(R.id.fnameLayout);
            lnameLayout = view.findViewById(R.id.lnamelayout);
            emailLayout = view.findViewById(R.id.emailLayout);
            passLayout = view.findViewById(R.id.passwordLayout);
            btnBack = view.findViewById(R.id.btnBack);
            spinner = view.findViewById(R.id.spinnerWeek);
            weeksLayout = view.findViewById(R.id.weeksLayout);
            BounceView.addAnimTo(btnBack);
            btnSubmit = view.findViewById(R.id.btnSubmit);
            BounceView.addAnimTo(btnSubmit);
            navController = Navigation.findNavController(view);
            navOptions = new NavOptions.Builder().setPopUpTo(R.id.welcomeScreenFragment, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();
            loadingDialog = new LoadingDialog(getActivity());

            RelativeLayout constraintLayout = view.findViewById(R.id.layout2);
            AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();

            SetWeeks setWeeks = new SetWeeks(SignInFragment.this);
            setWeeks.execute();

            //Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUSer();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.action_signInFragment_to_welcomeScreenFragment, null, navOptions);
                }
            });

        } catch (Exception e){
            toast("Something went wrong, Please try again");
            Log.e("Submit Error", "exception", e);
        }
    }

    private void registerUSer() {
        try{
            String firstName = fname.getText().toString();
            String lastName = lname.getText().toString();
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            String weeks = spinner.getText().toString();

            //The first if-else is to validate the length of the password
            if (firstName.isEmpty() || lastName.isEmpty() || emailText.isEmpty() || passText.isEmpty() || spinner.length() == 0){
                fnameLayout.setError("First Name is required");
                lnameLayout.setError("Last Name is required");
                emailLayout.setError("Email is required");
                passLayout.setError("Password is required");
                weeksLayout.setError("Current week is required");
            } else if (password.getText().length() < 6){
                passLayout.setError("Password should be at least 6 characters");
            } else {
                loadingDialog.startLoading("Processing");
                //Then this block of code is to check whether the email is existing or not
                mAuth.fetchSignInMethodsForEmail(emailText)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                //Log.e("TAG", "Is New User!");
                                if (isNewUser) {
                                    //While the next block of code is to create the account using Email and Password
                                    mAuth.createUserWithEmailAndPassword(emailText, passText)
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    //if successful, firebase will send a verification email to the user
                                                    if(task.isSuccessful()){
                                                        mAuth.getCurrentUser().sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //if the email verification is successfully sent, the first name, last name and email
                                                                //will be save in Realtime database
                                                                if(task.isSuccessful()){
                                                                    Users users = new Users(firstName, lastName, emailText, weeks);
                                                                    FirebaseDatabase.getInstance().getReference("users")
                                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            loadingDialog.stopLoading();
                                                                            toast("Please check your email for verification.");
                                                                            navController.navigate(R.id.action_signInFragment_to_welcomeScreenFragment, null, navOptions);
                                                                        }
                                                                    });
                                                                } else {
                                                                    loadingDialog.stopLoading();
                                                                    toast(task.getException().getMessage());
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                                        loadingDialog.stopLoading();
                                                        toast("Please try again.");
                                                    }
                                                }
                                            });
                                } else {
                                    //Log.e("TAG", "Is Old User!");
                                    loadingDialog.stopLoading();
                                    emailLayout.setError("The email address is already in use by another account.");
                                }
                            }
                        });
            }


        } catch (Exception e){
            toast("Submission Error, Please try again");
            Log.e("Submit Error", "exception", e);
        }
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private static class SetWeeks extends AsyncTask<Void, Void, List<String>>{
        WeakReference<SignInFragment> reference;
        WeakReference<UserDetailsBottomSheet> reference2;

        public SetWeeks(SignInFragment context){
            reference = new WeakReference<SignInFragment>(context);
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> weeks = new ArrayList<>();
            for(int i = 1; i <= 40; i++){
                weeks.add(i + " weeks");
            }
            return weeks;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            SignInFragment main = reference.get();
            if(main == null) return;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    main.getActivity(),
                    R.layout.drop_down_weeks,
                    strings
            );

            main.spinner.setAdapter(adapter);

            super.onPostExecute(strings);
        }
    }
}