package com.akhdmny.driver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.akhdmny.driver.Utils.AppUtils;
import com.akhdmny.driver.Utils.Dialog;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


/*
 * Created by troy379 on 05.04.17.
 */
public abstract class DemoDialogsActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        };
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
                false);
    }
}
