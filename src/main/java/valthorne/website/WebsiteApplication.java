package valthorne.website;

import valthorne.Application;
import valthorne.JGL;
import valthorne.Window;
import valthorne.graphics.Color;
import valthorne.ui.UIRoot;
import valthorne.ui.nodes.nano.NanoLabel;
import valthorne.ui.nodes.nano.NanoPanel;

/** A normal Valthorne UI application; the portable backend owns browser details. */
public final class WebsiteApplication implements Application {
    private UIRoot root;

    public static void main(String[] args) {
        JGL.init(new WebsiteApplication(), "Valthorne", 1440, 980);
    }

    @Override public void init() {
        root = new UIRoot();
        root.getLayout().width(Window.getWidth()).height(Window.getHeight());
        NanoPanel page = new NanoPanel().backgroundColor(new Color(0xFF101114));
        page.getLayout().column().widthPercent(100).heightPercent(100).padding(48).gap(18);
        page.add(label("VALTHORNE / JAVA GAME ENGINE", 14, new Color(0xFFC7E6FF)));
        page.add(label("Build worlds that move.", 48, Color.WHITE));
        NanoLabel intro = label("A complete Java game engine with 2D, 3D, Jolt physics, lighting, particles, audio, and portable web exports.", 22, new Color(0xFFBFC3CB));
        intro.getLayout().maxWidth(760);
        page.add(intro);
        NanoPanel features = new NanoPanel().backgroundColor(new Color(0xFF1B1D22));
        features.getLayout().row().widthPercent(100).padding(24).gap(28);
        features.add(label("3D + Jolt", 20, Color.WHITE));
        features.add(label("Lighting + particles", 20, Color.WHITE));
        features.add(label("Desktop + Web", 20, Color.WHITE));
        page.add(features);
        page.add(label("Open source · Apache-2.0 · Written in Java", 16, new Color(0xFF91979F)));
        root.add(page);
    }

    private static NanoLabel label(String text, float size, Color color) {
        return new NanoLabel(text).fontName("default").fontSize(size).color(color);
    }

    @Override public void update(float delta) {
        root.setSize(Window.getWidth(), Window.getHeight());
        root.update(delta);
    }

    @Override public void render() { root.draw(); }
    @Override public void dispose() { if (root != null) root.dispose(); }
}
