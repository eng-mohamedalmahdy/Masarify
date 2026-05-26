package tech.lightfeather.domain.usecase

import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeBackupRepository
import tech.lightfeather.domain.fake.FakeUserRepository
import tech.lightfeather.domain.model.ImportMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BackupUseCaseTest {
    @Test
    fun exportSuccessReturnsByteArray() =
        runTest {
            val result = ExportDataUseCase(FakeBackupRepository())()
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
        }

    @Test
    fun exportFailureReturnsDomainResultFailure() =
        runTest {
            val result = ExportDataUseCase(FakeBackupRepository(shouldFail = true))()
            assertTrue(result.isFailure)
        }

    @Test
    fun importCleanModeCallsCleanImport() =
        runTest {
            val repo = FakeBackupRepository()
            val data = byteArrayOf(1, 2, 3)
            val result = ImportDataUseCase(repo)(data, ImportMode.CLEAN)
            assertTrue(result.isSuccess)
            assertEquals(1, repo.importedData.size)
        }

    @Test
    fun importAppendModeCallsAppendImport() =
        runTest {
            val repo = FakeBackupRepository()
            val data = byteArrayOf(1, 2, 3)
            val result = ImportDataUseCase(repo)(data, ImportMode.APPEND)
            assertTrue(result.isSuccess)
            assertEquals(1, repo.importedData.size)
        }

    @Test
    fun clearLocalDataSuccessMarksClearedAndResetsSync() =
        runTest {
            val backupRepo = FakeBackupRepository()
            val userRepo = FakeUserRepository()
            val result = ClearLocalDataUseCase(backupRepo, userRepo)()
            assertTrue(result.isSuccess)
            assertTrue(backupRepo.cleared)
            assertEquals(0L, userRepo.lastSyncAtSet)
            assertTrue(userRepo.fcmTokenCleared)
        }

    @Test
    fun clearLocalDataFailureDoesNotResetSync() =
        runTest {
            val backupRepo = FakeBackupRepository(shouldFail = true)
            val userRepo = FakeUserRepository()
            val result = ClearLocalDataUseCase(backupRepo, userRepo)()
            assertTrue(result.isFailure)
            assertTrue(userRepo.lastSyncAtSet == null)
        }
}
