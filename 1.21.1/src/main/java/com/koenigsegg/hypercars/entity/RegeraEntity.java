package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg Regera (2021) - 5.0L Twin-Turbo V8 + Triple E-Motor KDD
 * Seats: 2 | Top Speed: 410 km/h | Horsepower: 1500 HP
 */
public class RegeraEntity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.45, 0.35, 0.17) // Driver Seat (Driver),
        new Vec3(0.45, 0.35, 0.17) // Co-Pilot Seat (Co-Pilot)
    };

    public RegeraEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.24f;
    }

    @Override
    public float getAccelerationRate() {
        return 0.092f;
    }

    @Override
    public float getRedlineRpm() {
        return 8250.0f;
    }

    @Override
    public float getElectricWhineMix() {
        return 0.90f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.REGERA_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.REGERA_ELECTRIC_SPOOL;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.REGERA_IGNITION;
    }
}
