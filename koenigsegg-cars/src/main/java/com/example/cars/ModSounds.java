package com.example.cars;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    public static SoundEvent ENGINE;
    public static SoundEvent HORN;

    private static SoundEvent reg(String name) {
        Identifier id = Identifier.of(CarsMod.ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        ENGINE = reg("engine");
        HORN = reg("horn");
    }

    private ModSounds() {}
}
