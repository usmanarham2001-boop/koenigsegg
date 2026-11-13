package com.example.cars;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class CarRenderer extends EntityRenderer<CarEntity> {
    private final CarModel model;

    public CarRenderer(EntityRendererFactory.Context ctx, CarModel model) {
        super(ctx);
        this.model = model;
        this.shadowRadius = 1.1f;
    }

    @Override
    public Identifier getTexture(CarEntity entity) {
        return CarModel.PAINT;
    }

    @Override
    public void render(CarEntity car, float yaw, float tickDelta, MatrixStack m,
                       VertexConsumerProvider vcp, int light) {
        m.push();
        m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - yaw));
        m.scale(-1.0f, -1.0f, 1.0f);
        m.translate(0.0f, -1.501f, 0.0f);
        model.render(car, car.getConfig(), tickDelta, m, vcp, light);
        m.pop();
        super.render(car, yaw, tickDelta, m, vcp, light);
    }
}
