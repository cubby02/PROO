package com.cubbysulotions.proo.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.cubbysulotions.proo.BackpressedListener;
import com.cubbysulotions.proo.Calendar.Utilities.AlarmReceiver;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarEvents;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.localDateToCalendar;
import static com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils.selectedDate;


public class EditEventFragment extends Fragment implements BackpressedListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_event_edit, container, false);
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

    //int requestCode, notificationID;

    String eventNameTxt;

    String id, name, content, dateString, timeString, notificationID, requestCode;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSave = view.findViewById(R.id.btnEditSaveEvent);
        btnCancel = view.findViewById(R.id.btnEditCancel);
        btnDate = view.findViewById(R.id.btnEditDate);
        btnTime = view.findViewById(R.id.btnEditTime);
        eventName = view.findViewById(R.id.editEventName);
        eventContent = view.findViewById(R.id.editEventContent);
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

        id = getArguments().getString("id");
        name = getArguments().getString("name");
        content = getArguments().getString("content");
        dateString = getArguments().getString("dateString");
        timeString = getArguments().getString("timeString");
        notificationID = getArguments().getString("notificationID");
        requestCode = getArguments().getString("requestCode");

        Log.e("Request", "Request Code: " + requestCode);

        showDetails();

        //Random random = new Random();
        //requestCode = random.nextInt(9999 - 1000 + 1) + 1000;
        //notificationID = random.nextInt(9999 - 1000 + 1) + 1000;

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
        bundle.putString("id", getArguments().getString("id"));

        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                .setEnterAnim(R.anim.slide_in_down_reverse)
                .setExitAnim(R.anim.wait_anim)
                .setPopEnterAnim(R.anim.wait_anim)
                .setPopExitAnim(R.anim.slide_in_down)
                .build();

        navController.navigate(R.id.action_editEventFragment_to_viewEventFragment, bundle, navOptions);
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

        //toast("Month: " + months + ", day: " + day);
        //toast(hour + ":" + minute);

        //Calendar startTime = Calendar.getInstance();
        //selectedDate = startTime.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        //set notification id and text
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("requestCode", Integer.valueOf(requestCode));
        intent.putExtra("notificationID", Integer.valueOf(notificationID));
        intent.putExtra("todo", eventNameTxt);
        intent.putExtra("delete", "null");

        //getBroadcast context, requestCode, intent, flags
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(),
                Integer.valueOf(requestCode),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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

                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        String date = getArguments().getString("date");
                        bundle.putString("date", date);
                        navController.navigate(R.id.action_editEventFragment_to_viewEventFragment, bundle);
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

    private void showDetails(){
        DatabaseReference ref1 = reference.child(id);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CalendarEvents calendarEvents = snapshot.getValue(CalendarEvents.class);
                if (calendarEvents != null){
                    eventName.setText(calendarEvents.name);
                    eventContent.setText(calendarEvents.content);
                    btnDate.setText(CalendarUtils.formattedDate(LocalDate.parse(calendarEvents.dateString)));
                    btnTime.setText(CalendarUtils.formattedTime(LocalTime.parse(calendarEvents.timeString)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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