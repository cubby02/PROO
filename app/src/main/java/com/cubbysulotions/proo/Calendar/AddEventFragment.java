package com.cubbysulotions.proo.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cubbysulotions.proo.BackpressedListener;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.Calendar.Utilities.AlarmReceiver;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
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
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.localDateToCalendar;


public class AddEventFragment extends Fragment implements BackpressedListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_event_add, container, false);
    }

    private Button btnSave, btnCancel, btnDate, btnTime;
    private TextView eventDate, eventTime;
    private EditText eventName, eventContent;
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
        btnSave = view.findViewById(R.id.btnSaveEvent);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);
        eventName = view.findViewById(R.id.eventName);
        eventContent = view.findViewById(R.id.eventContent);
        navController = Navigation.findNavController(view);

        time = LocalTime.now();
        btnDate.setText(CalendarUtils.formattedDate(selectedDate));
        btnTime.setText(CalendarUtils.formattedTime(time));

        loadingDialog = new LoadingDialog(getActivity());

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("events").child(currentUser.getUid());

        Random random = new Random();
        requestCode = random.nextInt(9999 - 1000 + 1) + 1000;
        notificationID = random.nextInt(9999 - 1000 + 1) + 1000;

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveActionEvent();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpDateDialog();
            }
        });
    }

    private void popUpDateDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                selectedDate = LocalDate.of(year1, month1 + 1, day1);
                btnDate.setText(CalendarUtils.formattedDate(selectedDate));
                year = year1;
                months = month1;
                day = day1;
            }
        };

        year = localDateToCalendar(selectedDate).get(Calendar.YEAR);
        months = localDateToCalendar(selectedDate).get(Calendar.MONTH);
        day = localDateToCalendar(selectedDate).get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, year, months, day);
        datePickerDialog.show();

    }

    private void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                time = LocalTime.of(selectedHour, selectedMinute);
                btnTime.setText(CalendarUtils.formattedTime(time));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener,time.getHour(), time.getMinute(), false);
        timePickerDialog.show();

    }

    private void cancel(){
        String flag = getArguments().getString("flag");
        String date = getArguments().getString("date");
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("source", "add");

        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                .setEnterAnim(R.anim.slide_in_down_reverse)
                .setExitAnim(R.anim.wait_anim)
                .setPopEnterAnim(R.anim.wait_anim)
                .setPopExitAnim(R.anim.slide_in_down)
                .build();

        navController.navigate(R.id.action_eventEditFragment_to_weeklyCalendarFragment, bundle, navOptions);
    }

    private void setNotification(){

        if (months == 0 && day == 0 && year == 0){
            year = localDateToCalendar(selectedDate).get(Calendar.YEAR);
            months = localDateToCalendar(selectedDate).get(Calendar.MONTH);
            day = localDateToCalendar(selectedDate).get(Calendar.DAY_OF_MONTH);
        }

        if (hour == 0 && minute == 0){
            hour = time.getHour();
            minute = time.getMinute();
        }

        //set notification id and text
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("notificationID", notificationID);
        intent.putExtra("title", eventNameTxt);
        intent.putExtra("content", eventContent.getText().toString());
        intent.putExtra("delete", "null");

        //getBroadcast context, requestCode, intent, flags
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(),
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        //Create time
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);

        startTime.set(Calendar.MONTH, months);
        startTime.set(Calendar.DAY_OF_MONTH, day);
        startTime.set(Calendar.YEAR, year);

        long alarmStart = startTime.getTimeInMillis();

        //set alarm
        //set type millisecond, intent
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStart, alarmIntent);
        toast("Notification set");
    }

    private void saveActionEvent() {
        try {
            if(eventName.getText().length() == 0){
                eventName.setError("Required");
            } else {
                loadingDialog.startLoading("Saving...");
                String flag = getArguments().getString("flag");
                eventNameTxt = eventName.getText().toString();

                String id = reference.push().getKey();
                CalendarEvents newEventToDB = new CalendarEvents(eventNameTxt,
                        id,
                        String.valueOf(selectedDate),
                        String.valueOf(time),
                        String.valueOf(requestCode),
                        String.valueOf(notificationID),
                        eventContent.getText().toString());


            reference.child(id).setValue(newEventToDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        loadingDialog.stopLoading();
                        setNotification();

                        cancel();
                    } else {
                        loadingDialog.stopLoading();
                        toast("Error: " + task.getException().getMessage());
                    }
                }
            });
            }

        } catch (Exception e){
            toast("Something went wrong, please try again");
            Log.e("Logout error", "exception", e);
        }




    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    public static BackpressedListener backpressedlistener;

    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }
}