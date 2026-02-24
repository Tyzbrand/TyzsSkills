package com.tyzsskills.integration;

import net.neoforged.fml.ModList;

public class Integrations {

    public static boolean isKubeJSLoaded(){
        return ModList.get().isLoaded("kubejs");
    }
}
