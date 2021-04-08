package com.example.telestrations.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.telestrations.R;
import com.example.telestrations.utils.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LobbyFragment extends Fragment {

    private View frag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_lobby, container, false);

        String roomCode = getArguments().getString(Constants.BUNDLE_ROOM_CODE);
        ((TextView) inflatedView.findViewById(R.id.roomCode)).setText(roomCode);

        if(!getArguments().getBoolean(Constants.BUNDLE_IS_HOST)){
            inflatedView.findViewById(R.id.startGameBtn).setEnabled(false);
        }

        //set onClick listener method for start game button
        inflatedView.findViewById(R.id.startGameBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LobbyActivity) getActivity()).startGame();
            }
        });

        frag = inflatedView;

        return inflatedView;
    }

    public void updatePlayerListUI(ListAdapter adapter){
        ListView playerList = frag.findViewById(R.id.playerList);
        playerList.setAdapter(adapter);
    }
}
