package com.example.telestrations.ui.drawing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.telestrations.R;
import com.example.telestrations.utils.BitMapConverter;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.ShakeDetector;
import com.example.telestrations.utils.ValueCallback;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DrawFragment extends Fragment {

    private final String tag = "DrawFragment";
    private static View frag;
    private DrawView drawView;
    private TextView brush;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                drawView.clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frag = inflater.inflate(R.layout.fragment_draw, container, false);

        //get the callback to perform on submit, and the phrase to draw
        final ValueCallback<String> onSubmit = (ValueCallback<String>) getArguments().getSerializable(Constants.BUNDLE_ON_SUBMITTED);
        String phraseToDraw = getArguments().getString(Constants.BUNDLE_PAYLOAD);

        drawView = frag.findViewById(R.id.draw_view);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        drawView.init(metrics);

        // Setting up clear button
        Button clear_button = frag.findViewById(R.id.clear);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clear();

            }
        });

        // Setting up color button
        Button color_button = frag.findViewById(R.id.color_switch);
        color_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.colorSwitch();
            }
        });

        //Button click listener
        brush = frag.findViewById(R.id.brush_size);
        Button brush_button = frag.findViewById(R.id.brush_size);
        brush_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                brush.setText(drawView.changeBrush());
            }
        });

        Button submit_button = frag.findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = BitMapConverter.BitMapToString(drawView.getmBitmap());
                onSubmit.onReceiveValue(payload);
            }
        });

        TextView textViewPhrase = frag.findViewById(R.id.phrase);

        String phrase= phraseToDraw;
        textViewPhrase.setText(phrase);
        return frag;
    }
}
