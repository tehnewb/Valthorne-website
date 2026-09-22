package com.example;

import valthorne.Application;
import valthorne.JGL;
import valthorne.Mouse;
import valthorne.Window;
import valthorne.event.events.MouseDragEvent;
import valthorne.event.events.MouseMoveEvent;
import valthorne.event.events.MousePressEvent;
import valthorne.event.events.MouseReleaseEvent;
import valthorne.event.listeners.MouseListener;
import valthorne.graphics.Color;
import valthorne.ui.NanoUtility;
import valthorne.ui.UINode;
import valthorne.ui.UIRoot;
import valthorne.ui.nodes.nano.*;

/**
 * Layout, interaction and painting are implemented entirely in the Java UI.
 */
public final class Main implements Application {
    static final Color INK = new Color(0xFF000000), PANEL = new Color(0xFF111113);
    static final Color WHITE = new Color(0xFFFFFFFF), MUTED = new Color(0xFFBFBFBF);
    static final Color VIOLET = new Color(0xFFB59AFA), LINE = new Color(0xFF242426), CLEAR = new Color(0x00000000);
    private UIRoot ui;
    private NanoScrollPanel scroll;
    private NanoContainer exhibitSlot;
    private NanoContainer documentationNavigation;
    private UINode projects, about;
    private SceneExhibit exhibit;
    private int page = -1, pendingPage = -2;
    private int width, height;
    private float time, navTime, navFrom, navTo, lastScroll;
    private boolean motion = true, navigating;
    private boolean pageDrag;
    private final MouseListener pageDragListener = new MouseListener() {
        @Override
        public void mousePressed(MousePressEvent event) {
            if (event.getButton() != Mouse.LEFT || event.isConsumed() || ui == null || scroll == null) return;
            pageDrag = shouldPageDrag(event.getX(), event.getY());
        }

        @Override
        public void mouseReleased(MouseReleaseEvent event) {
            if (event.getButton() == Mouse.LEFT) pageDrag = false;
        }

        @Override
        public void mouseDragged(MouseDragEvent event) {
            if (!pageDrag || event.getButton() != Mouse.LEFT || event.isConsumed() || scroll == null) return;
            scroll.scrollBy(0, -event.getDeltaY());
        }

        @Override
        public void mouseMoved(MouseMoveEvent event) {}
    };

    public static void main(String[] args) {JGL.init(new Main(), "Valthorne — Worlds begin with you.", 1440, 960);}

    @Override
    public void init() {
        long started = System.nanoTime();
        ui = new UIRoot();
        NanoUtility.loadResourceFont(ui.getNanoVGHandle(), "editorial", "fonts/editorial.ttf");
        NanoUtility.loadResourceFont(ui.getNanoVGHandle(), "code", "fonts/mono.ttf");
        exhibit = new SceneExhibit();
        Mouse.addMouseListener(pageDragListener);
        rebuild();
        System.out.println("Website UI initialized in " + (System.nanoTime() - started) / 1000000 + " ms");
    }

    private void rebuild() {
        float saved = scroll == null ? 0 : scroll.getScrollY();
        width = Window.getWidth();
        height = Window.getHeight();
        float gutter = width < 600 ? 22 : width * .05f;
        ui.clear();
        exhibit.clearSurfaces();
        ui.setSize(width, height);
        navigating = false;
        var root = new NanoContainer() {
            @Override
            public void draw(long vg) {
                float px = Mouse.getX(), py = Window.getHeight() - Mouse.getY();
                for (int i = 5; i > 0; i--) NanoUtility.strokeCircle(vg, px, py, i * 3, 0x0A9F8FFF, 6);
                NanoUtility.strokeCircle(vg, px, py, 2, 0xFFFFFFFF, 3);
                super.draw(vg);
            }
        };
        root.getLayout().widthPercent(100).heightPercent(100).column();
        ui.add(root);
        var ribbon = panel(CLEAR);
        ribbon.getLayout().widthPercent(100).height(92).row().itemsCenter().padding(gutter, 18).noShrink();
        var left = new NanoContainer();
        left.getLayout().width(0).grow();
        if (width >= 800) {
            left.getLayout().row().itemsCenter().gap(5);
            left.add(new SocialIconLink(SocialIconLink.GITHUB, "https://github.com/tehnewb/Valthorne"));
            left.add(new SocialIconLink(SocialIconLink.DISCORD, "https://discord.gg/APqcDzppDv"));
        }
        ribbon.add(left);
        var nav = panel(PANEL);
        nav.cornerRadius(30).borderWidth(1).borderColor(LINE);
        nav.getLayout().row().itemsCenter().padding(8).gap(3).noShrink();
        nav.add(button("Home", () -> {
            if (page >= 0) pendingPage = -1;
            else navigate(null);
        }));
        nav.add(button("Explore", () -> {
            if (page >= 0) pendingPage = -1;
            else navigate(projects);
        }));
        ribbon.add(nav);
        var right = new NanoContainer();
        right.getLayout().width(0).grow().itemsEnd();
        if (width >= 800) right.add(label("BUILT WITH VALTHORNE", 10, MUTED));
        ribbon.add(right);
        root.add(ribbon);
        scroll = new NanoScrollPanel().horizontal(false).horizontalBar(false).scrollSpeed(60);
        scroll.setStyle(NanoScrollPanel.BACKGROUND_COLOR_KEY, CLEAR);
        scroll.setStyle(NanoScrollPanel.BORDER_WIDTH_KEY, 0f);
        documentationNavigation = null;
        if (page >= 0 && width >= 900) {
            var workspace = new NanoContainer();
            workspace.getLayout().widthPercent(100).height(0).grow().minHeight(0).row();
            var sidebar = new NanoScrollPanel().horizontal(false).horizontalBar(false);
            sidebar.getLayout().width(260).heightPercent(100).noShrink();
            sidebar.setStyle(NanoScrollPanel.BACKGROUND_COLOR_KEY, PANEL);
            sidebar.setStyle(NanoScrollPanel.BORDER_WIDTH_KEY, 0f);
            documentationNavigation = new NanoContainer();
            documentationNavigation.getLayout().widthPercent(100).column().padding(18).gap(8).noShrink();
            sidebar.setContent(documentationNavigation);
            workspace.add(sidebar);
            scroll.getLayout().width(0).grow().heightPercent(100).minWidth(0);
            workspace.add(scroll);
            root.add(workspace);
        } else {
            scroll.getLayout().widthPercent(100).height(0).grow().minHeight(0);
            root.add(scroll);
        }
        var content = new NanoContainer();
        content.getLayout().widthPercent(100).column().noShrink();
        scroll.setContent(content);
        if (page >= 0) {
            buildFeature(content, gutter);
            ui.layout();
            scroll.scrollY(saved);
            return;
        }
        var hero = section(CLEAR, gutter, 0);
        hero.getLayout().gap(0);
        var row = new NanoContainer();
        row.getLayout().widthPercent(100).noShrink();
        if (width < 700) row.getLayout().column();
        else row.getLayout().row().itemsCenter();
        var copy = new NanoContainer();
        copy.getLayout().column().gap(14).noShrink().widthPercent(width < 700 ? 100 : 47);
        copy.add(label("valthorne", width < 700 ? 64 : Math.min(104, width * .075f), WHITE));
        copy.add(label("an open source Java game engine", width < 700 ? 14 : 17, MUTED));
        row.add(copy);
        exhibitSlot = new NanoContainer();
        exhibitSlot.setClickable(false);
        exhibitSlot.getLayout().height(width < 700 ? 350 : Math.max(390, Math.min(540, height * .58f))).noShrink();
        if (width < 700) exhibitSlot.getLayout().widthPercent(100);
        else exhibitSlot.getLayout().width(0).grow();
        row.add(exhibitSlot);
        hero.add(row);
        content.add(hero);
        var grid = section(INK, gutter, 32);
        grid.getLayout().gap(32);
        projects = grid;
        int columns = width >= 1050 ? 3 : width >= 700 ? 2 : 1;
        for (int i = 0; i < FeaturePage.PAGES.length; i += columns) {
            var cards = cardsRow();
            for (int j = i; j < Math.min(i + columns, FeaturePage.PAGES.length); j++) {
                var feature = FeaturePage.PAGES[j];
                cards.add(card(j, feature.title(), feature.summary(), feature.highlights()));
            }
            grid.add(cards);
        }
        content.add(grid);
        var footer = section(INK, gutter, 54);
        footer.getLayout().gap(14);
        about = footer;
        footer.add(label("Build something extraordinary.", width < 700 ? 31 : 42, WHITE));
        footer.add(label("Create a Java 25 project. Add Valthorne. Make it yours.", width < 700 ? 13 : 15, MUTED));
        footer.add(new CodeBlock("// build.gradle dependency\nimplementation 'io.github.tehnewb:Valthorne:2.1.0'"));
        footer.add(button("Back to top  ↑", () -> navigate(null)));
        footer.add(label("© 2026 Valthorne  /  Albert Beaupre          Apache-2.0", width < 700 ? 10 : 12, MUTED));
        footer.add(label("Jacaranda tree: Rico Cilliers / Poly Haven · CC0", 10, MUTED));
        content.add(footer);
        ui.layout();
        scroll.scrollY(saved);
    }

    private NanoContainer cardsRow() {
        var row = new NanoContainer();
        row.getLayout().widthPercent(100).gap(24).noShrink();
        if (width < 700) row.getLayout().column();
        else row.getLayout().row();
        return row;
    }

    private NanoContainer card(int kind, String title, String body, String highlights) {
        Runnable open = () -> pendingPage = kind;
        var card = new NanoContainer();
        card.getLayout().column().gap(9).noShrink();
        if (width >= 700) card.getLayout().width(0).grow();
        else card.getLayout().widthPercent(100);
        var art = new ShowcaseArt(kind, () -> time, open);
        exhibit.addSurface(art);
        art.getLayout().widthPercent(100).height(width < 700 ? 190 : 170).noShrink();
        card.add(art);
        card.add(label(title, width < 700 ? 27 : 24, WHITE));
        card.add(label(body, width < 700 ? 12 : 13, MUTED));
        card.add(label(highlights, 10, VIOLET));
        card.add(button("Explore features  →", open));
        return card;
    }

    private void buildFeature(NanoContainer content, float gutter) {
        exhibitSlot = null;
        var feature = FeaturePage.PAGES[page];
        var section = section(CLEAR, gutter, 42);
        section.getLayout().gap(22);
        projects = section;
        about = section;
        section.add(button("← All features", () -> pendingPage = -1));
        section.add(label("FEATURES  /  " + String.format("%02d", page + 1), 11, VIOLET));
        section.add(label(feature.title(), width < 700 ? 42 : 64, WHITE));
        section.add(label(feature.summary(), width < 700 ? 16 : 21, MUTED));
        var examples = FeatureExamples.forPage(page);
        var exampleLinks = new NanoContainer();
        exampleLinks.getLayout().column().gap(6).noShrink();
        if (documentationNavigation != null) {
            documentationNavigation.add(label("DOCUMENTATION", 12, VIOLET));
            for (int i = 0; i < FeaturePage.PAGES.length; i++) {
                final int target = i;
                var link = button(FeaturePage.PAGES[i].title(), () -> pendingPage = target);
                if (i == page) link.textColor(VIOLET);
                documentationNavigation.add(link);
                if (i == page) documentationNavigation.add(exampleLinks);
            }
        } else {
            section.add(label((examples.length + 1) + " code example" + (examples.length == 0 ? "" : "s") + "  /  On this page", 13, VIOLET));
            section.add(exampleLinks);
        }
        section.add(label(feature.details(), width < 700 ? 13 : 17, MUTED));
        var topics = feature.topics();
        for (int i = 0; i < topics.length; i += 2) {
            section.add(label(topics[i], width < 700 ? 26 : 32, WHITE));
            section.add(label(topics[i + 1], width < 700 ? 13 : 17, MUTED));
        }
        var start = label("Start building", width < 700 ? 26 : 32, WHITE);
        section.add(start);
        exampleLinks.add(button("01  Start building", () -> navigate(start)));
        section.add(new CodeBlock(feature.code()));
        int exampleNumber = 2;
        for (var example : examples) {
            var heading = label(example.title(), width < 700 ? 26 : 32, WHITE);
            section.add(heading);
            exampleLinks.add(button("0" + exampleNumber++ + "  " + example.title(), () -> navigate(heading)));
            section.add(label(example.note(), width < 700 ? 13 : 17, MUTED));
            section.add(new CodeBlock(example.code()));
        }
        section.add(label("Use these fragments in your Application lifecycle.\nImport the relevant Valthorne classes in your project.", width < 700 ? 12 : 14, MUTED));
        section.add(button("Next feature  →", () -> pendingPage = (page + 1) % FeaturePage.PAGES.length));
        content.add(section);
    }

    private NanoPanel section(Color color, float gutter, float vertical) {
        var p = panel(CLEAR);
        p.getLayout().widthPercent(100).column().padding(gutter, vertical).noShrink();
        return p;
    }

    private NanoLabel label(String text, float size, Color color) {
        var label = new NanoLabel(text);
        label.fontName(size >= 24 ? "editorial" : "default").fontSize(size).color(color).selectable(true);
        label.getLayout().noShrink();
        return label;
    }

    private static NanoPanel panel(Color c) {return new NanoPanel().backgroundColor(c).hoverBackgroundColor(c).focusedBackgroundColor(c).pressedBackgroundColor(c).borderWidth(0).cornerRadius(0);}

    private NanoButton button(String text, Runnable action) {
        var b = new NanoButton(text) {
            private float blend, previous;
            private final Color animated = new Color(0xFF111113);

            @Override
            public void draw(long vg) {
                float dt = Math.max(0, time - previous);
                previous = time;
                blend += ((isHovered() || isFocused() ? 1 : 0) - blend) * (motion ? 1 - (float) Math.exp(-dt * 14) : 1);
                float c = .067f + .075f * blend;
                animated.set(c, c, c + .008f, 1);
                backgroundColor(animated).hoverBackgroundColor(animated).focusedBackgroundColor(animated);
                super.draw(vg);
            }
        };
        b.fontSize(12).paddingX(17).paddingY(9).cornerRadius(18).borderWidth(0).textColor(MUTED).hoverTextColor(WHITE).focusedTextColor(WHITE).action(n -> action.run());
        b.getLayout().height(34).noShrink();
        return b;
    }

    private void navigate(UINode section) {
        navFrom = scroll.getScrollY();
        navTo = section == null ? 0 : section.getAbsoluteY() - scroll.getContent().getAbsoluteY();
        navTo = Math.min(scroll.getMaxScrollY(), Math.max(0, navTo));
        navTime = 0;
        lastScroll = navFrom;
        navigating = motion;
        if (!motion) scroll.scrollY(navTo);
    }

    @Override
    public void update(float delta) {
        if (motion) time += Math.min(delta, .05f);
        if (pendingPage != -2) {
            page = pendingPage;
            pendingPage = -2;
            scroll.scrollY(0);
            rebuild();
        }
        if (width != Window.getWidth() || height != Window.getHeight()) rebuild();
        ui.update(delta);
        if (navigating) {
            if (Math.abs(scroll.getScrollY() - lastScroll) > 1) {
                navigating = false;
                return;
            }
            navTime = Math.min(1, navTime + delta / .8f);
            float ease = navTime * navTime * (3 - 2 * navTime);
            scroll.scrollY(navFrom + (navTo - navFrom) * ease);
            lastScroll = scroll.getScrollY();
            navigating = navTime < 1;
        }
    }

    private boolean shouldPageDrag(float x, float y) {
        UINode target = ui.findNodeAt(x, y, UINode.CLICKABLE_BIT);
        return target == null || target == scroll || target == ui;
    }

    @Override
    public void render() {
        exhibit.render(exhibitSlot, scroll.getScrollY(), time, motion);
        ui.draw();
    }

    @Override
    public void dispose() {
        Mouse.removeMouseListener(pageDragListener);
        if (ui != null) ui.dispose();
        if (exhibit != null) exhibit.close();
    }
}
