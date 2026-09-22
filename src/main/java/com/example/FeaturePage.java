package com.example;

/**
 * Curated feature documentation and runnable API fragments for each showcase.
 */
record FeaturePage(String title, String summary, String highlights, String details, String code) {
    String wikiUrl() {
        String generated = WikiFeatureCatalog.wikiUrl(title);
        if (generated != null) return generated;
        String page = switch (title) {
            case "3D scenes & Filament" -> "04-Graphics-3D-Overview";
            case "2D & 3D lighting" -> "05-Lighting-Overview";
            case "Application lifecycle" -> "02-Core-Runtime";
            case "Retained UI system" -> "07-UI-System";
            case "Audio & ambient areas" -> "02-Core-Audio";
            case "3D physics with Jolt" -> "06-Physics-3D-Guide";
            case "Frame & transform animation" -> "03-Graphics-2D-Animation";
            case "Viewports & coordinates" -> "04-Graphics-Viewports";
            case "Asset loading & caching" -> "02-Core-Assets";
            case "Events & input" -> "02-Core-Events";
            case "Shaders & visual effects" -> "04-Graphics-Shaders";
            case "2D & 3D particles" -> "03-Graphics-2D-Particles";
            case "State machines & timing" -> "02-Core-State-Machines";
            case "Data structures & storage" -> "08-Data-Structures";
            case "Desktop & web platforms" -> "01-Start-Platforms";
            case "Diagnostics & UI inspection" -> "09-Developer-Diagnostics";
            default -> "Home";
        };
        return "https://github.com/tehnewb/Valthorne/wiki/" + page;
    }

    int artKind() {
        return WikiFeatureCatalog.artKind(title);
    }

    String[] topics() {
        String[] generated = WikiFeatureCatalog.topics(title);
        if (generated.length > 0) return generated;
        return switch (title) {
            case "3D scenes & Filament" -> new String[]{
                    "Build the scene", "Separate reusable model geometry from instances.\nPlace, rotate and scale each instance without\nrebuilding the source mesh. Compose props and\nenvironments around a perspective camera.",
                    "Give surfaces character", "Materials describe color, texture and roughness.\nMetallic surfaces respond differently to light.\nBalance geometry and texture detail against\nthe size of each object on screen.",
                    "Design for the frame budget", "Load assets outside your update and render loops.\nReuse geometry for repeated objects and keep\nshadow-casting detail intentional. This page's\ntree is loaded once, then transformed in place."};
            case "2D & 3D lighting" -> new String[]{
                    "Control the atmosphere", "Use light position, intensity and range to guide\nattention. Animate those properties in update\nso movement remains independent of frame rate.\nThe pointer light here uses the same scene.",
                    "Shadows in two dimensions", "The 2D lighting system includes alpha-based\nsprite occluders and ground shadows. Elevation\nand sprite silhouettes help suggest depth in\na world built from flat artwork.",
                    "Spend light deliberately", "Not every surface needs to cast a shadow.\nDecorative UI planes here do not: that avoids\nlarge dark bands across the interface. Limit\nlight range to the area that actually needs it."};
            case "Application lifecycle" -> new String[]{
                    "A predictable lifecycle", "Create long-lived resources in init. Advance\nsimulation and input-driven state in update.\nRender the resulting state, then release owned\nresources in dispose when the application ends.",
                    "Coordinate your game", "Keep gameplay state in Java objects shared by\nyour systems. A collision can trigger audio,\nan animation and a UI update without coupling\nthose systems to the drawing code.",
                    "Keep updates lightweight", "Avoid loading textures or building meshes in\nthe frame loop. Reuse working objects where\npractical, profile your real scene and measure\nbefore introducing additional complexity."};
            case "Retained UI system" -> new String[]{
                    "Compose, then arrange", "Build a hierarchy of containers and controls.\nRows, columns, padding and flexible sizing\nkeep layout separate from content. Scroll panels\nhandle content larger than the available space.",
                    "Interaction is part of UI", "Labels can opt into text selection. Buttons\nsupport actions and keyboard focus. Texture\nand Nano controls participate in the shared\nUI hierarchy instead of separate interfaces.",
                    "A practical example", "These pages use the engine UI for navigation,\ntext, scrolling and code snippets. Copy buttons\ncopy the original snippet, including indentation;\nlong code lines scroll horizontally."};
            case "Audio & ambient areas" -> new String[]{
                    "Sound has a place", "Position sources relative to the listener for\nspatial playback. Use a localized source for\na machine or creature, and a sound area for\nambience that belongs to an entire region.",
                    "Shape the listening area", "SoundArea supports circles, spheres, rectangles\nand boxes. A fade distance softens the boundary\nso entering a forest or leaving a room does not\nproduce an abrupt change in ambience.",
                    "Choose how audio is loaded", "The audio package includes decoded sound data\nand streams for WAV, Ogg and MP3. Choose the\nappropriate playback path for short effects\nversus longer tracks and release owned players."};
            case "3D physics with Jolt" -> new String[]{
                    "Describe physical behavior", "Body settings expose mass, friction, restitution\nand damping. Static, dynamic and kinematic\nmotion types let scenery, simulated objects\nand controlled actors play different roles.",
                    "Ask the world questions", "Ray hits and contact events help connect\nsimulation to gameplay. Collision layers and\nsensors let you separate physical obstacles\nfrom areas that only detect an overlap.",
                    "Keep simulation intentional", "Use simple collision shapes where possible.\nSleeping bodies reduce unnecessary activity;\ncontinuous collision is available for objects\nthat need protection against fast-motion misses."};
            case "Frame & transform animation" -> new String[]{
                    "Make frames into movement", "Animation advances a sequence of frames using\nelapsed time. Forward, reverse and bidirectional\nplayback control frame order; looping and loop\nlimits independently control repetition.",
                    "Respond to gameplay", "Pause, restart, change speed or play once for\nan attack or interaction. Frame-change, loop\nand completion callbacks connect visuals to\ngame events without guessing their timing.",
                    "Reuse your artwork", "Keep sprite assets loaded and reuse animation\nframes rather than decoding artwork each tick.\nUpdate playback in the game loop and draw\nthe current frame through your texture batch."};
            case "Asset loading & caching" -> new String[]{
                    "Virtual-thread loads", "Assets.loadAsync returns a typed future. Each\nuncached decoder runs on the owned virtual-thread\nexecutor, while stable keys deduplicate requests.",
                    "Prepared batch progress", "Queue parameter objects with prepare, call load,\nand observe progress for loading screens. Direct\nloads are intentionally a separate workflow.",
                    "GPU handoff", "A completed future is still off the graphics thread.\nUse JGL.runTask before creating textures, shaders,\nfont atlases, framebuffers or other GPU objects."};
            case "Events & input" -> new String[]{
                    "Typed routes", "EventType and EventTypes associate a handler with\nthe intended payload. Keyboard and pointer adapters\nlet a listener implement only the callbacks it needs.",
                    "Ordered synchronous dispatch", "Priority and insertion order make publication\ndeterministic. Handlers run on the publishing thread;\nthey are not automatically moved to rendering.",
                    "Consume and unsubscribe", "Consume an accepted interaction to stop unwanted\ndownstream handling. Retain the original handler\nobject so cleanup can unregister that same identity."};
            case "Shaders & visual effects" -> new String[]{
                    "Program lifecycle", "Shader classes compile, link, bind, configure uniforms\nand dispose programs. Uniform locations belong to\nthe linked program and can stale after relinking.",
                    "Packaged sources and effects", "ShaderSources loads packaged text and templates.\nSpecialized programs cover blur, glow, outline, flash,\nburn, reflection, water, meshes and textured quads.",
                    "Renderer contracts", "Match vertex layout, matrices, texture units and image\nbindings to the renderer supplying the data. Flush\nincompatible queued draws before changing programs."};
            case "2D & 3D particles" -> new String[]{
                    "Emitter and system", "An emitter describes the effect while ParticleSystem\ncoordinates active effects and updates reusable\nposition, motion, lifetime and visual state.",
                    "Spawn distributions", "Point, line, box, circle, cone, ring, rectangle-edge,\nspiral and radial-burst samplers create distinct\nbirth geometry without sampling in the update loop.",
                    "Borrowed visuals", "Recycled particles are mutable and not permanent game\nobjects. Textures remain separately owned and must\noutlive every active particle that references them."};
            case "State machines & timing" -> new String[]{
                    "States and context", "State objects receive enter, update and exit callbacks\nwith shared application context. The machine does not\nreplace scenes or manage graphics resources.",
                    "Guards and triggers", "Predicates control eligibility; named or retained\ntriggers represent discrete requests such as jump.\nTransitions can compete by priority and dwell time.",
                    "Synchronous selection", "Fire triggers from gameplay, then call update from one\nsimulation owner. Keep rendering outside transition\nselection and avoid uncontrolled reentrant changes."};
            case "Data structures & storage" -> new String[]{
                    "Primitive containers", "Fast arrays and stacks avoid boxing where measured hot\npaths need compact primitive storage. Pools and caches\nmake reuse policy explicit rather than automatic.",
                    "Specialized structures", "StringObjectMap, numeric UUID reuse queues and\nIntBinaryTree have documented sentinel, capacity and\nedge-case behavior distinct from java.util types.",
                    "Storage utilities", "Buffers, bits, settings, files, compression and encryption\ncover common persistence work. Ownership and validation\nremain the application's responsibility."};
            case "Desktop & web platforms" -> new String[]{
                    "Java 25 desktop library", "Native dependencies are published transitively and\nselected at runtime. Raster renderers require OpenGL\n3.3; platform-specific renderer support still varies.",
                    "TeaVM web target", "A separate portable build supplies browser backends for\nsupported engine APIs, including WebGL2 path tracing,\nWebGPU compute and persistent Java file operations.",
                    "Shared source, real differences", "Prefer engine operations in portable application code.\nRaw native calls stay desktop-specific, and browser\nwindow, file and graphics semantics are not identical."};
            case "Diagnostics & UI inspection" -> new String[]{
                    "PerformanceOverlay", "Display timing and rendering information while\nreproducing a representative interaction. Measurement\noverhead matters when comparing runs.",
                    "UIFrameStats", "Separate layout work from draw traversal. A high layout\ncount and high draw count indicate different problems\nand should not be collapsed into one metric.",
                    "UIInspector", "Inspect recorded node bounds, clipping, focus, capture and\nstyle data. Entries may retain live node references,\nso snapshots do not extend disposed node lifetimes."};
            default -> new String[]{
                    "Choose how the world fits", "FitViewport preserves the world aspect ratio\nwith unused space where needed. FillViewport\nfills the display, while StretchViewport trades\naspect preservation for complete coverage.",
                    "Separate world and screen", "ScreenViewport is useful for screen-space UI.\nPerspectiveViewport supports a 3D camera view.\nChoose the view appropriate to the content\ninstead of tying world units to display pixels.",
                    "Handle size changes", "Update your viewport when the window changes\nsize and apply it before the matching draw pass.\nTest wide and narrow layouts: camera framing\nand readable UI need different decisions."};
        };
    }

    static final FeaturePage[] LEGACY_PAGES = {
            new FeaturePage("3D scenes & Filament", "Scene3D, model instances, PBR materials and culling.", "SCENE3D  ·  FILAMENT  ·  MODELINSTANCE3D  ·  OPENGL 3.3",
                    "Build a Scene3D from model instances.\nEach instance has its own transform and material.\nUse textures, roughness and metallic surfaces\nto give objects their character.\n\nA perspective camera frames the world;\nFilamentRenderer3D renders its geometry and lights.",
                    "// Add a material-backed model to a scene\nvar scene = new Scene3D();\nvar material = new Material3D()\n    .setRoughness(0.7f);\nvar object = new ModelInstance3D()\n    .setModel(ModelBuilder3D.box(1, 1, 1))\n    .setMaterial(material);\nscene.add(object);"),
            new FeaturePage("2D & 3D lighting", "OpenGL 3.3 lighting, tiled shadows and compatibility APIs.", "LIGHTING2D  ·  LIGHTING3D  ·  SHADOWS  ·  FPS OVERLAY",
                    "Point lights have a position, range and intensity.\nMove them during the application update to\nchange illumination across the scene.\n\nMaterials can cast and receive shadows.\nThis website uses a pointer-driven light,\nwith a restrained violet fill for contrast.",
                    "// Move this light in your update callback\nvar light = new PointLight3D()\n    .setPosition(0, -2, 3)\n    .setIntensity(500)\n    .setRange(10)\n    .setCastsShadows(true);\nscene.addLight(light);"),
            new FeaturePage("Application lifecycle", "JGL drives init, update, render and dispose callbacks.", "APPLICATION  ·  JGL  ·  WINDOW  ·  MAIN-THREAD TASKS",
                    "Use init to create resources, update for game\nlogic, render to draw, and dispose for cleanup.\n\nRendering, input, physics and spatial audio\ncan share the state of your Java application.\nKeep resource creation out of the frame loop\nand release owned resources when you finish.",
                    "import valthorne.Application;\nimport valthorne.JGL;\n\npublic final class Game implements Application {\n    public static void main(String[] args) {\n        JGL.init(new Game(), \"My world\", 1280, 720);\n    }\n    public void init() {}\n    public void update(float delta) {}\n    public void render() {}\n    public void dispose() {}\n}"),
            new FeaturePage("Retained UI system", "One mixed texture and NanoVG tree with shared behavior.", "UIROOT  ·  YOGA LAYOUT  ·  TEXT EDITING  ·  VIRTUAL GRIDS",
                    "UIRoot owns layout and input dispatch.\nAdd labels, buttons, containers and scroll panels\nto build an interface that shares the engine.\n\nSelectable text and keyboard-focusable controls\nkeep the interface usable beyond the mouse.\nCall update and draw as part of your lifecycle.",
                    "// Imports: valthorne.ui and ui.nodes.nano\nvar ui = new UIRoot();\nvar title = new NanoLabel(\"Hello, world\")\n    .fontSize(24)\n    .selectable(true);\nui.add(title);\n\n// In the application callbacks:\nui.update(delta);\nui.draw();\n// On shutdown: ui.dispose();"),
            new FeaturePage("Audio & ambient areas", "Buffered or streamed playback with shaped area gain.", "SOUNDPLAYER  ·  STREAMING  ·  SOUND AREA  ·  OPENAL THREAD",
                    "Give your world an audible sense of space.\nUse spatial sources for individual emitters\nand sound areas for broader environments.",
                    "import valthorne.audio.sound.SoundArea;\n\n// A spherical region with a soft boundary\nvar forest = SoundArea.sphere(0, 0, 0, 20, 5);\n// Sample its gain at a listener position\nfloat gain = forest.gainAt(12, 0, 0);"),
            new FeaturePage("3D physics with Jolt", "Native Jolt 6.0 bodies, collision groups and filtered rays.", "PHYSICSWORLD3D  ·  JOLT JNI  ·  Z-UP  ·  16 GROUPS",
                    "Describe how objects move and interact.\nThe 3D physics API exposes bodies, shapes\nand world queries for gameplay simulation.",
                    "// Imports: valthorne.math.physics\nvar settings = new BodySettings3D(\n    CollisionShape3D.box(1, 1, 1),\n    MotionType3D.DYNAMIC)\n    .setPosition(0, 0, 4)\n    .setMass(2)\n    .setFriction(0.6f)\n    .setRestitution(0.2f);\n// Add to your initialized PhysicsWorld3D:\nvar body = world.createBody(settings);"),
            new FeaturePage("Frame & transform animation", "Timed sprite frames and interpolated 3D poses.", "FRAME DURATIONS  ·  PLAYBACK MODES  ·  LISTENERS  ·  QUATERNIONS",
                    "Turn sprite frames into reusable animation.\nControl time, direction and repetition, then\nconnect playback events to your game logic.",
                    "// frames is your loaded AnimationFrame[]\nvar walk = new Animation(PlaybackMode.FORWARD, frames);\nwalk.setLooping(true);\nwalk.setSpeed(1.2f);\n\n// In update(float delta):\nwalk.update(delta);\n\n// When gameplay needs to stop movement:\nwalk.pause();\n// Resume with walk.play();"),
            new FeaturePage("Viewports & coordinates", "Fit, fill, stretch, screen and perspective policies.", "FIT  ·  FILL  ·  SCREEN/WORLD CONVERSION  ·  HIGH DPI",
                    "Keep world dimensions separate from screen size.\nChoose a viewport policy for your game,\nthen update it as the window changes.",
                    "import valthorne.viewport.FitViewport;\n\n// Keep a 1280 by 720 logical world\nvar viewport = new FitViewport(1280, 720);\n\n// On initialization and window resize:\nviewport.update(Window.getWidth(), Window.getHeight());\n\n// Before the matching draw pass:\nviewport.apply();"),
            new FeaturePage("Asset loading & caching", "Virtual-thread decoding, keyed futures and batch progress.", "ASSETS  ·  LOADASYNC  ·  PREPARED BATCHES  ·  JGL.RUNTASK",
                    "Assets caches asynchronous CPU-side loads by stable asset key.\nUncached work uses a virtual-thread-per-task executor;\nprepared batches expose progress for loading screens.\n\nGPU objects must still be created on the engine thread\nwith JGL.runTask after decoding completes.",
                    "// Decode off-thread, then hand GPU creation to JGL\nvalthorne.asset.Assets.loadAsync(\n    valthorne.graphics.texture.TextureParameters\n        .fromClasspath(\"ui/bg.png\", \"background\"),\n    valthorne.graphics.texture.TextureData.class\n).thenAccept(data -> JGL.runTask(() -> {\n    // Create the Texture here on the engine thread.\n}));"),
            new FeaturePage("Events & input", "Typed routes, ordered handlers and consumable events.", "EVENTPUBLISHER  ·  EVENTTYPES  ·  PRIORITIES  ·  CONSUMPTION",
                    "EventPublisher keeps deterministic subscriptions ordered by\npriority and insertion sequence. Dispatch is synchronous,\nand accepted interactions can be consumed before later handlers.\n\nRetain handler identity so the same object can be unsubscribed\nduring screen or component cleanup.",
                    "// In update(float delta)\nif (Keyboard.isKeyDown(Keyboard.W)) {\n    x += speed * delta;\n}"),
            new FeaturePage("Shaders & visual effects", "Managed programs for raster, effects and compute work.", "SHADERSOURCES  ·  UNIFORMS  ·  COMPUTE  ·  BLUR/GLOW/WATER",
                    "Shader classes manage compilation, linking, binding and uniforms.\nSpecialized programs cover textured quads, meshes, shapes,\nblur, glow, outline, flash, burn, reflection and water.\n\nComputeShader requires the appropriate OpenGL capability;\nrenderer vertex layouts must match each shader contract.",
                    "// Keep render work in render(), after resources are ready\nWindow.clear(new Color(0xFF101018));\nviewport.apply();\n// Bind your initialized shader for its matching pass."),
            new FeaturePage("2D & 3D particles", "Reusable emitters with documented spawn distributions.", "PARTICLESYSTEM  ·  POINT/LINE/BOX  ·  RING/CONE  ·  REUSE",
                    "ParticleSystem coordinates emitters and reusable mutable particles.\nPoint, line, box, circle, cone, ring, rectangle-edge, spiral\nand radial-burst distributions define where particles begin.\n\nShared textures remain separately owned and must outlive\nevery active particle that borrows them.",
                    "// Advance effect state with frame time\ntime += delta;\nfloat life = Math.min(1, time / 2);\nfloat opacity = 1 - life;\n// Apply opacity to particles in the matching render pass."),
            new FeaturePage("State machines & timing", "Guarded transitions with triggers, priorities and dwell time.", "STATE  ·  GUARDS  ·  TRIGGERS  ·  TRANSITION ACTIONS",
                    "A state receives enter, update and exit callbacks plus shared\ncontext. Global and local transitions can use guards, named\ntriggers, priority and minimum dwell time.\n\nEvaluation is synchronous; one simulation owner calls update\nand rendering remains outside transition selection.",
                    "// Keep transitions explicit, even in a small state model\nint PLAYING = 1;\nint state = PLAYING;\ntime += delta;\nif (state == PLAYING && time > 30) time = 0;"),
            new FeaturePage("Data structures & storage", "Primitive containers, ID reuse, files and settings.", "FAST ARRAYS/STACKS  ·  STRINGOBJECTMAP  ·  UUID QUEUES  ·  BUFFERS",
                    "The wiki documents primitive arrays and stacks, caches, pools,\nbit helpers, buffers, compression, encryption, files and settings.\nSpecialized StringObjectMap and numeric ID reuse queues have\nexplicit capacity, sentinel and ownership contracts.\n\nUse their documented behavior rather than assuming they match\nsimilarly named java.util collections.",
                    "// Reuse storage in allocation-sensitive code\nbyte[] scratch = new byte[4096];\nint activeBytes = 0;\njava.util.Arrays.fill(scratch, 0, activeBytes, (byte) 0);"),
            new FeaturePage("Desktop & web platforms", "Java 25 desktop targets plus a separate TeaVM web runtime.", "JAVA 25  ·  OPENGL 3.3  ·  TEAVM  ·  WEBGL2/WEBGPU",
                    "Desktop native artifacts are selected at runtime for supported\nWindows, Linux and macOS architectures. The separate TeaVM\ntarget supplies browser backends for supported engine APIs.\n\nThe web runtime covers Jolt, Filament/raster 3D, lighting,\nparticles, audio, fonts, UI, assets and Tiled maps, while raw\nnative calls and some platform semantics remain target-specific.",
                    "Application app = new Application() {\n    public void init() {}\n    public void update(float delta) {}\n    public void render() {}\n    public void dispose() {}\n};\nJGL.init(app, \"My game\", 1280, 720);"),
            new FeaturePage("Diagnostics & UI inspection", "Frame counters, overlays and inspectable UI snapshots.", "PERFORMANCEOVERLAY  ·  UIFRAMESTATS  ·  UIINSPECTOR  ·  CLIPPING",
                    "PerformanceOverlay displays timing and render information.\nUIFrameStats separates draw and layout work; UIInspector records\nnode bounds, clipping, focus, capture and selected style values.\n\nCompare equivalent scenes and viewport settings, and disable\nexpensive diagnostics when the investigation is complete.",
                    "long started = System.nanoTime();\n// Run the operation being measured\nlong micros = (System.nanoTime() - started) / 1_000;\nSystem.out.println(\"Operation: \" + micros + \" µs\");")
    };

    static final FeaturePage[] PAGES = WikiFeatureCatalog.PAGES;
}
