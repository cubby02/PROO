package com.cubbysulotions.proo.Journal;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.BackpressedListener;
import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Journal.Journal;
import com.cubbysulotions.proo.Journal.JournalAdapter;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.MainActivity.MainActivity;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;

import hari.bounceview.BounceView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class JournalFragment extends Fragment implements JournalAdapter.OnItemClickListener, BackpressedListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }


    Button addNewEntry;
    TextView noPost;
    RecyclerView journalRV;

    JournalAdapter journalAdapter;
    List<Journal> list;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    LoadingDialog dialog;
    NavController navController;

    LocalTime time;
    LocalDate date;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //content = view.findViewById(R.id.contentTxt);
        //uploadImage = view.findViewById(R.id.imageContainer);
        //post = view.findViewById(R.id.postBtn);
        noPost = view.findViewById(R.id.txtNoJournal);
        journalRV = view.findViewById(R.id.timeLineRV);
        addNewEntry = view.findViewById(R.id.btnAddNewEntry);
        BounceView.addAnimTo(addNewEntry);
        //camera = view.findViewById(R.id.camera);

        dialog = new LoadingDialog(getActivity());
        ((MainActivity)getActivity()).hideNavigationBar(false);

        //((MainActivity)getActivity()).updateStatusBarColor("#FFFFFFFF");
        //((MainActivity)getActivity()).setLightStatusBar(true);

        list = new ArrayList<>();
        journalAdapter = new JournalAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        journalRV.setLayoutManager(manager);
        journalAdapter.setHasStableIds(true);
        journalRV.setAdapter(journalAdapter);
        journalRV.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("journal").child(currentUser.getUid());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child(currentUser.getUid()).child("images");

        time = LocalTime.now();
        date = LocalDate.now();

        populateTimeline();

        navController = Navigation.findNavController(view);

        journalAdapter.setOnItemClickListener(JournalFragment.this);

        addNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("save", "save");
                navController.navigate(R.id.action_journalFragment_to_addEntryFragment, bundle);
            }
        });
    }


    private void populateTimeline() {
        try {
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
                        journalAdapter.updateDataSet(list);

                        if(journalAdapter.getItemCount() < 1){
                            noPost.setVisibility(View.VISIBLE);
                        } else {
                            noPost.setVisibility(View.GONE);
                        }
                    } catch (Exception e){
                        Log.e("PopulateJournal_Error", "Message: ", e);
                        toast("Please wait...");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e){
            Log.e("MainAct Error", "Message: ", e);
            toast("Please wait...");
        }
    }


    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        Journal journal = list.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("save", "edit");
        bundle.putString("id", journal.getId());
        navController.navigate(R.id.action_journalFragment_to_addEntryFragment, bundle);
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).home();
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