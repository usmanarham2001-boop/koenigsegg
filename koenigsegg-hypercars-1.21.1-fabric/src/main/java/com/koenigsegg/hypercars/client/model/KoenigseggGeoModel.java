package com.koenigsegg.hypercars.client.model;

import com.koenigsegg.hypercars.entity.KoenigseggEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KoenigseggGeoModel extends GeoModel<KoenigseggEntity> {
    @Override
    public ResourceLocation getModelResource(KoenigseggEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "geo/" + entity.getCarModel().id + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KoenigseggEntity entity) {
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
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "textures/entity/finishes/" + finish + "_albedo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KoenigseggEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("koenigsegg", "animations/koenigsegg_doors.animation.json");
    }
}
