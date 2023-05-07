package com.csafinal.basedefense.data;

import java.util.Collections;
import java.util.List;

public record TowerData(
        String name,
        String imageName,
        String projectileImageName,
        int attack,
        double attackRate,
        int cost,
        boolean isSplashDamage,
        List<String> effects,
        int hp
) {

    @Override
    public List<String> effects() {
        return effects != null ? effects : Collections.emptyList();
    }
}
