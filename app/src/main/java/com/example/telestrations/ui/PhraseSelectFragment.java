package com.example.telestrations.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.telestrations.R;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.ValueCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PhraseSelectFragment extends Fragment {

    private View frag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ValueCallback<String> callback = (ValueCallback<String>) getArguments().getSerializable(Constants.BUNDLE_ON_SUBMITTED);

        frag = inflater.inflate(R.layout.fragment_phrase_select, container, false);

        Button submitPhrase = frag.findViewById(R.id.submitPhrase);
        submitPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputPhrase = frag.findViewById(R.id.inputPhrase);
                String phrase = inputPhrase.getText().toString();
                callback.onReceiveValue(phrase);
            }
        });

        return frag;
    }
}
