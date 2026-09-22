package com.example;

import valthorne.graphics.texture.TextureData;
import valthorne.ui.nodes.nano.NanoImageHyperlink;

import java.io.IOException;

/** A compact image-backed social link used in the site ribbon. */
final class SocialIconLink extends NanoImageHyperlink {
    static final int GITHUB = 0;
    static final int DISCORD = 1;

    SocialIconLink(int icon, String url) {
        super(icon == GITHUB ? "GitHub" : "Discord", url,
                load(icon == GITHUB ? "/images/github.png" : "/images/discord.png"));
        getLayout().width(34).height(34).noShrink();
    }

    private static TextureData load(String path) {
        try (var stream = SocialIconLink.class.getResourceAsStream(path)) {
            if (stream == null) throw new IllegalStateException("Missing social icon: " + path);
            return TextureData.load(stream.readAllBytes());
        } catch (IOException error) {
            throw new IllegalStateException("Could not load social icon: " + path, error);
        }
    }
}
