// Chrome launcher reads CHROME_BIN to find the browser binary.
// Set it to Brave (Chromium-based, compatible) before any launcher resolves.
process.env.CHROME_BIN = '/Applications/Brave Browser.app/Contents/MacOS/Brave Browser';

// Kotlin/WASM requires SharedArrayBuffer, which browsers only allow when the page is
// cross-origin isolated (COOP + COEP headers). Karma's built-in server doesn't send
// these headers, so we inject them via middleware.
config.plugins = config.plugins || [];
config.plugins.push({
    'middleware:coop-coep': ['factory', function () {
        return function (req, res, next) {
            res.setHeader('Cross-Origin-Opener-Policy', 'same-origin');
            res.setHeader('Cross-Origin-Embedder-Policy', 'require-corp');
            next();
        };
    }],
});
config.beforeMiddleware = config.beforeMiddleware || [];
config.beforeMiddleware.push('coop-coep');

// WASM module load + Kotlin runtime init takes time — extend all timeouts
config.set({
    captureTimeout: 120000,
    browserDisconnectTimeout: 10000,
    browserNoActivityTimeout: 120000,
});
