package com.tyzsskills.impl.client.active;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.ClientCache;
import net.minecraft.network.chat.Component;
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

    //SEARCH
    private static String currentSearchQuery = "";

    //ORDRE ACTUEL
    private static final List<ISkill> currentSkillOrder = new ArrayList<>();
    private static final List<ISkill> currentSkillOrderView = Collections.unmodifiableList(currentSkillOrder);


    public static void refreshList(){
        var cache = ClientCache.get();
        var toSort =  new ArrayList<>(cache.getAllSkills().values());

        if(currentCategory.is("bookmarks")) toSort.removeIf(s -> !cache.isSkillBookMarked(s.getID()));
        else if(!currentCategory.is("all")) toSort.removeIf(s -> !currentCategory.is(s.getCategory()));

        if(!currentSearchQuery.isBlank()){
            var query = currentSearchQuery.trim().toLowerCase(Locale.ROOT);
            toSort.removeIf(s -> queryCheck(query, s));
        }

        toSort.removeIf(s -> !s.isVisible() && cache.getSkillLevel(s.getID()) < 1);

        currentSkillOrder.clear();
        currentSkillOrder.addAll(toSort);
    }

    //Setters
    public static void setCategory(@NotNull Category cat){currentCategory = cat;}
    public static void setMenuType(Enums.MenuFocus type){menuType = type;}
    public static void setSearchQuery(@NotNull String query){currentSearchQuery = query;}

    //Getters
    public static @NotNull @UnmodifiableView List<ISkill> getCurrentSkillOrder() {return currentSkillOrderView;}
    public static @Nullable Category getCurrentCategory(){return currentCategory;}
    public static Enums.MenuFocus getCurrentMenuType(){return menuType;}
    public static @NotNull String getSearchQuery(){return currentSearchQuery;}

    //Utils
    public static void clearData(){currentCategory = null;}

    //Research
    private static boolean queryCheck(String query, ISkill skill){
        var id = skill.getID().toLowerCase();
        var name = Component.translatable(skill.getDisplayName()).getString().toLowerCase(Locale.ROOT);
        var description = Component.translatable(skill.getDescription()).getString().toLowerCase(Locale.ROOT);

        if(query.startsWith("#")){
            var subSubQuery = query.substring(1);
            return !toleranceMatch(id, subSubQuery);
        }
        else if (query.startsWith("@")){
            var subSubQuery = query.substring(1);
            return !description.contains(subSubQuery);
        }
        else {
            return !toleranceMatch(name, query) && !toleranceMatch(id, query);
        }
    }
    private static boolean toleranceMatch(String text, String query){
        if(query.isEmpty()) return true;
        if(text.contains(query)) return true;

        if(!Config.QUERY_TOLERANCE.get()) return false;

        var errorTolerance =  getAllowedErrors(query);
        if(errorTolerance <= 0) return false;

        for (int start = 0; start < text.length(); start++) {
            if (matchFrom(text, query, start, 0, errorTolerance)) return true;
        }
        return false;
    }
    private static int getAllowedErrors(String query){
        var length = query.length();

        if(length <= 2) return 0;
        if(length <= 5) return 1;
        return 2;
    }
    private static boolean matchFrom(String text, String query, int tIdx, int qIdx, int errorsLeft) {
        while (qIdx < query.length() && tIdx < text.length() && text.charAt(tIdx) == query.charAt(qIdx)) {
            tIdx++; qIdx++;
        }

        if (qIdx == query.length()) return true;
        if (errorsLeft <= 0) return false;
        if (tIdx == text.length()) return (query.length() - qIdx) <= errorsLeft;

        if (tIdx + 1 < text.length() && qIdx + 1 < query.length() && text.charAt(tIdx) == query.charAt(qIdx + 1) && text.charAt(tIdx + 1) == query.charAt(qIdx)) {
            if (matchFrom(text, query, tIdx + 2, qIdx + 2, errorsLeft - 1)) return true;
        }

        if (matchFrom(text, query, tIdx + 1, qIdx, errorsLeft - 1)) return true;
        if (matchFrom(text, query, tIdx, qIdx + 1, errorsLeft - 1)) return true;

        return matchFrom(text, query, tIdx + 1, qIdx + 1, errorsLeft - 1);
    }

}
