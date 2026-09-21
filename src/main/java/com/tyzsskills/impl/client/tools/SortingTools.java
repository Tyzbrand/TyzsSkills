package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SortingTools {
    //CATEGORIES
    private static String currentCategory = "";

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
    }

    //Setters
    public static void setCategory(@NotNull String cat){currentCategory = cat;}

    //Getters
    public static @NotNull @UnmodifiableView List<ISkill> getCurrentSkillOrder() {return currentSkillOrder;}
    public static @NotNull String getCurrentCategory(){return currentCategory;}

    //Utils
    public static void clearData(){currentCategory = "all";}

}
