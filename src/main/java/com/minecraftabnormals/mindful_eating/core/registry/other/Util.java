package com.minecraftabnormals.mindful_eating.core.registry.other;

import net.minecraft.resources.ResourceLocation;

public class Util {
    public static ResourceLocation parse(String input){
        String[] parts = input.split("\\.");
        String result = parts[1] + ":" + parts[2];
        return new ResourceLocation(result);
    }
}
