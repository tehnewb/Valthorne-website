# Valthorne landing page

The UI, layout, animations and interactive Jacaranda scene are implemented in Java 25 using Valthorne. The engine supplies its browser runtime and application shell.

## Develop

Import this Gradle project into IntelliJ with Java 25. Keep the compatible Valthorne engine checkout at `../Valthorne`.

```text
gradlew.bat run
gradlew.bat webExport
node tools/check-web-export.mjs
```

The export is written to this project's `build/web`. The current page uses unpublished engine changes, so Pages deploys the verified, prepared export in `site/` rather than rebuilding against an incompatible public engine version. To publish an update, replace `site/` with the complete tested export and commit both Java source changes and the export.

## Deployment

The Pages workflow validates startup files and tree resources, packages `site/`, and deploys it to https://tehnewb.github.io/Valthorne-website/ on pushes to `main`.

The floating navigation scrolls between sections. Move the pointer across the tree to move its light; use the pause control to stop decorative motion. Text remains selectable. See `ASSETS.md` for asset provenance.
