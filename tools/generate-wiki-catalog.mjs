import fs from 'node:fs';
import path from 'node:path';

const wikiDir = path.resolve(process.argv[2] || '../Valthorne.wiki');
const output = path.resolve('src/main/java/com/example/WikiFeatureCatalog.java');
const included = /^(02|03|04|05|06|07|08)-.*\.md$|^09-Developer-(Diagnostics|Platform-Integration)\.md$/;
const excluded = /04-Graphics-3D-Filament-Platforms|05-Lighting-Visual-Audit|06-Physics-Studio/;

const clean = value => value
  .replace(/\[([^\]]+)]\([^)]*\)/g, '$1')
  .replace(/`([^`]+)`/g, '$1')
  .replace(/\*\*/g, '')
  .replace(/<[^>]+>/g, '')
  .replace(/\s+/g, ' ')
  .trim();
const java = value => '"' + value
  .replace(/\\/g, '\\\\')
  .replace(/"/g, '\\"')
  .replace(/\r/g, '')
  .replace(/\n/g, '\\n') + '"';
const compact = (value, limit = 420) => value.length <= limit ? value : value.slice(0, limit - 1).replace(/\s+\S*$/, '') + '…';

function section(markdown, heading) {
  const start = markdown.indexOf(`## ${heading}`);
  if (start < 0) return '';
  const body = markdown.slice(start + heading.length + 3);
  const end = body.search(/^## /m);
  return end < 0 ? body : body.slice(0, end);
}

function firstParagraph(markdown, heading) {
  const body = section(markdown, heading);
  const paragraphs = body.split(/\n\s*\n/).map(clean).filter(text => text && !text.startsWith('|') && !text.startsWith('```'));
  return paragraphs[0] || '';
}

function tableFeatures(markdown) {
  const body = section(markdown, 'Features and when to use them');
  const rows = [];
  for (const line of body.split('\n')) {
    if (!line.startsWith('|') || /^\|\s*-/.test(line) || /\|\s*Feature\s*\|/i.test(line)) continue;
    const cells = line.split('|').slice(1, -1).map(clean);
    if (cells.length >= 2 && cells[0] && cells[1]) rows.push([cells[0], cells[1]]);
  }
  return rows;
}

function fallbackFeatures(markdown) {
  const ignored = /^(Purpose|Getting started|Ownership and lifecycle|Important behavior|Components and examples|Related guides|Run|Try it|See it)/i;
  const matches = [...markdown.matchAll(/^## (.+)$/gm)];
  const rows = [];
  for (let i = 0; i < matches.length && rows.length < 8; i++) {
    const title = clean(matches[i][1]);
    if (ignored.test(title)) continue;
    const from = matches[i].index + matches[i][0].length;
    const to = i + 1 < matches.length ? matches[i + 1].index : markdown.length;
    const text = markdown.slice(from, to).split(/\n\s*\n/)
      .filter(value => !value.includes('```') && !value.trim().startsWith('|'))
      .map(value => compact(clean(value)))
      .find(Boolean);
    if (text) rows.push([title, text]);
  }
  return rows;
}

function snippets(markdown) {
  const headings = [...markdown.matchAll(/^(#{2,6})\s+(.+)$/gm)].map(match => ({
    index: match.index,
    level: match[1].length,
    title: clean(match[2]),
    end: match.index + match[0].length,
  }));
  const generic = /^(Example|Example Usage|Usage|Getting started|Try it|Create and use)$/i;
  const fenced = [...markdown.matchAll(/```([^\n]*)\n([\s\S]*?)```/g)];
  const javaBlocks = fenced.filter(match => match[1].trim().toLowerCase() === 'java');
  const sourceBlocks = javaBlocks.length ? javaBlocks : fenced;
  const blocks = sourceBlocks.map(match => {
    const prior = headings.filter(heading => heading.index < match.index);
    const nearest = prior.at(-1);
    let named = nearest;
    if (!named || generic.test(named.title)) {
      named = [...prior].reverse().find(heading => !generic.test(heading.title)) || nearest;
    }
    const parent = [...prior].reverse().find(heading => heading.level <= 3 && heading !== named);
    let title = named?.title || 'Java example';
    if (nearest && generic.test(nearest.title)) title += ' example';
    else if (/^(Constructor|set|get|load|draw|update|create|open|close|add|remove)$/i.test(title) && parent) title = `${parent.title} — ${title}`;

    const contextHeading = nearest && generic.test(nearest.title) ? named : nearest;
    const before = markdown.slice(contextHeading?.end || Math.max(0, match.index - 1200), match.index);
    const afterStart = match.index + match[0].length;
    const nextHeading = headings.find(heading => heading.index > match.index);
    const after = markdown.slice(afterStart, nextHeading?.index || Math.min(markdown.length, afterStart + 1200));
    const paragraphs = value => value.split(/\n\s*\n/)
      .filter(text => !text.includes('```') && !text.trim().startsWith('|') && !text.trim().startsWith('<') && !text.trim().startsWith('#'))
      .map(text => compact(clean(text), 360))
      .filter(text => text && !/^Source$/i.test(text) && !/^System manual$/i.test(text));
    const beforeParagraphs = paragraphs(before);
    const afterParagraphs = paragraphs(after);
    const note = (nearest && generic.test(nearest.title)
      ? beforeParagraphs.at(-1) || afterParagraphs[0]
      : afterParagraphs[0] || beforeParagraphs.at(-1))
      || '';
    const code = match[2].trim();
    const usage = generic.test(nearest?.title || '') || ((code.match(/;/g) || []).length >= 2) || /\bnew\s+[A-Z]|\b(import|for|if|try)\b/.test(code);
    return { title, note, code, usage };
  }).filter(block => block.code.length > 0);

  const usage = blocks.filter(block => block.usage);
  const chosen = (usage.length ? usage : blocks).slice(0, 12);
  const names = new Map();
  for (const block of chosen) {
    const count = (names.get(block.title) || 0) + 1;
    names.set(block.title, count);
    if (count > 1) block.title += ` (${count})`;
  }
  return chosen;
}

function artKind(slug) {
  if (/Assets|Files|Buffers|Compression|Encryption|Settings/.test(slug)) return 8;
  if (/Input|Events/.test(slug)) return 9;
  if (/Shaders|Path-Tracing|Capabilities/.test(slug)) return 10;
  if (/Particles/.test(slug)) return 11;
  if (/State-Machines|Timing|Scenes|Plugins|Runtime/.test(slug)) return 12;
  if (/^08-/.test(slug)) return 13;
  if (/Portable|Platform/.test(slug)) return 14;
  if (/Diagnostics/.test(slug)) return 15;
  if (/Audio/.test(slug)) return 4;
  if (/Physics/.test(slug)) return 5;
  if (/Animation|Textures|Tiled|Fonts/.test(slug)) return 6;
  if (/Cameras|Viewports/.test(slug)) return 7;
  if (/Lighting/.test(slug)) return 1;
  if (/UI-/.test(slug)) return 3;
  if (/3D|Models|Filament/.test(slug)) return 0;
  return 2;
}

const fallbackExamples = {
  '02-Core-Portable-Runtime': {
    title: 'Run a bounded portable frame loop',
    note: 'FrameLoop starts one Application, caps fixed-step catch-up work, continues rendering while paused, and disposes the application when closed.',
    code: 'var loop = new valthorne.portable.FrameLoop(application, 1f / 60f, 8);\nloop.start();\nloop.frame(elapsedSeconds);\nloop.close();',
  },
  '08-Data-Settings': {
    title: 'Store and read a typed property',
    note: 'PropertySet stores named PropertyValue wrappers. Typed access verifies the stored class, while PropertyValue supplies explicit conversions.',
    code: 'var settings = new valthorne.utility.settings.PropertySet();\nsettings.set("volume", 0.8f);\nfloat volume = settings\n    .getRequired("volume", Float.class)\n    .asFloat();',
  },
  '09-Developer-Platform-Integration': {
    title: 'Check Filament support and capture a frame',
    note: 'FilamentPlatform reports published native-runtime availability. PlatformTools capture requires an active desktop OpenGL context and writes a vertically corrected PNG.',
    code: 'boolean filamentAvailable =\n    valthorne.graphics.FilamentPlatform.supported();\n\nvalthorne.PlatformTools.capture(\n    "build/frame.png",\n    Window.getWidth(),\n    Window.getHeight());',
  },
};

const guides = fs.readdirSync(wikiDir).filter(name => included.test(name) && !excluded.test(name)).sort().map(file => {
  const markdown = fs.readFileSync(path.join(wikiDir, file), 'utf8');
  const title = clean((markdown.match(/^# (.+)$/m) || [null, file.replace(/\.md$/, '')])[1]);
  const slug = file.replace(/\.md$/, '');
  const purpose = compact(firstParagraph(markdown, 'Purpose') || clean(markdown.split(/\n\s*\n/).slice(1).find(p => clean(p)) || title));
  let features = tableFeatures(markdown);
  if (!features.length) features = fallbackFeatures(markdown);
  if (!features.length) features = [['Overview', purpose]];
  const code = snippets(markdown);
  if (!code.length && fallbackExamples[slug]) code.push(fallbackExamples[slug]);
  for (const example of code) {
    if (!example.note) example.note = purpose;
    if (example.title === 'Java example') example.title = `${title} example`;
  }
  const highlights = features.slice(0, 4).map(([name]) => name.toUpperCase()).join('  ·  ');
  return { title, slug, purpose, features: features.map(([name, description]) => [name, compact(description)]), code, highlights, art: artKind(slug) };
});

const titleCounts = new Map();
const missingExamples = guides.filter(guide => guide.code.length === 0);
if (missingExamples.length) throw new Error(`Missing code examples for: ${missingExamples.map(guide => guide.slug).join(', ')}`);
for (const guide of guides) titleCounts.set(guide.title, (titleCounts.get(guide.title) || 0) + 1);
for (const guide of guides) {
  if (titleCounts.get(guide.title) > 1) {
    const qualifier = guide.slug.replace(/^\d+-/, '').replace(/-/g, ' ');
    guide.title += ` — ${qualifier}`;
  }
}

const lines = [];
lines.push('package com.example;', '', '/** Generated from the Valthorne GitHub wiki. Do not edit by hand. */', 'final class WikiFeatureCatalog {');
lines.push('    static final FeaturePage[] PAGES = {');
for (const guide of guides) {
  const topics = guide.features.flatMap(([name, description]) => [name, description]);
  const details = `${guide.purpose}\n\nThis page summarizes ${guide.features.length} documented ${guide.features.length === 1 ? 'capability' : 'capabilities'} from the Valthorne wiki.`;
  const code = guide.code[0]?.code || `// ${guide.title}\n// See the linked wiki guide for setup and API contracts.`;
  lines.push(`        new FeaturePage(${java(guide.title)}, ${java(guide.purpose)}, ${java(guide.highlights)}, ${java(details)}, ${java(code)}),`);
}
lines.push('    };', '');
lines.push('    static String[] topics(String title) {', '        return switch (title) {');
guides.forEach((guide, index) => {
  const values = guide.features.flatMap(([name, description]) => [java(name), java(description)]).join(', ');
  lines.push(`            case ${java(guide.title)} -> new String[]{${values}};`);
});
lines.push('            default -> new String[0];', '        };', '    }', '');
lines.push('    static FeatureExamples.Example[] examples(int page) {', '        return switch (page) {');
guides.forEach((guide, index) => {
  const extras = guide.code.slice(1);
  if (!extras.length) return;
  const values = extras.map(example => `new FeatureExamples.Example(${java(example.title)}, ${java(example.note)}, ${java(example.code)})`).join(', ');
  lines.push(`            case ${index} -> new FeatureExamples.Example[]{${values}};`);
});
lines.push('            default -> new FeatureExamples.Example[0];', '        };', '    }', '');
lines.push('    static String primaryExampleTitle(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${java(guide.code[0]?.title || `${guide.title} overview`)};`));
lines.push('            default -> "Start building";', '        };', '    }', '');
lines.push('    static String primaryExampleNote(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${java(guide.code[0]?.note || `See the full ${guide.title} wiki guide for setup and API contracts.`)};`));
lines.push('            default -> "Use this fragment within the documented application lifecycle.";', '        };', '    }', '');
lines.push('    static String wikiUrl(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${java(`https://github.com/tehnewb/Valthorne/wiki/${guide.slug}`)};`));
lines.push('            default -> null;', '        };', '    }', '');
lines.push('    static int artKind(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${guide.art};`));
lines.push('            default -> 2;', '        };', '    }', '', '    private WikiFeatureCatalog() {}', '}', '');

fs.writeFileSync(output, lines.join('\n'));
const featureCount = guides.reduce((sum, guide) => sum + guide.features.length, 0);
const snippetCount = guides.reduce((sum, guide) => sum + guide.code.length, 0);
console.log(`Generated ${guides.length} wiki-backed system pages, ${featureCount} documented capabilities and ${snippetCount} code snippets at ${output}`);
