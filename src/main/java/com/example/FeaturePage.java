package com.example;

/** Curated feature documentation and runnable API fragments for each showcase. */
record FeaturePage(String title, String summary, String details, String code) {
    String[] topics() {
        return switch(title) {
            case "A living world" -> new String[]{
                "Build the scene", "Separate reusable model geometry from instances.\nPlace, rotate and scale each instance without\nrebuilding the source mesh. Compose props and\nenvironments around a perspective camera.",
                "Give surfaces character", "Materials describe color, texture and roughness.\nMetallic surfaces respond differently to light.\nBalance geometry and texture detail against\nthe size of each object on screen.",
                "Design for the frame budget", "Load assets outside your update and render loops.\nReuse geometry for repeated objects and keep\nshadow-casting detail intentional. This page's\ntree is loaded once, then transformed in place."};
            case "Light & motion" -> new String[]{
                "Control the atmosphere", "Use light position, intensity and range to guide\nattention. Animate those properties in update\nso movement remains independent of frame rate.\nThe pointer light here uses the same scene.",
                "Shadows in two dimensions", "The 2D lighting system includes alpha-based\nsprite occluders and ground shadows. Elevation\nand sprite silhouettes help suggest depth in\na world built from flat artwork.",
                "Spend light deliberately", "Not every surface needs to cast a shadow.\nDecorative UI planes here do not: that avoids\nlarge dark bands across the interface. Limit\nlight range to the area that actually needs it."};
            case "Connected systems" -> new String[]{
                "A predictable lifecycle", "Create long-lived resources in init. Advance\nsimulation and input-driven state in update.\nRender the resulting state, then release owned\nresources in dispose when the application ends.",
                "Coordinate your game", "Keep gameplay state in Java objects shared by\nyour systems. A collision can trigger audio,\nan animation and a UI update without coupling\nthose systems to the drawing code.",
                "Keep updates lightweight", "Avoid loading textures or building meshes in\nthe frame loop. Reuse working objects where\npractical, profile your real scene and measure\nbefore introducing additional complexity."};
            case "Your next creation" -> new String[]{
                "Compose, then arrange", "Build a hierarchy of containers and controls.\nRows, columns, padding and flexible sizing\nkeep layout separate from content. Scroll panels\nhandle content larger than the available space.",
                "Interaction is part of UI", "Labels can opt into text selection. Buttons\nsupport actions and keyboard focus. Texture\nand Nano controls participate in the shared\nUI hierarchy instead of separate interfaces.",
                "A practical example", "These pages use the engine UI for navigation,\ntext, scrolling and code snippets. Copy buttons\ncopy the original snippet, including indentation;\nlong code lines scroll horizontally."};
            case "Spatial audio" -> new String[]{
                "Sound has a place", "Position sources relative to the listener for\nspatial playback. Use a localized source for\na machine or creature, and a sound area for\nambience that belongs to an entire region.",
                "Shape the listening area", "SoundArea supports circles, spheres, rectangles\nand boxes. A fade distance softens the boundary\nso entering a forest or leaving a room does not\nproduce an abrupt change in ambience.",
                "Choose how audio is loaded", "The audio package includes decoded sound data\nand streams for WAV, Ogg and MP3. Choose the\nappropriate playback path for short effects\nversus longer tracks and release owned players."};
            case "Physics & collisions" -> new String[]{
                "Describe physical behavior", "Body settings expose mass, friction, restitution\nand damping. Static, dynamic and kinematic\nmotion types let scenery, simulated objects\nand controlled actors play different roles.",
                "Ask the world questions", "Ray hits and contact events help connect\nsimulation to gameplay. Collision layers and\nsensors let you separate physical obstacles\nfrom areas that only detect an overlap.",
                "Keep simulation intentional", "Use simple collision shapes where possible.\nSleeping bodies reduce unnecessary activity;\ncontinuous collision is available for objects\nthat need protection against fast-motion misses."};
            case "Sprite animation" -> new String[]{
                "Make frames into movement", "Animation advances a sequence of frames using\nelapsed time. Forward, reverse and bidirectional\nplayback control frame order; looping and loop\nlimits independently control repetition.",
                "Respond to gameplay", "Pause, restart, change speed or play once for\nan attack or interaction. Frame-change, loop\nand completion callbacks connect visuals to\ngame events without guessing their timing.",
                "Reuse your artwork", "Keep sprite assets loaded and reuse animation\nframes rather than decoding artwork each tick.\nUpdate playback in the game loop and draw\nthe current frame through your texture batch."};
            default -> new String[]{
                "Choose how the world fits", "FitViewport preserves the world aspect ratio\nwith unused space where needed. FillViewport\nfills the display, while StretchViewport trades\naspect preservation for complete coverage.",
                "Separate world and screen", "ScreenViewport is useful for screen-space UI.\nPerspectiveViewport supports a 3D camera view.\nChoose the view appropriate to the content\ninstead of tying world units to display pixels.",
                "Handle size changes", "Update your viewport when the window changes\nsize and apply it before the matching draw pass.\nTest wide and narrow layouts: camera framing\nand readable UI need different decisions."};
        };
    }
    static final FeaturePage[] PAGES = {
        new FeaturePage("A living world", "Geometry, materials and cameras.",
            "Build a Scene3D from model instances.\nEach instance has its own transform and material.\nUse textures, roughness and metallic surfaces\nto give objects their character.\n\nA perspective camera frames the world;\nFilamentRenderer3D renders its geometry and lights.",
            "// Add a material-backed model to a scene\nvar scene = new Scene3D();\nvar material = new Material3D()\n    .setRoughness(0.7f);\nvar object = new ModelInstance3D()\n    .setModel(ModelBuilder3D.box(1, 1, 1))\n    .setMaterial(material);\nscene.add(object);"),
        new FeaturePage("Light & motion", "Shape a scene with responsive light.",
            "Point lights have a position, range and intensity.\nMove them during the application update to\nchange illumination across the scene.\n\nMaterials can cast and receive shadows.\nThis website uses a pointer-driven light,\nwith a restrained violet fill for contrast.",
            "// Move this light in your update callback\nvar light = new PointLight3D()\n    .setPosition(0, -2, 3)\n    .setIntensity(500)\n    .setRange(10)\n    .setCastsShadows(true);\nscene.addLight(light);"),
        new FeaturePage("Connected systems", "One application lifecycle.",
            "Use init to create resources, update for game\nlogic, render to draw, and dispose for cleanup.\n\nRendering, input, physics and spatial audio\ncan share the state of your Java application.\nKeep resource creation out of the frame loop\nand release owned resources when you finish.",
            "import valthorne.Application;\nimport valthorne.JGL;\n\npublic final class Game implements Application {\n    public static void main(String[] args) {\n        JGL.init(new Game(), \"My world\", 1280, 720);\n    }\n    public void init() {}\n    public void update(float delta) {}\n    public void render() {}\n    public void dispose() {}\n}"),
        new FeaturePage("Your next creation", "Compose an interface in Java.",
            "UIRoot owns layout and input dispatch.\nAdd labels, buttons, containers and scroll panels\nto build an interface that shares the engine.\n\nSelectable text and keyboard-focusable controls\nkeep the interface usable beyond the mouse.\nCall update and draw as part of your lifecycle.",
            "// Imports: valthorne.ui and ui.nodes.nano\nvar ui = new UIRoot();\nvar title = new NanoLabel(\"Hello, world\")\n    .fontSize(24)\n    .selectable(true);\nui.add(title);\n\n// In the application callbacks:\nui.update(delta);\nui.draw();\n// On shutdown: ui.dispose();"),
        new FeaturePage("Spatial audio", "Positioned sound and region-based ambience.",
            "Give your world an audible sense of space.\nUse spatial sources for individual emitters\nand sound areas for broader environments.",
            "import valthorne.audio.sound.SoundArea;\n\n// A spherical region with a soft boundary\nvar forest = SoundArea.sphere(0, 0, 0, 20, 5);\n// Sample its gain at a listener position\nfloat gain = forest.gainAt(12, 0, 0);"),
        new FeaturePage("Physics & collisions", "Rigid bodies, contacts and collision queries.",
            "Describe how objects move and interact.\nThe 3D physics API exposes bodies, shapes\nand world queries for gameplay simulation.",
            "// Imports: valthorne.math.physics\nvar settings = new BodySettings3D(\n    CollisionShape3D.box(1, 1, 1),\n    MotionType3D.DYNAMIC)\n    .setPosition(0, 0, 4)\n    .setMass(2)\n    .setFriction(0.6f)\n    .setRestitution(0.2f);\n// Add to your initialized PhysicsWorld3D:\nvar body = world.createBody(settings);"),
        new FeaturePage("Sprite animation", "Frame sequences with precise playback control.",
            "Turn sprite frames into reusable animation.\nControl time, direction and repetition, then\nconnect playback events to your game logic.",
            "// frames is your loaded AnimationFrame[]\nvar walk = new Animation(PlaybackMode.FORWARD, frames);\nwalk.setLooping(true);\nwalk.setSpeed(1.2f);\n\n// In update(float delta):\nwalk.update(delta);\n\n// When gameplay needs to stop movement:\nwalk.pause();\n// Resume with walk.play();"),
        new FeaturePage("Cameras & viewports", "Frame your world across different displays.",
            "Keep world dimensions separate from screen size.\nChoose a viewport policy for your game,\nthen update it as the window changes.",
            "import valthorne.viewport.FitViewport;\n\n// Keep a 1280 by 720 logical world\nvar viewport = new FitViewport(1280, 720);\n\n// On initialization and window resize:\nviewport.update(Window.getWidth(), Window.getHeight());\n\n// Before the matching draw pass:\nviewport.apply();")
    };
}
