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
    public String fname, lname, email, weeks;
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
            SetAgenda setAgenda = new SetAgenda(HomeFragment.this);
            setAgenda.execute();



        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Main Screen error", "exception", e);
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
                    weeks = users.weeks;
                    SetCurrentWeek setCurrentWeek = new SetCurrentWeek(HomeFragment.this);
                    setCurrentWeek.onPostExecute(weeks);
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

    private static class SetCurrentWeek extends AsyncTask<String, Void, String>{
        WeakReference<HomeFragment> reference;

        public SetCurrentWeek(HomeFragment context){
            reference = new WeakReference<HomeFragment>(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            strings[0] = strings[0].replaceAll("[^0-9]", " ");
            strings[0] = strings[0].replaceAll(" +", " ");

            if (strings[0].equals(""))
                return "-1";

            return strings[0];
        }

        @Override
        protected void onPostExecute(String s) {
            HomeFragment main = reference.get();
            if(main == null) return;

            s = s.replaceAll("[^0-9]", " ");
            s = s.replaceAll(" +", " ");

            if (s.equals(""))
                s = "-1";

            int week = Integer.parseInt(s.trim());
            String ordinal = "";
            String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
            switch (week % 100) {
                case 11:
                case 12:
                case 13:
                    ordinal = week + "th";
                default:
                    ordinal = week + suffixes[week % 10];
            }

            if (week <= 4){
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Hi there! You are on your first month of pregnancy, I hope you're doing fine.");
            } else if (week <= 8) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("You're doing great! Congrats on your second month of pregnancy.");
            } else if (week <= 12) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Keep up the good work, here comes the third month of pregnancy!");
            } else if (week <= 16) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("You're now on second trimester, fourth month here it comes!");
            } else if (week <= 20) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Keep up the good work, continue what your doctor advice you to do. Fifth month here it comes!");
            } else if (week <= 24) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Congrats on your sixth months of pregnancy!");
            } else if (week <= 28) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Whoa! Look where you are right now, you're on your third trimester.");
            } else if (week <= 32) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Hello there! You are on your eighth month of pregnancy. I hope you're doing fine.");
            } else if (week <= 36) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Ready for your little one? Your due is around the corner, be more careful now.");
            } else if (week <= 40) {
                main.txtTrimester.setText(ordinal + " week");
                main.txtTrimesterContent.setText("Please contact your medical provider");
            }

            super.onPostExecute(s);
        }
    }

    private static class SetAgenda extends AsyncTask<Void, Void, Void>{
        WeakReference<HomeFragment> reference;

        public SetAgenda(HomeFragment context){
            reference = new WeakReference<HomeFragment>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            HomeFragment main = reference.get();
            if(main == null) return;
            setAgenda();
            setMoments();
            super.onPostExecute(unused);
        }

        private void setAgenda() {
            HomeFragment main = reference.get();
            if(main == null) return;
            try{
                List<DailyEvent> events = new ArrayList<>();
                DatabaseReference reference = main.database.getReference().child("events").child(main.currentUser.getUid());
                reference.orderByChild("dateString").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
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


                            main.txtDay.setText(CalendarUtils.formattedMonth(LocalDate.parse(day)).toUpperCase());
                            main.txtDate.setText(CalendarUtils.formattedDayOnly(LocalDate.parse(date)));
                            main.txtAgendaTitle.setText(title);
                            if(content.length() > 120){
                                content = content.substring(0, Math.min(content.length(), 120));
                                main.txtAgendaContent.setText(content + "...");
                            } else {
                                main.txtAgendaContent.setText(content);
                            }
                        } catch (Exception e) {
                            LocalDate date = LocalDate.now();
                            main.txtDay.setText(CalendarUtils.formattedMonth(date).toUpperCase());
                            main.txtDate.setText(CalendarUtils.formattedDayOnly(date));
                            main.txtAgendaTitle.setText("No current Agenda");
                            main.txtAgendaContent.setText("No content");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });

            } catch (Exception e){
                Toast.makeText(main.getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "setAgenda: ", e);
            }
        }

        private void setMoments(){
            HomeFragment main = reference.get();
            if(main == null) return;
            try {
                List<Journal> list = new ArrayList<>();
                DatabaseReference reference = main.database.getReference().child("journal").child(main.currentUser.getUid());
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

                            Picasso.get().load(photo).into(main.imgMoments);
                            main.txtMomentsTitle.setText(title);
                            if(content.length() > 120){
                                content = content.substring(0, Math.min(content.length(), 120));
                                main.txtMomentsContent.setText(content + "...");
                            } else {
                                main.txtMomentsContent.setText(content);
                            }

                        } catch (Exception e){
                            main.txtMomentsTitle.setText("No added entry");
                            main.txtMomentsContent.setText("No content");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage() );
                    }
                });
            } catch (Exception e){
                Toast.makeText(main.getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "setMoments: ", e);
            }
        }
    }
}