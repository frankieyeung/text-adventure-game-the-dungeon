package com.example.textadventuregame;

public class Monster {

    private int monsterPos;

    private String killItem;

    private String dropItem;

    Monster()
    {
        monsterPos = 0;
    }

    Monster(int newMonsterPos)
    {
        monsterPos = newMonsterPos;
    }

    public int getMonsterPos()
    {
        return monsterPos;
    }

    public void setMonsterPos(int monsterPos)
    {
        this.monsterPos = monsterPos;
    }

    public String getDropItem()
    {
        return dropItem;
    }

    public void setDropItem(String dropItem)
    {
        this.dropItem = dropItem;
    }

    public String getKillItem()
    {
        return killItem;
    }

    public void setKillItem(String weakness)
    {
        this.killItem = weakness;
    }



}
