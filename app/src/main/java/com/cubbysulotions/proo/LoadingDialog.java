package com.cubbysulotions.proo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

public class LoadingDialog {

    Context context;
    Dialog dialog;

    public LoadingDialog(Context myContext){
        context = myContext;
    }

    public void startLoading(String loadingLabel){
        dialog = new Dialog(context);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(false);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //initialize the textview

        final TextView label = dialog.findViewById(R.id.textView26);
        label.setText(loadingLabel +"...");

        dialog.show();
    }

    public void addedToCart(){
        dialog = new Dialog(context);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(false);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //initialize the textview
        dialog.show();
    }

    public void stopLoading(){
        dialog.dismiss();
    }
}
