package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.BulkPurchaseResult;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Skill implements ISkill {


    public Skill(boolean active, String id, int maximumLevel,
                 List<Integer> prices, Enums.SkillType type, Enums.CategoryType category, boolean purchasable,
                 String icon, String displayName, String description, List<Modifier> modifiers, Map<String, ValueSet> customValues,
                int levelRequirement, List<String> incompatibleSkills)
    {
        this.active = active;
        this.id = id;
        this.maximumLevel = maximumLevel;
        this.prices = prices != null ?  new ArrayList<>(prices) : new ArrayList<>();
        this.type = type;
        this.category = category;
        this.purchasable = purchasable;

        this.icon = icon;
        this.displayName = displayName;
        this.description = description;

        this.modifiers = modifiers != null? new ArrayList<>(modifiers) : new ArrayList<>();

        this.customValues = customValues != null ? new HashMap<>(customValues) : new HashMap<>();

        this.levelRequirement = levelRequirement;
        this.incompatibleSkills = incompatibleSkills != null ? new HashSet<>(incompatibleSkills) : new HashSet<>();

        if(category == Enums.CategoryType.ALL || category == Enums.CategoryType.BOOKMARKS) this.category = Enums.CategoryType.MISC;

    }

    protected transient SkillBehavior behaviour;

    //Common data -----------------------
    protected boolean active;
    protected String id;
    protected int maximumLevel;
    protected List<Integer> prices;
    protected Enums.SkillType type;
    protected Enums.CategoryType category;
    protected boolean purchasable;
    protected int levelRequirement;
    protected HashSet<String> incompatibleSkills;

    //Visual -----------------------
    protected String icon;
    protected String displayName;
    protected String description;

    //Generic
    protected List<Modifier> modifiers;

    //Immutable
    protected Map<String, ValueSet> customValues;


    //Getters
    public boolean isSkillActive(){return active;}
    @Override
    public String getID() {return id;}
    @Override
    public int getMaximumLevel() {return maximumLevel;}
    @Override
    public List<Integer> getPrices() {return Collections.unmodifiableList(prices);}
    @Override
    public int getPrice(int lvl) {
        if(lvl <= 0 || lvl > prices.size()) return Integer.MAX_VALUE;
        return prices.get(lvl - 1);
    }
    @Override
    public Enums.SkillType getType(){return type;}
    @Override
    public Enums.CategoryType getCategory(){return category;}
    @Override
    public boolean isPurchasable(){return purchasable;}
    @Override
    public String getIcon(){return icon;}
    @Override
    public String getDisplayName(){return displayName;}
    @Override
    public String getDescription(){return description;}
    @Override
    public Map<String, ValueSet> getValues(){return Map.copyOf(customValues);}
    @Override
    public ValueSet getValueSet(String key){return customValues.getOrDefault(key, null);}
    @Override
    public List<Modifier> getModifiers(){return List.copyOf(modifiers);}
    @Override
    public int getRequiredLevel() {return levelRequirement;}
    @Override
    public boolean isSkillIncompatible(String skillID) {return incompatibleSkills.contains(skillID);}
    @Override
    public @NotNull List<String> getRawIncompatibilities() {return List.copyOf(incompatibleSkills);}


    public SkillBehavior getBehavior(){return behaviour;}
    public boolean hasBehaviour(){return behaviour != null;}

    //Setters
    public void setBehaviour(SkillBehavior behaviour){
        if(behaviour != null) this.behaviour = behaviour;
    }
    @Override
    public void addIncompatibility(String id) {if(id != null) incompatibleSkills.add(id);}

    //Behavior
    @Override
    public boolean canRefund(int currentLvl, boolean refundEnabled){
        if(!refundEnabled || !isPurchasable() || currentLvl <= 0) return false;

        return currentLvl <= prices.size();
    }

    @Override
    public boolean canBuy(int currentLvl, int playerLvl, int currentSP, @NotNull List<String> ownedSkillIds){
        if(!isPurchasable() || currentLvl >= maximumLevel) return false;

        if(!meetsLevelRequirement(playerLvl) || getIncompatibilities(ownedSkillIds) != null) return false;

        var price = prices.get(currentLvl);
        return price <= currentSP;
    }

    @Override
    public BulkPurchaseResult checkBulkBuy(int currentLvl, int playerLvl, int availableSp, @NotNull List<String> ownedSkillIds){
        var bulkResultFallback = new BulkPurchaseResult(0, 0);

        if(!isPurchasable() || currentLvl >= maximumLevel) return bulkResultFallback;

        if(!meetsLevelRequirement(playerLvl) || getIncompatibilities(ownedSkillIds) != null) return bulkResultFallback;

        var spToSpend = 0;
        var levelsToAdd = 0;

        for (int i = currentLvl; i < maximumLevel; i++) {
            if (i >= prices.size()) break;
            var price = prices.get(i);

            if (availableSp >= price) {
                availableSp -= price;
                spToSpend += price;
                levelsToAdd++;
            } else break;
        }

        return new BulkPurchaseResult(levelsToAdd, spToSpend);
    }

    @Override
    public int checkBulkRefund(int currentLvl, float refundPercentage, boolean refundEnabled){
        if(!refundEnabled || !isPurchasable() || currentLvl <= 0) return 0;

        var finalRefund = 0;

        for (int i = currentLvl - 1; i >= 0; i--) {
            if (i < prices.size()) {
                int levelPrice = prices.get(i);
                int levelRefund = (int) (levelPrice * (refundPercentage / 100f));

                if (levelPrice > 0) levelRefund = Math.max(1, levelRefund);
                finalRefund += levelRefund;
            }
        }
        return finalRefund;
    }

    @Override
    @Nullable
    public List<String> getIncompatibilities(@NotNull List<String> ownedSkillIds) {
        if(incompatibleSkills.isEmpty() || ownedSkillIds.isEmpty()) return null;

        var intersections = new ArrayList<String>();

        for(var id : ownedSkillIds){
            if(incompatibleSkills.contains(id)) intersections.add(id);
        }

        return intersections.isEmpty() ? null : intersections;
    }

    //Network
    public static final StreamCodec<FriendlyByteBuf, Skill> STREAM_CODEC = StreamCodec.ofMember(
            Skill::writeToBuffer,
            Skill::readSkillFromBuffer
    );


    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(active);
        buffer.writeUtf(id);
        buffer.writeInt(maximumLevel);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);

        buffer.writeEnum(type);
        buffer.writeEnum(category);

        buffer.writeBoolean(purchasable);

        buffer.writeUtf(icon);
        buffer.writeUtf(displayName);
        buffer.writeUtf(description);

        buffer.writeCollection(modifiers, (buf, modifier) ->  modifier.writeToBuffer(buf));
        buffer.writeMap(customValues, FriendlyByteBuf::writeUtf, (buf, valueSet) ->  valueSet.writeToBuffer(buf));

        buffer.writeInt(levelRequirement);
        buffer.writeCollection(incompatibleSkills, FriendlyByteBuf::writeUtf);
    }

    public static @NotNull Skill readSkillFromBuffer(FriendlyByteBuf buffer){
        boolean active = buffer.readBoolean();
        String id = buffer.readUtf();
        int maxLevel = buffer.readInt();

        List<Integer> prices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);

        Enums.SkillType type = buffer.readEnum(Enums.SkillType.class);
        Enums.CategoryType category = buffer.readEnum(Enums.CategoryType.class);

        boolean purchasable = buffer.readBoolean();

        String icon = buffer.readUtf();
        String displayName = buffer.readUtf();
        String description = buffer.readUtf();

        List<Modifier> readModifiers = buffer.readCollection(ArrayList::new, Modifier::readFromBuffer);
        Map<String, ValueSet> readCustomValues = buffer.readMap(FriendlyByteBuf::readUtf, ValueSet::readFromBuffer);

        int levelRequirement = buffer.readInt();
        List<String> incompatibleSkills = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);

        return new Skill(active, id, maxLevel, prices, type, category, purchasable,
                            icon, displayName, description, readModifiers, readCustomValues, levelRequirement, incompatibleSkills);
    }
}
