package com.cubbysulotions.proo.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.cubbysulotions.proo.Calendar.CalendarFragment;
import com.cubbysulotions.proo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            navigationView = findViewById(R.id.bottom_nav);
            getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
            navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

            navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;

                    switch (item.getItemId()){
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
                            fragment = new TodoFragment();
                            break;
                        case R.id.nav_basicfood:
                            fragment = new BasicFoodFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                    return true;
                }
            });
        } catch (Exception e){
            Log.e("MainAct Error", "Message: ", e);
        }


    }


}