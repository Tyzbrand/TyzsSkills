package com.tyzsskills.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Skill{

    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, String modifier)
    {
        this.active = active;
        this.id = id;
        this.maximumLevel = maximumLevel;
        this.prices = new ArrayList<>(prices);
        this.modifier = modifier;
    }



    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;

    protected String modifier;


    public boolean IsSkillActive(){return active;}
    public String GetID() {return id;}
    public int GetMaximumLevel() {return maximumLevel;}
    public List<Integer> GetPrices() {return Collections.unmodifiableList(prices);}
    public String GetModifier() {return modifier;}
}
