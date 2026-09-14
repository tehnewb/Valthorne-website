package com.example;

import valthorne.Application;
import valthorne.JGL;
import valthorne.Window;
import valthorne.Mouse;
import valthorne.graphics.Color;
import valthorne.graphics.texture.TextureData;
import valthorne.ui.NanoUtility;
import valthorne.ui.UINode;
import valthorne.ui.UIRoot;
import valthorne.ui.nodes.nano.*;

/** Layout, interaction and painting are implemented entirely in the Java UI. */
public final class Main implements Application {
    static final Color INK = new Color(0xFF000000), PANEL = new Color(0xFF111113);
    static final Color WHITE = new Color(0xFFFFFFFF), MUTED = new Color(0xFFBFBFBF);
    static final Color VIOLET = new Color(0xFFB59AFA), LINE = new Color(0xFF242426), CLEAR = new Color(0x00000000);
    private UIRoot ui;
    private NanoScrollPanel scroll;
    private NanoContainer exhibitSlot;
    private UINode projects, about;
    private SceneExhibit exhibit;
    private TextureData banner;
    private int width, height;
    private float time, navTime, navFrom, navTo, lastScroll;
    private boolean motion = true, navigating;

    public static void main(String[] args) { JGL.init(new Main(), "Valthorne — Worlds begin with you.", 1440, 960); }
    @Override public void init() {
        ui = new UIRoot();
        NanoUtility.loadResourceFont(ui.getNanoVGHandle(), "editorial", "fonts/editorial.ttf");
        banner = TextureData.load("branding/valthorne-logo.png", true);
        exhibit = new SceneExhibit(); rebuild();
    }
    private void rebuild() {
        float saved = scroll == null ? 0 : scroll.getScrollY();
        width = Window.getWidth(); height = Window.getHeight();
        float gutter = width < 600 ? 22 : width * .05f;
        ui.clear(); ui.setSize(width, height); navigating = false;
        var root = panel(CLEAR); root.getLayout().widthPercent(100).heightPercent(100).column(); ui.add(root);
        var ribbon = panel(INK);
        ribbon.getLayout().widthPercent(100).height(92).row().itemsCenter().padding(gutter, 18).noShrink();
        var left = new NanoContainer(); left.getLayout().width(0).grow();
        if (width >= 800) left.add(label("Open source  /  Java 25", 12, MUTED)); ribbon.add(left);
        var nav = panel(PANEL); nav.cornerRadius(30).borderWidth(1).borderColor(LINE);
        nav.getLayout().row().itemsCenter().padding(8).gap(3).noShrink();
        nav.add(button("Home", () -> navigate(null))); nav.add(button("Explore", () -> navigate(projects))); nav.add(button("About", () -> navigate(about)));
        var pause = button(motion ? "Ⅱ" : "▷", () -> {});
        pause.action(n -> { motion = !motion; pause.text(motion ? "Ⅱ" : "▷"); }); nav.add(pause); ribbon.add(nav);
        var right = new NanoContainer(); right.getLayout().width(0).grow().itemsEnd();
        if (width >= 800) right.add(label("BUILT WITH VALTHORNE", 10, MUTED)); ribbon.add(right); root.add(ribbon);
        scroll = new NanoScrollPanel().horizontal(false).horizontalBar(false).scrollSpeed(60);
        scroll.setStyle(NanoScrollPanel.BACKGROUND_COLOR_KEY, CLEAR); scroll.setStyle(NanoScrollPanel.BORDER_WIDTH_KEY, 0f);
        scroll.getLayout().widthPercent(100).height(0).grow().minHeight(0); root.add(scroll);
        var content = new NanoContainer(); content.getLayout().widthPercent(100).column().noShrink(); scroll.setContent(content);
        var hero = section(CLEAR, gutter, 0); hero.getLayout().gap(0);
        var logoRow = new NanoContainer(); logoRow.getLayout().widthPercent(100).itemsCenter().noShrink();
        var logo = new NanoImage(banner); logo.getLayout().width(170).height(70).noShrink(); logoRow.add(logo); hero.add(logoRow);
        var row = new NanoContainer(); row.getLayout().widthPercent(100).noShrink();
        if (width < 700) row.getLayout().column(); else row.getLayout().row().itemsCenter();
        var copy = new NanoContainer(); copy.getLayout().column().gap(14).noShrink().widthPercent(width < 700 ? 100 : 47);
        copy.add(label("valthorne", width < 700 ? 64 : Math.min(104, width * .075f), WHITE));
        copy.add(label("An open Java engine for extraordinary worlds.\nRendering, physics, audio and UI. Together.", width < 700 ? 14 : 17, MUTED)); row.add(copy);
        exhibitSlot = new NanoContainer() {
            @Override public void draw(long vg) {
                float px = Mouse.getX(), py = Window.getHeight() - Mouse.getY() + scroll.getScrollY();
                if (px < getAbsoluteX() || px > getAbsoluteX() + getWidth() || py < getAbsoluteY() || py > getAbsoluteY() + getHeight()) return;
                // A restrained visible source accompanies the real 3D point light.
                for (int i = 5; i > 0; i--) NanoUtility.strokeCircle(vg,px,py,i*3,0x0A9F8FFF,6);
                NanoUtility.strokeCircle(vg,px,py,2,0xFFFFFFFF,3);
            }
        }; exhibitSlot.setClickable(false);
        exhibitSlot.getLayout().height(width < 700 ? 350 : Math.max(390, Math.min(540, height * .58f))).noShrink();
        if (width < 700) exhibitSlot.getLayout().widthPercent(100); else exhibitSlot.getLayout().width(0).grow();
        row.add(exhibitSlot); hero.add(row); content.add(hero);
        var grid = section(INK, gutter, 32); grid.getLayout().gap(32); projects = grid;
        var first = cardsRow();
        first.add(card(0, "A living world", "Real geometry, textured surfaces and responsive light.\nOne scene, rendered by Valthorne.", "Explore the scene  ↑", () -> navigate(null)));
        first.add(card(1, "Light & motion", "Build expressive worlds with lighting and particles.\nA shared lifecycle keeps everything connected.", "Get started  ↗", () -> navigate(about))); grid.add(first);
        var second = cardsRow();
        second.add(card(2, "Connected systems", "Rendering, physics, input and spatial audio.\nThe building blocks for your next game.", "Explore Valthorne  ↗", () -> navigate(about)));
        second.add(card(3, "Your next creation", "Compose interfaces with the built-in Java UI.\nThis page is running inside the engine.", "Start building  ↗", () -> navigate(about))); grid.add(second); content.add(grid);
        var footer = section(INK, gutter, 54); footer.getLayout().gap(14); about = footer;
        footer.add(label("Build something extraordinary.", width < 700 ? 31 : 42, WHITE));
        footer.add(label("Create a Java 25 project. Add Valthorne. Make it yours.", width < 700 ? 13 : 15, MUTED));
        footer.add(label("implementation 'io.github.tehnewb:Valthorne:2.1.0'", width < 700 ? 12 : 16, VIOLET));
        footer.add(label("Select the dependency to copy it.", 11, MUTED)); footer.add(button("Back to top  ↑", () -> navigate(null)));
        footer.add(label("© 2026 Valthorne  /  Albert Beaupre          Apache-2.0", width < 700 ? 10 : 12, MUTED));
        footer.add(label("Jacaranda tree: Rico Cilliers / Poly Haven · CC0", 10, MUTED)); content.add(footer);
        ui.layout(); scroll.scrollY(saved);
    }
    private NanoContainer cardsRow() {
        var row = new NanoContainer(); row.getLayout().widthPercent(100).gap(24).noShrink();
        if (width < 700) row.getLayout().column(); else row.getLayout().row(); return row;
    }
    private NanoContainer card(int kind, String title, String body, String link, Runnable action) {
        var card = new NanoContainer(); card.getLayout().column().gap(9).noShrink();
        if (width >= 700) card.getLayout().width(0).grow(); else card.getLayout().widthPercent(100);
        var art = new ShowcaseArt(kind, () -> time);
        art.getLayout().widthPercent(100).height(width < 700 ? 230 : Math.min(340, width * .24f)).noShrink();
        card.add(art); card.add(label(title, 30, WHITE)); card.add(label(body, width < 700 ? 12 : 13, MUTED)); card.add(button(link, action)); return card;
    }
    private NanoPanel section(Color color, float gutter, float vertical) {
        var p = panel(color); p.getLayout().widthPercent(100).column().padding(gutter, vertical).noShrink(); return p;
    }
    private NanoLabel label(String text, float size, Color color) {
        var label = new NanoLabel(text); label.fontName(size >= 24 ? "editorial" : "default").fontSize(size).color(color).selectable(true);
        label.getLayout().noShrink(); return label;
    }
    private static NanoPanel panel(Color c) { return new NanoPanel().backgroundColor(c).hoverBackgroundColor(c).focusedBackgroundColor(c).pressedBackgroundColor(c).borderWidth(0).cornerRadius(0); }
    private NanoButton button(String text, Runnable action) {
        var b = new NanoButton(text) {
            private float blend, previous;
            private final Color animated = new Color(0xFF111113);
            @Override public void draw(long vg) {
                float dt = Math.max(0, time - previous); previous = time;
                blend += ((isHovered() || isFocused() ? 1 : 0) - blend) * (motion ? 1 - (float)Math.exp(-dt * 14) : 1);
                float c = .067f + .075f * blend; animated.set(c,c,c + .008f,1);
                backgroundColor(animated).hoverBackgroundColor(animated).focusedBackgroundColor(animated); super.draw(vg);
            }
        };
        b.fontSize(12).paddingX(17).paddingY(9).cornerRadius(18).borderWidth(0).textColor(MUTED).hoverTextColor(WHITE).focusedTextColor(WHITE).action(n -> action.run());
        b.getLayout().height(34).noShrink(); return b;
    }
    private void navigate(UINode section) {
        navFrom = scroll.getScrollY(); navTo = section == null ? 0 : section.getAbsoluteY() - scroll.getContent().getAbsoluteY();
        navTo = Math.min(scroll.getMaxScrollY(), Math.max(0, navTo)); navTime = 0; lastScroll = navFrom; navigating = motion;
        if (!motion) scroll.scrollY(navTo);
    }
    @Override public void update(float delta) {
        if (motion) time += Math.min(delta, .05f);
        if (width != Window.getWidth() || height != Window.getHeight()) rebuild(); ui.update(delta);
        if (navigating) {
            if (Math.abs(scroll.getScrollY() - lastScroll) > 1) { navigating = false; return; }
            navTime = Math.min(1, navTime + delta / .8f); float ease = navTime * navTime * (3 - 2 * navTime);
            scroll.scrollY(navFrom + (navTo - navFrom) * ease); lastScroll = scroll.getScrollY(); navigating = navTime < 1;
        }
    }
    @Override public void render() { Window.clear(INK); exhibit.render(exhibitSlot, scroll.getScrollY(), time, motion); ui.draw(); }
    @Override public void dispose() { if (ui != null) ui.dispose(); if (exhibit != null) exhibit.close(); if (banner != null) banner.dispose(); }
}
