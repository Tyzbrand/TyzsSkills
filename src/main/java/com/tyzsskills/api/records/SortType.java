package com.tyzsskills.api.records;

import com.tyzsskills.api.interfaces.ISkill;

import java.util.Comparator;

public record SortType(String name, Comparator<ISkill> comparator, int u, int v, int uHover, int vHover) {}
