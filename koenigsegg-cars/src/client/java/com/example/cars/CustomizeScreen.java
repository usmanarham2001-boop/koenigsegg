package com.example.cars;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Paint-shop screen: hex color for every part + part options. Changes apply instantly. */
public class CustomizeScreen extends Screen {
    private static final int[] PALETTE = {
        0xF2F2F2, 0xC4C7CC, 0x8A8D91, 0x2A2A2A, 0x111111,
        0xC8102E, 0xD0201E, 0xFF6A00, 0xE3B100, 0x9ACD32,
        0x148C46, 0x00A3A3, 0x1E3CA0, 0x5B2A86, 0xD81B8C,
        0x6B4423, 0xC9A66B, 0xFFFFFF, 0x000000, 0x777777
    };
    private static final int ROW = 20;

    private final CarEntity car;
    private CarConfig cfg;
    private final int[] palIdx = new int[CarConfig.COLOR_COUNT];
    private final int[] colX = new int[CarConfig.COLOR_COUNT];
    private final int[] colY = new int[CarConfig.COLOR_COUNT];

    public CustomizeScreen(CarEntity car) {
        super(Text.literal("Customize your car"));
        this.car = car;
        this.cfg = car.getConfig().copy();
    }

    private void send() {
        ClientPlayNetworking.send(new CarPayload(car.getId(), "config", cfg.encode()));
    }

    private static String hex(int c) {
        return String.format("%06X", c & 0xFFFFFF);
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 185;
        int right = this.width / 2 + 5;
        int y0 = 30;

        for (int i = 0; i < CarConfig.COLOR_COUNT; i++) {
            final int idx = i;
            int cx = i < 5 ? left : right;
            int y = y0 + (i < 5 ? i : i - 5) * ROW;
            colX[i] = cx;
            colY[i] = y;

            TextFieldWidget field = new TextFieldWidget(this.textRenderer, cx + 90, y, 46, 18, Text.empty());
            field.setMaxLength(6);
            field.setTextPredicate(s -> s.matches("[0-9a-fA-F]{0,6}"));
            field.setText(hex(cfg.colors[i]));
            field.setChangedListener(s -> {
                if (s.length() == 6) {
                    cfg.colors[idx] = Integer.parseInt(s, 16);
                    send();
                }
            });
            this.addDrawableChild(field);

            this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
                palIdx[idx] = (palIdx[idx] + 1) % PALETTE.length;
                field.setText(hex(PALETTE[palIdx[idx]]));
            }).dimensions(cx + 160, y, 18, 18).build());
        }

        // part options (right column, below the colors)
        int oy = y0 + 4 * ROW;
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Spoiler: " + CarConfig.SPOILER_NAMES[cfg.spoiler]), b -> {
                cfg.spoiler = (cfg.spoiler + 1) % CarConfig.SPOILERS;
                b.setMessage(Text.literal("Spoiler: " + CarConfig.SPOILER_NAMES[cfg.spoiler]));
                send();
            }).dimensions(right, oy, 178, 18).build());
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Wheels: " + CarConfig.WHEEL_NAMES[cfg.wheelStyle]), b -> {
                cfg.wheelStyle = (cfg.wheelStyle + 1) % CarConfig.WHEELS;
                b.setMessage(Text.literal("Wheels: " + CarConfig.WHEEL_NAMES[cfg.wheelStyle]));
                send();
            }).dimensions(right, oy + ROW, 178, 18).build());
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Stripes: " + CarConfig.STRIPE_NAMES[cfg.stripe]), b -> {
                cfg.stripe = (cfg.stripe + 1) % CarConfig.STRIPES;
                b.setMessage(Text.literal("Stripes: " + CarConfig.STRIPE_NAMES[cfg.stripe]));
                send();
            }).dimensions(right, oy + 2 * ROW, 178, 18).build());

        // reset (left column, below the colors)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset to factory"), b -> {
            cfg = CarConfig.defaults(car.variant);
            send();
            this.clearAndInit();
        }).dimensions(left, y0 + 5 * ROW, 178, 18).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> this.close())
            .dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        for (int i = 0; i < CarConfig.COLOR_COUNT; i++) {
            ctx.drawTextWithShadow(this.textRenderer, CarConfig.COLOR_NAMES[i], colX[i], colY[i] + 5, 0xFFFFFF);
            ctx.fill(colX[i] + 139, colY[i], colX[i] + 157, colY[i] + 18, 0xFFFFFFFF);
            ctx.fill(colX[i] + 140, colY[i] + 1, colX[i] + 156, colY[i] + 17, 0xFF000000 | cfg.colors[i]);
        }
        ctx.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Type a hex color (e.g. FF0000) or press > for presets"),
            this.width / 2, this.height - 42, 0xAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
