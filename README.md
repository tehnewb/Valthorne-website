# Valthorne landing page

A Java 25 application using Valthorne UI nodes, vector drawing and an embedded
Filament 3D scene. The application layout and interactions are implemented in Java.

## Run

```text
gradlew.bat run
```

Open this folder in IntelliJ as a Gradle project. The included build uses the sibling
`../Valthorne` checkout so engine UI and web-port changes can be tested together.

## Export to web

```text
gradlew.bat webExport
```

The completed export is in this project's `build/web` directory. Serve that
directory over HTTP; do not open its index file directly. Exporting does not
publish or modify the live GitHub Pages website.

## Interactions

- Navigation buttons smoothly scroll to the engine overview and quick start.
- One full-window scene lights the tree, page backdrop and card surfaces. Move the pointer anywhere on the page to move its light. Text remains a selectable, readable UI overlay.
- Text is selectable, including multiline text, word/line selection by repeated
  clicks, Shift extension and Ctrl/Cmd+A/C shortcuts.
- Narrow layouts stack content and the live 3D exhibit vertically.
- Compact card previews open sixteen in-application feature pages spanning the wiki's major engine systems: runtime, 2D and 3D graphics, lighting, physics, UI, audio, assets, input, rendering, effects, state, data, platforms and diagnostics. Home/All features returns to the landing page. These Java views do not change the browser URL.
- Cards lift and tilt on hover. Their materials no longer cast the large shadow bands across the page.
- Code blocks use JetBrains Mono with syntax highlighting and horizontal scrolling. Copy code exports the original snippet, including whitespace; selection is also supported.
- The eight original deep-dive pages retain nine code examples each; the eight new overview pages add a focused starting example, for 80 displayed snippets total. Fragments assume the named resources already exist unless the surrounding note says otherwise.
- Desktop documentation uses an independently scrolling left sidebar, with the current chapter's example links expanded. Narrow screens use an inline example list.
- This document-only export passes `-PwebPhysics=false`: the snippets describe physics APIs but do not execute a physics simulation. Game exports retain physics by default. Scene rendering uses PERFORMANCE quality (no MSAA, half-resolution ambient occlusion).

## Code example checks

Run `gradlew.bat checkSnippets` to compile all 80 displayed snippets against the
engine API. This check also runs with `check`. It supplies the documented context
and imports without launching graphics, audio or physics; it validates types and
API usage, not the runtime behavior of a complete game.

See `ASSETS.md` for the model, texture, font and design-reference credits.

## Loading checks

The tree is shipped as preconverted gzip-compressed binary geometry, not runtime-parsed OBJ. All 98,375 triangles and three materials are retained. The Java UI is initialized before geometry construction. Startup timings are reported in the browser console.

```text
node tools/check-packed-tree.mjs
node tools/check-web-export.mjs
```

Measured generated JavaScript size: 43,274,044 bytes before this change, approximately 16.7 MB after (about 61% smaller before HTTP compression). This is a payload comparison, not a claim about cold-network load time.
