package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class SortingTools {

    //TYPE
    private static final List<SortType> activeSortTypes = new ArrayList<>();
    private static int currentSortTypeIndex = 0;

    //SENS
    private static Enums.SortingDirection currentSortingDirection = Enums.SortingDirection.ASCENDING;

    //CATEGORIES
    private static final List<String> rawCategories = new ArrayList<>();
    private static String currentCategory = "";
    private static int catOffset = 0;
    private static final int MAX_CATEGORIES = 4;

    private static Enums.SortingCategory mainCategory = Enums.SortingCategory.ALL;

    //BOOLS
    private static boolean showMaxed = true;
    private static boolean showUnbuyable = true;

    //SEARCH BAR
    private static String currentSearchQuery = "";


    private static final List<Skill> currentSkillOrder = new ArrayList<>();


    public static void registerSortingType(@NotNull SortType sortType){
        activeSortTypes.add(sortType);}

    public static void registerCategories(@NotNull List<String> categories){
        rawCategories.clear();
        rawCategories.addAll(categories);
    }


    public static List<Skill> refreshList(){
        List<Skill> listToSort =  new ArrayList<>(ClientCache.getAllSkills());


        if(!currentSearchQuery.isBlank()){
            var query = currentSearchQuery.trim().toLowerCase(Locale.ROOT);
            listToSort.removeIf(s -> queryCheck(query, s));
        }

        if(!showUnbuyable) listToSort.removeIf(s ->
                !s.canBuy(ClientCache.getCurrentContext(s.getID()), ClientCache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true)));

        if(!showMaxed) listToSort.removeIf(s -> ClientCache.getSkillLevel(s.getID()) >= s.getMaximumLevel());

        if(mainCategory != null){
            if(mainCategory == Enums.SortingCategory.BOOKMARKS) listToSort.removeIf(s -> !ClientCache.getAllBookmarkedIDs().contains(s.getID()));
        }
        else listToSort.removeIf(s -> !s.getCategory().equals(currentCategory));

        listToSort.removeIf(s -> !s.isVisible() && ClientCache.getSkillLevel(s.getID()) < 1);

        if(!activeSortTypes.isEmpty()){
            var currentSortType = activeSortTypes.get(currentSortTypeIndex);

            var comparator = currentSortType.comparator();
            if(comparator == null) Collections.shuffle(listToSort);
            else {
                if(currentSortingDirection == Enums.SortingDirection.DESCENDING) comparator = comparator.reversed();
                listToSort.sort(comparator);
            }

        }

        currentSkillOrder.clear();
        currentSkillOrder.addAll(listToSort);

        return listToSort;
    }

    public static void CycleSortType(){
        currentSortTypeIndex =  (currentSortTypeIndex + 1) % activeSortTypes.size();
    }

    public static void CycleSortDirection(){
        currentSortingDirection = currentSortingDirection == Enums.SortingDirection.ASCENDING ?
                Enums.SortingDirection.DESCENDING : Enums.SortingDirection.ASCENDING;
    }


    public static void toggleShowMaxed(){
        showMaxed = !showMaxed;
    }
    public static void toggleShowUnbuyable(){
        showUnbuyable = !showUnbuyable;
    }

    public static void setSearchQuery(@NotNull String searchQuery){
        currentSearchQuery = searchQuery.toLowerCase();
    }

    public static void incrCatOffset(){
        if (catOffset + MAX_CATEGORIES < rawCategories.size()) catOffset += MAX_CATEGORIES;
    }

    public static void decrCatOffset(){
        if (catOffset - MAX_CATEGORIES >= 0) catOffset -= MAX_CATEGORIES;
        else catOffset = 0;
    }

    public static void setCategory(@NotNull String cat){currentCategory = cat;}

    public static void setMainCategory(Enums.SortingCategory cat){mainCategory = cat;}

    public static void clearData(){
        currentSortTypeIndex = 0;
        currentSortingDirection = Enums.SortingDirection.ASCENDING;
        showMaxed = true;
        showUnbuyable = true;
        currentSearchQuery = "";

        catOffset = 0;
        currentCategory = "";
        rawCategories.clear();
    }


    //Getters
    public static List<Skill> getCurrentSkillOrder() {
        return activeSortTypes.isEmpty() ? refreshList() : List.copyOf(currentSkillOrder);
    }

    public static SortType getCurrentSortType(){
        return activeSortTypes.get(currentSortTypeIndex);
    }

    public static Enums.SortingDirection getCurrentSortingDirection(){return currentSortingDirection;}

    public static String getCurrentSearchQuery(){
        return currentSearchQuery;
    }

    public static boolean getShowMaxedState() {return showMaxed;}

    public static boolean getShowUnbuyableState() {return showUnbuyable;}

    public static boolean isRightCatOverlaps(){return catOffset + MAX_CATEGORIES < rawCategories.size();}

    public static boolean isLeftCatOverlaps(){return catOffset > 0;}

    @Nullable
    public static Enums.SortingCategory getMainCategory(){return mainCategory;}

    @NotNull
    public static String getCurrentCategory(){return currentCategory;}



    @NotNull
    public static Category[] getVisibleCategories(){
        var categories = new Category[MAX_CATEGORIES];

        for(int i = 0; i < MAX_CATEGORIES; i++){
            var index = catOffset + i;

            if(index >= rawCategories.size()) categories[i] = null;
            else categories[i] = ClientCache.getCategory(rawCategories.get(index));
        }
        return categories;
    }

    //Util
    private static boolean queryCheck(String query, Skill skill){
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

        var errorTolerance =  Config.QUERY_TOLERANCE.getAsBoolean() ? getAllowedErrors(query) : 0;
        return toleranceRecursive(text, query, 0, 0, errorTolerance);
    }


    private static int getAllowedErrors(String query){
        var length = query.length();

        if(length <= 2) return 0;
        if(length <= 5) return 1;
        return 2;
    }
    private static boolean toleranceRecursive(String text, String query, int textIndex, int queryIndex, int errorsLeft){
        if(queryIndex == query.length()) return true;

        if (textIndex == text.length()) return (query.length() - queryIndex) <= errorsLeft;

        if(errorsLeft == 0){
            while (textIndex < text.length() && queryIndex < query.length()){
                if(text.charAt(textIndex) == query.charAt(queryIndex)) queryIndex++;
                textIndex++;
            }
            return  queryIndex == query.length();
        }

        if(text.charAt(textIndex) == query.charAt(queryIndex)){
            if(toleranceRecursive(text, query, textIndex + 1, queryIndex + 1, errorsLeft)) return true;
        }

        if(toleranceRecursive(text, query, textIndex + 1, queryIndex, errorsLeft)) return true;

        if(toleranceRecursive(text, query, textIndex, queryIndex + 1, errorsLeft - 1)) return true;

        if(toleranceRecursive(text, query, textIndex + 1, queryIndex + 1, errorsLeft - 1)) return true;

        return false;
    }


}
