package com.tyzsskills.impl.client.active;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.ClientCache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SortingManager {
    //CATEGORIES
    private static Category currentCategory = null;

    //TYPE
    private static Enums.MenuFocus menuType = Enums.MenuFocus.SKILL;

    //ORDRE ACTUEL
    private static final List<ISkill> currentSkillOrder = new ArrayList<>();
    private static final List<ISkill> currentSkillOrderView = Collections.unmodifiableList(currentSkillOrder);


    public static void refreshList(){
        var cache = ClientCache.get();
        var toSort =  new ArrayList<>(cache.getAllSkills().values());

        if(currentCategory.id().equalsIgnoreCase("bookmarks")) toSort.removeIf(s -> !cache.isSkillBookMarked(s.getID()));
        else if(!currentCategory.id().equalsIgnoreCase("all")) toSort.removeIf(s -> !s.getCategory().equalsIgnoreCase(currentCategory.id()));

        toSort.removeIf(s -> !s.isVisible() && cache.getSkillLevel(s.getID()) < 1);

        currentSkillOrder.clear();
        currentSkillOrder.addAll(toSort);
    }

    //Setters
    public static void setCategory(@NotNull Category cat){currentCategory = cat;}
    public static void setMenuType(Enums.MenuFocus type){menuType = type;}

    //Getters
    public static @NotNull @UnmodifiableView List<ISkill> getCurrentSkillOrder() {return currentSkillOrderView;}
    public static @Nullable Category getCurrentCategory(){return currentCategory;}
    public static Enums.MenuFocus getCurrentMenuType(){return menuType;}

    //Utils
    public static void clearData(){currentCategory = null;}

}
