package com.tyzsskills.impl.server.Level;

import com.tyzsskills.api.records.LevelData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class LevelPoolPreset {

    @NotNull
    public static Map<Integer, LevelData> getLevelDataPreset(){
        var map = new LinkedHashMap<Integer, LevelData>();

        map.put(1, new LevelData(350, 5));
        map.put(2, new LevelData(400, 5));
        map.put(3, new LevelData(475, 5));
        map.put(4, new LevelData(550, 5));
        map.put(5, new LevelData(650, 6));
        map.put(6, new LevelData(775, 6));
        map.put(7, new LevelData(925, 6));
        map.put(8, new LevelData(1075, 6));
        map.put(9, new LevelData(1250, 10));
        map.put(10, new LevelData(1450, 7));

        map.put(11, new LevelData(1675, 7));
        map.put(12, new LevelData(1925, 7));
        map.put(13, new LevelData(2175, 7));
        map.put(14, new LevelData(2450, 15));
        map.put(15, new LevelData(2750, 8));
        map.put(16, new LevelData(3050, 8));
        map.put(17, new LevelData(3400, 8));
        map.put(18, new LevelData(3750, 8));
        map.put(19, new LevelData(4100, 20));
        map.put(20, new LevelData(4500, 9));

        map.put(21, new LevelData(4900, 9));
        map.put(22, new LevelData(5325, 9));
        map.put(23, new LevelData(5775, 9));
        map.put(24, new LevelData(6250, 25));
        map.put(25, new LevelData(6725, 10));
        map.put(26, new LevelData(7225, 10));
        map.put(27, new LevelData(7750, 10));
        map.put(28, new LevelData(8300, 10));
        map.put(29, new LevelData(8850, 30));
        map.put(30, new LevelData(9425, 11));

        map.put(31, new LevelData(10025, 11));
        map.put(32, new LevelData(10650, 11));
        map.put(33, new LevelData(11275, 11));
        map.put(34, new LevelData(11950, 35));
        map.put(35, new LevelData(12625, 12));
        map.put(36, new LevelData(13300, 12));
        map.put(37, new LevelData(14025, 12));
        map.put(38, new LevelData(14750, 12));
        map.put(39, new LevelData(15500, 40));

        map.put(40, new LevelData(16275, 13));
        map.put(41, new LevelData(17050, 13));
        map.put(42, new LevelData(17875, 13));
        map.put(43, new LevelData(18700, 13));
        map.put(44, new LevelData(19525, 45));
        map.put(45, new LevelData(20400, 14));
        map.put(46, new LevelData(21275, 14));
        map.put(47, new LevelData(22175, 14));
        map.put(48, new LevelData(23100, 14));
        map.put(49, new LevelData(24050, 50));

        map.put(-1, new LevelData(15000, 20));

        return map;
    }


}
