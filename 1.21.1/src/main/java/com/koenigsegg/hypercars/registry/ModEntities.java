package com.koenigsegg.hypercars.registry;

import com.koenigsegg.hypercars.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<JeskoEntity> JESKO = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "jesko"),
        EntityType.Builder.of(JeskoEntity::new, MobCategory.MISC)
            .sized(2.40f, 1.35f)
            .clientTrackingRange(12)
            .build("jesko")
    );

    public static final EntityType<GemeraEntity> GEMERA = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "gemera"),
        EntityType.Builder.of(GemeraEntity::new, MobCategory.MISC)
            .sized(2.50f, 1.45f)
            .clientTrackingRange(12)
            .build("gemera")
    );

    public static final EntityType<AgeraRSEntity> AGERA_RS = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "agera_rs"),
        EntityType.Builder.of(AgeraRSEntity::new, MobCategory.MISC)
            .sized(2.35f, 1.32f)
            .clientTrackingRange(12)
            .build("agera_rs")
    );

    public static final EntityType<RegeraEntity> REGERA = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "regera"),
        EntityType.Builder.of(RegeraEntity::new, MobCategory.MISC)
            .sized(2.42f, 1.32f)
            .clientTrackingRange(12)
            .build("regera")
    );

    public static final EntityType<CC850Entity> CC850 = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "cc850"),
        EntityType.Builder.of(CC850Entity::new, MobCategory.MISC)
            .sized(2.36f, 1.33f)
            .clientTrackingRange(12)
            .build("cc850")
    );

    public static final EntityType<One1Entity> ONE_1 = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "one_1"),
        EntityType.Builder.of(One1Entity::new, MobCategory.MISC)
            .sized(2.38f, 1.36f)
            .clientTrackingRange(12)
            .build("one_1")
    );

    public static void register() {
        // Static fields register into BuiltInRegistries.ENTITY_TYPE during ModInitializer
    }
}
