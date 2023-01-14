package com.example.textadventuregame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Save player, monster position and inventories
    public static final String SAVE_FILE = "save";
    public static final String PLAYERPOSITION = "playerPosition";
    public static final String GOBLINPOSITION = "goblinPosition";
    public static final String ZOMBIEPOSITION = "zombiePosition";
    public static final String CYCLOPSPOSITION = "cyclopsPosition";
    public static final String DIABLOPOSITION = "diabloPosition";
    public static final String PLAYERINVENTORYSIZE = "playerInventorySize";
    public static final String ROOMNUMBER = "roomNumber";


    SharedPreferences sharedPrefs;


    static final int NUMBER_OF_ROOMS = 20;

    Room[] thedungeon;
    MediaPlayer dungeonMediaPlayer;

    //player and monsters
    Player thePlayer;
    Monster goblin, zombie, cyclops, diablo;

    //Controls
    TextView statusTextView, descriptionTextView, playerInventoryTextView, roomInventoryTextView;
    Button northButton, eastButton, southButton, westButton, middleButton, pickupButton, dropButton, attackButton, runButton, exitButton, saveButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle extras = getIntent().getExtras();
        if (extras != null) //load game
        {
            //Load game
            String playerPosition = extras.getString("pPosition");
            String goblinPosition = extras.getString("gPosition");
            String zombiePosition = extras.getString("zPosition");
            String cyclopsPosition = extras.getString("cPosition");
            String diabloPosition = extras.getString("dPosition");
            String playerInventorySize = extras.getString("pSize");


            thePlayer = new Player( Integer.valueOf(playerPosition) );
            goblin = new Monster(Integer.valueOf(goblinPosition));
            zombie = new Monster(Integer.valueOf(zombiePosition));
            cyclops = new Monster(Integer.valueOf(cyclopsPosition));
            diablo = new Monster(Integer.valueOf(diabloPosition));
            for (int i = 0; i < Integer.valueOf(playerInventorySize); i++)
            {
                thePlayer.getInventory().add(extras.getString("pInventory_" + i));
            }


            initTheDungeon();

            for (int i = 0; i < NUMBER_OF_ROOMS; i++)
            {
                int roomInventorySize = extras.getInt("r_" + i + "_inventorySize");
                for (int j = 0; j < roomInventorySize; j++)
                {
                    thedungeon[i].getInventory().add(extras.getString("r_" + i + "_inventory_" + j));
                }
            }


            for (int i = 0; i < NUMBER_OF_ROOMS; i++)
            {
                String roomProperty = extras.getString("r_" + i + "_property");
                thedungeon[i].setProperty(roomProperty);
            }


        }

        else
        {
            // new game
            setupPlayer();
            setupMonsters();
            initTheDungeon();
            setupRoomsProperties();


        }
        readXMLFile();
        playDungeonSong();
        setupControls();
        updateRoomInformation();




    }   //  protected void onCreate(Bundle savedInstanceState)


    protected void setupPlayer()
    {
        thePlayer = new Player();

    } //protected void setupPlayer()


    protected void setupMonsters()
    {
        goblin = new Monster();
        goblin.setMonsterPos(2);
        goblin.setDropItem("Boots");
        goblin.setKillItem("Helmet");

        zombie = new Monster();
        zombie.setMonsterPos(9);
        zombie.setDropItem("Heavy Armor");
        zombie.setKillItem("Boots");

        cyclops = new Monster();
        cyclops.setMonsterPos(18);
        cyclops.setDropItem("Master Sword");
        cyclops.setKillItem("Heavy Armor");

        diablo = new Monster();
        diablo.setMonsterPos(16);
        diablo.setKillItem("Master Sword");
    } //protected void setupMonsters()


    protected void initTheDungeon()
    {
        thedungeon = new Room[NUMBER_OF_ROOMS]; // 0 to 4 = 5 slots !!!

        for (int pos = 0; pos < NUMBER_OF_ROOMS; pos++)
        {
            thedungeon[pos] = new Room();
        }


    } // public static void initTheDungeon()


    protected void setupRoomsProperties()
    {
        for (int i = 0; i < NUMBER_OF_ROOMS; i ++)
        {
            thedungeon[i].setProperty("empty");
        }

        thedungeon[11].setProperty("monsterDead");
        thedungeon[15].setProperty("clear");
        thedungeon[19].setProperty("dead");
        thedungeon[goblin.getMonsterPos()].setProperty("monster");
        thedungeon[zombie.getMonsterPos()].setProperty("monster");
        thedungeon[cyclops.getMonsterPos()].setProperty("monster");
        thedungeon[diablo.getMonsterPos()].setProperty("monster");
        thedungeon[1].getInventory().add(("Helmet"));
        thedungeon[10].getInventory().add(("Banana"));
    }// protected void setupRoomsProperties()


    //Play song
    protected void playDungeonSong()
    {
        dungeonMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.dungeonsong);
        dungeonMediaPlayer.start();
    }

    protected void onPause()
    {
        super.onPause();
        dungeonMediaPlayer.pause();
    }

    protected void onResume()
    {
        super.onResume();
        dungeonMediaPlayer.start();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        dungeonMediaPlayer.release();
    }


    protected void updateRoomInformation()
    {
        descriptionTextView.setText( thedungeon[thePlayer.getPlayerPos()].getDescription() );
        validDirections(thePlayer.getPlayerPos());
        validActionButton(thePlayer.getPlayerPos());
        validGameOver(thePlayer.getPlayerPos());
        updateRoomImage( thePlayer.getPlayerPos() );
        updateStatus( thePlayer.getPlayerPos() );
        updateInventoryInformation();
    }


    protected void updateRoomImage(int currentPos)
    {
        if (thedungeon[currentPos].getProperty().equals("empty"))
        {
            imageView.setImageResource(R.drawable.torch);
        }

        if (thedungeon[currentPos].getProperty().equals("dead"))
        {
            imageView.setImageResource(R.drawable.grave);
        }

        if (thedungeon[currentPos].getProperty().equals("clear"))
        {
            imageView.setImageResource(R.drawable.chest);
        }

        if (currentPos == goblin.getMonsterPos())
        {
            imageView.setImageResource(R.drawable.goblin);
        }

        if (currentPos == zombie.getMonsterPos())
        {
            imageView.setImageResource(R.drawable.zombie);
        }

        if (currentPos == cyclops.getMonsterPos())
        {
            imageView.setImageResource(R.drawable.cyclops);
        }

        if (currentPos == diablo.getMonsterPos())
        {
            imageView.setImageResource(R.drawable.diablo);
        }


    }   //protected void updateRoomImage()


    protected void updateInventoryInformation()
    {
        playerInventoryTextView.setText( "Player Inventory = " + thePlayer.getInventory() );

        roomInventoryTextView.setText( "Room Inventory = " + thedungeon[thePlayer.getPlayerPos()].getInventory()  );
    }


    //Update status textview
    protected void updateStatus(int currentPos)
    {
        if(currentPos == goblin.getMonsterPos())
        {
            statusTextView.setText("You encounter a Goblin! What do you want to do?");
        }

        else if (currentPos == zombie.getMonsterPos())
        {
            statusTextView.setText("You encounter a Zombie! What do you want to do?");
        }

        else if (currentPos == cyclops.getMonsterPos())
        {
            statusTextView.setText("You encounter a Cyclops! What do you want to do?");
        }

        else if (currentPos == diablo.getMonsterPos())
        {
            statusTextView.setText("You encounter the Diablo! What do you want to do?");
        }

        else if (thedungeon[currentPos].getProperty().equals("empty"))
        {
            statusTextView.setText("Which way do you want to go?");
        }

        else if (thedungeon[currentPos].getProperty().equals("clear"))
        {
            statusTextView.setText("Congratulations! You found the treasure.");
        }

        else if (thedungeon[currentPos].getProperty().equals("dead"))
        {
            statusTextView.setText("You are dead. Your journey ends here. Game Over.");
        }

    } //protected void updateStatus(int currentPos)



    protected void setupControls()
    {
        imageView = findViewById(R.id.imageView);
        statusTextView = findViewById(R.id.statusTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        playerInventoryTextView = findViewById(R.id.playerInventoryTextView);
        roomInventoryTextView = findViewById(R.id.roomInventoryTextView);


        northButton = findViewById(R.id.northButton);
        northButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                thePlayer.setTempPos(thePlayer.getPlayerPos());
                thePlayer.setPlayerPos(  thedungeon[thePlayer.getPlayerPos()].getNorth() ); // move north !!!
                updateRoomInformation();
            }
        });

        eastButton = findViewById(R.id.eastButton);
        eastButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                thePlayer.setTempPos(thePlayer.getPlayerPos());
                thePlayer.setPlayerPos( thedungeon[thePlayer.getPlayerPos()].getEast() );  // move east !!!
                updateRoomInformation();
            }
        });

        southButton = findViewById(R.id.southButton);
        southButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                thePlayer.setTempPos(thePlayer.getPlayerPos());
                thePlayer.setPlayerPos( thedungeon[thePlayer.getPlayerPos()].getSouth() );   // move south !!!
                updateRoomInformation();
            }
        });

        westButton = findViewById(R.id.westButton);
        westButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                thePlayer.setTempPos(thePlayer.getPlayerPos());
                thePlayer.setPlayerPos( thedungeon[thePlayer.getPlayerPos()].getWest() );   /// move west !!!
                updateRoomInformation();
            }
        });

        middleButton = findViewById(R.id.middleButton);

        pickupButton = findViewById(R.id.pickupButton);
        pickupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (thedungeon[thePlayer.getPlayerPos()].getInventory().size() > 0 )
                {
                    thePlayer.getInventory().addAll(( thedungeon[thePlayer.getPlayerPos()].getInventory() ));
                    thedungeon[thePlayer.getPlayerPos()].getInventory().clear();
                    DisplayMessage("You picked up an item.");
                    updateInventoryInformation();

                }
            }
        });

        dropButton = findViewById(R.id.dropButton);
        dropButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                thedungeon[thePlayer.getPlayerPos()].getInventory().addAll((thePlayer.getInventory()));
                thePlayer.getInventory().clear();
                DisplayMessage("You dropped your item.");
                updateInventoryInformation();
                cheat();


            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveGame();
            }
        });

        attackButton = findViewById(R.id.attackButton);
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attack();
                updateRoomInformation();
            }
        });

        runButton = findViewById(R.id.runButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thePlayer.setPlayerPos(thePlayer.getTempPos());
                updateRoomInformation();
            }
        });

        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }   //  protected void setupControls()


    //check attack result
    protected void attack() {
        if (thePlayer.getPlayerPos() == goblin.getMonsterPos()) {

            if (thePlayer.getInventory().contains(goblin.getKillItem())) {
                thedungeon[goblin.getMonsterPos()].getInventory().add(goblin.getDropItem());
                DisplayMessage("With the protection from helmet, you defeated the Goblin!");
                DisplayMessage("Goblin has dropped an item.");
                goblin.setMonsterPos(11);
                thedungeon[thePlayer.getPlayerPos()].setProperty("empty");
            } else {
                DisplayMessage("The Goblin killed you by knocking your head.");
                thePlayer.setPlayerPos(19);
            }
        }

        if (thePlayer.getPlayerPos() == zombie.getMonsterPos()) {
            if (thePlayer.getInventory().contains(zombie.getKillItem())) {
                thedungeon[zombie.getMonsterPos()].getInventory().add(zombie.getDropItem());
                DisplayMessage("With the speed increased by the boots, you defeated the Zombie!");
                DisplayMessage("Zombie has dropped an item.");
                zombie.setMonsterPos(11);
                thedungeon[thePlayer.getPlayerPos()].setProperty("empty");
            } else {
                DisplayMessage("The Zombie caught you and killed you.");
                thePlayer.setPlayerPos(19);
            }
        }

        if (thePlayer.getPlayerPos() == cyclops.getMonsterPos()) {
            if (thePlayer.getInventory().contains(cyclops.getKillItem())) {
                thedungeon[cyclops.getMonsterPos()].getInventory().add(cyclops.getDropItem());
                DisplayMessage("With the protection from the heavy armor, you defeated the Cyclops!");
                DisplayMessage("Cyclops has dropped an item.");
                cyclops.setMonsterPos(11);
                thedungeon[thePlayer.getPlayerPos()].setProperty("empty");
            } else {
                DisplayMessage("The Cyclops killed you by punching your body.");
                thePlayer.setPlayerPos(19);
            }
        }

        if (thePlayer.getPlayerPos() == diablo.getMonsterPos()) {
            if (thePlayer.getInventory().contains(diablo.getKillItem())) {
                thedungeon[diablo.getMonsterPos()].getInventory().add(diablo.getDropItem());
                DisplayMessage("With the Master Sword, you defeated the Diablo!");
                diablo.setMonsterPos(11);
                thedungeon[thePlayer.getPlayerPos()].setProperty("empty");
            } else {
                DisplayMessage("The Diablo is stronger than you, you have been killed.");
                thePlayer.setPlayerPos(19);
            }
        }


    } // protected void attack()


    //write the save to sharedprefs
    protected void saveGame()
    {
        //write position to sharedprefs
        sharedPrefs = getSharedPreferences(SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putInt(PLAYERPOSITION, thePlayer.getPlayerPos() );
        edit.putInt(GOBLINPOSITION, goblin.getMonsterPos());
        edit.putInt(ZOMBIEPOSITION, zombie.getMonsterPos());
        edit.putInt(CYCLOPSPOSITION, cyclops.getMonsterPos());
        edit.putInt(DIABLOPOSITION, diablo.getMonsterPos());


        //write playerinventory to sharedprefs
        edit.putInt(PLAYERINVENTORYSIZE, thePlayer.getInventory().size());
        for (int i = 0; i < thePlayer.getInventory().size(); i++)
        {
            edit.remove("playerInventory_" + i);
            edit.putString("playerInventory_" + i, thePlayer.getInventory().get(i));
        }


        //write roominventory to sharedprefs
        edit.putInt(ROOMNUMBER, NUMBER_OF_ROOMS);
        for (int i = 0; i < NUMBER_OF_ROOMS; i ++)
        {
            edit.remove("room_" + i + "_inventorySize");
            edit.putInt("room_" + i + "_inventorySize", thedungeon[i].getInventory().size());
            for (int j = 0; j < thedungeon[i].getInventory().size(); j++)
            {
                edit.remove("room_" + i + "_inventory_" + j);
                edit.putString("room_" + i + "_inventory_" + j, thedungeon[i].getInventory().get(j));
            }
        }


        //write room properties to sharedprefs
        for (int i = 0; i < NUMBER_OF_ROOMS; i++)
        {
            edit.remove("room_" + i + "_property");
            edit.putString("room_" + i + "_property", thedungeon[i].getProperty());
        }


        edit.commit();

        DisplayMessage("Player Position Saved !");
    } //protected void saveGame()



    public void DisplayMessage(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }


    //drop the banana to dungeon[0] will clear the game
    protected void cheat()
    {
        if( thedungeon[0].getInventory().contains("Banana"))
            thePlayer.setPlayerPos(15);
        updateRoomInformation();
    }



    protected void validDirections(int currentPos)
    {
        if (thedungeon[currentPos].getNorth() == Room.NO_EXIT)
        {
            northButton.setEnabled(false);
        }
        else
        {
            northButton.setEnabled(true);
        }

        if (thedungeon[currentPos].getEast() == Room.NO_EXIT)
        {
            eastButton.setEnabled(false);
        }
        else
        {
            eastButton.setEnabled(true);
        }

        if (thedungeon[currentPos].getSouth() == Room.NO_EXIT)
        {
            southButton.setEnabled(false);
        }
        else
        {
            southButton.setEnabled(true);
        }

        if (thedungeon[currentPos].getWest() == Room.NO_EXIT)
        {
            westButton.setEnabled(false);
        }
        else
        {
            westButton.setEnabled(true);
        }



    }
    //  protected void validDirections(int currentPos)


    //set encounter monster button
    protected void validActionButton(int currentPos)
    {
        if (thedungeon[currentPos].getProperty().equals("monster"))
        {
            attackButton.setEnabled(true);
            runButton.setEnabled(true);
            northButton.setEnabled(false);
            eastButton.setEnabled(false);
            southButton.setEnabled(false);
            westButton.setEnabled(false);
            pickupButton.setEnabled(false);
            dropButton.setEnabled(false);
            exitButton.setEnabled(false);
            middleButton.setEnabled(false);
        } else {
            attackButton.setEnabled(false);
            runButton.setEnabled(false);
            pickupButton.setEnabled(true);
            dropButton.setEnabled(true);
        }

    }//protected void validActionButton(int currentPos)


    //set gameover button
    protected void validGameOver (int currentPos)
    {
        if (thedungeon[currentPos].getProperty().equals("clear") || thedungeon[currentPos].getProperty().equals("dead") )
        {
            exitButton.setEnabled(true);
            northButton.setEnabled(false);
            eastButton.setEnabled(false);
            southButton.setEnabled(false);
            westButton.setEnabled(false);
            pickupButton.setEnabled(false);
            dropButton.setEnabled(false);
            saveButton.setEnabled(false);
            middleButton.setEnabled(false);
        }
        else
        {
            exitButton.setEnabled(false);
        }

    } //protected void validGameOver (int currentPos)



    public void readXMLFile()
    {
        int pos = 0; // May be use this variable, to keep track of what position of the array of Room Objects.
        try
        {
            int room_count = 0;

            XmlResourceParser xpp = getResources().getXml(R.xml.dungeon); // Open xml file
            xpp.next();
            int eventType = xpp.getEventType();
            String elemtext = null;

            while (eventType != XmlPullParser.END_DOCUMENT)  // AKA End Of File
            {
                if (eventType == XmlPullParser.START_TAG)
                {
                    String elemName = xpp.getName();
                    if (elemName.equals("dungeon"))
                    {
                        String titleAttr = xpp.getAttributeValue(null,"title");
                        String authorAttr = xpp.getAttributeValue(null,"author");

                    } // if (elemName.equals("dungeon"))

                    if (elemName.equals("room"))
                    {
                        room_count = room_count + 1;
                    }
                    if (elemName.equals("north"))
                    {
                        elemtext = "north";
                    }
                    if (elemName.equals("east"))
                    {
                        elemtext = "east";
                    }
                    if (elemName.equals("south"))
                    {
                        elemtext = "south";
                    }
                    if (elemName.equals("west"))
                    {
                        elemtext = "west";
                    }
                    if (elemName.equals("description"))
                    {
                        elemtext = "description";
                    }
                } // if (eventType == XmlPullParser.START_TAG)
                // You will need to add code in this section to read each element of the XML file
                // And then store the value in the current Room Object.
                // NOTE: This method initTheDungeon() creates and array of Room Objects, ready to be populated!
                // As you can see at the moment the data/text is displayed in the LogCat Window
                // Hint: xpp.getText()
                else if (eventType == XmlPullParser.TEXT)
                {
                    if (elemtext.equals("north"))
                    {
                        Log.w("ROOM", "north = " + xpp.getText());
                        thedungeon[room_count-1].setNorth( Integer.valueOf(xpp.getText()));
                    }
                    else if (elemtext.equals("east"))
                    {
                        Log.w("ROOM", "east = " + xpp.getText());
                        thedungeon[room_count-1].setEast(Integer.valueOf(xpp.getText()));
                    }
                    else if (elemtext.equals("south"))
                    {
                        Log.w("ROOM", "south = " + xpp.getText());
                        thedungeon[room_count-1].setSouth(Integer.valueOf(xpp.getText()));
                    }
                    else if (elemtext.equals("west"))
                    {
                        Log.w("ROOM", "west = " + xpp.getText());
                        thedungeon[room_count-1].setWest(Integer.valueOf(xpp.getText()));
                    }
                    else if (elemtext.equals("description"))
                    {
                        Log.w("ROOM", "description = " + xpp.getText());
                        thedungeon[room_count-1].setDescription( xpp.getText() );
                    }
                } // else if (eventType == XmlPullParser.TEXT)

                eventType = xpp.next();

            } // while (eventType != XmlPullParser.END_DOCUMENT)
        } // try
        catch (XmlPullParserException e)
        {

        }
        catch (IOException e)
        {

        }
    } // public void readXMLFile()


    public void displayRooms()
    {
        Log.w("display ROOM", "**** start of display rooms ****");

        for (int pos = 0; pos < NUMBER_OF_ROOMS; pos++)
        {
            Log.w("display ROOM", "Pos = " + pos);
            Log.w("display ROOM", "North = " + thedungeon[pos].getNorth());
            Log.w("display ROOM", "East = " + thedungeon[pos].getEast());
            Log.w("display ROOM", "South = " + thedungeon[pos].getSouth());
            Log.w("display ROOM", "West = " + thedungeon[pos].getWest());
            Log.w("display ROOM", "Description = " + thedungeon[pos].getDescription());
            Log.w(" ", " ");
        }

        Log.w("display ROOM", "**** end of display rooms ****");

    } // public void displayRooms() {






}