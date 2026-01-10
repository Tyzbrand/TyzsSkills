package com.tyzsskills.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassiveSkill extends Skill{

    public PassiveSkill(Boolean active, String id, Integer maximumLevel,
                        List<Integer> prices, String modifier , List<Float> values, SkillType type, SkillCategory category)
    {
        super(active, id, maximumLevel, prices, modifier);
        this.values = new ArrayList<>(values);

        if (this.maximumLevel > 10) this.maximumLevel = 10;
    }

    public enum SkillType {GENERIC, CUSTOM, IMMUTABLE}
    public enum SkillCategory {ABILITIES, FIGHT, MISC, SPECIAL}

    protected List<Float> values;
    protected SkillType type;
    protected SkillCategory category;

    public List<Float> GetValues() {return Collections.unmodifiableList(values);}
    public SkillType GetType(){return type;}
    public SkillCategory GetCategory(){return category;}

}