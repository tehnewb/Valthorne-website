package com.example;

import valthorne.graphics.texture.TextureData;
import valthorne.ui.nodes.nano.NanoImage;

import java.io.IOException;

/** The supplied Valthorne banner used in the landing-page hero. */
final class BannerImage extends NanoImage {
    BannerImage() {
        super(load());
    }

    private static TextureData load() {
        try (var stream = BannerImage.class.getResourceAsStream("/images/valthorne-banner.png")) {
            if (stream == null) throw new IllegalStateException("Missing Valthorne banner");
            return TextureData.load(stream.readAllBytes());
        } catch (IOException error) {
            throw new IllegalStateException("Could not load Valthorne banner", error);
        }
    }
}
