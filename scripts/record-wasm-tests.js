#!/usr/bin/env node
// Record WASM browser tests via Chrome DevTools Protocol screencast.
// Gradle launches this before wasmJsBrowserTest and kills it (SIGTERM) after.
//
// Prerequisites:
//   npm install            (installs playwright)
//   brew install ffmpeg    (assembles frames into MP4; frames kept as fallback)
//
// Usage: node scripts/record-wasm-tests.js <outputDir>

'use strict';

const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const OUTPUT_DIR = path.resolve(process.argv[2] || 'build/reports/recordings');
const CDP_URL = 'http://localhost:9222';
const MAX_RETRIES = 60;   // 30 s total (60 * 500 ms)
const RETRY_MS = 500;

async function connectWithRetry() {
    for (let i = 0; i < MAX_RETRIES; i++) {
        try {
            return await chromium.connectOverCDP(CDP_URL);
        } catch {
            await new Promise(r => setTimeout(r, RETRY_MS));
        }
    }
    throw new Error(`CDP not available at ${CDP_URL} after ${(MAX_RETRIES * RETRY_MS) / 1000}s`);
}

async function main() {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    const framesDir = path.join(OUTPUT_DIR, '_wasm_frames');
    fs.mkdirSync(framesDir, { recursive: true });

    console.log('[wasm-recorder] Waiting for browser CDP endpoint...');
    const browser = await connectWithRetry();
    console.log('[wasm-recorder] Connected.');

    const contexts = browser.contexts();
    const page = contexts[0]?.pages()[0];
    if (!page) throw new Error('No page found — Karma may not have opened one yet.');

    const cdp = await contexts[0].newCDPSession(page);
    let frameIndex = 0;

    cdp.on('Page.screencastFrame', async ({ data, sessionId }) => {
        const framePath = path.join(framesDir, `frame-${String(frameIndex++).padStart(6, '0')}.jpg`);
        fs.writeFileSync(framePath, Buffer.from(data, 'base64'));
        try { await cdp.send('Page.screencastFrameAck', { sessionId }); } catch { /* closed */ }
    });

    await cdp.send('Page.startScreencast', {
        format: 'jpeg',
        quality: 80,
        maxWidth: 1280,
        maxHeight: 720,
        everyNthFrame: 3,
    });
    console.log('[wasm-recorder] Screencast started.');

    // Wait until Gradle sends SIGTERM/SIGINT (doLast hook)
    await new Promise(resolve => {
        process.on('SIGTERM', resolve);
        process.on('SIGINT', resolve);
    });

    try { await cdp.send('Page.stopScreencast'); } catch { /* ignore if browser already closed */ }
    await browser.close();
    console.log(`[wasm-recorder] Captured ${frameIndex} frames.`);

    if (frameIndex === 0) {
        console.warn('[wasm-recorder] No frames — skipping video assembly.');
        return;
    }

    const videoPath = path.join(OUTPUT_DIR, 'wasm-test.mp4');
    try {
        execSync(
            `ffmpeg -y -framerate 10 -i "${framesDir}/frame-%06d.jpg"` +
            ` -c:v libx264 -pix_fmt yuv420p "${videoPath}"`,
            { stdio: 'inherit' },
        );
        fs.rmSync(framesDir, { recursive: true, force: true });
        console.log(`[wasm-recorder] Video saved: ${videoPath}`);
    } catch {
        console.warn(`[wasm-recorder] ffmpeg not found. Frames kept at: ${framesDir}`);
        console.warn('  Install with: brew install ffmpeg');
    }
}

main().catch(e => {
    console.error('[wasm-recorder] Fatal:', e.message);
    process.exit(1);
});
