package com.tyzsskills.api.model;

public class Cooldown {
    public final String spellId;
    public int ticks;

    public Cooldown(String spellId, int startTick){
        this.spellId = spellId;
        ticks = startTick;
    }

}
