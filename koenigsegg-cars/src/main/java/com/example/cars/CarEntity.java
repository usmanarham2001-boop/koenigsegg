package com.example.cars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CarEntity extends PathAwareEntity {
    private static final TrackedData<String> CONFIG =
        DataTracker.registerData(CarEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final float[] SEAT_X = {0.34f, -0.34f, 0.34f, -0.34f};

    public final int variant;

    private String cacheStr = null;
    private CarConfig cacheCfg = null;

    // client-side animation state
    public float doorOpen = 1f, prevDoorOpen = 1f;
    public float wheelRot = 0f, prevWheelRot = 0f;

    public CarEntity(EntityType<? extends CarEntity> type, World world) {
        super(type, world);
        this.variant = CarsMod.variantOf(type);
        this.setPersistent();
        if (!world.isClient) {
            this.dataTracker.set(CONFIG, CarConfig.defaults(variant).encode());
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes(double speed) {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
            .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CONFIG, "");
    }

    public CarConfig getConfig() {
        String s = this.dataTracker.get(CONFIG);
        if (cacheCfg == null || !s.equals(cacheStr)) {
            cacheCfg = CarConfig.decode(s, variant);
            cacheStr = s;
        }
        return cacheCfg;
    }

    public void setConfig(CarConfig cfg) {
        this.dataTracker.set(CONFIG, cfg.encode());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("CarConfig", this.dataTracker.get(CONFIG));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CarConfig")) {
            this.dataTracker.set(CONFIG, nbt.getString("CarConfig"));
        }
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox().expand(2.4, 0.6, 2.4);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            prevDoorOpen = doorOpen;
            float target = hasPassengers() ? 0f : 1f;
            doorOpen += (target - doorOpen) * 0.12f;

            prevWheelRot = wheelRot;
            double dx = getX() - prevX;
            double dz = getZ() - prevZ;
            double yawRad = Math.toRadians(getYaw());
            double signed = dx * -Math.sin(yawRad) + dz * Math.cos(yawRad);
            wheelRot += (float) (signed / 0.34);
        }
    }

    // ------------------------------------------------------------------ riding
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getPassengerList().size() < CarsMod.SEATS[variant]) {
            if (!this.getWorld().isClient) player.startRiding(this);
            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < CarsMod.SEATS[variant];
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof PlayerEntity p ? p : null;
    }

    @Override
    public boolean isPushable() {
        return !this.hasPassengers();
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) return;
        int i = Math.min(this.getPassengerList().indexOf(passenger), 3);
        float sz = (i < 2) ? CarsMod.SEAT_Z[variant] : CarsMod.REAR_SEAT_Z;
        double left = SEAT_X[i];
        double fwd = -sz / 16.0;
        double yawRad = Math.toRadians(this.getYaw());
        double fx = -Math.sin(yawRad), fz = Math.cos(yawRad);
        double lx = Math.cos(yawRad), lz = Math.sin(yawRad);
        positionUpdater.accept(passenger,
            this.getX() + lx * left + fx * fwd,
            this.getY() - 0.72,
            this.getZ() + lz * left + fz * fwd);
    }

    @Override
    public void travel(Vec3d input) {
        LivingEntity driver = this.getControllingPassenger();
        if (driver != null && this.isAlive()) {
            this.setYaw(driver.getYaw());
            this.setPitch(0);
            this.bodyYaw = this.getYaw();
            this.headYaw = this.getYaw();
            this.prevYaw = this.getYaw();

            float forward = driver.forwardSpeed;
            if (forward < 0) forward *= 0.4f; // slower reverse
            if (this.isLogicalSideForUpdatingMovement()) {
                this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                super.travel(new Vec3d(0, input.y, forward));
            }
        } else {
            super.travel(input);
        }
    }
}
