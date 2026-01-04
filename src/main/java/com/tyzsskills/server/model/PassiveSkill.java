package com.tyzsskills.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassiveSkill extends Skill{

    public PassiveSkill(Boolean active, String id, Integer maximumLevel,
                        List<Integer> prices, SkillType type, SkillCategory category, String modifier , List<Float> values)
    {
        super(active, id, maximumLevel, prices, type, category, modifier);
        this.values = new ArrayList<>(values);

        if (this.maximumLevel > 10) this.maximumLevel = 10;
    }

    protected List<Float> values;

    public List<Float> GetValues() {return Collections.unmodifiableList(values);}

}