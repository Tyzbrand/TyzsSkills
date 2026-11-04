package com.tyzsskills.impl.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class MainKeybind {
    public static final KeyMapping OPEN_SKILL_MENU = new KeyMapping(
            "key.tyzs_skills.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.tyzs_skills"
    );

    public static final KeyMapping OPEN_SPELL_WHEEL = new KeyMapping(
            //TODO: translation
            "Open Spell Wheel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "key.categories.tyzs_skills"
    );
}
