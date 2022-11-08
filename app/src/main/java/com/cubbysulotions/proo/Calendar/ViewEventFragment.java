package com.cubbysulotions.proo.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.cubbysulotions.proo.Calendar.Utilities.AlarmReceiver;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.localDateToCalendar;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;


public class ViewEventFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_event_view, container, false);
    }

    private Button btnMore, btnCancel, btnDate, btnTime;
    private TextView eventName, eventContent;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;

    LocalTime time;

    NavController navController;

    LoadingDialog loadingDialog;

    int hour, minute;
    int year, months, day;

    int requestCode, notificationID;

    String eventNameTxt;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnMore = view.findViewById(R.id.btnDetailsMore);
        btnCancel = view.findViewById(R.id.btnCancelDetails);
        btnDate = view.findViewById(R.id.btnDateTxt);
        btnTime = view.findViewById(R.id.btnTimeTxt);
        eventName = view.findViewById(R.id.eventNameTxt);
        eventContent = view.findViewById(R.id.eventContentTxt);
        navController = Navigation.findNavController(view);

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("events").child(currentUser.getUid());

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreMenu(view);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    private void cancel(){
        String flag = getArguments().getString("flag");
        String date = getArguments().getString("date");
        Bundle bundle = new Bundle();
        bundle.putString("date", date);

        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                .setEnterAnim(R.anim.slide_in_down_reverse)
                .setExitAnim(R.anim.wait_anim)
                .setPopEnterAnim(R.anim.wait_anim)
                .setPopExitAnim(R.anim.slide_in_down)
                .build();

        navController.navigate(R.id.action_viewEventFragment_to_weeklyCalendarFragment, bundle, navOptions);
    }

    private void moreMenu(View view) {
        try {
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.edit:
                            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                                    .setEnterAnim(R.anim.slide_in_up)
                                    .setExitAnim(R.anim.wait_anim)
                                    .setPopEnterAnim(R.anim.wait_anim)
                                    .setPopExitAnim(R.anim.slide_in_down)
                                    .build();

                            Bundle bundle = new Bundle();
                            String date = getArguments().getString("date");
                            bundle.putString("date", date);
                            navController.navigate(R.id.action_viewEventFragment_to_editEventFragment, bundle, navOptions);
                            return true;
                        case R.id.delete:
                            //delete();
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popupMenu.inflate(R.menu.view_details_menu);
            popupMenu.show();

        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Logout error", "exception", e);
        }




    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}