package com.koenigsegg.hypercars.client.model;

import com.koenigsegg.hypercars.entity.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KoenigseggGeoModel extends GeoModel<AbstractKoenigseggEntity> {
    private static String resolveModelName(AbstractKoenigseggEntity entity) {
        if (entity instanceof JeskoEntity) return "jesko";
        if (entity instanceof GemeraEntity) return "gemera";
        if (entity instanceof AgeraRSEntity) return "agera_rs";
        if (entity instanceof RegeraEntity) return "regera";
        if (entity instanceof CC850Entity) return "cc850";
        return "one_1";
    }

    @Override
    public ResourceLocation getModelResource(AbstractKoenigseggEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "geo/" + resolveModelName(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AbstractKoenigseggEntity entity) {
        String name = resolveModelName(entity);
        String finish;
        switch (entity.getPaintVariant()) {
            case 1: finish = "imperial_blue"; break;
            case 2: finish = "raw_carbon"; break;
            case 3: finish = "crystal_white"; break;
            case 4: finish = "emerald_carbon"; break;
            case 5: finish = "battleship_grey"; break;
            case 6: finish = "liquid_silver"; break;
            case 7: finish = "swiss_gold"; break;
            case 8: finish = "obsidian_red"; break;
            default: finish = "tang_orange"; break;
        }
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "textures/entity/" + name + "_" + finish + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractKoenigseggEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "animations/koenigsegg_doors.animation.json");
    }
}
