package com.example.telestrations.ui;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.telestrations.R;
import com.example.telestrations.model.Game;
import com.example.telestrations.model.Notepad;
import com.example.telestrations.model.Player;
import com.example.telestrations.utils.BitMapConverter;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.DatabaseUtil;
import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.LocalPlayer;
import com.example.telestrations.utils.ValueCallback;

import org.w3c.dom.Text;

import java.util.List;

public class ResultsFragment extends Fragment {

    private View frag;
    private Game activeGame;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final EmptyCallback callback = (EmptyCallback) getArguments().getSerializable(Constants.BUNDLE_ON_SUBMITTED);
        activeGame = Game.getGameInstance();
//          getting the notepads from the database

        ValueCallback<Notepad> onPlayerOrderRead = new ValueCallback<Notepad>() {
            @Override
            public void onReceiveValue(Notepad myNotepad) {
//                only showing the local player's notepad for now
//                getting it from the notepads

                List<String> payloads = myNotepad.getPayloads();
                TextView initialPhrase = frag.findViewById(R.id.initialPhrase);
                initialPhrase.setText(payloads.get(0));
                ScrollView svPayloads = frag.findViewById(R.id.payloads);

                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 20, 0, 20);

                for (int i = 1; i < payloads.size(); i++) {
                    if (i%2 == 1){
//                        drawing
                        ImageView drawing = new ImageView(getContext());
                        drawing.setImageBitmap(BitMapConverter.StringToBitMap(payloads.get(i)));
                        drawing.invalidate();

                        ll.addView(drawing);
                    }
                    else{
//                        phrase
                        TextView phrase = new TextView(getContext());
                        phrase.setText(payloads.get(i));
                        phrase.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        phrase.setGravity(Gravity.CENTER);
                        phrase.setLayoutParams(llp);
                        ll.addView(phrase);

                    }
                }
                svPayloads.addView(ll);


            }
        };

        String myId = LocalPlayer.getLocalPlayer(getContext()).getId();
        DatabaseUtil.getNotepad(activeGame.getRoomCode(),myId,activeGame.getPlayers(),onPlayerOrderRead);

        frag = inflater.inflate(R.layout.fragment_results, container, false);

        Button nextScreen = frag.findViewById(R.id.nextResults);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCallback();
            }
        });

        return frag;
    }
}
