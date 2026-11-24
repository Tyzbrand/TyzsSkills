package com.tyzsskills.impl.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.screen.MainMenu;
import com.tyzsskills.impl.client.screen.SpellWheel;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyMapping OPEN_SKILL_MENU;
    public static KeyMapping OPEN_SPELL_WHEEL;
    public static KeyMapping OPEN_SETTINGS;

    public static KeyMapping QUICK_CAST_1;
    public static KeyMapping QUICK_CAST_2;
    public static KeyMapping QUICK_CAST_3;
    public static KeyMapping QUICK_CAST_4;
    public static KeyMapping QUICK_CAST_5;

    private static final String CATEGORY_KEY = "key.categories.tyzs_skills";

    public static void registerKeybinds(RegisterKeyMappingsEvent event){
        KeyActions.clearInputs();

        OPEN_SKILL_MENU = KeyActions.registerInput(event,
                new KeyMapping("key.tyzs_skills.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY_KEY),
                () -> Minecraft.getInstance().setScreen(new MainMenu()));

        //TODO: translation
        OPEN_SPELL_WHEEL = KeyActions.registerInput(event,
                new KeyMapping("Open Spell Wheel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY_KEY),
                () -> Minecraft.getInstance().setScreen(new SpellWheel(Enums.WheelMode.CAST, null)));

        //TODO: translation
        OPEN_SETTINGS = KeyActions.registerInput(event,
                new KeyMapping("Open Settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY_KEY),
                () -> {return;});


        //TODO: translation
        QUICK_CAST_1 = KeyActions.registerInput(event,
                new KeyMapping("Quick cast slot 1", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_KEY),
                () -> quickCast(1));

        //TODO: translation
        QUICK_CAST_2 = KeyActions.registerInput(event,
                new KeyMapping("Quick cast slot 2", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_KEY),
                () -> quickCast(2));

        //TODO: translation
        QUICK_CAST_3 = KeyActions.registerInput(event,
                new KeyMapping("Quick cast slot 3", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_KEY),
                () -> quickCast(3));

        //TODO: translation
        QUICK_CAST_4 = KeyActions.registerInput(event,
                new KeyMapping("Quick cast slot 4", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_KEY),
                () -> quickCast(4));

        //TODO: translation
        QUICK_CAST_5 = KeyActions.registerInput(event,
                new KeyMapping("Quick cast slot 5", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY_KEY),
                () -> quickCast(5));
    }


    //UTILS
    private static void quickCast(int slot){
        var cache = ClientCache.get();

        if(slot > cache.SPELLS.getMaxSlots()) return;

        var spellInSlot = cache.SPELLS.getSpellInSlot(slot);
        if(spellInSlot == null) return;

        cache.SPELLS.triggerSpellAction(spellInSlot, Enums.SpellAction.CAST, slot);
    }
}
