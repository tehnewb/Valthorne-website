# Website asset provenance

- Jacaranda Tree — Rico Cilliers, guidance by Rob Tuytel: https://polyhaven.com/a/jacaranda_tree
- Model and textures are CC0: https://polyhaven.com/license
- The downloaded glTF geometry and 1K diffuse/opacity textures were checked against the Poly Haven API's MD5 hashes. `tools/prepare-jacaranda.mjs` converts and simplifies the original 3,863,832 triangles to 98,375, retaining separate branch, trunk and leaf materials with UVs and normals. Coordinates are converted to Z-up and grounded by the engine. Leaf opacity is packed into the diffuse PNG and rendered with an alpha cutoff; lighting uses scalar roughness, not normal or roughness maps.
- Conversion tooling requires `npm install --prefix build/model-tools --no-save meshoptimizer@1.2.0 sharp`, then `node tools/prepare-jacaranda.mjs`. Original downloads and conversion statistics are in `build/model-sources/jacaranda`, outside the web payload. The prepared OBJ, MTL and textures are application resources; exporting does not require conversion tooling. Previous sculpture assets are retained locally in `build/model-sources/retired-sculptures`, not shipped.
- Cormorant Garamond — Christian Thalmann, Google Fonts; SIL Open Font License, included at src/main/resources/fonts/OFL.txt.
- Existing Valthorne banner supplied by the project owner.

Design study: https://joshw.io/ — black (#000), white (#fff), 75%-white secondary text, charcoal surfaces, violet accents, serif display typography, floating navigation, animated project presentation and selectable text. The reference's proprietary fonts, artwork and code are not copied.
