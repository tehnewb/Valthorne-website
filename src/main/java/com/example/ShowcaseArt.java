package com.example;

import valthorne.ui.NanoUtility;
import valthorne.ui.nodes.nano.NanoPanel;

import java.util.function.DoubleSupplier;

/**
 * Lightweight animated, engine-drawn previews; no borrowed project screenshots.
 */
final class ShowcaseArt extends NanoPanel {
    private final int kind;
    private final DoubleSupplier clock;
    private final Runnable open;
    private float hover, previous;
    private boolean pressed;

    ShowcaseArt(int kind, DoubleSupplier clock, Runnable open) {
        this.kind = kind;
        this.clock = clock;
        this.open = open;
        backgroundColor(Main.CLEAR).hoverBackgroundColor(Main.CLEAR).focusedBackgroundColor(Main.CLEAR).pressedBackgroundColor(Main.CLEAR);
        cornerRadius(12).borderWidth(0);
        setClickable(true);
        setFocusable(true);
    }

    @Override
    public void onMousePress(valthorne.event.events.MousePressEvent event) {pressed = event.getButton() == valthorne.Mouse.LEFT;}

    @Override
    public void onMouseRelease(valthorne.event.events.MouseReleaseEvent event) {
        boolean activate = pressed && isActivationRelease(event);
        pressed = false;
        if (activate) open.run();
    }

    @Override
    public void onPointerCancel() {
        pressed = false;
        super.onPointerCancel();
    }

    @Override
    public void onKeyPress(valthorne.event.events.KeyPressEvent event) {
        if (event.getKey() == valthorne.Keyboard.ENTER || event.getKey() == valthorne.Keyboard.SPACE) open.run();
    }

    float depthHover() {return hover;}

    @Override
    public void draw(long vg) {
        super.draw(vg);
        float x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight(), t = (float) clock.getAsDouble();
        float dt = Math.max(0, t - previous);
        previous = t;
        var pointer = screenToLayout(valthorne.Mouse.getX(), valthorne.Mouse.getY());
        boolean inside = pointer.x >= x && pointer.x <= x + w && pointer.y >= y && pointer.y <= y + h;
        hover += ((inside || isFocused() ? 1 : 0) - hover) * (1 - (float) Math.exp(-dt * 12));
        y -= hover * 5;
        if (kind == 0) {
            for (int row = 0; row < 8; row++)
                for (int col = 0; col < 8; col++) {
                    float xx = x + w * .5f + (col - row) * w * .042f, yy = y + h * .48f + (col + row - 7) * h * .027f;
                    int color = (row + col) % 2 == 0 ? 0xFF615570 : 0xFF26222F;
                    NanoUtility.strokeLine(vg, xx, yy, xx + w * .042f, yy + h * .027f, color, 2);
                    NanoUtility.strokeLine(vg, xx + w * .042f, yy + h * .027f, xx, yy + h * .054f, color, 2);
                    NanoUtility.strokeLine(vg, xx, yy + h * .054f, xx - w * .042f, yy + h * .027f, color, 2);
                    NanoUtility.strokeLine(vg, xx - w * .042f, yy + h * .027f, xx, yy, color, 2);
                    if (row < 2 || row > 5) {
                        float lift = h * (.045f + .006f * (float) Math.sin(t + col));
                        NanoUtility.strokeLine(vg, xx, yy, xx, yy - lift, 0xFFAA9BBC, 5);
                        NanoUtility.strokeCircle(vg, xx, yy - lift, 4, 0xFFD2C4E7, 3);
                    }
                }
        } else if (kind == 1) {
            for (int i = 0; i < 36; i++) {
                float u = i / 35f, yy = y + h * .5f + (float) Math.sin(u * 6 + t * .5f) * h * .19f;
                NanoUtility.strokeLine(vg, x + w * .1f + u * w * .8f, yy, x + w * .1f + u * w * .8f, y + h * .8f, 0xFF4D286A, 3);
                NanoUtility.strokeCircle(vg, x + w * .1f + u * w * .8f, yy, 3, 0xFFCAA8FF, 2);
            }
        } else if (kind == 2) {
            for (int i = 0; i < 24; i++) {
                double a = i * 2.39996 + t * .04;
                float r = (float) Math.sqrt(i / 24f) * Math.min(w, h) * .37f;
                float px = x + w * .5f + (float) Math.cos(a) * r, py = y + h * .5f + (float) Math.sin(a) * r;
                NanoUtility.strokeLine(vg, x + w * .5f, y + h * .5f, px, py, 0xFF30273F, 1);
                NanoUtility.strokeCircle(vg, px, py, 3 + i % 3, 0xFFB9A4DE, 1.5f);
            }
        } else if (kind == 4) {
            for (int i = 0; i < 6; i++) {
                float phase = (t * .22f + i / 6f) % 1;
                NanoUtility.strokeCircle(vg, x + w * .5f, y + h * .5f, 12 + phase * h * .35f, 0xFF7D62AA, 1.5f);
            }
            NanoUtility.strokeCircle(vg, x + w * .5f, y + h * .5f, 7, 0xFFE0CEFF, 3);
        } else if (kind == 5) {
            NanoUtility.strokeLine(vg, x + w * .15f, y + h * .78f, x + w * .85f, y + h * .78f, 0xFF69557F, 2);
            for (int i = 0; i < 5; i++) {
                float xx = x + w * (.25f + i * .125f), yy = y + h * (.68f - .3f * (float) Math.abs(Math.sin(t * .9f + i * .6f)));
                NanoUtility.strokeCircle(vg, xx, yy, h * .055f, 0xFFB9A4DE, 2);
                NanoUtility.strokeLine(vg, xx, y + h * .77f, xx, y + h * .78f, 0xFFB9A4DE, 5);
            }
        } else if (kind == 6) {
            for (int i = 0; i < 5; i++) {
                float xx = x + w * (.18f + i * .16f), yy = y + h * .5f;
                int tint = (int) (t * 4) % 5 == i ? 0xFFE0CEFF : 0xFF584569;
                NanoUtility.strokeCircle(vg, xx, yy - h * .12f, 7, tint, 2);
                NanoUtility.strokeLine(vg, xx, yy - h * .1f, xx, yy + h * .06f, tint, 2);
                float stride = (float) Math.sin(i * 1.5) * w * .04f;
                NanoUtility.strokeLine(vg, xx, yy + h * .06f, xx - stride, yy + h * .19f, tint, 2);
                NanoUtility.strokeLine(vg, xx, yy + h * .06f, xx + stride, yy + h * .19f, tint, 2);
            }
        } else if (kind == 7) {
            for (int i = 0; i < 4; i++) {
                float ww = w * (.6f - i * .12f), hh = h * (.68f - i * .12f), xx = x + (w - ww) / 2, yy = y + (h - hh) / 2;
                NanoUtility.strokeLine(vg, xx, yy, xx + ww, yy, 0xFFAA8ED3, 1.5f);
                NanoUtility.strokeLine(vg, xx + ww, yy, xx + ww, yy + hh, 0xFFAA8ED3, 1.5f);
                NanoUtility.strokeLine(vg, xx + ww, yy + hh, xx, yy + hh, 0xFFAA8ED3, 1.5f);
                NanoUtility.strokeLine(vg, xx, yy + hh, xx, yy, 0xFFAA8ED3, 1.5f);
            }
        } else if (kind == 8) {
            // Assets: a compact stack of files flowing into a shared content store.
            for (int i = 0; i < 4; i++) {
                float yy = y + h * (.22f + i * .14f);
                NanoUtility.strokeLine(vg, x + w * .16f, yy, x + w * .38f, yy, 0xFFB9A4DE, 2);
                NanoUtility.strokeLine(vg, x + w * .16f, yy, x + w * .16f, yy + h * .09f, 0xFF69557F, 2);
                NanoUtility.strokeLine(vg, x + w * .16f, yy + h * .09f, x + w * .38f, yy + h * .09f, 0xFF69557F, 2);
                NanoUtility.strokeLine(vg, x + w * .38f, yy, x + w * .38f, yy + h * .09f, 0xFF69557F, 2);
                NanoUtility.strokeLine(vg, x + w * .4f, yy + h * .045f, x + w * .59f, y + h * .5f, 0xFF393242, 1);
            }
            for (int i = 0; i < 3; i++)
                NanoUtility.strokeCircle(vg, x + w * .69f, y + h * (.36f + i * .14f), w * .105f, i == 1 ? 0xFFCAA8FF : 0xFF7D62AA, 2);
        } else if (kind == 9) {
            // Input: keyboard keys, mouse motion and a routed event path.
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 5; col++) {
                    float xx = x + w * (.13f + col * .075f), yy = y + h * (.34f + row * .14f);
                    NanoUtility.strokeCircle(vg, xx, yy, 5, (row + col) % 4 == (int) (t * 3) % 4 ? 0xFFE0CEFF : 0xFF69557F, 1.5f);
                }
            float mx = x + w * (.65f + .06f * (float) Math.sin(t));
            float my = y + h * (.5f + .08f * (float) Math.cos(t * .8f));
            NanoUtility.strokeCircle(vg, mx, my, h * .14f, 0xFFAA8ED3, 2);
            NanoUtility.strokeLine(vg, mx, my - h * .14f, mx, my, 0xFFAA8ED3, 2);
            NanoUtility.strokeLine(vg, x + w * .5f, y + h * .5f, mx - h * .15f, my, 0xFF4D286A, 2);
        } else if (kind == 10) {
            // Shaders: a warped GPU mesh with a moving highlight.
            for (int row = 0; row < 7; row++)
                for (int col = 0; col < 10; col++) {
                    float u = col / 9f, v = row / 6f;
                    float xx = x + w * (.12f + u * .76f);
                    float yy = y + h * (.2f + v * .6f + .05f * (float) Math.sin(u * 7 + t + v * 3));
                    if (col < 9) NanoUtility.strokeLine(vg, xx, yy, x + w * (.12f + (col + 1) / 9f * .76f), y + h * (.2f + v * .6f + .05f * (float) Math.sin((col + 1) / 9f * 7 + t + v * 3)), 0xFF65458D, 1);
                    NanoUtility.strokeCircle(vg, xx, yy, 1.5f, Math.abs(u - (t * .12f % 1)) < .12f ? 0xFFE0CEFF : 0xFF8D6FBC, 1);
                }
        } else if (kind == 11) {
            // Particles: an animated emitter with varied trajectories.
            float cx = x + w * .5f, cy = y + h * .72f;
            for (int i = 0; i < 28; i++) {
                float phase = (t * (.18f + i % 4 * .025f) + i * .137f) % 1;
                float angle = -2.75f + (i % 9) / 8f * 2.35f;
                float r = phase * h * .68f;
                float px = cx + (float) Math.cos(angle) * r, py = cy + (float) Math.sin(angle) * r + phase * phase * h * .22f;
                NanoUtility.strokeCircle(vg, px, py, 1.5f + (i % 3), i % 4 == 0 ? 0xFFE0CEFF : 0xFF9673EE, 1.5f);
            }
            NanoUtility.strokeCircle(vg, cx, cy, 7, 0xFFCAA8FF, 2);
        } else if (kind == 12) {
            // State and timing: linked states driven by a moving clock pulse.
            float[] px = {.18f, .42f, .68f, .82f}, py = {.5f, .28f, .64f, .37f};
            for (int i = 0; i < 3; i++) NanoUtility.strokeLine(vg, x + w * px[i], y + h * py[i], x + w * px[i + 1], y + h * py[i + 1], 0xFF4D286A, 2);
            for (int i = 0; i < 4; i++) NanoUtility.strokeCircle(vg, x + w * px[i], y + h * py[i], 10, i == (int) (t * .65f) % 4 ? 0xFFE0CEFF : 0xFF8D6FBC, 2);
            float sweep = (t * .18f % 1) * w * .64f;
            NanoUtility.strokeLine(vg, x + w * .18f, y + h * .82f, x + w * .82f, y + h * .82f, 0xFF393242, 2);
            NanoUtility.strokeCircle(vg, x + w * .18f + sweep, y + h * .82f, 4, 0xFFCAA8FF, 2);
        } else if (kind == 13) {
            // Data: a collection of live values and cache buckets.
            for (int i = 0; i < 8; i++) {
                float xx = x + w * (.16f + i * .095f), bar = h * (.18f + .28f * (.5f + .5f * (float) Math.sin(i * 1.4f + t * .45f)));
                NanoUtility.strokeLine(vg, xx, y + h * .78f, xx, y + h * .78f - bar, i % 3 == 0 ? 0xFFCAA8FF : 0xFF69557F, 7);
                NanoUtility.strokeCircle(vg, xx, y + h * .78f - bar, 4, 0xFFB9A4DE, 1.5f);
            }
            NanoUtility.strokeLine(vg, x + w * .12f, y + h * .78f, x + w * .88f, y + h * .78f, 0xFF393242, 2);
        } else if (kind == 14) {
            // Portability: the same scene framed by desktop, browser and mobile targets.
            float[] left = {.1f, .39f, .72f}, widths = {.25f, .27f, .16f}, heights = {.48f, .38f, .55f};
            for (int i = 0; i < 3; i++) {
                float xx = x + w * left[i], yy = y + h * (.5f - heights[i] / 2);
                NanoUtility.strokeLine(vg, xx, yy, xx + w * widths[i], yy, 0xFFAA8ED3, 2);
                NanoUtility.strokeLine(vg, xx + w * widths[i], yy, xx + w * widths[i], yy + h * heights[i], 0xFFAA8ED3, 2);
                NanoUtility.strokeLine(vg, xx + w * widths[i], yy + h * heights[i], xx, yy + h * heights[i], 0xFFAA8ED3, 2);
                NanoUtility.strokeLine(vg, xx, yy + h * heights[i], xx, yy, 0xFFAA8ED3, 2);
                NanoUtility.strokeCircle(vg, xx + w * widths[i] * .5f, yy + h * heights[i] * .5f, 6, 0xFF9673EE, 2);
            }
            NanoUtility.strokeLine(vg, x + w * .35f, y + h * .5f, x + w * .39f, y + h * .5f, 0xFF4D286A, 2);
            NanoUtility.strokeLine(vg, x + w * .66f, y + h * .5f, x + w * .72f, y + h * .5f, 0xFF4D286A, 2);
        } else if (kind == 15) {
            // Diagnostics: frame-time graph, budget line and active sample points.
            NanoUtility.strokeLine(vg, x + w * .12f, y + h * .72f, x + w * .88f, y + h * .72f, 0xFF393242, 1);
            NanoUtility.strokeLine(vg, x + w * .12f, y + h * .35f, x + w * .88f, y + h * .35f, 0xFF4D286A, 1);
            for (int i = 0; i < 34; i++) {
                float u1 = i / 34f, u2 = (i + 1) / 34f;
                float v1 = .56f + .13f * (float) Math.sin(i * .7f + t) + (i % 11 == 0 ? -.2f : 0);
                float v2 = .56f + .13f * (float) Math.sin((i + 1) * .7f + t) + ((i + 1) % 11 == 0 ? -.2f : 0);
                NanoUtility.strokeLine(vg, x + w * (.12f + u1 * .76f), y + h * v1, x + w * (.12f + u2 * .76f), y + h * v2, v1 < .36f ? 0xFFE0CEFF : 0xFF9673EE, 2);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                float xx = x + w * (.12f + i * .25f), yy = y + h * (.22f + i * .07f);
                NanoUtility.strokeLine(vg, xx, yy, xx + w * .27f, yy, 0xFF393242, 1);
                NanoUtility.strokeLine(vg, xx, yy, xx, yy + h * .47f, 0xFF393242, 1);
                NanoUtility.strokeLine(vg, xx, yy + h * .47f, xx + w * .27f, yy + h * .47f, 0xFF393242, 1);
                NanoUtility.strokeLine(vg, xx + w * .27f, yy, xx + w * .27f, yy + h * .47f, 0xFF393242, 1);
                for (int j = 0; j < 28; j++) {
                    float u = j / 27f;
                    NanoUtility.strokeLine(vg, xx + w * .025f + u * w * .22f, yy + h * .23f + (float) Math.sin(u * 15 + t + i) * h * .04f,
                            xx + w * .025f + (u + .015f) * w * .22f, yy + h * .23f + (float) Math.sin((u + .015f) * 15 + t + i) * h * .04f, 0xFF9673EE, 3);
                }
            }
        }
    }
}
