package com.koenigsegg.hypercars.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent JESKO_ENGINE_LOOP = registerSound("entity.jesko.engine_loop");
    public static final SoundEvent JESKO_TURBO_FLUTTER = registerSound("entity.jesko.turbo_flutter");
    public static final SoundEvent JESKO_IGNITION = registerSound("entity.jesko.ignition");

    public static final SoundEvent GEMERA_ENGINE_LOOP = registerSound("entity.gemera.engine_loop");
    public static final SoundEvent GEMERA_ELECTRIC_SPOOL = registerSound("entity.gemera.electric_spool");
    public static final SoundEvent GEMERA_IGNITION = registerSound("entity.gemera.ignition");

    public static final SoundEvent AGERA_RS_ENGINE_LOOP = registerSound("entity.agera_rs.engine_loop");
    public static final SoundEvent AGERA_RS_TURBO_FLUTTER = registerSound("entity.agera_rs.turbo_flutter");
    public static final SoundEvent AGERA_RS_IGNITION = registerSound("entity.agera_rs.ignition");

    public static final SoundEvent REGERA_ENGINE_LOOP = registerSound("entity.regera.engine_loop");
    public static final SoundEvent REGERA_ELECTRIC_SPOOL = registerSound("entity.regera.electric_spool");
    public static final SoundEvent REGERA_IGNITION = registerSound("entity.regera.ignition");

    public static final SoundEvent CC850_ENGINE_LOOP = registerSound("entity.cc850.engine_loop");
    public static final SoundEvent CC850_TURBO_FLUTTER = registerSound("entity.cc850.turbo_flutter");
    public static final SoundEvent CC850_IGNITION = registerSound("entity.cc850.ignition");

    public static final SoundEvent ONE_1_ENGINE_LOOP = registerSound("entity.one_1.engine_loop");
    public static final SoundEvent ONE_1_TURBO_FLUTTER = registerSound("entity.one_1.turbo_flutter");
    public static final SoundEvent ONE_1_IGNITION = registerSound("entity.one_1.ignition");

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("koenigsegg", name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
        // Static fields register into BuiltInRegistries.SOUND_EVENT during ModInitializer
    }
}
