#!/usr/bin/env node
// Generate a unified HTML test report for all platforms.
// Exits with code 1 if any failures are detected (for CI/CD enforcement).
//
// Usage: node scripts/generate-test-report.js <composeAppBuildDir>
//   e.g. node scripts/generate-test-report.js composeApp/build

'use strict';

const fs = require('fs');
const path = require('path');

const BUILD = path.resolve(process.argv[2] || 'composeApp/build');
const REPORTS_DIR = path.join(BUILD, 'reports');
const OUT = path.join(REPORTS_DIR, 'test-report.html');

const PLATFORMS = [
    {
        // instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test) wires all commonTest
        // (ViewModel + Compose UI) to connectedDebugAndroidTest on a real emulator/device.
        name: 'Android',
        xmlDir: path.join(BUILD, 'outputs', 'androidTest-results', 'connected', 'debug'),
        htmlReport: path.join(BUILD, 'reports', 'androidTests', 'connected', 'debug', 'index.html'),
        recording: path.join(BUILD, 'reports', 'recordings', 'android-test.mp4'),
        note: 'All commonTest (ViewModel + Compose UI) via connectedAndroidTest on emulator',
    },
    {
        name: 'iOS Simulator',
        xmlDir: path.join(BUILD, 'test-results', 'iosSimulatorArm64Test'),
        htmlReport: null,
        recording: path.join(BUILD, 'reports', 'recordings', 'ios-test.mp4'),
        note: 'XCTest via iosSimulatorArm64Test',
    },
    {
        name: 'WASM Browser',
        xmlDir: path.join(BUILD, 'test-results', 'wasmJsBrowserTest'),
        htmlReport: null,
        recording: path.join(BUILD, 'reports', 'recordings', 'wasm-test-results.png'),
        note: 'Karma + ChromeHeadless (Brave)',
    },
];

// Parse totals from JUnit XML files in a directory
function parseXmlDir(dir) {
    const result = { tests: 0, failures: 0, errors: 0, skipped: 0 };
    if (!fs.existsSync(dir)) return result;
    for (const file of fs.readdirSync(dir).filter(f => f.endsWith('.xml'))) {
        const xml = fs.readFileSync(path.join(dir, file), 'utf8');
        const int = key => parseInt(xml.match(new RegExp(`${key}="(\\d+)"`))?.[1] ?? '0');
        result.tests    += int('tests');
        result.failures += int('failures');
        result.errors   += int('errors');
        result.skipped  += int('skipped');
    }
    return result;
}

function relPath(filePath) {
    if (!filePath || !fs.existsSync(filePath)) return null;
    return path.relative(REPORTS_DIR, filePath);
}

const rows = PLATFORMS.map(p => {
    const s = parseXmlDir(p.xmlDir);
    const failed = s.failures + s.errors;
    const passed = s.tests - failed - s.skipped;
    const status = s.tests === 0 ? 'NO DATA' : failed > 0 ? 'FAIL' : 'PASS';
    return { ...p, s, passed, failed, status, reportRel: relPath(p.htmlReport), recordingRel: relPath(p.recording) };
});

const totalTests  = rows.reduce((a, r) => a + r.s.tests, 0);
const totalFailed = rows.reduce((a, r) => a + r.failed, 0);
const totalPassed = rows.reduce((a, r) => a + r.passed, 0);
const allPassed   = rows.filter(r => r.s.tests > 0).every(r => r.status === 'PASS');

const COLOR = { PASS: '#22c55e', FAIL: '#ef4444', 'NO DATA': '#f59e0b' };

const tableRows = rows.map(r => `
    <tr>
      <td>
        <strong>${r.name}</strong>
        <div class="note">${r.note}</div>
      </td>
      <td><span class="badge" style="background:${COLOR[r.status]}22;color:${COLOR[r.status]}">${r.status}</span></td>
      <td class="num">${r.s.tests}</td>
      <td class="num" style="color:#22c55e">${r.passed}</td>
      <td class="num" style="color:${r.failed > 0 ? '#ef4444' : 'inherit'}">${r.failed}</td>
      <td class="num" style="color:#94a3b8">${r.s.skipped}</td>
      <td>${r.reportRel    ? `<a href="${r.reportRel}">Report</a>`    : '<span class="na">—</span>'}</td>
      <td>${r.recordingRel ? `<a href="${r.recordingRel}">Video</a>` : '<span class="na">—</span>'}</td>
    </tr>`).join('');

const html = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Masarify — Test Report</title>
<style>
  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
  body  { font-family: system-ui, -apple-system, sans-serif; background: #0f172a; color: #e2e8f0; padding: 2.5rem; line-height: 1.5; }
  h1   { font-size: 1.75rem; color: #60a5fa; margin-bottom: .25rem; }
  .sub { color: #475569; font-size: .8rem; margin-bottom: 2rem; }
  .banner { display: inline-flex; align-items: center; gap: .5rem; padding: .5rem 1.25rem; border-radius: .5rem;
            font-weight: 700; font-size: 1rem; margin-bottom: 2rem; }
  .banner.pass { background: #14532d40; color: #22c55e; border: 1px solid #22c55e40; }
  .banner.fail { background: #450a0a40; color: #ef4444; border: 1px solid #ef444440; }
  .stats { display: flex; gap: 1rem; margin-bottom: 2.5rem; flex-wrap: wrap; }
  .stat  { background: #1e293b; border-radius: .75rem; padding: 1rem 1.5rem; min-width: 110px; }
  .stat-val   { font-size: 2rem; font-weight: 700; }
  .stat-label { font-size: .7rem; color: #64748b; text-transform: uppercase; letter-spacing: .05em; margin-top: .25rem; }
  table { width: 100%; border-collapse: collapse; }
  th   { background: #1e293b; padding: .625rem 1rem; text-align: left; color: #64748b;
         font-size: .7rem; text-transform: uppercase; letter-spacing: .06em; font-weight: 600; }
  td   { padding: .875rem 1rem; border-bottom: 1px solid #1e293b; vertical-align: top; }
  tr:last-child td { border-bottom: none; }
  tr:hover td { background: #1e293b55; }
  .badge { display: inline-block; padding: .2rem .65rem; border-radius: .3rem; font-size: .75rem; font-weight: 700; }
  .note  { font-size: .75rem; color: #64748b; margin-top: .2rem; }
  .num   { text-align: right; font-variant-numeric: tabular-nums; }
  .na    { color: #334155; }
  a { color: #60a5fa; text-decoration: none; }
  a:hover { text-decoration: underline; }
  footer { margin-top: 2rem; color: #334155; font-size: .75rem; }
</style>
</head>
<body>
<h1>Masarify — Test Report</h1>
<p class="sub">Generated: ${new Date().toISOString()}</p>

<div class="banner ${allPassed ? 'pass' : 'fail'}">
  ${allPassed ? 'All platforms passing' : 'Failures detected'}
</div>

<div class="stats">
  <div class="stat">
    <div class="stat-val">${totalTests}</div>
    <div class="stat-label">Total Tests</div>
  </div>
  <div class="stat">
    <div class="stat-val" style="color:#22c55e">${totalPassed}</div>
    <div class="stat-label">Passed</div>
  </div>
  <div class="stat">
    <div class="stat-val" style="color:${totalFailed > 0 ? '#ef4444' : 'inherit'}">${totalFailed}</div>
    <div class="stat-label">Failed</div>
  </div>
  <div class="stat">
    <div class="stat-val">${PLATFORMS.length}</div>
    <div class="stat-label">Platforms</div>
  </div>
</div>

<table>
  <thead>
    <tr>
      <th>Platform</th><th>Status</th>
      <th class="num">Total</th><th class="num">Passed</th>
      <th class="num">Failed</th><th class="num">Skipped</th>
      <th>Report</th><th>Recording</th>
    </tr>
  </thead>
  <tbody>${tableRows}</tbody>
</table>

<footer>Build dir: ${BUILD}</footer>
</body>
</html>`;

fs.mkdirSync(REPORTS_DIR, { recursive: true });
fs.writeFileSync(OUT, html);
console.log(`Test report: ${OUT}`);

// Exit 1 on failures so Gradle marks generateTestReport as failed in CI
if (totalFailed > 0) process.exit(1);
