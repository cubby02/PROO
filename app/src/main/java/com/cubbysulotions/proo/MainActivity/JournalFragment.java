package com.cubbysulotions.proo.MainActivity;

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

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;
import com.cubbysulotions.proo.Calendar.Utilities.Events.DailyEvent;
import com.cubbysulotions.proo.Journal.Journal;
import com.cubbysulotions.proo.Journal.JournalAdapter;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.cubbysulotions.proo.Calendar.Utilities.CalendarUtils;

import hari.bounceview.BounceView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class JournalFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE_REQUEST = 1;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int IMAGE_PICK_CODE = 1000;
    private final int REQUEST_WRITE_STORAGE = 2;
    private String currentPhotoPath;

    private Uri contentURI, photoURI;

    private EditText content;
    private ImageView uploadImage, camera;
    private Button post;
    private TextView noPost;
    private RecyclerView journalRV;
    private String imageFileName;

    JournalAdapter journalAdapter;
    List<Journal> list;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;

    LocalTime time;
    LocalDate date;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = view.findViewById(R.id.contentTxt);
        uploadImage = view.findViewById(R.id.imageContainer);
        post = view.findViewById(R.id.postBtn);
        noPost = view.findViewById(R.id.txtNoJournal);
        BounceView.addAnimTo(post);
        journalRV = view.findViewById(R.id.timeLineRV);
        camera = view.findViewById(R.id.camera);

        ((MainActivity)getActivity()).updateStatusBarColor("#FFFFFFFF");
        ((MainActivity)getActivity()).setLightStatusBar(true);

        list = new ArrayList<>();
        journalAdapter = new JournalAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        journalRV.setLayoutManager(manager);
        journalAdapter.setHasStableIds(true);
        journalRV.setAdapter(journalAdapter);

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


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkGalleryPermissions()){
                    openGallery();
                }else{
                    requestGalleryPermission();
                }
                //choosePhoto();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCamPermissions()){
                    takePicture();
                }else{
                    requestCamPermission();
                }
                //openCamera();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentURI == null && content.getText().length() == 0){
                    content.setError("Required");
                    Toast.makeText(getActivity(), "No Image Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK
                    && data != null && data.getData() != null){
                contentURI = data.getData();
                Picasso.get().load(contentURI).into(uploadImage);
            }

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                contentURI = photoURI;
                uploadImage.setImageBitmap(setPic());
                //Picasso.get().load(contentURI).into(uploadImage);
            }
        } catch (Exception e) {
            Log.e("Add Photo", "exception", e);
            toast("Something went wrong, please try again");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraPermission){
                        toast("Permissions Granted..");
                        takePicture();
                    }else{
                        toast("Permissions denied..");
                    }
                }
            }
            case IMAGE_PICK_CODE: {
                if (grantResults.length > 0) {
                    boolean galleryPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(galleryPermission){
                        toast("Permissions Granted..");
                        openGallery();
                    }else{
                        toast("Permissions denied..");
                    }
                }
            }
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storagePermission){
                        toast("Permissions Granted..");
                    }else{
                        toast("Permissions denied..");
                    }
                }
            }
        }
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
                        journalAdapter.updateDataSet(list);
                        journalRV.setHasFixedSize(true);

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

    private void uploadImage(){
        try {
            if(contentURI != null){
                imageFileName = UUID.randomUUID().toString() + "." +getFileExtension(contentURI);
                StorageReference ref = storageReference.child(imageFileName);
                ref.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        toast("Image Uploaded");
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String id = reference.push().getKey();
                                Journal journal = new Journal(
                                        content.getText().toString(),
                                        uri.toString(),
                                        String.valueOf(date),
                                        String.valueOf(time),
                                        "unlike"
                                );

                                reference.child(id).setValue(journal);

                                journalAdapter.clear();
                                content.setText("");
                                uploadImage.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_24);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Image not uploaded", Toast.LENGTH_SHORT).show();
                        Log.e("Upload image", "Error ", e);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    /*
                    double progress
                            = (100.0
                            * taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded "
                                    + (int)progress + "%"); */
                    }
                });
            }
        } catch (Exception e){
            Log.e("upload_error", "Message: ", e);
            toast("Please wait...");
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private boolean checkGalleryPermissions(){
        int camPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void requestGalleryPermission(){
        int PERMISSION_CODE = 1001;
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    private boolean checkCamPermissions(){
        int camPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCamPermission(){
        int PERMISSION_CODE = 200;
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
    }

    private boolean checkStoragePermissions(){
        int camPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return camPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void takePicture() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if(checkStoragePermissions()){
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    File compressedFile = null;
                    try {
                        photoFile = createImageFile();
                        //compressedFile = new Compressor(getActivity()).compressToFile(photoFile);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.cubbysulotions.proo",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
            else{
                requestStoragePermission();
            }
        } catch (Exception e){
            Log.e("TakePic_Error", "Message: ", e);
            toast("Please wait...");
        }


    }

    // creating root img file  (only for the application and cannot be seen in gallery)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGES_" + timeStamp + "_";
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void requestStoragePermission(){
        int PERMISSION_CODE = 201;
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = uploadImage.getWidth();
        int targetH = uploadImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        return bitmap;
    }

    private void toast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}