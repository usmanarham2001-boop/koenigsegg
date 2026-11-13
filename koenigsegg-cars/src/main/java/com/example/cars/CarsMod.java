package com.example.cars;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CarsMod implements ModInitializer {
    public static final String ID = "cars";

    public static final String[] NAMES = {"jesko", "regera", "gemera", "cc850"};
    public static final double[] SPEED = {0.42, 0.36, 0.34, 0.38};
    public static final int[] SEATS = {2, 2, 4, 2};
    /** Front seat position along the car, in model pixels (positive = toward the rear). */
    public static final float[] SEAT_Z = {4f, 6f, 2f, 3f};
    public static final float REAR_SEAT_Z = 16f;
    private static final int[][] EGG = {
        {0xF2F2F2, 0x9ACD32}, {0xC8102E, 0x111111}, {0x8A8D91, 0xE3B100}, {0xC4C7CC, 0xD0201E}
    };

    public static final List<EntityType<CarEntity>> TYPES = new ArrayList<>();

    public static int variantOf(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(id.getPath())) return i;
        }
        return 0;
    }

    private static void registerCar(int i) {
        String name = NAMES[i];
        EntityType<CarEntity> type = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(ID, name),
            EntityType.Builder.create(CarEntity::new, SpawnGroup.MISC)
                .dimensions(2.0f, 1.2f)
                .maxTrackingRange(10)
                .trackingTickInterval(1)
                .build(name));

        FabricDefaultAttributeRegistry.register(type, CarEntity.createAttributes(SPEED[i]));

        SpawnEggItem egg = Registry.register(
            Registries.ITEM,
            Identifier.of(ID, name + "_spawn_egg"),
            new SpawnEggItem(type, EGG[i][0], EGG[i][1], new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(e -> e.add(egg));

        TYPES.add(type);
    }

    @Override
    public void onInitialize() {
        ModSounds.register();
        PayloadTypeRegistry.playC2S().register(CarPayload.ID, CarPayload.CODEC);
        for (int i = 0; i < NAMES.length; i++) registerCar(i);

        ServerPlayNetworking.registerGlobalReceiver(CarPayload.ID,
            (payload, context) -> handle(payload, context.player()));
    }

    private static void handle(CarPayload p, ServerPlayerEntity player) {
        Entity e = player.getServerWorld().getEntityById(p.entityId());
        if (!(e instanceof CarEntity car) || player.squaredDistanceTo(car) > 100.0) return;
        switch (p.action()) {
            case "config" -> car.setConfig(CarConfig.decode(p.data(), car.variant));
            case "horn" -> {
                if (player.getVehicle() == car) {
                    car.getWorld().playSound(null, car.getX(), car.getY(), car.getZ(),
                        ModSounds.HORN, SoundCategory.NEUTRAL, 3.0f, 1.0f);
                }
            }
            default -> { }
        }
    }
}
