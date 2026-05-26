#!/usr/bin/env node
// Take a full-page screenshot of the Karma HTML test report after WASM tests complete.
// No CDP connection — no interference with test execution.
//
// Usage: node scripts/screenshot-wasm-report.js <composeAppBuildDir>

'use strict';

const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const BUILD = path.resolve(process.argv[2] || 'composeApp/build');
const REPORT = path.join(BUILD, 'reports', 'tests', 'wasmJsBrowserTest', 'index.html');
const OUT = path.join(BUILD, 'reports', 'recordings', 'wasm-test-results.png');

async function main() {
    if (!fs.existsSync(REPORT)) {
        console.log('[wasm-screenshot] Karma HTML report not found:', REPORT);
        return;
    }

    const browser = await chromium.launch();
    try {
        const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
        await page.goto('file://' + REPORT);
        await page.waitForLoadState('networkidle');
        fs.mkdirSync(path.dirname(OUT), { recursive: true });
        await page.screenshot({ path: OUT, fullPage: true });
        console.log('[wasm-screenshot] Saved:', OUT);
    } finally {
        await browser.close();
    }
}

main().catch(e => {
    console.error('[wasm-screenshot]', e.message);
    process.exit(1);
});
