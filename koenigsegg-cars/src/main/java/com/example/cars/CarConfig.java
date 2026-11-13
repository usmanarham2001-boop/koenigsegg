package com.example.cars;

/** Everything about a car that the player can change. Colors are 0xRRGGBB. */
public final class CarConfig {
    public static final int BODY = 0, ACCENT = 1, AERO = 2, GLASS = 3, LIGHTS = 4,
        RIMS = 5, SEATS = 6, STEER = 7, DASH = 8;
    public static final int COLOR_COUNT = 9;
    public static final String[] COLOR_NAMES = {
        "Body", "Accents", "Aero / carbon", "Glass", "Headlights",
        "Rims", "Seats", "Steering wheel", "Dashboard"
    };
    public static final String[] SPOILER_NAMES = {"None", "Lip", "Big wing", "GT wing"};
    public static final String[] WHEEL_NAMES = {"Disc", "Spoked", "Disc + cap"};
    public static final String[] STRIPE_NAMES = {"None", "Center", "Dual", "Triple"};
    public static final int SPOILERS = 4, WHEELS = 3, STRIPES = 4;

    // body, accent, aero, glass, lights, rims, seats, steer, dash, spoiler, wheel, stripe
    private static final int[][] DEFAULTS = {
        // Jesko: white with lime accents
        {0xF2F2F2, 0x9ACD32, 0x555555, 0x1B2A33, 0xF5F8FF, 0x2A2A2A, 0x1C1C1C, 0x1C1C1C, 0x303030, 2, 2, 0},
        // Regera: red
        {0xC8102E, 0x111111, 0x555555, 0x1B2A33, 0xF5F8FF, 0x3A3A3A, 0x222222, 0x111111, 0x2A2A2A, 3, 0, 0},
        // Gemera: grey with yellow interior
        {0x8A8D91, 0xE3B100, 0x555555, 0x22303A, 0xF5F8FF, 0x2A2A2A, 0xE3B100, 0xE3B100, 0x333333, 1, 2, 0},
        // CC850: silver with red details
        {0xC4C7CC, 0xD0201E, 0x555555, 0x1B2A33, 0xF5F8FF, 0xB8B8B8, 0xD0201E, 0x1C1C1C, 0x2A2A2A, 1, 2, 0},
    };

    public final int[] colors = new int[COLOR_COUNT];
    public int spoiler, wheelStyle, stripe;

    public static CarConfig defaults(int variant) {
        int[] d = DEFAULTS[Math.floorMod(variant, DEFAULTS.length)];
        CarConfig c = new CarConfig();
        for (int i = 0; i < COLOR_COUNT; i++) c.colors[i] = d[i];
        c.spoiler = d[COLOR_COUNT];
        c.wheelStyle = d[COLOR_COUNT + 1];
        c.stripe = d[COLOR_COUNT + 2];
        return c;
    }

    public CarConfig copy() {
        CarConfig c = new CarConfig();
        System.arraycopy(colors, 0, c.colors, 0, COLOR_COUNT);
        c.spoiler = spoiler;
        c.wheelStyle = wheelStyle;
        c.stripe = stripe;
        return c;
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        for (int c : colors) sb.append(c & 0xFFFFFF).append(',');
        sb.append(spoiler).append(',').append(wheelStyle).append(',').append(stripe);
        return sb.toString();
    }

    public static CarConfig decode(String s, int variant) {
        CarConfig c = defaults(variant);
        if (s == null || s.isEmpty()) return c;
        String[] p = s.split(",");
        if (p.length != COLOR_COUNT + 3) return c;
        try {
            for (int i = 0; i < COLOR_COUNT; i++) c.colors[i] = Integer.parseInt(p[i].trim()) & 0xFFFFFF;
            c.spoiler = Math.floorMod(Integer.parseInt(p[COLOR_COUNT].trim()), SPOILERS);
            c.wheelStyle = Math.floorMod(Integer.parseInt(p[COLOR_COUNT + 1].trim()), WHEELS);
            c.stripe = Math.floorMod(Integer.parseInt(p[COLOR_COUNT + 2].trim()), STRIPES);
        } catch (NumberFormatException e) {
            return defaults(variant);
        }
        return c;
    }
}
