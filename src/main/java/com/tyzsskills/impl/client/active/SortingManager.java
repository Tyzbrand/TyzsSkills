package com.tyzsskills.impl.client.active;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.skills.SkillRules;
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

    //TOGGLES
    private static boolean showMaxed = true;
    private static boolean showUnaffordable = true;
    private static boolean showLocked = true;

    //SORTING BUTTONS
    private static Enums.SortingType sortingType = Enums.SortingType.DEFAULT;
    private static Enums.SortingDirection sortingDirection = Enums.SortingDirection.ASCENDING;

    private static Comparator<ISkill> byName = Comparator.comparing(
            s -> Component.translatable(s.getDisplayName()).getString(),  String.CASE_INSENSITIVE_ORDER);

    private static Comparator<ISkill> byPrice = Comparator.<ISkill>comparingInt(
            s -> s.getPrice(ClientCache.get().getSkillLevel(s.getID()) + 1)).thenComparing(byName);

    private static Comparator<ISkill> byLevel = Comparator.<ISkill>comparingInt(
            s -> ClientCache.get().getSkillLevel(s.getID())).thenComparing(byName);

    private static final Comparator<ISkill> byDefault = Comparator.<ISkill>comparingInt(s -> {
        var cache = ClientCache.get();
        var lvl = cache.getSkillLevel(s.getID());
        if (lvl >= s.getMaximumLevel()) return 2;
        if (!SkillRules.isAvailable(cache.getSkillContext(s.getID()), cache.getPlayerContext())) return 3;
        if (lvl > 0) return 1;
        return 0;
    }).thenComparing(byName);

    //ORDRE ACTUEL
    private static final List<ISkill> currentSkillOrder = new ArrayList<>();
    private static final List<ISkill> currentSkillOrderView = Collections.unmodifiableList(currentSkillOrder);


    public static void refreshList(){
        var cache = ClientCache.get();
        var toSort =  new ArrayList<>(cache.getAllSkills().values());

        //BOOKMARK OR ALL
        if(currentCategory.is("bookmarks")) toSort.removeIf(s -> !cache.isSkillBookMarked(s.getID()));
        else if(!currentCategory.is("all")) toSort.removeIf(s -> !currentCategory.is(s.getCategory()));

        //SKILL IS SET ON NOT VISIBLE
        toSort.removeIf(s -> !s.isVisible() && cache.getSkillLevel(s.getID()) < 1);

        var playerContext = cache.getPlayerContext();

        //TOGGLES
        if(!showMaxed) toSort.removeIf(s -> cache.getSkillLevel(s.getID()) >= s.getMaximumLevel());
        if(!showUnaffordable) toSort.removeIf(s -> !SkillRules.canBuy(cache.getSkillContext(s.getID()), playerContext,
                cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true)));
        if(!showLocked) toSort.removeIf(s -> !SkillRules.isAvailable(cache.getSkillContext(s.getID()), playerContext));

        if(!currentSearchQuery.isBlank()){
            var query = currentSearchQuery.trim().toLowerCase(Locale.ROOT);
            toSort.removeIf(s -> queryCheck(query, s));
        }

        //SORTING TYPE
        var comparator = switch (sortingType){
            case NAME -> byName;
            case PRICE -> byPrice;
            case LEVEL -> byLevel;
            case DEFAULT -> byDefault;
        };

        comparator = sortingDirection == Enums.SortingDirection.ASCENDING ? comparator : comparator.reversed();
        toSort.sort(comparator);

        currentSkillOrder.clear();
        currentSkillOrder.addAll(toSort);
    }

    //Setters
    public static void setCategory(@NotNull Category cat){currentCategory = cat;}
    public static void setMenuType(Enums.MenuFocus type){menuType = type;}
    public static void setSearchQuery(@NotNull String query){currentSearchQuery = query;}
    public static void toggleShowMaxed(){showMaxed = !showMaxed;}
    public static void toggleShowUnaffordable(){showUnaffordable = !showUnaffordable;}
    public static void toggleShowLocked(){showLocked = !showLocked;}
    public static void toggleSortingType(){
        sortingType = Enums.SortingType.values()[(sortingType.ordinal() + 1) % Enums.SortingType.values().length];
    }
    public static void toggleSortingDirection(){
        sortingDirection = Enums.SortingDirection.values()[(sortingDirection.ordinal() + 1) % Enums.SortingDirection.values().length];
    }

    //Getters
    public static @NotNull @UnmodifiableView List<ISkill> getCurrentSkillOrder() {return currentSkillOrderView;}
    public static @Nullable Category getCurrentCategory(){return currentCategory;}
    public static Enums.MenuFocus getCurrentMenuType(){return menuType;}
    public static @NotNull String getSearchQuery(){return currentSearchQuery;}
    public static boolean showMaxed(){return showMaxed;}
    public static boolean showUnaffordable(){return showUnaffordable;}
    public static boolean showLocked(){return showLocked;}
    public static Enums.SortingType getSortingType(){return sortingType;}
    public static Enums.SortingDirection getSortingDirection(){return sortingDirection;}

    //Utils
    public static void clearData(){
        currentCategory = null;
        menuType = Enums.MenuFocus.SKILL;
        currentSearchQuery = "";
        showMaxed = true;
        showUnaffordable = true;
        showLocked = true;
        sortingType = Enums.SortingType.DEFAULT;
        sortingDirection = Enums.SortingDirection.ASCENDING;
    }

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
