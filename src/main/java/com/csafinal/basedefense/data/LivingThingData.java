package com.csafinal.basedefense.data;

import com.csafinal.basedefense.DropApp;

public record LivingThingData(DropApp.Type type, int hp, String name, int damage, double speed) {
}