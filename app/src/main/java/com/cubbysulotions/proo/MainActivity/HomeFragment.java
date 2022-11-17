package com.cubbysulotions.proo.MainActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.Calendar.Utilities.AllTaskAdapter;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Chatbot.DBController;
import com.cubbysulotions.proo.Journal.Journal;
import com.cubbysulotions.proo.LoginSignupScreen.WelcomeActivity;
import com.cubbysulotions.proo.Firebase.Users;
import com.cubbysulotions.proo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_home, container, false);
    }

    public TextView greetingsUser, txtTrimester, txtTrimesterContent;
    public TextView txtDay, txtDate, txtAgendaTitle, txtAgendaContent;
    public TextView txtMomentsTitle, txtMomentsContent;
    public ImageView imgTrimester;
    public RoundedImageView imgMoments;
    public Button btnLogout;
    public String fname, lname, email;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public FirebaseDatabase database;
    DBController db;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            greetingsUser = view.findViewById(R.id.txtHelloUser);
            btnLogout = view.findViewById(R.id.btnLogout);
            imgMoments = view.findViewById(R.id.imgMoments);
            imgTrimester = view.findViewById(R.id.mgTrimester);
            txtTrimester = view.findViewById(R.id.txtTrimester);
            txtTrimesterContent = view.findViewById(R.id.txtTrimesterContent);
            txtDay = view.findViewById(R.id.txtDay);
            txtDate = view.findViewById(R.id.txtDate);
            txtAgendaTitle = view.findViewById(R.id.txtAgendaTitle);
            txtAgendaContent = view.findViewById(R.id.txtAgendaContent);
            txtMomentsTitle = view.findViewById(R.id.txtMomentsTitle);
            txtMomentsContent = view.findViewById(R.id.txtMomentsContent);

            //Initialize firebase
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            database = FirebaseDatabase.getInstance();
            db = new DBController(getActivity());

            DBController db = new DBController(getActivity());
            String savedMonth = db.getSavedMonth();

            if(savedMonth.equals("")){
                boolean add = db.saveMonth("First month");
                if(add){
                    Log.e(TAG, "onCreate: " + "Saved month");
                } else {
                    Log.e(TAG, "onCreate: " + "Failed");
                }
            }

            ((MainActivity)getActivity()).updateStatusBarColor("#FFB787CB");
            ((MainActivity)getActivity()).setLightStatusBar(false);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutUser();
                }
            });

            firstName();
            setTrismester();
            setAgenda();
            setMoments();


        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Main Screen error", "exception", e);
        }
    }

    private void setTrismester() {
        String savedMonth = db.getSavedMonth();
        switch (savedMonth){
            case "First month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Hi there! You are on your first month of pregnancy, I hope you're doing fine.");
                break;
            case "Second month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("You're doing great! Congrats on your second month of pregnancy.");
                break;
            case "Third month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Keep up the good work, here comes the third month of pregnancy!");
                break;
            case "Fourth month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("You're now on second trimester, fourth month here it comes!");
                break;
            case "Fifth month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Keep up the good work, continue what your doctor advice you to do. Fifth month here it comes!");
                break;
            case "Sixth month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Congrats on your sixth months of pregnancy!");
                break;
            case "Seventh month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Whoa! Look where you are right now, you're on your third trimester.");
                break;
            case "Eighth month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Hello there! You are on your eighth month of pregnancy. I hope you're doing fine.");
                break;
            case "Ninth month":
                txtTrimester.setText(savedMonth);
                txtTrimesterContent.setText("Ready for your little one? Your due is around the corner, be more careful now.");
                break;
            default:
                txtTrimester.setText("First Month");
                txtTrimesterContent.setText("Hi there! You are on your first month of pregnancy, I hope you're doing fine.");
                break;
        }


    }

    private void setAgenda() {
        try{
            List<DailyEvent> events = new ArrayList<>();
            DatabaseReference reference = database.getReference().child("events").child(currentUser.getUid());
            reference.orderByChild("dateString").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyEvent ev = new DailyEvent();
                    for(DataSnapshot data : snapshot.getChildren()){
                        ev = data.getValue(DailyEvent.class);
                        events.add(ev);
                    }
                    Collections.reverse(events);
                    String day = events.get(0).getDateString();
                    String date = events.get(0).getDateString();
                    String title = events.get(0).getName();
                    String content = events.get(0).getContent();


                    txtDay.setText(CalendarUtils.formattedMonth(LocalDate.parse(day)).toUpperCase());
                    txtDate.setText(CalendarUtils.formattedDayOnly(LocalDate.parse(date)));
                    txtAgendaTitle.setText(title);
                    if(content.length() > 120){
                        content = content.substring(0, Math.min(content.length(), 120));
                        txtAgendaContent.setText(content + "...");
                    } else {
                        txtAgendaContent.setText(content);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                }
            });

        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e(TAG, "setAgenda: ", e);
        }
    }

    private void setMoments(){
        try {
            List<Journal> list = new ArrayList<>();
            DatabaseReference reference = database.getReference().child("journal").child(currentUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Journal ev = new Journal();

                        for(DataSnapshot data : snapshot.getChildren()){
                            ev = data.getValue(Journal.class);
                            list.add(ev);
                        }

                        Collections.reverse(list);
                        String title = list.get(0).getTitle();
                        String content = list.get(0).getContent();
                        String photo = list.get(0).getPhoto();

                        Picasso.get().load(photo).into(imgMoments);
                        txtMomentsTitle.setText(title);
                        if(content.length() > 120){
                            content = content.substring(0, Math.min(content.length(), 120));
                            txtMomentsContent.setText(content + "...");
                        } else {
                            txtMomentsContent.setText(content);
                        }

                    } catch (Exception e){
                        Log.e("PopulateJournal_Error", "Message: ", e);
                        toast("Please wait...");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.getMessage() );
                }
            });
        } catch (Exception e){
            Log.e(TAG, "setMoments: ", e);
            toast("Please wait...");
        }
    }

    private void firstName() {
        DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null){
                    fname = users.firstname;
                    greetingsUser.setText("Hi, " + fname.trim() + "!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void logoutUser() {
        try{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Logout error", "exception", e);
        }
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}