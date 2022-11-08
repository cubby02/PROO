package com.cubbysulotions.proo.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cubbysulotions.proo.Calendar.AddEventFragment;
import com.cubbysulotions.proo.Calendar.CalendarFragment;
import com.cubbysulotions.proo.Calendar.EditEventFragment;
import com.cubbysulotions.proo.Calendar.ViewEventFragment;
import com.cubbysulotions.proo.Calendar.WeeklyCalendarFragment;
import com.cubbysulotions.proo.Journal.AddEntryFragment;
import com.cubbysulotions.proo.Journal.JournalFragment;
import com.cubbysulotions.proo.Journal.ZoomImageFragment;
import com.cubbysulotions.proo.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            home();

            navigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {
                    Fragment fragment = null;

                    switch (i){
                        case R.id.nav_home:
                            fragment = new HomeFragment();
                            break;
                        case R.id.nav_chat:
                            fragment = new ChatbotFragment();
                            break;
                        case R.id.nav_calendar:
                            fragment = new CalendarContainerFragment();
                            break;
                        case R.id.nav_todo:
                            fragment = new JournalContainer();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                }
            });
        } catch (Exception e){
            Log.e("MainAct Error", "Message: ", e);
        }
    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void setLightStatusBar(boolean flag){
        View view = getWindow().getDecorView();
        if(flag){
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            view.setSystemUiVisibility(0);
        }

    }

    public void hideNavigationBar(boolean flag){
        if(flag){
            navigationView.setVisibility(View.GONE);
        } else{
            navigationView.setVisibility(View.VISIBLE);
        }

    }

    public void home(){
        navigationView = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        navigationView.setItemSelected(R.id.nav_home, true);
    }



    @Override
    public void onBackPressed() {

        if(AddEntryFragment.backpressedlistener !=null){
            // accessing the backpressedlistener of fragment
            AddEntryFragment.backpressedlistener.onBackPressed();
        } else if (ZoomImageFragment.backpressedlistener != null){
            ZoomImageFragment.backpressedlistener.onBackPressed();
        } else if (WeeklyCalendarFragment.backpressedlistener != null){
            WeeklyCalendarFragment.backpressedlistener.onBackPressed();
        } else if (AddEventFragment.backpressedlistener != null){
            AddEventFragment.backpressedlistener.onBackPressed();
        } else if (ViewEventFragment.backpressedlistener != null){
            ViewEventFragment.backpressedlistener.onBackPressed();
        } else if (EditEventFragment.backpressedlistener != null){
            EditEventFragment.backpressedlistener.onBackPressed();
        } else if (CalendarFragment.backpressedlistener != null){
            CalendarFragment.backpressedlistener.onBackPressed();
        } else if (JournalFragment.backpressedlistener != null){
            JournalFragment.backpressedlistener.onBackPressed();
        } else if (ChatbotFragment.backpressedlistener != null){
            ChatbotFragment.backpressedlistener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}