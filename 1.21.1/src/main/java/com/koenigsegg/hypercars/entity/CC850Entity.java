package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg CC850 (2024) - 5.0L Twin-Turbo V8 · 6-Speed ESS Gated Manual
 * Seats: 2 | Top Speed: 460 km/h | Horsepower: 1385 HP
 */
public class CC850Entity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.45, 0.35, 0.15) // Driver Seat (Driver),
        new Vec3(0.45, 0.35, 0.15) // Passenger Seat (Co-Pilot)
    };

    public CC850Entity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.30f;
    }

    @Override
    public float getAccelerationRate() {
        return 0.088f;
    }

    @Override
    public float getRedlineRpm() {
        return 8500.0f;
    }

    @Override
    public float getElectricWhineMix() {
        return 0.02f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.CC850_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.CC850_TURBO_FLUTTER;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.CC850_IGNITION;
    }
}
