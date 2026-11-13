package com.koenigsegg.hypercars.client.sound;

import com.koenigsegg.hypercars.entity.AbstractKoenigseggEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class EngineSoundInstance extends AbstractTickableSoundInstance {
    private final AbstractKoenigseggEntity vehicle;

    public EngineSoundInstance(AbstractKoenigseggEntity vehicle) {
        super(vehicle.getEngineLoopSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.vehicle = vehicle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.75f;
        this.pitch = 0.72f;
    }

    @Override
    public void tick() {
        if (this.vehicle.isRemoved() || !this.vehicle.isVehicle()) {
            this.stop();
            return;
        }
        this.x = this.vehicle.getX();
        this.y = this.vehicle.getY();
        this.z = this.vehicle.getZ();

        float rpmRatio = Mth.clamp(this.vehicle.getEngineRpm() / this.vehicle.getRedlineRpm(), 0.08f, 1.0f);
        this.pitch = 0.65f + rpmRatio * (1.28f + this.vehicle.getElectricWhineMix() * 0.35f);
        this.volume = 0.55f + rpmRatio * 0.65f;
    }
}
