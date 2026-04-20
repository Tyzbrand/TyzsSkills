package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApiStatus.Internal
public class SortTools {

    //TYPE
    private static final List<SortType> activeSortTypes = new ArrayList<>();
    private static int currentSortTypeIndex = 0;

    //SENS
    private static Enums.SortingDirection currentSortingDirection = Enums.SortingDirection.ASCENDING;

    //CATEGORY
    private static Enums.CategoryType currentSkillCategory = Enums.CategoryType.ALL;

    //BOOLS
    private static boolean showUnowned = true;

    //SEARCH BAR
    private static String currentSearchQuery = "";


    private static final List<Skill> currentSkillOrder = new ArrayList<>();


    public static void registerSortingType(@NotNull SortType sortType){
        activeSortTypes.add(sortType);}


    public static List<Skill> refreshList(){
        List<Skill> listToSort =  new ArrayList<>(ClientCache.getAllSkills());

        if(!currentSearchQuery.isBlank()){
            var query = currentSearchQuery.trim().toLowerCase(Locale.ROOT);
            listToSort.removeIf(s -> queryCheck(query, s));
        }

        if(!showUnowned) listToSort.removeIf(s -> ClientCache.getSkillLevel(s.getID()) <= 0);

        if(currentSkillCategory == Enums.CategoryType.BOOKMARKS) listToSort.removeIf(s -> !ClientCache.isSkillBookmarked(s.getID()));
        else if(currentSkillCategory != Enums.CategoryType.ALL) listToSort.removeIf(s -> s.getCategory() != currentSkillCategory);

        if(!activeSortTypes.isEmpty()){
            var currentSortType = activeSortTypes.get(currentSortTypeIndex);
            var comparator = currentSortingDirection == Enums.SortingDirection.ASCENDING ?
                    currentSortType.comparator() : currentSortType.comparator().reversed();

            listToSort.sort(comparator);
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

    public static void SetCategoryType(Enums.CategoryType category){
        currentSkillCategory = category;
    }

    public static void ToggleUnowned(){
        showUnowned = !showUnowned;
    }

    public static void setSearchQuery(@NotNull String searchQuery){
        currentSearchQuery = searchQuery.toLowerCase();
    }


    //Getters
    public static List<Skill> getCurrentSkillOrder() {
        return activeSortTypes.isEmpty() ? refreshList() : List.copyOf(currentSkillOrder);
    }

    public static SortType getCurrentSortType(){
        return activeSortTypes.get(currentSortTypeIndex);
    }

    public static Enums.CategoryType getCurrentSkillCategory(){
        return currentSkillCategory;
    }

    public static String getCurrentSearchQuery(){
        return currentSearchQuery;
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
