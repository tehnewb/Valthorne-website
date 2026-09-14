package com.example;

import java.util.function.DoubleSupplier;
import valthorne.ui.NanoUtility;
import valthorne.ui.nodes.nano.NanoContainer;

/** Concept-inspired linework reinterpreted in the website's monochrome/violet palette. */
final class Ornament extends NanoContainer {
    private final int kind;
    private final DoubleSupplier clock;
    Ornament(int kind, DoubleSupplier clock) { this.kind = kind; this.clock = clock; setClickable(false); }
    @Override public void draw(long vg) {
        float x = getAbsoluteX(), y = getAbsoluteY(), w = getWidth(), h = getHeight();
        float cx = x + w / 2, cy = y + h / 2;
        if (kind == 0) {
            line(vg, x, cy, cx - 20, cy, 0xFF435044); line(vg, cx + 20, cy, x + w, cy, 0xFF435044);
            diamond(vg, cx, cy, 7, 0xFFE0BD7D); diamond(vg, cx, cy, 3, 0xFF55C8F5); return;
        }
        if (kind < 4) {
            float ix = x + 28;
            diamond(vg, ix, cy, 25, 0xFF91794E); diamond(vg, ix, cy, 19, 0xFF297AAA);
            if (kind == 1) {
                line(vg, ix - 12, cy + 7, ix, cy - 12, 0xFFE0BD7D); line(vg, ix, cy - 12, ix + 12, cy + 7, 0xFFE0BD7D); line(vg, ix - 12, cy + 7, ix + 12, cy + 7, 0xFFE0BD7D);
            } else if (kind == 2) {
                circle(vg, ix, cy, 10, 0xFFE0BD7D); line(vg, ix - 15, cy, ix + 15, cy, 0xFF55C8F5);
            } else {
                line(vg, ix - 10, cy - 9, ix - 10, cy + 9, 0xFFE0BD7D); line(vg, ix - 10, cy + 9, ix + 10, cy + 9, 0xFFE0BD7D); line(vg, ix + 10, cy + 9, ix + 10, cy - 9, 0xFFE0BD7D);
            }
            line(vg, ix + 44, cy, x + w, cy, 0xFF435044); return;
        }
        float r = Math.min(w * .42f, h * .42f), t = (float) clock.getAsDouble();
        circle(vg, cx, cy, r, 0xFF91794E); circle(vg, cx, cy, r - 7, 0xFF435044); circle(vg, cx, cy, r * .67f, 0xFF235273);
        for (int i = 0; i < 32; i++) {
            double a = i * Math.PI / 16; float dx = (float) Math.cos(a), dy = (float) Math.sin(a);
            line(vg, cx + dx * (r - 3), cy + dy * (r - 3), cx + dx * (r + (i % 4 == 0 ? 7 : 2)), cy + dy * (r + (i % 4 == 0 ? 7 : 2)), 0xFF91794E);
        }
        for (int i = 0; i < 4; i++) {
            double a = t * .12 + i * Math.PI / 2;
            float px = cx + (float) Math.cos(a) * r * .67f, py = cy + (float) Math.sin(a) * r * .67f;
            line(vg, cx, cy, px, py, 0xFF235273); diamond(vg, px, py, 9, 0xFFE0BD7D); diamond(vg, px, py, 4, 0xFF55C8F5);
        }
        circle(vg, cx, cy, 35 + (float) Math.sin(t) * 3, 0xFF235273);
        diamond(vg, cx, cy, 29, 0xFFE0BD7D); diamond(vg, cx, cy, 21, 0xFF55C8F5);
        line(vg, cx, cy - 21, cx, cy + 21, 0xFF55C8F5); line(vg, cx - 21, cy, cx + 21, cy, 0xFF55C8F5);
    }
    private static void line(long vg, float x, float y, float xx, float yy, int color) {
        NanoUtility.strokeLine(vg,x,y,xx,yy,palette(color),1);
    }
    private static void circle(long vg, float x, float y, float radius, int color) {
        NanoUtility.strokeCircle(vg,x,y,radius,palette(color),1);
    }
    private static void diamond(long vg, float x, float y, float r, int color) {
        NanoUtility.strokeDiamond(vg,x,y,r,palette(color),1.3f);
    }
    private static int palette(int color) {
        return switch (color) {
            case 0xFFE0BD7D -> 0xFFC6B4F3;
            case 0xFF55C8F5 -> 0xFF9874EC;
            case 0xFF91794E -> 0xFF666170;
            case 0xFF297AAA, 0xFF235273 -> 0xFF393048;
            default -> 0xFF29292E;
        };
    }
}
