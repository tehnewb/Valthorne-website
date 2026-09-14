package com.example;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/** Compiles the exact displayed fragments; never starts graphics, physics or audio. */
public final class SnippetCompilationCheck {
    private static final String IMPORTS = """
        import valthorne.*;
        import valthorne.graphics.Color;
        import valthorne.graphics.model.*;
        import valthorne.graphics.lighting2d.*;
        import valthorne.graphics.animation.*;
        import valthorne.audio.sound.*;
        import valthorne.math.physics.*;
        import valthorne.viewport.*;
        import valthorne.ui.*;
        import valthorne.ui.nodes.nano.*;
        import valthorne.ui.behavior.TextEditing;
        import org.joml.Vector3f;
        """;
    private static final String CONTEXT = """
        Scene3D scene; ModelInstance3D object; PointLight3D light;
        UIRoot ui; Animation walk; AnimationFrame[] frames;
        SoundData data; SoundPlayer player; PhysicsWorld3D world;
        RigidBody3D body; Viewport viewport; Vector3f positionBuffer;
        float time, delta, x, speed;
        """;
    public static void main(String[] args) throws Exception {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("A full JDK is required");
        List<JavaFileObject> sources = new ArrayList<>();
        for (int page = 0; page < FeaturePage.PAGES.length; page++) {
            add(sources, FeaturePage.PAGES[page].code());
            var examples = FeatureExamples.forPage(page);
            if (examples.length < 4) throw new AssertionError("Too few examples: " + page);
            for (var example : examples) {
                if (example.title().isBlank() || example.note().isBlank())
                    throw new AssertionError("Missing example guidance");
                add(sources, example.code());
            }
        }
        Path output = Path.of("build", "snippet-check");
        Files.createDirectories(output);
        try (var manager = compiler.getStandardFileManager(null, null, null)) {
            var options = List.of("-proc:none", "-classpath", System.getProperty("java.class.path"), "-d", output.toString());
            if (!compiler.getTask(null, manager, null, options, null, sources).call())
                throw new AssertionError("Displayed code snippets failed compilation");
        }
        System.out.println("Compiled " + sources.size() + " displayed snippets against the engine API.");
    }
    private static void add(List<JavaFileObject> sources, String code) {
        String name = "Snippet" + sources.size();
        String source;
        if (code.contains("public final class Game")) {
            name = "Game";
            source = code;
        } else {
            String body = code.replaceAll("(?m)^import [^;]+;\\s*", "");
            source = IMPORTS + "class " + name + " {\n" + CONTEXT + "void check() {\n" + body + "\n}}";
        }
        final String content = source;
        sources.add(new SimpleJavaFileObject(URI.create("string:///" + name + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) { return content; }
        });
    }
}
