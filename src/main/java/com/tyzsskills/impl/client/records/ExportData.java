package com.tyzsskills.impl.client.records;

import java.util.Map;

public record ExportData(long worldTime, int currentLvl, float currentXP,
                         int currentSP, float avgXP, Map<String, Integer> purchasedSkills) {
}
