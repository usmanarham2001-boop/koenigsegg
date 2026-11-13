package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg Gemera (2025) - TFG 3-Cylinder Twin-Turbo + Tri-Motor EV
 * Seats: 4 | Top Speed: 400 km/h | Horsepower: 2300 HP
 */
public class GemeraEntity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.46, 0.38, 0.56) // Driver Seat (Driver),
        new Vec3(0.46, 0.38, 0.56) // Front Co-Pilot (Co-Pilot),
        new Vec3(-0.44, 0.38, -0.44) // Rear Left Bucket (Rear Passenger),
        new Vec3(0.44, 0.38, -0.44) // Rear Right Bucket (Rear Passenger)
    };

    public GemeraEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.18f;
    }

    @Override
    public float getAccelerationRate() {
        return 0.095f;
    }

    @Override
    public float getRedlineRpm() {
        return 8500.0f;
    }

    @Override
    public float getElectricWhineMix() {
        return 0.96f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.GEMERA_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.GEMERA_ELECTRIC_SPOOL;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.GEMERA_IGNITION;
    }
}
