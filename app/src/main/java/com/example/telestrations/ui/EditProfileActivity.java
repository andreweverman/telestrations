package com.example.telestrations.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.telestrations.ArrayAdapterWithIcon;
import com.example.telestrations.R;
import com.example.telestrations.model.Player;
import com.example.telestrations.ui.drawing.DrawView;
import com.example.telestrations.utils.LocalPlayer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class EditProfileActivity extends AppCompatActivity {
    private EditText mPlayerName;
    private Button mSetAvatar;
    private DrawView drawView;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Player player = LocalPlayer.getLocalPlayer(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        //Toast.makeText(this, player.getNameColor(), Toast.LENGTH_LONG).show();
        mPlayerName = (EditText) findViewById(R.id.playerName);
        mPlayerName.setText(player.getDisplayName());
        mPlayerName.setTextColor(player.getNameColor());
        mPlayerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //not sure of this //mPlayerName.setText(charSequence.toString());
                //set the text to the file
                //player.setDisplayName((String) charSequence);
                //LocalPlayer.storeLocalPlayerDataToMemory(getApplicationContext());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //empty
                //player.setDisplayName(editable.toString());
                //LocalPlayer.storeLocalPlayerDataToMemory(getApplicationContext());
            }
        });

        mSetAvatar = (Button) findViewById(R.id.button);
        final String[] avatarPath = {player.getImageFileName()};
        final Context context = this;
        mSetAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Field[] fields =  R.drawable.class.getDeclaredFields();
                    final android.R.drawable drawableResources = new android.R.drawable();
                    final List<Integer> lipsList = new ArrayList<>();
                    final List<String> blankStrings = new ArrayList<>();
                    int resId;
                    for (int i = 0; i < fields.length; i++) {
                        try {
                            if (fields[i].getName().contains("avatar_")){
                                resId = fields[i].getInt(drawableResources);
                                lipsList.add(resId);
                                //blankStrings.add("");
                                blankStrings.add(fields[i].getName().toString());
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }

                ListAdapter adapter = new ArrayAdapterWithIcon(context, blankStrings, lipsList);

                new AlertDialog.Builder(context).setTitle("Select Image")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //Toast.makeText(context, "Number Selected: " + item
                                //        + "\n Avatar Selected: " + blankStrings.get(item), Toast.LENGTH_LONG).show();
                                avatarPath[0] = blankStrings.get(item);
                            }
                        }).show();
            }
        });
        drawView = findViewById(R.id.draw_view);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        drawView.init(metrics);
        Button color_button = findViewById(R.id.color_switch);
        color_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.nameColorSwitch();
            }
        });
        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = mPlayerName.getText().toString();
                player.setDisplayName(playerName);
                player.setImageFileName(avatarPath[0]);
                player.setNameColor(drawView.getCurrentColor());
                LocalPlayer.storeLocalPlayerDataToMemory(getApplicationContext());
                mPlayerName.setTextColor(drawView.getCurrentColor());
                finish();
                }
            });
    }
}

