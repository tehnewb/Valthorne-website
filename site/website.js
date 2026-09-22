(() => {
  'use strict';

  const features = Array.isArray(window.VALTHORNE_FEATURES) ? window.VALTHORNE_FEATURES : [];
  const grid = document.querySelector('#feature-grid');
  const stats = document.querySelector('#catalog-stats');
  const filter = document.querySelector('#feature-filter');
  const empty = document.querySelector('#empty-state');
  const svgNS = 'http://www.w3.org/2000/svg';
  const artNames = ['3D scene','lighting','map graph','interface','audio','physics','animation','viewport','assets','input','shader','particles','state flow','data','platforms','diagnostics','fonts','textures','tile map','plugins','encryption','files','themes','geometry'];

  const node = (tag, className, text) => {
    const element = document.createElement(tag);
    if (className) element.className = className;
    if (text !== undefined) element.textContent = text;
    return element;
  };

  const svgNode = (tag, attributes = {}) => {
    const element = document.createElementNS(svgNS, tag);
    for (const [name, value] of Object.entries(attributes)) element.setAttribute(name, String(value));
    return element;
  };

  const hash = text => {
    let value = 2166136261;
    for (let i = 0; i < text.length; i++) value = Math.imul(value ^ text.charCodeAt(i), 16777619);
    return value >>> 0;
  };

  const randomFor = seed => {
    let value = seed || 1;
    return () => {
      value ^= value << 13;
      value ^= value >>> 17;
      value ^= value << 5;
      return (value >>> 0) / 4294967296;
    };
  };

  function featureArt(feature) {
    const svg = svgNode('svg', { class: 'feature-art', viewBox: '0 0 800 250', role: 'img', 'aria-label': `${feature.title} illustration` });
    const seed = hash(feature.title);
    const rand = randomFor(seed);
    const kind = feature.art % artNames.length;
    const accent = ['#b99af8','#8664d0','#d8c6ff','#6f4ba0'][seed % 4];
    svg.append(svgNode('rect', { width: 800, height: 250, fill: '#08080b' }));

    const line = (x1, y1, x2, y2, color = '#493b5d', width = 2) => svg.append(svgNode('line', { x1, y1, x2, y2, stroke: color, 'stroke-width': width, 'stroke-linecap': 'round' }));
    const circle = (cx, cy, r, color = accent, width = 2) => svg.append(svgNode('circle', { cx, cy, r, fill: 'none', stroke: color, 'stroke-width': width }));
    const rect = (x, y, width, height, color = '#6f5591', radius = 4) => svg.append(svgNode('rect', { x, y, width, height, rx: radius, fill: 'none', stroke: color, 'stroke-width': 2 }));

    if ([3, 7, 14, 17, 18, 22].includes(kind)) {
      const columns = kind === 18 ? 10 : 6;
      const rows = kind === 3 ? 3 : 4;
      for (let row = 0; row < rows; row++) for (let column = 0; column < columns; column++) {
        const x = 64 + column * (620 / columns) + rand() * 8;
        const y = 42 + row * (145 / rows) + rand() * 8;
        const active = (row * columns + column + seed) % (3 + seed % 4) === 0;
        rect(x, y, kind === 7 ? 75 - row * 8 : 42 + rand() * 28, 20 + rand() * 18, active ? accent : '#3b3148', kind === 22 ? 18 : 3);
      }
    } else if ([1, 4, 6, 10, 15, 16].includes(kind)) {
      let lastX = 54, lastY = 126;
      for (let i = 0; i < 34; i++) {
        const x = 54 + i * 20;
        const frequency = 2.5 + (seed % 7) * .25;
        const y = 126 + Math.sin(i / frequency + seed * .001) * (25 + kind * 1.4) + (rand() - .5) * 18;
        line(lastX, lastY, x, y, i % 6 === seed % 6 ? accent : '#554168', i % 7 === 0 ? 4 : 2);
        if (i % 5 === 0) circle(x, y, 4 + (i + seed) % 5, i % 10 === 0 ? '#e4d7ff' : accent, 2);
        lastX = x;
        lastY = y;
      }
    } else if ([0, 2, 9, 11, 12, 19, 21, 23].includes(kind)) {
      const points = Array.from({ length: 9 + seed % 7 }, (_, index) => ({
        x: 72 + rand() * 650,
        y: 35 + rand() * 160,
        r: 3 + (index + seed) % 9
      }));
      for (let i = 1; i < points.length; i++) {
        const parent = points[Math.floor(rand() * i)];
        line(parent.x, parent.y, points[i].x, points[i].y, i % 3 === 0 ? '#6d4e91' : '#332a3e', 1.5);
      }
      points.forEach((point, index) => circle(point.x, point.y, point.r, index % 4 === seed % 4 ? '#e4d7ff' : accent, 2));
    } else if ([5, 8, 13, 20].includes(kind)) {
      for (let i = 0; i < 12; i++) {
        const x = 66 + i * 56;
        const height = 30 + rand() * 115;
        line(x, 196, x, 196 - height, i % 4 === seed % 4 ? accent : '#4a395e', 7);
        circle(x, 196 - height, 4 + i % 4, i % 4 === seed % 4 ? '#e4d7ff' : '#8463ad', 2);
      }
    } else {
      for (let ring = 0; ring < 7; ring++) circle(400, 122, 18 + ring * (12 + seed % 5), ring % 2 ? '#4c3a61' : accent, 1.5);
      for (let spoke = 0; spoke < 8; spoke++) {
        const angle = spoke * Math.PI / 4 + seed * .0001;
        line(400, 122, 400 + Math.cos(angle) * 120, 122 + Math.sin(angle) * 90, '#49375d', 1.5);
      }
    }

    const category = svgNode('text', { x: 28, y: 31, fill: '#b99af8', 'font-size': 12, 'font-family': 'Code, monospace', 'letter-spacing': 2 });
    category.textContent = `${String(feature.index + 1).padStart(2, '0')} / ${artNames[kind].toUpperCase()}`;
    const title = svgNode('text', { x: 28, y: 226, fill: '#f5f1f8', 'font-size': 19, 'font-family': 'system-ui, sans-serif' });
    title.textContent = feature.title.length > 58 ? `${feature.title.slice(0, 57)}…` : feature.title;
    svg.append(category, title);
    return svg;
  }

  const documentation = document.querySelector('#documentation');
  const documentationTree = document.querySelector('#documentation-tree');
  const documentationContent = document.querySelector('#documentation-content');
  const backButton = document.querySelector('#back-to-features');
  const catalog = document.querySelector('#features');
  const home = document.querySelector('#home');
  const pageFooter = document.querySelector('footer');

  const slug = text => text.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');

  function featureCard(feature) {
    const article = node('article', 'feature-card');
    article.tabIndex = 0;
    article.setAttribute('role', 'link');
    article.setAttribute('aria-label', `Open ${feature.title} documentation`);
    article.dataset.search = [feature.title, feature.summary, ...feature.capabilities.flatMap(item => [item.title, item.description])].join(' ').toLowerCase();
    article.append(featureArt(feature));
    const content = node('div', 'feature-content');
    content.append(node('span', 'feature-number', `SYSTEM ${String(feature.index + 1).padStart(2, '0')}`));
    content.append(node('h3', '', feature.title));
    content.append(node('p', 'feature-summary', feature.summary));

    const meta = node('div', 'feature-meta');
    meta.append(node('span', '', `${feature.capabilities.length} capabilities`));
    meta.append(node('span', '', `${feature.examples.length} examples`));
    content.append(meta);

    const highlights = node('ul', 'card-highlights');
    feature.capabilities.slice(0, 4).forEach(capability => highlights.append(node('li', '', capability.title)));
    content.append(highlights);
    content.append(node('span', 'open-feature', 'Open documentation  →'));
    article.append(content);

    const open = () => openFeature(feature.index, true);
    article.addEventListener('click', open);
    article.addEventListener('keydown', event => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        open();
      }
    });
    return article;
  }

  function jumpTo(element) {
    element?.scrollIntoView({ behavior: matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth', block: 'start' });
  }

  function treeButton(label, target, className = 'tree-anchor') {
    const button = node('button', className, label);
    button.type = 'button';
    button.addEventListener('click', () => jumpTo(target));
    return button;
  }

  function renderDocumentation(feature) {
    documentationTree.replaceChildren();
    documentationContent.replaceChildren();

    documentationTree.append(node('p', 'tree-label', 'DOCUMENTATION'));
    features.forEach((system, index) => {
      const branch = node('div', `tree-branch${index === feature.index ? ' active' : ''}`);
      const systemButton = node('button', 'tree-system', `${String(index + 1).padStart(2, '0')}  ${system.title}`);
      systemButton.type = 'button';
      systemButton.addEventListener('click', () => openFeature(index, true));
      branch.append(systemButton);
      if (index === feature.index) {
        const children = node('div', 'tree-children');
        branch.append(children);
        queueMicrotask(() => {
          children.append(treeButton('Overview', document.querySelector('#doc-overview')));
          feature.capabilities.forEach((capability, capabilityIndex) => {
            children.append(treeButton(capability.title, document.querySelector(`#doc-capability-${capabilityIndex}`)));
          });
          const exampleGroup = node('p', 'tree-group', `CODE EXAMPLES · ${feature.examples.length}`);
          children.append(exampleGroup);
          feature.examples.forEach((example, exampleIndex) => {
            children.append(treeButton(`${String(exampleIndex + 1).padStart(2, '0')}  ${example.title}`, document.querySelector(`#doc-example-${exampleIndex}`)));
          });
        });
      }
      documentationTree.append(branch);
    });

    const overview = node('header', 'documentation-hero');
    overview.id = 'doc-overview';
    overview.append(node('p', 'eyebrow', `FEATURES / ${String(feature.index + 1).padStart(2, '0')}`));
    overview.append(node('h2', '', feature.title));
    overview.append(node('p', 'documentation-summary', feature.summary));
    const counts = node('div', 'feature-meta');
    counts.append(node('span', '', `${feature.capabilities.length} documented capabilities`));
    counts.append(node('span', '', `${feature.examples.length} relevant examples`));
    overview.append(counts);
    const wiki = node('a', 'wiki-link', 'Read the source wiki guide ↗');
    wiki.href = feature.wikiUrl;
    wiki.target = '_blank';
    wiki.rel = 'noreferrer';
    overview.append(wiki);
    documentationContent.append(overview);

    const capabilitiesHeading = node('div', 'documentation-section-heading');
    capabilitiesHeading.append(node('p', 'eyebrow', 'WHAT THE SYSTEM PROVIDES'));
    capabilitiesHeading.append(node('h3', '', 'Capabilities'));
    documentationContent.append(capabilitiesHeading);
    feature.capabilities.forEach((capability, index) => {
      const section = node('section', 'capability-section');
      section.id = `doc-capability-${index}`;
      section.append(node('span', 'section-number', String(index + 1).padStart(2, '0')));
      section.append(node('h4', '', capability.title));
      section.append(node('p', '', capability.description));
      documentationContent.append(section);
    });

    const examplesHeading = node('div', 'documentation-section-heading examples-intro');
    examplesHeading.append(node('p', 'eyebrow', 'JAVA IN PRACTICE'));
    examplesHeading.append(node('h3', '', `${feature.examples.length} code examples`));
    examplesHeading.append(node('p', 'documentation-summary', 'Every relevant example recovered from the Valthorne wiki for this system is included below with its original context.'));
    documentationContent.append(examplesHeading);
    feature.examples.forEach((example, index) => {
      const section = node('section', 'documentation-example');
      section.id = `doc-example-${index}`;
      section.append(node('span', 'section-number', `EXAMPLE ${String(index + 1).padStart(2, '0')}`));
      section.append(node('h4', '', example.title));
      section.append(node('p', '', example.note));
      const pre = node('pre');
      pre.append(node('code', '', example.code));
      section.append(pre);
      documentationContent.append(section);
    });

    const next = node('button', 'next-feature', `Next: ${features[(feature.index + 1) % features.length].title}  →`);
    next.type = 'button';
    next.addEventListener('click', () => openFeature((feature.index + 1) % features.length, true));
    documentationContent.append(next);
  }

  function openFeature(index, updateHistory) {
    const feature = features[index];
    if (!feature) return;
    renderDocumentation(feature);
    home.hidden = true;
    catalog.hidden = true;
    documentation.hidden = false;
    pageFooter.hidden = true;
    if (updateHistory) history.pushState({ feature: index }, '', `#feature=${slug(feature.title)}`);
    window.scrollTo({ top: 0, behavior: 'auto' });
  }

  function closeDocumentation(updateHistory, target = catalog) {
    documentation.hidden = true;
    home.hidden = false;
    catalog.hidden = false;
    pageFooter.hidden = false;
    if (updateHistory) history.pushState({}, '', '#features');
    jumpTo(target);
  }

  function syncRoute() {
    const match = location.hash.match(/^#feature=(.+)$/);
    if (match) {
      const index = features.findIndex(feature => slug(feature.title) === decodeURIComponent(match[1]));
      if (index >= 0) {
        openFeature(index, false);
        return;
      }
    }
    if (!documentation.hidden) closeDocumentation(false, location.hash === '#home' ? home : catalog);
  }

  const cards = features.map(feature => featureCard(feature));
  grid.append(...cards);
  const capabilityCount = features.reduce((total, feature) => total + feature.capabilities.length, 0);
  const exampleCount = features.reduce((total, feature) => total + feature.examples.length, 0);

  function updateFilter() {
    const query = filter.value.trim().toLowerCase();
    let visible = 0;
    cards.forEach(card => {
      const show = !query || card.dataset.search.includes(query);
      card.hidden = !show;
      if (show) visible++;
    });
    empty.hidden = visible !== 0;
    stats.textContent = `${visible} of ${features.length} systems · ${capabilityCount} capabilities · ${exampleCount} Java examples`;
  }

  filter.addEventListener('input', updateFilter);
  backButton.addEventListener('click', () => closeDocumentation(true));
  window.addEventListener('popstate', syncRoute);
  window.addEventListener('hashchange', syncRoute);
  updateFilter();
  syncRoute();
})();
