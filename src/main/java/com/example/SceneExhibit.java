package com.example;

import java.util.ArrayList;
import valthorne.Window;
import valthorne.Mouse;
import valthorne.camera.PerspectiveCamera;
import valthorne.graphics.Color;
import valthorne.graphics.model.*;
import valthorne.ui.UINode;

/** One full-window scene shared by the hero and every UI surface. */
final class SceneExhibit implements AutoCloseable {
    private final Scene3D scene = new Scene3D();
    private final PerspectiveCamera camera = new PerspectiveCamera();
    private final FilamentRenderer3D renderer = new FilamentRenderer3D();
    private final Model3D plane = ModelBuilder3D.plane(1,1);
    private final ModelInstance3D background;
    private final PointLight3D pointer;
    private final ArrayList<UINode> nodes = new ArrayList<>();
    private final ArrayList<ModelInstance3D> surfaces = new ArrayList<>();
    private PackedTree tree;
    private float lightX, lightZ, previousTime;
    private int frames;
    private static final float DISTANCE=9, FOV=37;

    SceneExhibit() {
        background=surface(new Color(0xFF08070B)); background.setPosition(0,1,0); scene.add(background);
        pointer = new PointLight3D().setColor(new Color(0xFFE9DEFF)).setIntensity(350).setRange(9).setCastsShadows(true);
        scene.addLight(pointer);
        scene.addLight(new PointLight3D().setPosition(4,0,3).setColor(new Color(0xFF8761DC)).setIntensity(200).setRange(9));
        // A document does not need four-sample scene buffers or full-resolution AO.
        renderer.setQuality(FilamentRenderer3D.Quality.PERFORMANCE); renderer.setEnvironmentIntensity(40); renderer.setExposure(1.1f);
        camera.setClipPlanes(.05f,40); camera.setFieldOfViewDegrees(FOV);
        camera.setPosition(0,-DISTANCE,0); camera.lookAt(0,0,0,0,0,1);
    }
    private ModelInstance3D surface(Color color) {
        return new ModelInstance3D().setModel(plane).setRotation((float)Math.PI/2,0,0)
            .setMaterial(new Material3D().setTint(color).setRoughness(1).setMetallic(.35f).setCastsShadow(false).setReceivesShadow(false).setCullBackFaces(false));
    }
    void clearSurfaces() {
        for(var surface:surfaces)scene.remove(surface);
        nodes.clear(); surfaces.clear();
    }
    void addSurface(UINode node) {
        var surface=surface(new Color(0xFF1A1721)); nodes.add(node); surfaces.add(surface); scene.add(surface);
    }
    void render(UINode slot,float scrollY,float time,boolean moving) {
        int w=Window.getWidth(), h=Window.getHeight(); if(w<1||h<1)return;
        float scale=2*DISTANCE*(float)Math.tan(Math.toRadians(FOV/2))/h;
        float dt=Math.max(0,Math.min(.05f,time-previousTime)); previousTime=time;
        float response=moving?1-(float)Math.exp(-dt*12):1;
        float mx=(Mouse.getX()-w*.5f)*scale, mz=(Mouse.getY()-h*.5f)*scale;
        lightX+=(mx-lightX)*response; lightZ+=(mz-lightZ)*response;
        pointer.setPosition(lightX,-1.7f,lightZ);
        background.setScale(w*scale*2,h*scale*2,1);
        for(int i=0;i<nodes.size();i++) {
            var node=nodes.get(i); var surface=surfaces.get(i);
            float top=node.getAbsoluteY()-scrollY;
            float hover=moving&&node instanceof ShowcaseArt art?art.depthHover():0;
            float depth=-hover*.15f, projected=scale*(DISTANCE+depth)/DISTANCE;
            float dx=Math.max(-1,Math.min(1,(Mouse.getX()-node.getAbsoluteX())/node.getWidth()*2-1));
            float dy=Math.max(-1,Math.min(1,(h-Mouse.getY()-top)/node.getHeight()*2-1));
            surface.setPosition((node.getAbsoluteX()+node.getWidth()/2-w*.5f)*projected,depth,(h*.5f-top-node.getHeight()/2+hover*5)*projected);
            surface.setRotation((float)Math.PI/2+dy*hover*.065f,0,-dx*hover*.045f);
            surface.setScale(node.getWidth()*projected,node.getHeight()*projected,1);
        }
        // Present the UI before decoding geometry, rather than blocking Application.init.
        if(slot!=null && tree==null && frames++>=2) {
            long started=System.nanoTime(); tree=new PackedTree(scene);
            System.out.println("Packed tree constructed in " + (System.nanoTime()-started)/1000000 + " ms");
        }
        if(tree!=null)for(var part:tree.parts)part.setVisible(slot!=null);
        if(tree!=null && slot!=null) {
            float top=slot.getAbsoluteY()-scrollY;
            float size=Math.min(slot.getWidth()*.94f/tree.width,slot.getHeight()*.82f/tree.height)*scale;
            for(var part:tree.parts) {
                part.setScale(size).setPosition((slot.getAbsoluteX()+slot.getWidth()*.5f-w*.5f)*scale,-.2f,(h*.5f-top-slot.getHeight()*.88f)*scale);
                part.setRotation(0,0,moving?mx*.025f+scrollY*.0002f:0);
            }
        }
        renderer.render(scene,camera);
    }
    @Override public void close(){renderer.close();if(tree!=null)tree.close();}
}
