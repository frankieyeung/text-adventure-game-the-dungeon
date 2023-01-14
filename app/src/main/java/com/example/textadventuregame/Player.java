package com.example.textadventuregame;

import java.util.ArrayList;

public class Player
{

    private int playerPos;
    private int tempPos;

    private ArrayList<String> inventory = new ArrayList<>();

    Player()                //  new game
    {
        playerPos = 0;


    }   // Player()

    Player(int newPlayerPos)    // load game
    {
        playerPos = newPlayerPos;


    }   //  Player(int newPlayerPos)

    public int getPlayerPos()
    {
        return playerPos;
    }

    public void setPlayerPos(int playerPos)
    {
        this.playerPos = playerPos;
    }

    public int getTempPos()
    {
        return tempPos;
    }

    public void setTempPos(int tempPos)
    {
        this.tempPos = tempPos;
    }


    public ArrayList<String> getInventory() {
        return inventory;
    }


}   //  public class Player
