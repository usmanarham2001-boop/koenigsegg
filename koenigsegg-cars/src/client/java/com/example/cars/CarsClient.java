package com.example.cars;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class CarsClient implements ClientModInitializer {
    private static final Map<Integer, EngineSound> ENGINES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // renderers: one model layer per car type
        for (int i = 0; i < CarsMod.NAMES.length; i++) {
            final int variant = i;
            EntityModelLayer layer = new EntityModelLayer(Identifier.of(CarsMod.ID, CarsMod.NAMES[i]), "main");
            EntityModelLayerRegistry.registerModelLayer(layer, () -> CarModel.getTexturedModelData(variant));
            EntityRendererRegistry.register(CarsMod.TYPES.get(i),
                ctx -> new CarRenderer(ctx, new CarModel(ctx.getPart(layer))));
        }

        KeyBinding customizeKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.cars.customize", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.cars"));
        KeyBinding hornKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.cars.horn", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.cars"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (customizeKey.wasPressed()) {
                if (client.player == null || client.currentScreen != null) continue;
                CarEntity target = null;
                if (client.player.getVehicle() instanceof CarEntity ridden) {
                    target = ridden;
                } else if (client.crosshairTarget instanceof EntityHitResult hit
                    && hit.getEntity() instanceof CarEntity looked) {
                    target = looked;
                }
                if (target != null) client.setScreen(new CustomizeScreen(target));
            }
            while (hornKey.wasPressed()) {
                if (client.player != null && client.player.getVehicle() instanceof CarEntity car) {
                    ClientPlayNetworking.send(new CarPayload(car.getId(), "horn", ""));
                }
            }
        });

        // start an engine loop for every car that has a driver
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            ENGINES.entrySet().removeIf(e -> e.getValue().isDone());
            for (Entity e : world.getEntities()) {
                if (e instanceof CarEntity car && car.hasPassengers() && !ENGINES.containsKey(car.getId())) {
                    EngineSound s = new EngineSound(car);
                    ENGINES.put(car.getId(), s);
                    MinecraftClient.getInstance().getSoundManager().play(s);
                }
            }
        });
    }
}
