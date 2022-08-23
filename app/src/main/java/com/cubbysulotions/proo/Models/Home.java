package com.cubbysulotions.proo.Models;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.cubbysulotions.proo.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            /*
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if(firebaseUser != null){
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } */
        } catch(Exception e){
            Log.e("Error", "Message: ", e);
        }

    }
}
