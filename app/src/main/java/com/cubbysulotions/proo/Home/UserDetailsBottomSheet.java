package com.cubbysulotions.proo.Home;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.cubbysulotions.proo.Firebase.Users;
import com.cubbysulotions.proo.LoginSignupScreen.SignInFragment;
import com.cubbysulotions.proo.LoginSignupScreen.WelcomeActivity;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UserDetailsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "UserDetailsBottomSheet";
    View rootView;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;

    public UserDetailsBottomSheet() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_details, container, false);
        return rootView;
    }

    private TextView userFullName, userWeek;
    private Button close, edit, logout;

    private Button cancelEdit, saveEidt;
    private TextInputLayout f_nameLayout, l_nameLayout, weeksLayout;
    private TextInputEditText fName, lName;
    public AutoCompleteTextView weeks;
    private RelativeLayout userDetailsLayout, loading, editUserLayout;

    String fname, lname, weekString, email;

    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public FirebaseDatabase database;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userDetailsLayout = view.findViewById(R.id.userDetails);
        editUserLayout = view.findViewById(R.id.editUser);
        loading = view.findViewById(R.id.loading);

        userFullName = view.findViewById(R.id.userFullName);
        userWeek = view.findViewById(R.id.userWeek);
        close = view.findViewById(R.id.btnCloseUserDetails);
        edit = view.findViewById(R.id.btnEditUserDetails);
        logout = view.findViewById(R.id.btnLogoutAccount);

        cancelEdit = view.findViewById(R.id.btnCancelEdit);
        saveEidt = view.findViewById(R.id.btnSaveEdit);
        f_nameLayout = view.findViewById(R.id.fnameEditLayout);
        l_nameLayout = view.findViewById(R.id.lnameEditLayout);
        weeksLayout = view.findViewById(R.id.weeksEditLayout);
        fName = view.findViewById(R.id.txtEditFname);
        lName = view.findViewById(R.id.txtEditLname);
        weeks = view.findViewById(R.id.spinnerEditWeek);

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        firstName();


        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        LinearLayout layout = dialog.findViewById(R.id.userDetailsMain);
        assert layout != null;
        layout.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels)/2);


        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                dialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserLayout.setVisibility(View.VISIBLE);
                userDetailsLayout.setVisibility(View.GONE);
            }
        });

        saveEidt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fName.getText().toString().isEmpty() || lName.getText().toString().isEmpty()) {
                    f_nameLayout.setError("Required");
                    l_nameLayout.setError("Required");
                } else {
                    loading.setVisibility(View.VISIBLE);
                    f_nameLayout.setError(null);
                    l_nameLayout.setError(null);
                    Users users = new Users(fName.getText().toString(), lName.getText().toString(), email, weeks.getText().toString());
                    database.getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "Details updated", Toast.LENGTH_SHORT).show();
                                    editUserLayout.setVisibility(View.GONE);
                                    userDetailsLayout.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.INVISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.setVisibility(View.INVISIBLE);
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }
            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserLayout.setVisibility(View.GONE);
                userDetailsLayout.setVisibility(View.VISIBLE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void firstName() {
        DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null){
                    fname = users.firstname;
                    lname = users.lastname;
                    weekString = users.weeks;
                    email = users.email;

                    userFullName.setText(fname + " " + lname);
                    userWeek.setText(ordinal(weekString) + " week of Pregnancy");

                    fName.setText(fname);
                    lName.setText(lname);
                    weeks.setText(weekString);
                    SetWeeks setWeeks = new SetWeeks(UserDetailsBottomSheet.this);
                    setWeeks.execute();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private String ordinal(String weekString){

        weekString = weekString.replaceAll("[^0-9]", " ");
        weekString = weekString.replaceAll(" +", " ");

        if (weekString.equals(""))
            weekString = "-1";

        int week = Integer.parseInt(weekString.trim());
        String ordinal = "";
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (week % 100) {
            case 11:
            case 12:
            case 13:
                return week + "th";
            default:
                return week + suffixes[week % 10];
        }
    }

    private void logoutUser() {
        try{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e){
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            Log.e("Logout error", "exception", e);
        }
    }

    private static class SetWeeks extends AsyncTask<Void, Void, List<String>> {
        WeakReference<UserDetailsBottomSheet> reference;

        public SetWeeks(UserDetailsBottomSheet context){
            reference = new WeakReference<UserDetailsBottomSheet>(context);
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
            UserDetailsBottomSheet main = reference.get();
            if(main == null) return;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    main.getActivity(),
                    R.layout.drop_down_weeks,
                    strings
            );

            main.weeks.setAdapter(adapter);

            super.onPostExecute(strings);
        }
    }

}
