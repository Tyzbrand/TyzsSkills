package com.tyzsskills.api.tools;

import com.tyzsskills.Settings;
import com.tyzsskills.api.model.settings.*;
import com.tyzsskills.impl.server.active.ErrorManager;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SettingsParser {
    private static String header(boolean isClient){
         var prefix = isClient ? "Client Settings, " : "Server Settings, ";
        return prefix + Settings.CONFIG_VERSION;
    }

    private static @NotNull Path mainPath(boolean isClient){
        var fileName = isClient ? "client_settings.cfg" : "server_settings.cfg";
        return FMLPaths.CONFIGDIR.get().resolve("tyzs_skills").resolve(fileName);
    }

    public static void writeSettings(boolean isClient){
        var path = mainPath(isClient);
        var sideMark = isClient ? "client" : "server";

        try{Files.createDirectories(path.getParent());}
        catch (IOException ex){
            throw new IllegalStateException("Unable to init Tyz's Skills config folders (" + sideMark + ")");
        }

        var settings = isClient ? new ArrayList<>(Settings.getClientSettings()) : new ArrayList<>(Settings.getServerSettings());
        settings.sort(Comparator.comparingInt(setting -> setting.meta().category().priority()));

        var prevCat = settings.getFirst().meta().category();

        var sb = new StringBuilder();
        comment(sb, header(isClient));
        brk(sb);
        brk(sb);
        cat(sb, prevCat.label());

        for(var setting : settings){
            var currentCat = setting.meta().category();
            if(currentCat != prevCat){
                brk(sb);
                prevCat = currentCat;
                cat(sb, currentCat.label());
            }

            var comment = setting.comment();
            if(!comment.isEmpty()) comment(sb, comment);
            range(sb, setting);
            value(sb, setting);

            brk(sb);
        }

        var finalString = sb.toString();
        try {Files.writeString(path, finalString, StandardCharsets.UTF_8);}
        catch (IOException ex){throw new IllegalStateException("Unable to write Tyz's Skills default settings (" + sideMark + ")", ex);}
    }
    public static void parseSettings(boolean isClient){
        var path = mainPath(isClient);
        if(!Files.exists(path)) {
            writeSettings(isClient);
            return;
        }

        var sideMark = isClient ? "client" : "server";

        List<String> lines;
        try{lines = Files.readAllLines(path, StandardCharsets.UTF_8);}
        catch(IOException ex){throw new IllegalStateException("Unable to read Tyz's Skills settings ("+ sideMark + ")", ex);}

        var fileVersion = "";
        var foundKeys = new HashSet<>();
        var hasInconsistencies = false;

        for(var rawLine : lines){
            var line = rawLine.trim();

            if(line.startsWith("#" + header(isClient))){
                fileVersion = line.split(",", 2)[1].trim();
                continue;
            }

            if(line.startsWith("#") || line.startsWith("~") || line.startsWith("[") || line.isEmpty()) continue;

            var parts = line.split("=", 2);
            if(parts.length < 2) continue;

            var key = parts[0].trim();
            var value = parts[1].trim();

            var setting = Settings.getByKey(key, isClient);
            if(setting == null) {
                hasInconsistencies = true;
                continue;
            }
            else if (!foundKeys.add(key)) hasInconsistencies = true;

            try{
                switch (setting) {
                    case IntSetting param -> param.set(Integer.parseInt(value));
                    case FloatSetting param -> param.set(Float.parseFloat(value));
                    case BoolSetting param -> {
                        if(!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)){
                            throw new NumberFormatException();
                        }
                        param.set(Boolean.parseBoolean(value));
                    }
                    case ColorSetting param -> {
                        if(!value.matches(ColorSetting.REGEX)) throw new NumberFormatException();
                        param.set(value);
                    }
                }
            }
            catch (NumberFormatException ex){
                ErrorManager.registerSettingWarn(key, "invalid value format", isClient);
            }
        }

        var sizes = isClient ? Settings.getClientSettings().size() : Settings.getServerSettings().size();
        var missingKeys = foundKeys.size() != sizes;
        var changedVersion = !Settings.CONFIG_VERSION.equalsIgnoreCase(fileVersion);

        if(hasInconsistencies || missingKeys ||changedVersion) writeSettings(isClient);
    }

    //UTILS
    private static void brk(StringBuilder sb){sb.append("\n");}
    private static void cat(StringBuilder sb, String cat){sb.append(String.format("[%s]", cat)); brk(sb);}
    private static void comment(StringBuilder sb, String comment){sb.append(String.format("#%s", comment)); brk(sb);}
    private static void range(StringBuilder sb, Setting<?> set){sb.append(String.format("# ~ %s", set.range())); brk(sb);}
    private static void value(StringBuilder sb, Setting<?> set){sb.append(String.format("%s = %s", set.meta().key(), set.get())); brk(sb);}
}
