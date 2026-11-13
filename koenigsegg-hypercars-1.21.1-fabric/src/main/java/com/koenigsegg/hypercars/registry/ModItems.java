package com.koenigsegg.hypercars.registry;

import com.koenigsegg.hypercars.item.HypercarKeyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item CARBON_MONOCOQUE = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "carbon_monocoque"),
        new Item(new Item.Properties().stacksTo(16))
    );

    public static final Item JESKO_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "jesko_key"),
        new HypercarKeyItem(() -> ModEntities.JESKO, new Item.Properties().stacksTo(1))
    );

    public static final Item GEMERA_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "gemera_key"),
        new HypercarKeyItem(() -> ModEntities.GEMERA, new Item.Properties().stacksTo(1))
    );

    public static final Item AGERA_RS_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "agera_rs_key"),
        new HypercarKeyItem(() -> ModEntities.AGERA_RS, new Item.Properties().stacksTo(1))
    );

    public static final Item REGERA_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "regera_key"),
        new HypercarKeyItem(() -> ModEntities.REGERA, new Item.Properties().stacksTo(1))
    );

    public static final Item CC850_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "cc850_key"),
        new HypercarKeyItem(() -> ModEntities.CC850, new Item.Properties().stacksTo(1))
    );

    public static final Item ONE_1_KEY = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.fromNamespaceAndPath("koenigsegg", "one_1_key"),
        new HypercarKeyItem(() -> ModEntities.ONE_1, new Item.Properties().stacksTo(1))
    );

    public static void register() {
        // Static fields register into BuiltInRegistries.ITEM during ModInitializer
    }
}
