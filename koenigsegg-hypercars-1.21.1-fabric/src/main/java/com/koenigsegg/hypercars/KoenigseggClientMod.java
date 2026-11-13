package com.koenigsegg.hypercars;

import com.koenigsegg.hypercars.client.renderer.KoenigseggRenderer;
import com.koenigsegg.hypercars.client.sound.EngineSoundInstance;
import com.koenigsegg.hypercars.entity.KoenigseggEntity;
import com.koenigsegg.hypercars.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;

public class KoenigseggClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.JESKO, KoenigseggRenderer::new);
        EntityRendererRegistry.register(ModEntities.GEMERA, KoenigseggRenderer::new);
        EntityRendererRegistry.register(ModEntities.AGERA_RS, KoenigseggRenderer::new);
        EntityRendererRegistry.register(ModEntities.REGERA, KoenigseggRenderer::new);
        EntityRendererRegistry.register(ModEntities.CC850, KoenigseggRenderer::new);
        EntityRendererRegistry.register(ModEntities.ONE_1, KoenigseggRenderer::new);

        KoenigseggEntity.clientEngineAudioStarter = vehicle ->
            Minecraft.getInstance().getSoundManager().play(new EngineSoundInstance(vehicle));

        KoenigseggMod.LOGGER.info("Koenigsegg Hypercars Fabric Client renderers & audio hooks registered");
    }
}
