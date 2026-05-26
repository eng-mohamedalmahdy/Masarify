/**
 * Clears the Masarify WASM SQLite database (OPFS: database.db).
 * Paste into the browser DevTools console while the app is running.
 *
 * The worker closes the DB, deletes the OPFS file, then reopens a fresh one.
 * The page reloads automatically on success.
 */
(async () => {
  const worker = globalThis.__masarifyDbWorker;
  if (!worker) {
    console.error("Worker not found. Is the app running?");
    return;
  }

  const id = "clear_" + Math.random().toString(36).slice(2, 9);

  await new Promise((resolve, reject) => {
    worker.addEventListener(
      "message",
      (e) => {
        if (e.data && e.data.id === id) {
          if (e.data.error) reject(new Error(e.data.error));
          else resolve();
        }
      },
      { once: true },
    );
    worker.postMessage({ id, action: "clear" });
  });

  console.log("Database cleared. Reloading…");
  location.reload();
})();
