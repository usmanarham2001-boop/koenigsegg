package com.example.cars;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Multi-part hypercar model. Every part group is tinted separately from the CarConfig.
 * Model space: front of the car is -Z, up is -Y, ground is y = 24, left side is +X.
 * All numbers below are in model pixels (16 px = 1 block).
 */
public class CarModel {
    public static final Identifier PAINT = Identifier.of(CarsMod.ID, "textures/entity/paint.png");
    public static final Identifier CARBON = Identifier.of(CarsMod.ID, "textures/entity/carbon.png");
    public static final Identifier LEATHER = Identifier.of(CarsMod.ID, "textures/entity/leather.png");
    public static final Identifier PLAIN = Identifier.of(CarsMod.ID, "textures/entity/plain.png");

    // per variant: nose z, tail z, wheel z, glass front, glass rear, roof front, roof rear, roof top, hood top
    private static final float[][] DIM = {
        {-38, 38, 22, -10, 16, -2, 12, 16.0f, 9.3f},   // Jesko
        {-37, 38, 22, -9, 20, -1, 16, 16.5f, 9.3f},    // Regera
        {-38, 40, 24, -10, 30, -2, 26, 17.0f, 10.0f},  // Gemera
        {-37, 37, 22, -11, 15, -3, 11, 16.0f, 9.0f},   // CC850
    };
    private static final float[][] EXHAUST = {
        {-3.5f, 0f, 3.5f}, {-3f, 3f}, {}, {-7f, -2.5f, 2.5f, 7f}
    };

    private static final int TIRE = 0xFF151515;
    private static final int TAIL = 0xFFFF2020;
    private static final int METAL = 0xFF303030;

    // ------------------------------------------------------------------ geometry helpers
    /** Box from heights hb..ht (px above ground) and z range zf..zr. */
    private static void cube(ModelPartBuilder b, float x1, float x2, float hb, float ht, float zf, float zr) {
        b.uv(0, 0).cuboid(x1, 24f - ht, zf, x2 - x1, ht - hb, zr - zf);
    }

    /** Same box on both sides of the car (xa..xb on the left, mirrored on the right). */
    private static void sym(ModelPartBuilder b, float xa, float xb, float hb, float ht, float zf, float zr) {
        cube(b, xa, xb, hb, ht, zf, zr);
        cube(b, -xb, -xa, hb, ht, zf, zr);
    }

    private static void stripeBoxes(ModelPartBuilder b, float xa, float xb, float nose, float tail,
                                    float gf, float gr, float rf, float rr, float rt, float ht, boolean both) {
        if (both) {
            sym(b, xa, xb, ht, ht + 0.3f, nose + 7, gf - 4);
            sym(b, xa, xb, rt, rt + 0.3f, rf, rr);
            sym(b, xa, xb, 11f, 11.3f, gr - 1, tail - 12);
        } else {
            cube(b, -xb, xb, ht, ht + 0.3f, nose + 7, gf - 4);
            cube(b, -xb, xb, rt, rt + 0.3f, rf, rr);
            cube(b, -xb, xb, 11f, 11.3f, gr - 1, tail - 12);
        }
    }

    public static TexturedModelData getTexturedModelData(int v) {
        float[] d = DIM[v];
        float nose = d[0], tail = d[1], wz = d[2], gf = d[3], gr = d[4];
        float rf = d[5], rr = d[6], rt = d[7], ht = d[8];
        float seatZ = CarsMod.SEAT_Z[v];

        ModelData md = new ModelData();
        ModelPartData root = md.getRoot();

        // ---------------- body (paint)
        ModelPartBuilder body = ModelPartBuilder.create();
        cube(body, -14.5f, 14.5f, 3.5f, 8f, nose + 5, gf + 1);      // front tub
        cube(body, -14.5f, 14.5f, 3.5f, 8f, gr - 1, tail - 3);      // rear tub
        cube(body, -12.5f, 12.5f, 3.5f, 7f, nose, nose + 5);        // nose
        cube(body, -12.5f, 12.5f, 8f, ht, nose + 7, gf - 4);        // hood
        cube(body, -13.5f, 13.5f, 8f, 11f, gf - 4, gf + 1);         // cowl
        cube(body, -14f, 14f, 8f, 11f, gr - 1, tail - 12);          // rear deck
        cube(body, -12.5f, 12.5f, 4f, 9.5f, tail - 12, tail);       // tail
        cube(body, -10f, 10f, 15.5f, rt, rf, rr);                   // roof
        for (float z : new float[]{-wz, wz}) {                       // fender arches
            float hc = z > 0 ? 13.2f : 12.6f;
            cube(body, 10f, 17.2f, 11.1f, hc, z - 6.5f, z + 6.5f);
            cube(body, -17.2f, -10f, 11.1f, hc, z - 6.5f, z + 6.5f);
            cube(body, 9.5f, 11.5f, 8f, 11.1f, z - 6.5f, z + 6.5f);
            cube(body, -11.5f, -9.5f, 8f, 11.1f, z - 6.5f, z + 6.5f);
        }
        sym(body, 14.6f, 17.2f, 11.2f, 12.6f, gf, gf + 3);          // mirrors
        root.addChild("body", body, ModelTransform.NONE);

        // ---------------- accents
        ModelPartBuilder acc = ModelPartBuilder.create();
        sym(acc, 14.5f, 15.3f, 3.5f, 4.9f, gf + 1, gr - 1);         // side skirts
        cube(acc, -12.5f, 12.5f, 3.5f, 4.3f, nose - 0.5f, nose + 1.5f);
        cube(acc, -12.5f, 12.5f, 3.5f, 4.3f, tail - 1.5f, tail + 0.5f);
        root.addChild("accent", acc, ModelTransform.NONE);

        // ---------------- aero / carbon
        ModelPartBuilder aero = ModelPartBuilder.create();
        cube(aero, -13.5f, 13.5f, 2.5f, 3.5f, nose - 2, nose + 8);  // splitter
        cube(aero, -11f, 11f, 2.5f, 4.5f, tail - 8, tail + 1);      // diffuser
        sym(aero, 14f, 14.7f, 8.5f, 10.5f, gr + 1, gr + 9);         // side intakes
        if (v == 0 || v == 3) cube(aero, -3f, 3f, rt, rt + 1.3f, rf + 1, rf + 6); // roof scoop
        root.addChild("aero", aero, ModelTransform.NONE);

        ModelPartBuilder ex = ModelPartBuilder.create();
        for (float x : EXHAUST[v]) cube(ex, x - 1.4f, x + 1.4f, 5f, 7.8f, tail - 1, tail + 1.6f);
        root.addChild("exhaust", ex, ModelTransform.NONE);

        // ---------------- lights
        ModelPartBuilder lights = ModelPartBuilder.create();
        sym(lights, 8.5f, 12.3f, 5f, 6.4f, nose - 0.2f, nose + 1.2f);
        root.addChild("lights", lights, ModelTransform.NONE);
        ModelPartBuilder tailL = ModelPartBuilder.create();
        sym(tailL, 4f, 12.2f, 6f, 7.2f, tail - 0.3f, tail + 0.3f);
        root.addChild("taillights", tailL, ModelTransform.NONE);

        // ---------------- glass
        ModelPartBuilder glass = ModelPartBuilder.create();
        cube(glass, -12f, 12f, 11f, 15.5f, gf + 1, gr - 1);
        cube(glass, -12f, 12f, 11f, 13.2f, gf - 5, gf + 1);
        root.addChild("glass", glass, ModelTransform.NONE);

        // ---------------- interior
        ModelPartBuilder seats = ModelPartBuilder.create();
        float[] rows = (v == 2) ? new float[]{seatZ, CarsMod.REAR_SEAT_Z} : new float[]{seatZ};
        for (float sz : rows) {
            for (float cx : new float[]{5.5f, -5.5f}) {
                cube(seats, cx - 3, cx + 3, 4.5f, 6.5f, sz - 3, sz + 3);         // base
                cube(seats, cx - 3, cx + 3, 6.5f, 11.5f, sz + 3, sz + 5);        // back
                cube(seats, cx - 1.6f, cx + 1.6f, 11.5f, 13.2f, sz + 3, sz + 4.6f); // headrest
            }
        }
        root.addChild("seats", seats, ModelTransform.NONE);

        ModelPartBuilder steer = ModelPartBuilder.create();
        float stz = seatZ - 8f;
        cube(steer, 3.1f, 7.9f, 11f, 11.8f, stz, stz + 0.8f);
        cube(steer, 3.1f, 7.9f, 8.2f, 9f, stz, stz + 0.8f);
        cube(steer, 3.1f, 3.9f, 9f, 11f, stz, stz + 0.8f);
        cube(steer, 7.1f, 7.9f, 9f, 11f, stz, stz + 0.8f);
        cube(steer, 4.6f, 6.4f, 9.6f, 10.2f, stz, stz + 0.8f);
        root.addChild("steer", steer, ModelTransform.NONE);

        ModelPartBuilder dash = ModelPartBuilder.create();
        float tunnelEnd = ((v == 2) ? CarsMod.REAR_SEAT_Z : seatZ) + 3f;
        cube(dash, -12.5f, 12.5f, 3.5f, 4.5f, gf + 1, gr - 1);       // floor
        cube(dash, -12.5f, 12.5f, 4.5f, 10f, gf + 1, gf + 4.5f);     // dashboard
        cube(dash, -1.5f, 1.5f, 4.5f, 8f, gf + 4.5f, tunnelEnd);     // center tunnel
        root.addChild("dash", dash, ModelTransform.NONE);

        // ---------------- butterfly doors (hinged at the top front)
        for (int s : new int[]{1, -1}) {
            ModelPartBuilder db = ModelPartBuilder.create();
            db.uv(0, 0).cuboid(-1f, 0f, 0f, 2f, 6.5f, (gr - 1) - (gf + 1));
            root.addChild(s > 0 ? "doorL" : "doorR", db, ModelTransform.pivot(s * 13.5f, 13f, gf + 1));
        }

        // ---------------- stripes
        ModelPartBuilder st1 = ModelPartBuilder.create();
        stripeBoxes(st1, 0f, 2f, nose, tail, gf, gr, rf, rr, rt, ht, false);
        root.addChild("stripe1", st1, ModelTransform.NONE);
        ModelPartBuilder st2 = ModelPartBuilder.create();
        stripeBoxes(st2, 2.6f, 3.8f, nose, tail, gf, gr, rf, rr, rt, ht, true);
        root.addChild("stripe2", st2, ModelTransform.NONE);
        ModelPartBuilder st3 = ModelPartBuilder.create();
        stripeBoxes(st3, 0f, 1.4f, nose, tail, gf, gr, rf, rr, rt, ht, false);
        stripeBoxes(st3, 3.2f, 4.2f, nose, tail, gf, gr, rf, rr, rt, ht, true);
        root.addChild("stripe3", st3, ModelTransform.NONE);

        // ---------------- spoilers
        ModelPartBuilder s1 = ModelPartBuilder.create();
        cube(s1, -12.5f, 12.5f, 9.5f, 10.8f, tail - 4, tail + 0.5f);
        root.addChild("spoiler1", s1, ModelTransform.NONE);
        ModelPartBuilder s2 = ModelPartBuilder.create();
        sym(s2, 7.5f, 9f, 9.5f, 17f, tail - 9, tail - 7.5f);
        cube(s2, -15.5f, 15.5f, 17f, 18.6f, tail - 13, tail - 4);
        sym(s2, 15.5f, 16.4f, 15f, 20f, tail - 14, tail - 3);
        root.addChild("spoiler2", s2, ModelTransform.NONE);
        ModelPartBuilder s3 = ModelPartBuilder.create();
        sym(s3, 5.5f, 7f, 9.5f, 14f, tail - 8, tail - 6.5f);
        cube(s3, -14f, 14f, 14f, 15f, tail - 11, tail - 4);
        cube(s3, -14f, 14f, 15f, 15.8f, tail - 4, tail - 3.2f);
        root.addChild("spoiler3", s3, ModelTransform.NONE);

        // ---------------- wheels: 0 FL, 1 FR, 2 RL, 3 RR
        float[] wx = {14f, -14f, 14f, -14f};
        float[] wzs = {-wz, -wz, wz, wz};
        for (int i = 0; i < 4; i++) {
            boolean left = wx[i] > 0;
            ModelTransform pv = ModelTransform.pivot(wx[i], 18.5f, wzs[i]);

            ModelPartBuilder tire = ModelPartBuilder.create();
            tire.uv(0, 0).cuboid(-2.5f, -5.5f, -4f, 5f, 11f, 8f)
                .cuboid(-2.5f, -4f, -5.5f, 5f, 8f, 11f);
            root.addChild("tire" + i, tire, pv);

            float xa = left ? 2.5f : -3.3f;
            ModelPartBuilder disc = ModelPartBuilder.create();
            disc.uv(0, 0).cuboid(xa, -3.2f, -2.2f, 0.8f, 6.4f, 4.4f)
                .cuboid(xa, -2.2f, -3.2f, 0.8f, 4.4f, 6.4f);
            root.addChild("rim0_" + i, disc, pv);

            ModelPartBuilder spokes = ModelPartBuilder.create();
            spokes.uv(0, 0).cuboid(xa, -4f, -0.5f, 0.8f, 8f, 1f)
                .cuboid(xa, -0.5f, -4f, 0.8f, 1f, 8f)
                .cuboid(xa, -1.3f, -1.3f, 0.8f, 2.6f, 2.6f);
            ModelPartData spokeData = root.addChild("rim1_" + i, spokes, pv);
            ModelPartBuilder diag = ModelPartBuilder.create();
            diag.uv(0, 0).cuboid(xa, -4f, -0.5f, 0.8f, 8f, 1f)
                .cuboid(xa, -0.5f, -4f, 0.8f, 1f, 8f);
            spokeData.addChild("diag", diag, ModelTransform.rotation(0.7853982f, 0f, 0f));

            ModelPartBuilder cap = ModelPartBuilder.create();
            cap.uv(0, 0).cuboid(left ? 3.3f : -4.0f, -1.7f, -1.7f, 0.7f, 3.4f, 3.4f);
            root.addChild("cap" + i, cap, pv);
        }

        return TexturedModelData.of(md, 128, 128);
    }

    // ------------------------------------------------------------------ runtime
    private final ModelPart body, accent, aero, exhaust, lights, taillights, glass, seats, steer, dash, doorL, doorR;
    private final ModelPart[] spoilers = new ModelPart[4];
    private final ModelPart[] stripes = new ModelPart[4];
    private final ModelPart[] tires = new ModelPart[4];
    private final ModelPart[] rim0 = new ModelPart[4];
    private final ModelPart[] rim1 = new ModelPart[4];
    private final ModelPart[] caps = new ModelPart[4];

    public CarModel(ModelPart r) {
        body = r.getChild("body");
        accent = r.getChild("accent");
        aero = r.getChild("aero");
        exhaust = r.getChild("exhaust");
        lights = r.getChild("lights");
        taillights = r.getChild("taillights");
        glass = r.getChild("glass");
        seats = r.getChild("seats");
        steer = r.getChild("steer");
        dash = r.getChild("dash");
        doorL = r.getChild("doorL");
        doorR = r.getChild("doorR");
        for (int i = 1; i < 4; i++) {
            spoilers[i] = r.getChild("spoiler" + i);
            stripes[i] = r.getChild("stripe" + i);
        }
        for (int i = 0; i < 4; i++) {
            tires[i] = r.getChild("tire" + i);
            rim0[i] = r.getChild("rim0_" + i);
            rim1[i] = r.getChild("rim1_" + i);
            caps[i] = r.getChild("cap" + i);
        }
    }

    private static int argb(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public void render(CarEntity car, CarConfig cfg, float tickDelta, MatrixStack m,
                       VertexConsumerProvider vcp, int light) {
        int ov = OverlayTexture.DEFAULT_UV;

        // animation
        float open = MathHelper.lerp(tickDelta, car.prevDoorOpen, car.doorOpen);
        float spin = MathHelper.lerp(tickDelta, car.prevWheelRot, car.wheelRot);
        doorL.pitch = open * 1.15f;
        doorL.roll = -open * 0.5f;
        doorR.pitch = open * 1.15f;
        doorR.roll = open * 0.5f;
        for (int i = 0; i < 4; i++) {
            tires[i].pitch = spin;
            rim0[i].pitch = spin;
            rim1[i].pitch = spin;
            caps[i].pitch = spin;
        }

        int bodyC = argb(cfg.colors[CarConfig.BODY]);
        int accentC = argb(cfg.colors[CarConfig.ACCENT]);
        int aeroC = argb(cfg.colors[CarConfig.AERO]);

        // painted parts
        VertexConsumer paint = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(PAINT));
        body.render(m, paint, light, ov, bodyC);
        doorL.render(m, paint, light, ov, bodyC);
        doorR.render(m, paint, light, ov, bodyC);
        accent.render(m, paint, light, ov, accentC);
        if (cfg.stripe > 0) stripes[cfg.stripe].render(m, paint, light, ov, accentC);

        // carbon / aero parts
        VertexConsumer carbon = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(CARBON));
        aero.render(m, carbon, light, ov, aeroC);
        if (cfg.spoiler > 0) spoilers[cfg.spoiler].render(m, carbon, light, ov, aeroC);
        exhaust.render(m, carbon, light, ov, METAL);

        // interior
        VertexConsumer leather = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(LEATHER));
        seats.render(m, leather, light, ov, argb(cfg.colors[CarConfig.SEATS]));
        steer.render(m, leather, light, ov, argb(cfg.colors[CarConfig.STEER]));
        dash.render(m, leather, light, ov, argb(cfg.colors[CarConfig.DASH]));

        // wheels
        VertexConsumer plain = vcp.getBuffer(RenderLayer.getEntityCutoutNoCull(PLAIN));
        int rimC = argb(cfg.colors[CarConfig.RIMS]);
        for (int i = 0; i < 4; i++) {
            tires[i].render(m, plain, light, ov, TIRE);
            if (cfg.wheelStyle == 1) {
                rim1[i].render(m, plain, light, ov, rimC);
            } else {
                rim0[i].render(m, plain, light, ov, rimC);
                if (cfg.wheelStyle == 2) caps[i].render(m, plain, light, ov, accentC);
            }
        }

        // glowing lights
        int full = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        lights.render(m, plain, full, ov, argb(cfg.colors[CarConfig.LIGHTS]));
        taillights.render(m, plain, full, ov, TAIL);

        // translucent glass last
        VertexConsumer glassVc = vcp.getBuffer(RenderLayer.getEntityTranslucent(PLAIN));
        int glassC = (0x99 << 24) | (cfg.colors[CarConfig.GLASS] & 0xFFFFFF);
        glass.render(m, glassVc, light, ov, glassC);
    }
}
