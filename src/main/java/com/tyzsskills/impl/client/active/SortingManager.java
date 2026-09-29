package com.tyzsskills.impl.client.active;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SortingManager {
    //CATEGORIES
    private static String currentCategory = "";

    //TYPE
    private static Enums.MenuFocus currentType = Enums.MenuFocus.SKILL;
    private static ISkill focusedSkill;

    //ORDRE ACTUEL
    private static final List<ISkill> currentSkillOrder = new ArrayList<>();
    private static final List<ISkill> currentSkillOrderView = Collections.unmodifiableList(currentSkillOrder);


    public static void refreshList(){
        var cache = ClientCache.get();
        var toSort =  new ArrayList<>(cache.getAllSkills().values());

        if(currentCategory.equalsIgnoreCase("bookmarks")) toSort.removeIf(s -> !cache.isSkillBookMarked(s.getID()));
        else if(!currentCategory.equalsIgnoreCase("all")) toSort.removeIf(s -> !s.getCategory().equals(currentCategory));

        toSort.removeIf(s -> !s.isVisible() && cache.getSkillLevel(s.getID()) < 1);

        currentSkillOrder.clear();
        currentSkillOrder.addAll(toSort);

        if(focusedSkill == null && !currentSkillOrder.isEmpty()) focusedSkill = currentSkillOrder.getFirst();
    }

    //Setters
    public static void setCategory(@NotNull String cat){currentCategory = cat;}
    public static void focusSkill(@NotNull ISkill skill){focusedSkill = skill;}

    //Getters
    public static @NotNull @UnmodifiableView List<ISkill> getCurrentSkillOrder() {return currentSkillOrderView;}
    public static @NotNull String getCurrentCategory(){return currentCategory;}
    public static @NotNull ISkill getFocusedSkill(){return focusedSkill;}

    //Utils
    public static void clearData(){currentCategory = "all";}

}
