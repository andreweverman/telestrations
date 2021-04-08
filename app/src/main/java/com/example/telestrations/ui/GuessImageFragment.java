package com.example.telestrations.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.telestrations.R;
import com.example.telestrations.utils.BitMapConverter;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.ValueCallback;

public class GuessImageFragment extends Fragment {

    private View frag;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frag = inflater.inflate(R.layout.fragment_guess_picture, container, false);

        //get the callback to perform on submit
        final ValueCallback<String> onSubmit = (ValueCallback<String>) getArguments().getSerializable(Constants.BUNDLE_ON_SUBMITTED);

        //set the image from the bundle
        final String encodedImage = getArguments().getString(Constants.BUNDLE_PAYLOAD);
        final ImageView display = frag.findViewById(R.id.display);
        display.setImageBitmap(BitMapConverter.StringToBitMap(encodedImage));
        display.invalidate();

        //Button on click (submit)
        Button submitPhrase = frag.findViewById(R.id.submitGuessImage);
        submitPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputPhrase = frag.findViewById(R.id.guessImage);
                String phrase = inputPhrase.getText().toString();

                onSubmit.onReceiveValue(phrase);
            }
        });

        return frag;
    }
}
