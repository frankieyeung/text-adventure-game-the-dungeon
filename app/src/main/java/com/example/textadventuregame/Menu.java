package com.example.textadventuregame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    public static final String SAVE_FILE = "save";
    public static final String PLAYERPOSITION = "playerPosition";
    public static final String GOBLINPOSITION = "goblinPosition";
    public static final String ZOMBIEPOSITION = "zombiePosition";
    public static final String CYCLOPSPOSITION = "cyclopsPosition";
    public static final String DIABLOPOSITION = "diabloPosition";
    public static final String PLAYERINVENTORYSIZE = "playerInventorySize";





    SharedPreferences sharedPrefs;
    MediaPlayer menuMediaPlayer;
    Button newGameButton, loadGameButton, helpButton, aboutButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        playMenuSong();
        setupControls();
    }   //  protected void onCreate(Bundle savedInstanceState)

    protected void playMenuSong() {
        menuMediaPlayer = MediaPlayer.create(Menu.this, R.raw.menusong);
        menuMediaPlayer.start();

    }

    protected void onPause()
    {
        super.onPause();
        menuMediaPlayer.pause();
    }

    protected void onResume()
    {
        super.onResume();
        menuMediaPlayer.start();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        menuMediaPlayer.release();
    }

    protected void setupControls()
    {
        newGameButton = findViewById(R.id.newGameButton);
        loadGameButton = findViewById(R.id.loadGameButton);
        helpButton = findViewById(R.id.helpButton);
        aboutButton = findViewById(R.id.aboutButton);
        exitButton = findViewById(R.id.exitButton);


        newGameButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        loadGameButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);

                sharedPrefs = getSharedPreferences(SAVE_FILE, MODE_PRIVATE);
                int playerPosition = sharedPrefs.getInt(PLAYERPOSITION, 0);
                int goblinPosition = sharedPrefs.getInt(GOBLINPOSITION, 0);
                int zombiePosition = sharedPrefs.getInt(ZOMBIEPOSITION, 0);
                int cyclopsPosition = sharedPrefs.getInt(CYCLOPSPOSITION, 0);
                int diabloPosition = sharedPrefs.getInt(DIABLOPOSITION, 0);

                int playerInventorySize = sharedPrefs.getInt(PLAYERINVENTORYSIZE, 0);

                for (int i = 0; i < playerInventorySize; i++)
                {
                    String inventory = sharedPrefs.getString("playerInventory_" + i, "");
                    intent.putExtra("pInventory_" + i, inventory);
                }


                for (int i = 0; i < 20; i++)
                {
                    int roomInventorySize = sharedPrefs.getInt("room_" + i + "_inventorySize", 0);
                    intent.putExtra("r_" + i + "_inventorySize", roomInventorySize);
                    for (int j = 0; j < roomInventorySize; j++)
                    {
                        String roomInventory = sharedPrefs.getString("room_" + i + "_inventory_" + j, "");
                        intent.putExtra("r_" + i + "_inventory_" + j, roomInventory);
                    }
                }

                for (int i = 0; i < 20; i++)
                {
                    String property = sharedPrefs.getString("room_" + i + "_property", "");
                    intent.putExtra("r_" + i + "_property", property);
                }



                intent.putExtra("pPosition", Integer.toString(playerPosition));
                intent.putExtra("gPosition", Integer.toString(goblinPosition));
                intent.putExtra("zPosition", Integer.toString(zombiePosition));
                intent.putExtra("cPosition", Integer.toString(cyclopsPosition));
                intent.putExtra("dPosition", Integer.toString(diabloPosition));
                intent.putExtra("pSize",Integer.toString(playerInventorySize));


                startActivity(intent);
            }
        });


        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Help.class);
                startActivity(intent);
            }
        });


        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), About.class);
                startActivity(intent);
            }
        });


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }   //  protected void setupControls()


}