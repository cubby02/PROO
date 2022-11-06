package com.cubbysulotions.proo.Journal;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubbysulotions.proo.BackpressedListener;
import com.cubbysulotions.proo.Firebase.Users;
import com.cubbysulotions.proo.LoadingDialog;
import com.cubbysulotions.proo.MainActivity.MainActivity;
import com.cubbysulotions.proo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

import hari.bounceview.BounceView;


public class AddEntryFragment extends Fragment implements BackpressedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_entry, container, false);
    }

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE_REQUEST = 1;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int IMAGE_PICK_CODE = 1000;
    private final int REQUEST_WRITE_STORAGE = 2;
    private String currentPhotoPath;

    private String imageFileName;

    private String id;

    private Uri contentURI, photoURI;

    EditText content, title;
    ImageView viewPhoto, editPhoto;
    Button save, more,edit;
    private NavOptions navOptions;
    RelativeLayout backLayout, editMode, viewMode;
    String modeString = "";
    TextView viewTitle, viewContent;
    String photoLink = "";

    private FirebaseAuth mAuth;
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
        navController = Navigation.findNavController(view);
        content = view.findViewById(R.id.editContent);
        title = view.findViewById(R.id.editTitle);
        more = view.findViewById(R.id.btnMore);
        save = view.findViewById(R.id.btnSaveEntry);
        edit = view.findViewById(R.id.btnEdit);
        backLayout = view.findViewById(R.id.backLayout);
        editMode = view.findViewById(R.id.editTexts);
        viewMode = view.findViewById(R.id.viewTexts);
        editPhoto = view.findViewById(R.id.editImage);
        viewPhoto = view.findViewById(R.id.viewImage);
        viewTitle = view.findViewById(R.id.textTitle);
        viewContent = view.findViewById(R.id.contentText);

        ((MainActivity)getActivity()).updateStatusBarColor("#FFFFFFFF");
        ((MainActivity)getActivity()).setLightStatusBar(true);
        ((MainActivity)getActivity()).hideNavigationBar(false);

        dialog = new LoadingDialog(getActivity());

        modeString = getArguments().getString("save");

        if (modeString.equals("save")) {
            editMode.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            viewMode.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
        } else {
            viewMode.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            editMode.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("journal").child(currentUser.getUid());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child(currentUser.getUid()).child("images");

        time = LocalTime.now();
        date = LocalDate.now();

        id = getArguments().getString("id");
        showEntry();

        BounceView.addAnimTo(save);
        BounceView.addAnimTo(edit);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                .setEnterAnim(R.anim.slide_left_to_right)
                .setExitAnim(R.anim.wait_anim)
                .setPopEnterAnim(R.anim.wait_anim)
                .setPopExitAnim(R.anim.slide_l2r_reverse)
                .build();

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_addEntryFragment_to_journalFragment, null, navOptions);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.fav:
                                toast("Favorite");
                                return true;
                            case R.id.del:
                                toast("Delete");
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.add_entry_menu2);
                popupMenu.show();
            }
        });

        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.upload:
                                if(checkGalleryPermissions()){
                                    openGallery();
                                }else{
                                    requestGalleryPermission();
                                }
                                return true;
                            case R.id.camera:
                                if(checkCamPermissions()){
                                    takePicture();
                                }else{
                                    requestCamPermission();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.add_entry_menu);
                popupMenu.show();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.getText().length() == 0){
                    title.setError("Required");
                    content.setError("Required");
                } else if(contentURI == null) {
                    Toast.makeText(getActivity(), "No Image Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.startLoading("Uploading...");
                    uploadImage();
                }
            }
        });

        viewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("photo", photoLink);
                bundle.putString("save", modeString);
                navController.navigate(R.id.action_addEntryFragment_to_zoomImageFragment, bundle);
            }
        });
    }

    private void showEntry() {
        DatabaseReference reference1 = reference.child(id);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Journal journal = dataSnapshot.getValue(Journal.class);
                if (journal != null){
                    photoLink = journal.photo;
                    viewTitle.setText(journal.title);
                    viewContent.setText(journal.content);
                    Picasso.get().load(journal.photo).into(viewPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Picasso.get().load(contentURI).into(editPhoto);
            }

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                contentURI = photoURI;
                editPhoto.setImageBitmap(setPic());
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

    private void uploadImage(){
        try {
            if(contentURI != null){
                imageFileName = UUID.randomUUID().toString() + "." +getFileExtension(contentURI);
                StorageReference ref = storageReference.child(imageFileName);
                ref.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        toast("Image Uploaded");
                        dialog.stopLoading();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String id = reference.push().getKey();
                                Journal journal = new Journal(
                                        id,
                                        title.getText().toString(),
                                        content.getText().toString(),
                                        uri.toString(),
                                        "unlike",
                                        String.valueOf(date),
                                        String.valueOf(time)
                                );

                                reference.child(id).setValue(journal);
                                title.setText("");
                                content.setText("");
                                contentURI = null;
                                editPhoto.setBackgroundResource(R.drawable.splash_bg);

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
        int targetW = editPhoto.getWidth();
        int targetH = editPhoto.getHeight();

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


    @Override
    public void onBackPressed() {
        navController.navigate(R.id.action_addEntryFragment_to_journalFragment, null, navOptions);
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