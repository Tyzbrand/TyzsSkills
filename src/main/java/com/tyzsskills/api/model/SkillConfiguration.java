package com.tyzsskills.api.model;

import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkillConfiguration {
    public SkillConfiguration (int levelRequirement, List<String> incompatibilities){
        this.levelRequirement = levelRequirement;
        this.incompatibleSkills = incompatibilities == null ? null : new HashSet<>(incompatibilities);
    }


    //Level Requirement
    private final int levelRequirement;
    public int levelRequirement(){return levelRequirement;}

    //Incompatibilities
    private Set<String> incompatibleSkills;
    @NotNull
    public List<String> incompatibleSkills(){return incompatibleSkills == null ? Collections.emptyList() :  List.copyOf(incompatibleSkills);}
    public void addIncompatibility(String skillID){
        if(incompatibleSkills == null) incompatibleSkills = new HashSet<>();
        incompatibleSkills.add(skillID);
    }



    //Network
    public static final StreamCodec<FriendlyByteBuf, SkillConfiguration> STREAM_CODEC = StreamCodec.ofMember(
            SkillConfiguration::writeToBuffer,
            SkillConfiguration::readConfigFromBuffer
    );

    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeInt(levelRequirement);

        List<String> toWrite = incompatibleSkills == null ? Collections.emptyList() : List.copyOf(incompatibleSkills);
        buffer.writeCollection(toWrite, FriendlyByteBuf::writeUtf);
    }

    public static @NotNull SkillConfiguration readConfigFromBuffer(FriendlyByteBuf buffer){
        int levelRequirement = buffer.readInt();

        List<String> incompatibleSkills = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);

        return new SkillConfiguration(levelRequirement, incompatibleSkills);
    }
}
