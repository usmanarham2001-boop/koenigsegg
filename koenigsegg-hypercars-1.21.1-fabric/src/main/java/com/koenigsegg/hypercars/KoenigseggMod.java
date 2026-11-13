package com.koenigsegg.hypercars;

import com.koenigsegg.hypercars.registry.ModEntities;
import com.koenigsegg.hypercars.registry.ModItems;
import com.koenigsegg.hypercars.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoenigseggMod implements ModInitializer {
    public static final String MOD_ID = "koenigsegg";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CreativeModeTab KOENIGSEGG_TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "hypercars"),
        FabricItemGroup.builder()
            .title(Component.translatable("itemGroup." + MOD_ID + ".hypercars"))
            .icon(() -> new ItemStack(ModItems.JESKO_KEY))
            .displayItems((params, output) -> {
                output.accept(ModItems.CARBON_MONOCOQUE);
                output.accept(ModItems.JESKO_KEY);
                output.accept(ModItems.GEMERA_KEY);
                output.accept(ModItems.AGERA_RS_KEY);
                output.accept(ModItems.REGERA_KEY);
                output.accept(ModItems.CC850_KEY);
                output.accept(ModItems.ONE_1_KEY);
            })
            .build()
    );

    @Override
    public void onInitialize() {
        ModSounds.register();
        ModEntities.register();
        ModItems.register();
        LOGGER.info("Koenigsegg Hypercars Fabric Mod (1.21.1) initialized");
    }
}
