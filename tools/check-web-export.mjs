import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';

const root = process.argv[2] ? path.resolve(process.argv[2]) : path.resolve(import.meta.dirname, '../build/web');
const html = fs.readFileSync(path.join(root, 'index.html'), 'utf8');
assert(!/Physics in the light|Scene controls|Drop a body|id="enter"/.test(html), 'Application export must not contain physics-demo UI');
assert(!fs.existsSync(path.join(root,'branding/valthorne-logo.png')), 'Removed banner must not ship');
assert(fs.existsSync(path.join(root,'fonts/mono.ttf')), 'Code font must ship');
for (const [, resource] of html.matchAll(/<script[^>]*src="([^"]+)"/g)) {
    assert(fs.existsSync(path.join(root, resource)), `Missing startup resource: ${resource}`);
}
for (const resource of ['models/jacaranda/tree.vtr.gz', 'models/jacaranda/leaves.png']) {
    assert(fs.statSync(path.join(root, resource)).size > 0, `Missing tree resource: ${resource}`);
}
console.log('Web export verified: clean application shell, startup scripts and tree resources.');
