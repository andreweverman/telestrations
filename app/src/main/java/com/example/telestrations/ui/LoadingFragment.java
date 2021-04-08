package com.example.telestrations.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.example.telestrations.R;
import com.example.telestrations.utils.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class LoadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final String message = getArguments().getString(Constants.LOADING_MESSAGE);
        View frag = inflater.inflate(R.layout.fragment_loading, container, false);

        TextView loadingMessage = frag.findViewById(R.id.loadingMessage);
        loadingMessage.setText(message);
        return frag;
    }
}
