package com.tyzsskills.api;

import com.tyzsskills.api.interfaces.*;
import com.tyzsskills.impl.server.Level.LevelWrapper;
import com.tyzsskills.impl.server.skills.SkillWrapper;
import com.tyzsskills.impl.server.sp.SpWrapper;
import com.tyzsskills.impl.server.spells.SpellWrapper;
import com.tyzsskills.impl.server.xp.XpWrapper;
import org.jetbrains.annotations.NotNull;

public class TyzsSkillsAPI {

    private static final ISpManager spManager = new SpWrapper();
    public static @NotNull ISpManager sp(){return spManager;}

    private static final ILevelManager levelManager = new LevelWrapper();
    public static @NotNull ILevelManager level(){return levelManager;}

    private static final IXpManager xpManager = new XpWrapper();
    public static @NotNull IXpManager xp(){return xpManager;}

    private static final ISkillManager skillManager = new SkillWrapper();
    public static @NotNull ISkillManager skills(){return skillManager;}

    private static final ISpellManager spellManager = new SpellWrapper();
    public static @NotNull ISpellManager spells(){return spellManager;}
}
