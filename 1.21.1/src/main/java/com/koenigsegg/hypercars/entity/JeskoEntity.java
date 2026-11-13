package com.koenigsegg.hypercars.entity;

import com.koenigsegg.hypercars.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Koenigsegg Jesko (2024) - 5.0L Twin-Turbo V8 · 9-Speed LST
 * Seats: 2 | Top Speed: 483 km/h | Horsepower: 1600 HP
 */
public class JeskoEntity extends AbstractKoenigseggEntity {
    private static final Vec3[] SEATS = new Vec3[] {
        new Vec3(-0.46, 0.36, 0.18) // Driver Seat (Driver),
        new Vec3(0.46, 0.36, 0.18) // Co-Pilot Seat (Co-Pilot)
    };

    public JeskoEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Vec3[] getSeatOffsets() {
        return SEATS;
    }

    @Override
    public float getMaxSpeedBlocksPerTick() {
        return 2.35f;
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
        return 0.08f;
    }

    @Override
    public SoundEvent getEngineLoopSound() {
        return ModSounds.JESKO_ENGINE_LOOP;
    }

    @Override
    public SoundEvent getTurboFlutterSound() {
        return ModSounds.JESKO_TURBO_FLUTTER;
    }

    @Override
    public SoundEvent getIgnitionSound() {
        return ModSounds.JESKO_IGNITION;
    }
}
