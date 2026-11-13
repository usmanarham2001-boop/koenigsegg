package com.koenigsegg.hypercars.entity;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractKoenigseggEntity extends Entity implements GeoEntity {
    public static Consumer<AbstractKoenigseggEntity> clientEngineAudioStarter = vehicle -> {};

    private static final EntityDataAccessor<Boolean> DOORS_OPEN =
        SynchedEntityData.defineId(AbstractKoenigseggEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ENGINE_RPM =
        SynchedEntityData.defineId(AbstractKoenigseggEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PAINT_VARIANT =
        SynchedEntityData.defineId(AbstractKoenigseggEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float currentSpeed = 0f;
    private float prevThrottle = 0f;
    private boolean clientSoundActive = false;

    protected AbstractKoenigseggEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public abstract Vec3[] getSeatOffsets();
    public abstract float getMaxSpeedBlocksPerTick();
    public abstract float getAccelerationRate();
    public abstract float getRedlineRpm();
    public abstract float getElectricWhineMix();
    public abstract SoundEvent getEngineLoopSound();
    public abstract SoundEvent getTurboFlutterSound();
    public abstract SoundEvent getIgnitionSound();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DOORS_OPEN, false);
        builder.define(ENGINE_RPM, 900.0f);
        builder.define(PAINT_VARIANT, 0);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getSeatOffsets().length;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof LivingEntity ? (LivingEntity) first : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        int seatIndex = Math.min(this.getPassengers().indexOf(passenger), getSeatOffsets().length - 1);
        if (seatIndex < 0) seatIndex = 0;
        Vec3 local = getSeatOffsets()[seatIndex];
        return local.yRot(-this.getYRot() * ((float) Math.PI / 180F));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                boolean next = !this.areDoorsOpen();
                this.entityData.set(DOORS_OPEN, next);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!this.level().isClientSide) {
            if (this.canAddPassenger(player)) {
                player.startRiding(this);
                if (this.getPassengers().size() == 1) {
                    this.level().playSound(null, this.blockPosition(), getIgnitionSound(), SoundSource.NEUTRAL, 1.1f, 1.0f);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
        }

        Entity controller = this.getControllingPassenger();
        if (controller instanceof Player) {
            Player driver = (Player) controller;
            float forward = driver.zza;
            float steer = driver.xxa;

            if (Math.abs(forward) > 0.05f) {
                this.currentSpeed = Mth.clamp(
                    this.currentSpeed + forward * getAccelerationRate(),
                    -0.35f,
                    getMaxSpeedBlocksPerTick()
                );
            } else {
                this.currentSpeed *= 0.965f;
            }

            if (this.prevThrottle > 0.4f && forward <= 0.05f && Math.abs(this.currentSpeed) > 0.7f) {
                this.level().playSound(null, this.blockPosition(), getTurboFlutterSound(), SoundSource.NEUTRAL, 0.95f, 1.0f);
            }
            this.prevThrottle = forward;

            if (Math.abs(this.currentSpeed) > 0.02f) {
                float steerFactor = Math.min(1.0f, Math.abs(this.currentSpeed) * 1.4f);
                this.setYRot(this.getYRot() - steer * 3.4f * steerFactor);
            }

            float targetRpm = 900.0f + (Math.abs(this.currentSpeed) / getMaxSpeedBlocksPerTick()) * (getRedlineRpm() - 900.0f);
            if (!this.level().isClientSide) {
                this.entityData.set(ENGINE_RPM, Mth.lerp(0.18f, this.getEngineRpm(), targetRpm));
            }
        } else {
            this.currentSpeed *= 0.94f;
            if (!this.level().isClientSide) {
                this.entityData.set(ENGINE_RPM, Mth.lerp(0.1f, this.getEngineRpm(), 880.0f));
            }
        }

        Vec3 look = Vec3.directionFromRotation(0, this.getYRot());
        Vec3 motion = new Vec3(look.x * this.currentSpeed, this.getDeltaMovement().y, look.z * this.currentSpeed);
        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.level().isClientSide && !this.clientSoundActive && this.isVehicle()) {
            this.clientSoundActive = true;
            clientEngineAudioStarter.accept(this);
        } else if (!this.isVehicle()) {
            this.clientSoundActive = false;
        }
    }

    public boolean areDoorsOpen() {
        return this.entityData.get(DOORS_OPEN);
    }

    public float getEngineRpm() {
        return this.entityData.get(ENGINE_RPM);
    }

    public int getPaintVariant() {
        return this.entityData.get(PAINT_VARIANT);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(DOORS_OPEN, tag.getBoolean("DoorsOpen"));
        this.entityData.set(PAINT_VARIANT, tag.getInt("PaintVariant"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("DoorsOpen", this.areDoorsOpen());
        tag.putInt("PaintVariant", this.getPaintVariant());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "dihedral_doors", 6, state ->
            state.setAndContinue(this.areDoorsOpen()
                ? RawAnimation.begin().thenPlayAndHold("animation.koenigsegg.doors_open")
                : RawAnimation.begin().thenPlayAndHold("animation.koenigsegg.doors_close"))
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
