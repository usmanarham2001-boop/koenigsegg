package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg Agera RS (2018) - 5.0L Twin-Turbo V8 · 7-Speed Single-Clutch
 * Seats: 2 | Top Speed: 447 km/h | Horsepower: 1341 HP
 */
public class AgeraRSEntity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.45, 0.35, 0.16) // Driver Seat (Driver),
        new Vec3(0.45, 0.35, 0.16) // Passenger Seat (Co-Pilot)
    };

    public AgeraRSEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.28f;
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
        return 0.02f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.AGERA_RS_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.AGERA_RS_TURBO_FLUTTER;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.AGERA_RS_IGNITION;
    }
}
