package com.example.cars;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

/** Looping engine sound that follows the car; pitch and volume rise with speed and throttle. */
public class EngineSound extends MovingSoundInstance {
    private static final float[] BASE = {1.0f, 0.9f, 1.2f, 1.05f};
    private final CarEntity car;
    private float rev;

    public EngineSound(CarEntity car) {
        super(ModSounds.ENGINE, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        this.car = car;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.4f;
        this.pitch = 0.6f;
        this.x = car.getX();
        this.y = car.getY();
        this.z = car.getZ();
    }

    @Override
    public void tick() {
        if (car.isRemoved() || !car.hasPassengers()) {
            this.setDone();
            return;
        }
        this.x = car.getX();
        this.y = car.getY();
        this.z = car.getZ();

        double blocksPerSecond = Math.hypot(car.getX() - car.prevX, car.getZ() - car.prevZ) * 20.0;
        float speed = (float) MathHelper.clamp(blocksPerSecond / 18.0, 0.0, 1.0);
        float throttle = 0f;
        LivingEntity driver = car.getControllingPassenger();
        if (driver != null) throttle = Math.min(1f, Math.abs(driver.forwardSpeed));

        float target = Math.max(speed, throttle * 0.4f);
        rev += (target - rev) * 0.12f;
        this.pitch = MathHelper.clamp(BASE[car.variant] * (0.6f + 1.15f * rev), 0.5f, 2.0f);
        this.volume = 0.35f + 0.65f * rev;
    }
}
