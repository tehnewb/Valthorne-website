package com.example;

import valthorne.Window;
import valthorne.Mouse;
import valthorne.PlatformTools;
import valthorne.camera.PerspectiveCamera;
import valthorne.graphics.Color;
import valthorne.graphics.model.*;
import valthorne.ui.UINode;
import java.io.IOException;

/** Real textured geometry, submitted to the engine's Filament renderer, never a rendered image. */
final class SceneExhibit implements AutoCloseable {
    private final Scene3D scene = new Scene3D();
    private final PerspectiveCamera camera = new PerspectiveCamera();
    private final FilamentRenderer3D renderer = new FilamentRenderer3D();
    private final ObjModel3D tree;
    private final ModelInstance3D jacaranda;
    private final PointLight3D key, rim;
    private float yaw, previousTime, lightX, lightZ = 3;

    SceneExhibit() {
        tree = load("jacaranda/tree");
        for (var part : tree.getParts()) part.material().setRoughness(.85f).setAlphaCutoff(.45f).setCullBackFaces(false);
        var bounds = tree.getLocalBounds();
        float scale = 3.6f / Math.max(bounds.maxX - bounds.minX, bounds.maxY - bounds.minY);
        jacaranda = new ModelInstance3D().setModel(tree)
                .setMaterial(new Material3D().setRoughness(.85f).setAlphaCutoff(.45f).setCullBackFaces(false))
                .setScale(scale).setPosition(0, 0, 0);
        scene.add(jacaranda);
        key = new PointLight3D().setPosition(-3,-4,5).setColor(new Color(0xFFE9E5FF)).setIntensity(1800).setRange(15).setCastsShadows(true);
        rim = new PointLight3D().setPosition(3,2,3.3f).setColor(new Color(0xFF9472FF)).setIntensity(1400).setRange(12);
        scene.addLight(key); scene.addLight(rim);
        scene.addLight(new PointLight3D().setPosition(-3,1,1.4f).setColor(new Color(0xFF7098DB)).setIntensity(180).setRange(10));
        renderer.setQuality(FilamentRenderer3D.Quality.HIGH); renderer.setEnvironmentIntensity(55); renderer.setExposure(1.15f);
        camera.setClipPlanes(.05f, 60); camera.setFieldOfViewDegrees(37);
    }
    private static ObjModel3D load(String name) {
        return ObjModel3D.load("models/" + name + ".obj", path -> {
            try (var input = SceneExhibit.class.getResourceAsStream("/" + path.replace('\\','/'))) {
                if(input==null) throw new IOException("Missing model asset: " + path);
                return input.readAllBytes();
            }
        }, true);
    }
    void render(UINode slot, float scrollY, float time, boolean moving) {
        float top = slot.getAbsoluteY() - scrollY;
        int x = Math.round(slot.getAbsoluteX()), y = Math.round(Window.getHeight() - top - slot.getHeight());
        int w = Math.round(slot.getWidth()), h = Math.round(slot.getHeight());
        if (w < 1 || h < 1 || top >= Window.getHeight() || top + h <= 78) return;
        float mx = Math.max(-1, Math.min(1, (Mouse.getX() - slot.getAbsoluteX()) / w * 2 - 1));
        float my = Math.max(-1, Math.min(1, (Window.getHeight() - Mouse.getY() - top) / h * 2 - 1));
        float target = moving ? mx * .13f + scrollY * .00035f : yaw;
        float elapsed = Math.max(0, Math.min(.05f, time - previousTime));
        previousTime = time;
        float response = moving ? 1 - (float)Math.exp(-9 * elapsed) : 1;
        key.setIntensity(key.getIntensity() + (2200 - key.getIntensity()) * response);
        lightX += (mx * 3.2f - lightX) * response;
        lightZ += ((1 - my) * 2.3f - lightZ) * response;
        key.setPosition(lightX, -2.2f, lightZ);
        yaw += (target-yaw)*response;
        jacaranda.setRotation(0,0,yaw);
        camera.setPosition(mx * .12f,-6.2f,2.8f); camera.lookAt(0,0,1.55f,0,0,1);
        PlatformTools.viewport(x,y,w,h);
        try { renderer.render(scene,camera); }
        finally { PlatformTools.viewport(0,0,Window.getWidth(),Window.getHeight()); }
    }
    @Override public void close() { renderer.close(); tree.dispose(); }
}
