package com.tyzsskills.api.model;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillConfiguration {
    public SkillConfiguration(){this(null, null, null, null, null, null);}

    public SkillConfiguration (Integer levelRequirement,
                               List<String> incompatibilities, List<String> prerequisites,
                               Boolean refundable, Boolean purchasable, Boolean visible){


        this.refundable = refundable;
        this.purchasable = purchasable;
        this.visible = visible;

        this.levelRequirement = levelRequirement;

        this.incompatibleSkills = incompatibilities == null ? null : new HashSet<>(incompatibilities);
        this.skillPrerequisites = prerequisites == null ? null : new HashSet<>(prerequisites);
    }


    //Bools
    private final Boolean refundable;
    public boolean refundable(){return refundable == null || refundable;}

    private final Boolean purchasable;
    public boolean purchasable(){return purchasable == null || purchasable;}

    private final Boolean visible;
    public boolean visible(){return visible == null || visible;}


    //Ints
    private final Integer levelRequirement;
    public int levelRequirement(){return levelRequirement == null ? 0 : levelRequirement;}

    //Lists
    private Set<String> incompatibleSkills;
    @NotNull
    public List<String> incompatibleSkills(){return incompatibleSkills == null ? Collections.emptyList() :  List.copyOf(incompatibleSkills);}
    public void addIncompatibility(String skillID){
        if(incompatibleSkills == null) incompatibleSkills = new HashSet<>();
        incompatibleSkills.add(skillID);
    }
    public void removeIncompatibility(String skillID){if(incompatibleSkills != null) incompatibleSkills.remove(skillID);}

    private Set<String> skillPrerequisites;
    @NotNull
    public List<String> skillPrerequisites(){return skillPrerequisites == null ? Collections.emptyList() :  List.copyOf(skillPrerequisites);}
    public void removePrerequisite(String skillID){if(skillPrerequisites != null) skillPrerequisites.remove(skillID);}



    //Network
    public static final StreamCodec<FriendlyByteBuf, SkillConfiguration> STREAM_CODEC = StreamCodec.ofMember(
            SkillConfiguration::writeToBuffer,
            SkillConfiguration::readConfigFromBuffer
    );

    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeBoolean(purchasable());
        buffer.writeBoolean(refundable());
        buffer.writeBoolean(visible());

        buffer.writeInt(levelRequirement());

        buffer.writeCollection(incompatibleSkills(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(skillPrerequisites(), FriendlyByteBuf::writeUtf);
    }

    public static @NotNull SkillConfiguration readConfigFromBuffer(FriendlyByteBuf buffer){
        var purchasableToRead = buffer.readBoolean();
        Boolean purchasable = purchasableToRead ? null : false;

        var refundableToRead = buffer.readBoolean();
        Boolean refundable = refundableToRead ? null : false;

        var visibleToRead = buffer.readBoolean();
        Boolean visible = visibleToRead ? null : false;

        var intToRead = buffer.readInt();
        Integer levelRequirement =  intToRead <= 0 ? null : intToRead;

        List<String> incompatibleSkills = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);

        List<String> skillPrerequisites = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);

        return new SkillConfiguration(levelRequirement, incompatibleSkills, skillPrerequisites,refundable, purchasable, visible);
    }
}
