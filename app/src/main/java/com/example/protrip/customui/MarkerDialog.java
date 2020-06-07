package com.example.protrip.customui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.protrip.R;
import com.google.android.gms.maps.model.Marker;

public class MarkerDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    private EditText title;
    private ImageButton delete, message, add;
    private Marker marker;

    public MarkerDialog(Activity activity, Marker marker) {
        super(activity);
        this.activity = activity;
        this.marker = marker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.marker_dialog); // Set your custom layout for the dialog

        initUI();

    }

    private void initUI() {

        title = findViewById(R.id.title);
        delete = findViewById(R.id.delete);
        message = findViewById(R.id.message);
        add = findViewById(R.id.add);
        delete.setOnClickListener(this);
        message.setOnClickListener(this);
        add.setOnClickListener(this);
        title.setText(marker.getTitle());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.delete:
                deleteMarker();
                break;
                
            case R.id.message:
                messageMarker();
                break;
                
            case R.id.add:
                add();
                break;
                
            default:
                break;
        }
        dismiss();
    }

    private void add() {
    }

    private void messageMarker() {
    }

    private void deleteMarker() {

        marker.remove();
    }
}