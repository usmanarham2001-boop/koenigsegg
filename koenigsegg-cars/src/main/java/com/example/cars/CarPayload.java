package com.example.cars;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Client -> server: action is "config" (data = encoded CarConfig) or "horn". */
public record CarPayload(int entityId, String action, String data) implements CustomPayload {
    public static final CustomPayload.Id<CarPayload> ID =
        new CustomPayload.Id<>(Identifier.of(CarsMod.ID, "car"));

    public static final PacketCodec<RegistryByteBuf, CarPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, CarPayload::entityId,
        PacketCodecs.STRING, CarPayload::action,
        PacketCodecs.STRING, CarPayload::data,
        CarPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
