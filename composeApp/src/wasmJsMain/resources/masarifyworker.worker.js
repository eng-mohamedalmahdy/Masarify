importScripts("sqlite3.js");

let db = null;
let sqlite3 = null;

async function createDatabase() {
  sqlite3 = await sqlite3InitModule();

  // TODO: Parameterize storage location, and storage type
  db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
}

async function handleMessage() {
  const data = this.data;

  switch (data && data.action) {
    case "exec":
      if (!data["sql"]) {
        throw new Error("exec: Missing query string");
      }

      return postMessage({
        id: data.id,
        results: { values: db.exec({ sql: data.sql, bind: data.params, returnValue: "resultRows" }) },
      })
    case "begin_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("BEGIN TRANSACTION;"),
      })
    case "end_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("END TRANSACTION;"),
      })
    case "rollback_transaction":
      return postMessage({
        id: data.id,
        results: db.exec("ROLLBACK TRANSACTION;"),
      })
    case "export":
      const exportBytes = db.serialize();
      return postMessage(
        { id: data.id, results: { bytes: exportBytes.buffer } },
        [exportBytes.buffer],
      );
    case "import_clean": {
      db.close();
      const opfsRoot = await navigator.storage.getDirectory();
      const fileHandle = await opfsRoot.getFileHandle("database.db", { create: true });
      const writable = await fileHandle.createWritable();
      await writable.write(data.bytes);
      await writable.close();
      db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
      return postMessage({ id: data.id, results: { success: true } });
    }
    case "clear": {
      db.close();
      const opfsRoot = await navigator.storage.getDirectory();
      try { await opfsRoot.removeEntry("database.db"); } catch (_) {}
      db = new sqlite3.oo1.DB("file:database.db?vfs=opfs", "c");
      return postMessage({ id: data.id, results: { success: true } });
    }
    default:
      throw new Error(`Unsupported action: ${data && data.action}`);
  }
}

function handleError(err) {
  return postMessage({
    id: this.data.id,
    error: err,
  });
}

if (typeof importScripts === "function") {
  db = null;
  const sqlModuleReady = createDatabase();
  self.onmessage = (event) => {
    return sqlModuleReady.then(handleMessage.bind(event))
    .catch(handleError.bind(event));
  }
}