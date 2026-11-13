package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg One:1 (2015) - 5.0L Twin-Turbo V8 · Titanium Exhaust
 * Seats: 2 | Top Speed: 453 km/h | Horsepower: 1360 HP
 */
public class One1Entity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.45, 0.35, 0.16) // Driver Seat (Driver),
        new Vec3(0.45, 0.35, 0.16) // Co-Pilot Seat (Co-Pilot)
    };

    public One1Entity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.33f;
    }

    @Override
    public float getAccelerationRate() {
        return 0.088f;
    }

    @Override
    public float getRedlineRpm() {
        return 8250.0f;
    }

    @Override
    public float getElectricWhineMix() {
        return 0.04f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.ONE_1_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.ONE_1_TURBO_FLUTTER;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.ONE_1_IGNITION;
    }
}
