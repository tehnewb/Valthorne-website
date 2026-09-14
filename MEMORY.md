# Memory investigation

Local browser measurements, September 13, 2026. Diagnostics were collected with
`?profileMemory` through the exported engine host. Measurements are not equivalent
to Chrome Task Manager memory: JS heap estimates, WASM capacity and GPU allocations
can overlap or be accounted for differently. Do not add these into a process total.

## Confirmed findings

- The document website initialized Jolt despite not creating a physics world for
  its content. Jolt reported a 134,217,728-byte (128 MiB) heap.
- HIGH scene quality enabled 4-sample MSAA and full-resolution ambient occlusion.
- Each highlighted token, including individual whitespace/punctuation tokens,
  was represented by a separate UI/layout node.
- The existing UI detach path frees Yoga subtrees; renderer resource counts also
  fall when leaving a page. No monotonically increasing resource count was
  reproduced in the short navigation check. This is not a long-duration leak audit.

## Changes and measurements

| Counter | Before | After |
| --- | ---: | ---: |
| Physics WASM capacity | 128 MiB | 0 (runtime not initialized) |
| Rendering feature page layout handles | 436 | 244 (including new sidebar) |
| Filament WASM capacity | 98,893,824 bytes | 98,893,824 bytes |
| Scene multisampling | 4 samples | disabled |
| Ambient occlusion resolution | full | half |

Adjacent same-color syntax tokens now share a label. Original copied text is
unchanged. After repeated rendering/audio chapter changes the rendering page
returned to 244 layout handles, one scene entry and one cached mesh. The graphics
handle count stayed at 26, image count at three and vector context count at one.
Returning to the landing page restored its small layout and four cached meshes.

Baseline idle JS samples were approximately 154–174 million bytes. Final idle
landing samples were approximately 167–169 million bytes. These do **not** show a
proven reduction in JS heap; the verified memory removal is the unused physics
runtime, and the layout/rendering changes reduce work and resource demand.

## Remaining uncertainty

The reported 1–2 GB total footprint was not reproduced as JS heap usage. GPU,
browser-process memory and long-running growth still need a process-specific
profile if that total persists. The detailed tree and Filament runtime remain;
this work does not claim a lightweight static-document footprint.

## Checks

- All 40 displayed snippets compile (`gradlew.bat checkSnippets`).
- Web export and `node tools/check-web-export.mjs` pass.
- Three engine runtime-selection tests pass: disabled avoids importing physics,
  default still enables it, and startup failures propagate.
- Browser inspection: sidebar chapter changes, example jumps, independent content
  scrolling, and 390px inline navigation fallback. No final startup errors recorded.

Changes are local only. The website opts out with `-PwebPhysics=false`; normal
engine exports retain physics support by default. Remove that option if adding
an actual physics simulation to the website.
