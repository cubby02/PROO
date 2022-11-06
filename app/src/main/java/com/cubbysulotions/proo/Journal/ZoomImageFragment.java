package com.cubbysulotions.proo.Journal;

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
import android.widget.ImageView;

import com.cubbysulotions.proo.BackpressedListener;
import com.cubbysulotions.proo.MainActivity.MainActivity;
import com.cubbysulotions.proo.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static org.apache.commons.lang3.StringUtils.split;

public class ZoomImageFragment extends Fragment implements BackpressedListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zoom_image, container, false);
    }


    ImageView photo;
    Button back;
    NavController navController;
    NavOptions navOptions;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photo = view.findViewById(R.id.photoZoom);
        back = view.findViewById(R.id.btnBackDetails);
        navOptions = new NavOptions.Builder().setPopUpTo(R.id.journalFragment, true)
                .setEnterAnim(R.anim.slide_left_to_right)
                .setExitAnim(R.anim.wait_anim)
                .setPopEnterAnim(R.anim.wait_anim)
                .setPopExitAnim(R.anim.slide_l2r_reverse)
                .build();
        navController = Navigation.findNavController(view);

        ((MainActivity)getActivity()).updateStatusBarColor("#000000");
        ((MainActivity)getActivity()).setLightStatusBar(false);
        ((MainActivity)getActivity()).hideNavigationBar(true);

        Picasso.get().load(getArguments().getString("photo")).into(photo);

        String url = getArguments().getString("photo");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

    }

    private void back(){
        Bundle bundle = new Bundle();
        bundle.putString("id", getArguments().getString("id"));
        bundle.putString("save", getArguments().getString("save"));
        navController.navigate(R.id.action_zoomImageFragment_to_addEntryFragment, bundle, navOptions);
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

    @Override
    public void onBackPressed() {
        back();
    }
}