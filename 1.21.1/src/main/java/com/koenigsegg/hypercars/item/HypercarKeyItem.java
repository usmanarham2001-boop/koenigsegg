package com.koenigsegg.hypercars.item;

import com.koenigsegg.hypercars.entity.AbstractKoenigseggEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class HypercarKeyItem extends Item {
    private final Supplier<EntityType<? extends AbstractKoenigseggEntity>> entityTypeSupplier;

    public HypercarKeyItem(Supplier<EntityType<? extends AbstractKoenigseggEntity>> entityTypeSupplier, Properties properties) {
        super(properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            AbstractKoenigseggEntity car = this.entityTypeSupplier.get().create(level);
            if (car != null) {
                Vec3 pos = context.getClickLocation();
                float yaw = context.getPlayer() != null ? context.getPlayer().getYRot() : 0f;
                car.moveTo(pos.x, pos.y + 0.1, pos.z, yaw, 0f);
                level.addFreshEntity(car);
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
