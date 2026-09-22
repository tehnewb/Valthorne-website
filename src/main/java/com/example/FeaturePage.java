package com.example;

/**
 * Curated feature documentation and runnable API fragments for each showcase.
 */
record FeaturePage(String title, String summary, String highlights, String details, String code) {
    String[] topics() {
        return switch (title) {
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
            case "Assets & content" -> new String[]{
                    "Load once", "Resolve packaged assets during initialization.\nReuse decoded textures, models, fonts and audio\ninstead of rebuilding them in frame callbacks.",
                    "Prepare runtime data", "Files, buffers and compression support custom\ncontent pipelines. Validate data at the boundary\nbefore gameplay systems consume it.",
                    "Track ownership", "Dispose native and GPU resources with the scene\nor application that owns them. Removing one\ninstance need not destroy a shared asset."};
            case "Input & events" -> new String[]{
                    "Read continuous state", "Poll keyboard and mouse state for movement and\ncamera control. Scale continuous actions by delta\ntime for consistent behavior across frame rates.",
                    "React to transitions", "Listeners handle presses, releases, movement and\nfocus changes. Routed events let the accepting\nlayer prevent duplicate handling below it.",
                    "Map actions", "Translate physical inputs into gameplay actions.\nCentral mappings make rebinding, controllers and\nautomated input tests easier to support."};
            case "Shaders & rendering" -> new String[]{
                    "Choose a rendering path", "Use batched 2D drawing, custom OpenGL shaders or\nFilament's physically based 3D renderer according\nto the visual job and target capabilities.",
                    "Manage GPU state", "Create shaders and buffers outside the render loop.\nGroup compatible work and restore shared state\nwhen custom passes meet engine renderers.",
                    "Check capabilities", "Hardware and browser support varies. Query optional\nfeatures and provide graceful fallbacks instead\nof assuming every target is identical."};
            case "Particles & effects" -> new String[]{
                    "Describe emitters", "Spawn rate, lifetime, velocity, size and color turn\none particle system into smoke, sparks, weather\nor impact effects through configuration.",
                    "Work in 2D or 3D", "Sprite particles fit texture-batched scenes while\n3D particles use world placement and depth. Pick\nthe system that matches the effect's space.",
                    "Control the budget", "Cap live particles, pool reusable objects and limit\nlarge translucent layers. Effects should reinforce\ngameplay without consuming the frame."};
            case "State & timing" -> new String[]{
                    "Model explicit states", "State machines keep menus, actors and game flow in\nnamed transitions with clear enter, update and exit\nbehavior instead of scattered boolean flags.",
                    "Use the right clock", "Timers and fixed-step helpers separate presentation\ntime from simulation time for cooldowns, physics\nand deterministic gameplay work.",
                    "Broadcast transitions", "Events can notify audio, UI and analytics about a\nstate change without coupling those consumers to\nthe state owner's internal rules."};
            case "Data & utilities" -> new String[]{
                    "Purpose-built structures", "Primitive arrays, stacks, bits, caches and pools\nreduce boxing and allocation in measured hot paths\nwhile keeping common operations concise.",
                    "Persistence foundations", "Settings, buffers, files, compression and encryption\ncover common storage needs. Version and validate data\nbefore exposing it to the rest of the game.",
                    "Shared math and helpers", "Focused utilities support engine and application code\nwithout requiring direct platform or renderer access.\nUse the narrowest abstraction that fits."};
            case "Platforms & portability" -> new String[]{
                    "Desktop and browser", "LWJGL powers desktop builds while the portable runtime\nexports supported engine APIs to the web from the\nsame application-oriented Java structure.",
                    "Hide platform assumptions", "Prefer engine operations over direct native calls in\nshared code. Isolate unavoidable platform behavior\nand test every target early.",
                    "Package deliberately", "Ship native libraries, resources and launch settings\nas part of the distribution. Reproduce clean builds\ninstead of depending on an IDE classpath."};
            case "Diagnostics & performance" -> new String[]{
                    "Measure first", "Profile update, rendering and loading independently.\nCounters and timers identify real bottlenecks before\noptimization makes the code more complicated.",
                    "Audit lifetimes", "Track native handles, listeners, scene membership and\npooled values from creation through cleanup. Stale\nregistrations often surface as later frame bugs.",
                    "Scale with evidence", "Batch work, reuse buffers and cull hidden geometry\nwhere measurements justify it. Keep code outside hot\npaths direct and readable."};
            default -> new String[]{
                    "Choose how the world fits", "FitViewport preserves the world aspect ratio\nwith unused space where needed. FillViewport\nfills the display, while StretchViewport trades\naspect preservation for complete coverage.",
                    "Separate world and screen", "ScreenViewport is useful for screen-space UI.\nPerspectiveViewport supports a 3D camera view.\nChoose the view appropriate to the content\ninstead of tying world units to display pixels.",
                    "Handle size changes", "Update your viewport when the window changes\nsize and apply it before the matching draw pass.\nTest wide and narrow layouts: camera framing\nand readable UI need different decisions."};
        };
    }

    static final FeaturePage[] PAGES = {
            new FeaturePage("A living world", "Models, materials and complete 3D scenes.", "MODELS  ·  MATERIALS  ·  SCENES  ·  CULLING",
                    "Build a Scene3D from model instances.\nEach instance has its own transform and material.\nUse textures, roughness and metallic surfaces\nto give objects their character.\n\nA perspective camera frames the world;\nFilamentRenderer3D renders its geometry and lights.",
                    "// Add a material-backed model to a scene\nvar scene = new Scene3D();\nvar material = new Material3D()\n    .setRoughness(0.7f);\nvar object = new ModelInstance3D()\n    .setModel(ModelBuilder3D.box(1, 1, 1))\n    .setMaterial(material);\nscene.add(object);"),
            new FeaturePage("Light & motion", "2D, 3D and raycast lighting systems.", "SHADOWS  ·  RADIANCE  ·  RAYCASTING  ·  STUDIO",
                    "Point lights have a position, range and intensity.\nMove them during the application update to\nchange illumination across the scene.\n\nMaterials can cast and receive shadows.\nThis website uses a pointer-driven light,\nwith a restrained violet fill for contrast.",
                    "// Move this light in your update callback\nvar light = new PointLight3D()\n    .setPosition(0, -2, 3)\n    .setIntensity(500)\n    .setRange(10)\n    .setCastsShadows(true);\nscene.addLight(light);"),
            new FeaturePage("Connected systems", "Runtime, scenes and the application lifecycle.", "RUNTIME  ·  SCENES  ·  PLUGINS  ·  GAME FLOW",
                    "Use init to create resources, update for game\nlogic, render to draw, and dispose for cleanup.\n\nRendering, input, physics and spatial audio\ncan share the state of your Java application.\nKeep resource creation out of the frame loop\nand release owned resources when you finish.",
                    "import valthorne.Application;\nimport valthorne.JGL;\n\npublic final class Game implements Application {\n    public static void main(String[] args) {\n        JGL.init(new Game(), \"My world\", 1280, 720);\n    }\n    public void init() {}\n    public void update(float delta) {}\n    public void render() {}\n    public void dispose() {}\n}"),
            new FeaturePage("Your next creation", "A complete retained UI system in Java.", "LAYOUT  ·  CONTROLS  ·  THEMES  ·  DATA BINDING",
                    "UIRoot owns layout and input dispatch.\nAdd labels, buttons, containers and scroll panels\nto build an interface that shares the engine.\n\nSelectable text and keyboard-focusable controls\nkeep the interface usable beyond the mouse.\nCall update and draw as part of your lifecycle.",
                    "// Imports: valthorne.ui and ui.nodes.nano\nvar ui = new UIRoot();\nvar title = new NanoLabel(\"Hello, world\")\n    .fontSize(24)\n    .selectable(true);\nui.add(title);\n\n// In the application callbacks:\nui.update(delta);\nui.draw();\n// On shutdown: ui.dispose();"),
            new FeaturePage("Spatial audio", "Playback, positioned sound and ambience.", "WAV  ·  OGG  ·  MP3  ·  SPATIAL AREAS",
                    "Give your world an audible sense of space.\nUse spatial sources for individual emitters\nand sound areas for broader environments.",
                    "import valthorne.audio.sound.SoundArea;\n\n// A spherical region with a soft boundary\nvar forest = SoundArea.sphere(0, 0, 0, 20, 5);\n// Sample its gain at a listener position\nfloat gain = forest.gainAt(12, 0, 0);"),
            new FeaturePage("Physics & collisions", "Jolt-powered rigid bodies and world queries.", "BODIES  ·  SHAPES  ·  CONTACTS  ·  RAY CASTS",
                    "Describe how objects move and interact.\nThe 3D physics API exposes bodies, shapes\nand world queries for gameplay simulation.",
                    "// Imports: valthorne.math.physics\nvar settings = new BodySettings3D(\n    CollisionShape3D.box(1, 1, 1),\n    MotionType3D.DYNAMIC)\n    .setPosition(0, 0, 4)\n    .setMass(2)\n    .setFriction(0.6f)\n    .setRestitution(0.2f);\n// Add to your initialized PhysicsWorld3D:\nvar body = world.createBody(settings);"),
            new FeaturePage("Sprite animation", "Textures, sprites, maps, fonts and animation.", "SPRITES  ·  TILED MAPS  ·  FONTS  ·  PLAYBACK",
                    "Turn sprite frames into reusable animation.\nControl time, direction and repetition, then\nconnect playback events to your game logic.",
                    "// frames is your loaded AnimationFrame[]\nvar walk = new Animation(PlaybackMode.FORWARD, frames);\nwalk.setLooping(true);\nwalk.setSpeed(1.2f);\n\n// In update(float delta):\nwalk.update(delta);\n\n// When gameplay needs to stop movement:\nwalk.pause();\n// Resume with walk.play();"),
            new FeaturePage("Cameras & viewports", "Frame 2D and 3D worlds on any display.", "CAMERAS  ·  FIT  ·  FILL  ·  SCREEN SPACE",
                    "Keep world dimensions separate from screen size.\nChoose a viewport policy for your game,\nthen update it as the window changes.",
                    "import valthorne.viewport.FitViewport;\n\n// Keep a 1280 by 720 logical world\nvar viewport = new FitViewport(1280, 720);\n\n// On initialization and window resize:\nviewport.update(Window.getWidth(), Window.getHeight());\n\n// Before the matching draw pass:\nviewport.apply();"),
            new FeaturePage("Assets & content", "Resource loading and production-ready content flow.", "ASSETS  ·  FILES  ·  BUFFERS  ·  COMPRESSION",
                    "Load packaged resources predictably and keep them reusable.\nValthorne provides asset, file and buffer utilities for\ntextures, models, audio and custom runtime formats.",
                    "// Resolve a packaged resource without a machine-specific path\nvar loader = Thread.currentThread().getContextClassLoader();\ntry (var stream = loader.getResourceAsStream(\"data/level.bin\")) {\n    if (stream == null) throw new IllegalStateException(\"Missing level\");\n    byte[] bytes = stream.readAllBytes();\n} catch (java.io.IOException error) {\n    throw new IllegalStateException(\"Could not load level\", error);\n}"),
            new FeaturePage("Input & events", "Keyboard, mouse and routed interaction.", "KEYBOARD  ·  MOUSE  ·  LISTENERS  ·  ROUTING",
                    "Combine immediate device state with discrete event listeners.\nUse continuous input for movement and routed events for\nUI actions, focus and transitions.",
                    "// In update(float delta)\nif (Keyboard.isKeyDown(Keyboard.W)) {\n    x += speed * delta;\n}"),
            new FeaturePage("Shaders & rendering", "Custom GPU work and multiple rendering paths.", "OPENGL  ·  FILAMENT  ·  SHADERS  ·  CAPABILITIES",
                    "Build custom shader effects beside batched 2D drawing and\nphysically based Filament scenes. Capability checks help\nselect features supported by each platform.",
                    "// Keep render work in render(), after resources are ready\nWindow.clear(new Color(0xFF101018));\nviewport.apply();\n// Bind your initialized shader for its matching pass."),
            new FeaturePage("Particles & effects", "Reusable 2D and 3D effect systems.", "EMITTERS  ·  LIFETIMES  ·  POOLING  ·  3D EFFECTS",
                    "Describe emitters with spawn, motion, color and lifetime data.\nUse lightweight 2D particles for sprites or scene-aware\n3D particles when effects need depth.",
                    "// Advance effect state with frame time\ntime += delta;\nfloat life = Math.min(1, time / 2);\nfloat opacity = 1 - life;\n// Apply opacity to particles in the matching render pass."),
            new FeaturePage("State & timing", "State machines, clocks and deterministic flow.", "STATES  ·  TIMERS  ·  TRANSITIONS  ·  FIXED STEPS",
                    "Keep menus, actors and game flow in explicit states.\nTiming helpers coordinate cooldowns, transitions and\nfixed-step simulation without scattered counters.",
                    "// Keep transitions explicit, even in a small state model\nint PLAYING = 1;\nint state = PLAYING;\ntime += delta;\nif (state == PLAYING && time > 30) time = 0;"),
            new FeaturePage("Data & utilities", "Focused structures for real-time applications.", "COLLECTIONS  ·  CACHE  ·  SETTINGS  ·  ENCRYPTION",
                    "Primitive collections, pools, caches, settings, files, math,\ncompression and encryption cover the less visible systems\nthat keep a game reliable and responsive.",
                    "// Reuse storage in allocation-sensitive code\nbyte[] scratch = new byte[4096];\nint activeBytes = 0;\njava.util.Arrays.fill(scratch, 0, activeBytes, (byte) 0);"),
            new FeaturePage("Platforms & portability", "Desktop and web from a shared Java codebase.", "LWJGL  ·  WEB EXPORT  ·  NATIVES  ·  FALLBACKS",
                    "Keep platform details behind engine APIs and package resources\nfor each target. The portable runtime brings supported\nValthorne applications to the browser.",
                    "Application app = new Application() {\n    public void init() {}\n    public void update(float delta) {}\n    public void render() {}\n    public void dispose() {}\n};\nJGL.init(app, \"My game\", 1280, 720);"),
            new FeaturePage("Diagnostics & performance", "Tools and practices for production scale.", "PROFILING  ·  CULLING  ·  BATCHING  ·  LIFETIMES",
                    "Measure frame work, audit resource lifetimes and optimize the\npaths that matter. Valthorne exposes the pieces needed to\ninspect rendering, UI and runtime behavior.",
                    "long started = System.nanoTime();\n// Run the operation being measured\nlong micros = (System.nanoTime() - started) / 1_000;\nSystem.out.println(\"Operation: \" + micros + \" µs\");")
    };
}
