# Local review checks

The live site was inspected before editing. Confirmed defects: oversized shadow bands from card meshes, depth-induced mismatch with the UI bounds, and all card links returning to shared landing-page sections.

Implemented locally:

- Banner removed from both layout and exported resources; backup retained in build/model-sources.
- Card surfaces no longer cast/receive shadows. Their projected size compensates for depth, with a restrained hover lift/tilt.
- Four separate Java feature views, with Home, All features and Next feature navigation. These are in-application views; browser history/URLs are unchanged.
- Pre-tokenized, monospace code blocks with explicit whitespace widths, syntax colors, horizontal scrolling and original-text clipboard export.
- Tree framed against both available width and height.

Verification:

- Java web export and portable API validation passed.
- Packed geometry validation passed (98,375 triangles, three materials).
- Export checks passed, including no banner and presence of the code font.
- Opened all four feature views, including next-page transitions and home navigation.
- Copied rendering and UI snippets; verified original text, indentation and newlines through clipboard checks.
- Checked the UI feature view at 390×844; code scrolls horizontally and Copy remains visible.
- Dragging from blank page space onto a preview does not activate it.

GitHub Pages was not updated; changes are intentionally local for review.
