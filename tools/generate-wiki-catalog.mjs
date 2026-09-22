import fs from 'node:fs';
import path from 'node:path';

const wikiDir = path.resolve(process.argv[2] || '../Valthorne.wiki');
const output = path.resolve('src/main/java/com/example/WikiFeatureCatalog.java');
const included = /^(02|03|04|05|06|07|08)-.*\.md$|^09-Developer-(Diagnostics|Platform-Integration)\.md$/;

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
  return [...markdown.matchAll(/```java\s*\n([\s\S]*?)```/g)]
    .map(match => match[1].trim())
    .filter(code => code.length > 0)
    .slice(0, 4);
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

const guides = fs.readdirSync(wikiDir).filter(name => included.test(name)).sort().map(file => {
  const markdown = fs.readFileSync(path.join(wikiDir, file), 'utf8');
  const title = clean((markdown.match(/^# (.+)$/m) || [null, file.replace(/\.md$/, '')])[1]);
  const slug = file.replace(/\.md$/, '');
  const purpose = compact(firstParagraph(markdown, 'Purpose') || clean(markdown.split(/\n\s*\n/).slice(1).find(p => clean(p)) || title));
  let features = tableFeatures(markdown);
  if (!features.length) features = fallbackFeatures(markdown);
  if (!features.length) features = [['Overview', purpose]];
  const code = snippets(markdown);
  const highlights = features.slice(0, 4).map(([name]) => name.toUpperCase()).join('  ·  ');
  return { title, slug, purpose, features: features.map(([name, description]) => [name, compact(description)]), code, highlights, art: artKind(slug) };
});

const titleCounts = new Map();
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
  const code = guide.code[0] || `// ${guide.title}\n// See the linked wiki guide for setup and API contracts.`;
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
  const values = extras.map((code, snippet) => `new FeatureExamples.Example(${java(`Wiki example ${snippet + 2}`)}, ${java(`From the ${guide.title} wiki guide. Supply the surrounding application state described there.`)}, ${java(code)})`).join(', ');
  lines.push(`            case ${index} -> new FeatureExamples.Example[]{${values}};`);
});
lines.push('            default -> new FeatureExamples.Example[0];', '        };', '    }', '');
lines.push('    static String wikiUrl(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${java(`https://github.com/tehnewb/Valthorne/wiki/${guide.slug}`)};`));
lines.push('            default -> null;', '        };', '    }', '');
lines.push('    static int artKind(String title) {', '        return switch (title) {');
guides.forEach(guide => lines.push(`            case ${java(guide.title)} -> ${guide.art};`));
lines.push('            default -> 2;', '        };', '    }', '', '    private WikiFeatureCatalog() {}', '}', '');

fs.writeFileSync(output, lines.join('\n'));
const featureCount = guides.reduce((sum, guide) => sum + guide.features.length, 0);
const snippetCount = guides.reduce((sum, guide) => sum + guide.code.length, 0);
console.log(`Generated ${guides.length} wiki-backed system pages, ${featureCount} documented capabilities and ${snippetCount} Java snippets at ${output}`);
