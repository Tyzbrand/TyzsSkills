package com.tyzsskills.impl.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class XpBlock {
    private final static Map<String, Float> Xpvalues = new HashMap<>();
    private static final Map<TagKey<Block>, Float> XpTagValues = new HashMap<>();

    private final static Map<Block, Float> CACHE = new HashMap<>();


    //Setup
    public static void LoadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);

            if(!category.has("xp")){
                ErrorManager.RegisterLoadError("loading '" + categoryKey + "' in block xp values", "Missing 'xp' value");
                continue;
            }

            if(!category.has("blocks")){
                ErrorManager.RegisterLoadError("loading '" + categoryKey + "' in block xp values", "Missing 'block' list");
                continue;
            }

            float xpValue = category.get("xp").getAsFloat();
            for (var iteration : category.getAsJsonArray("blocks")){

                String entry = iteration.getAsString();
                if(entry.startsWith("#")){
                    String tag = entry.substring(1);
                    try{
                        var location = ResourceLocation.parse(tag);
                        var tagKey = TagKey.create(Registries.BLOCK, location);
                        XpTagValues.put(tagKey, xpValue);
                    }
                    catch (Exception ex){
                        System.err.println("TyzSkills: Error when loading tags '" + entry + "' : " + ex.getMessage());
                        ErrorManager.RegisterLoadError("parsing block/tag '" + entry + "' in block xp values", "Invalid ResourceLocation format");
                    }
                }
                else{Xpvalues.put(entry, xpValue);}
            }
        }
    }

    public static void ClearValues(){
        Xpvalues.clear();
        XpTagValues.clear();
        CACHE.clear();
    }

    //Actifs
    public static void BlockBreakProfit(BlockState state, ServerPlayer player){

        if(state.getBlock() instanceof CropBlock crop){
            if(!crop.isMaxAge(state)) return;
        }
        else if (state.getBlock() instanceof NetherWartBlock) {
            if (state.getValue(NetherWartBlock.AGE) < 3) return;
        }


        var amount = GetBlockValue(state) * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        if(amount > 0) XpManager.addXP(player, amount);
    }



    //Getters
    public static float GetBlockValue(BlockState state) {

        var block = state.getBlock();

        if(CACHE.containsKey(block)){return CACHE.get(block);}

        var blockID = BuiltInRegistries.BLOCK.getKey(block).toString();
        var cacheValue = 0f;

        if(Xpvalues.containsKey(blockID)) {cacheValue = Xpvalues.get(blockID);}
        else{
            for(var entry : XpTagValues.entrySet()){
                if(state.is(entry.getKey())){cacheValue = entry.getValue(); break;}
            }
        }

        CACHE.put(block, cacheValue);
        return cacheValue;
    }

}
