package tech.lightfeather.data.local.database

private fun sendExportMessage(): JsAny =
    js(
        """
        new Promise(function(resolve) {
            var worker = globalThis.__masarifyDbWorker;
            if (!worker) { resolve(null); return; }
            var id = 'exp_' + Math.random().toString(36).substr(2, 9);
            function handler(e) {
                if (e.data && e.data.id === id) {
                    worker.removeEventListener('message', handler);
                    resolve(e.data.results ? e.data.results.bytes : null);
                }
            }
            worker.addEventListener('message', handler);
            worker.postMessage({ id: id, action: 'export' });
        })
        """,
    )

@Suppress("UnusedParameter")
private fun sendImportCleanMessage(buffer: JsAny): JsAny =
    js(
        """
        new Promise(function(resolve) {
            var worker = globalThis.__masarifyDbWorker;
            if (!worker) { resolve(null); return; }
            var id = 'imp_' + Math.random().toString(36).substr(2, 9);
            function handler(e) {
                if (e.data && e.data.id === id) {
                    worker.removeEventListener('message', handler);
                    resolve(e.data.results && e.data.results.success ? {} : null);
                }
            }
            worker.addEventListener('message', handler);
            worker.postMessage({ id: id, action: 'import_clean', bytes: buffer }, [buffer]);
        })
        """,
    )

// Parameters are used inside js() blocks — suppress false-positive UnusedParameter warnings.
@Suppress("UnusedParameter")
private fun jsGetUint8Array(buffer: JsAny): JsAny = js("new Uint8Array(buffer)")

@Suppress("UnusedParameter")
private fun jsArrayLength(arr: JsAny): Int = js("arr.length")

@Suppress("UnusedParameter")
private fun jsArrayGet(
    arr: JsAny,
    index: Int,
): Int = js("arr[index]")

@Suppress("UnusedParameter")
private fun jsNewArrayBuffer(size: Int): JsAny = js("new ArrayBuffer(size)")

@Suppress("UnusedParameter")
private fun jsNewUint8FromBuffer(buf: JsAny): JsAny = js("new Uint8Array(buf)")

@Suppress("UnusedParameter")
private fun jsSetValue(
    arr: JsAny,
    index: Int,
    value: Int,
): Unit = js("arr[index] = value")

class WasmDbFileAccessor : DbFileAccessor {
    // WASM: WebWorkerDriver manages DB lifecycle; no external reset needed.
    override val needsExternalDriverReset: Boolean = false

    override suspend fun exportDatabaseBytes(): ByteArray? {
        val buffer: JsAny = sendExportMessage() ?: return null
        return jsArrayBufferToByteArray(buffer)
    }

    override suspend fun importDatabaseClean(bytes: ByteArray): Boolean {
        val buffer = byteArrayToJsArrayBuffer(bytes)
        val result: JsAny? = sendImportCleanMessage(buffer)
        return result != null
    }

    // Append not supported on WASM in v1 — caller handles null as "not supported".
    override suspend fun writeTempFile(bytes: ByteArray): String? = null

    override suspend fun deleteTempFile(path: String) = Unit

    // --- JS interop helpers ---

    private fun jsArrayBufferToByteArray(buffer: JsAny): ByteArray {
        val uint8 = jsGetUint8Array(buffer)
        val length = jsArrayLength(uint8)
        return ByteArray(length) { i -> jsArrayGet(uint8, i).toByte() }
    }

    private fun byteArrayToJsArrayBuffer(bytes: ByteArray): JsAny {
        val buffer = jsNewArrayBuffer(bytes.size)
        val uint8 = jsNewUint8FromBuffer(buffer)
        bytes.forEachIndexed { i, b -> jsSetValue(uint8, i, b.toInt() and 0xFF) }
        return buffer
    }
}
