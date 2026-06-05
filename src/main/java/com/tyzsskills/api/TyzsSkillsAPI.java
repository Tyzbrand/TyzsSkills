package com.tyzsskills.api;

import com.tyzsskills.api.interfaces.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class TyzsSkillsAPI {

    private static ISpManager spManager;
    private static ILevelManager levelManager;
    private static IXpManager xpManager;
    private static ISkillManager skillManager;

    @ApiStatus.Internal
    public static void  registerSpManager(ISpManager manager){spManager = manager;}
    @ApiStatus.Internal
    public static void registerLevelManager(ILevelManager manager){levelManager = manager;}
    @ApiStatus.Internal
    public static void registerXpManager(IXpManager manager){xpManager = manager;}
    @ApiStatus.Internal
    public static void registerSkillManager(ISkillManager manager){skillManager = manager;}



    public static ISpManager sp(){
        if(spManager == null) ex("SpManager");
        return spManager;
    }

    public static ILevelManager level(){
        if(levelManager == null) ex("LevelManager");
        return levelManager;
    }

    public static IXpManager xp(){
        if(xpManager == null) ex("XpManager");
        return xpManager;
    }

    public static ISkillManager skills(){
        if(skillManager == null) ex("SkillManager");
        return skillManager;
    }


    //Util
    private static void ex(@NotNull String id){throw new RuntimeException("TyzsSkillsAPI Error: " + id + " is not registered! " +
            "Make sure Tyz's Skills mod is properly loaded.");}
}
