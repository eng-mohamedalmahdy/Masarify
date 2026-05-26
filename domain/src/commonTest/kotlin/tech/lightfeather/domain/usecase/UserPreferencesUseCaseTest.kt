package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.fake.FakeUserRepository
import tech.lightfeather.domain.model.AppLanguages
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserPreferencesUseCaseTest {
    @Test
    fun getUserDarkModeReturnsFalseByDefault() {
        val result = GetUserDarkMode(FakeUserRepository())()
        assertFalse(result)
    }

    @Test
    fun toggleDarkModeDoesNotThrow() {
        ToggleDarkMode(FakeUserRepository())()
    }

    @Test
    fun getUserLanguageReturnsArabicWhenNotSet() {
        val result = GetUserLanguage(FakeUserRepository())()
        assertEquals(AppLanguages.Arabic, result)
    }

    @Test
    fun isOnboardingCompleteReturnsFalseByDefault() {
        val result = IsOnboardingComplete(FakeUserRepository())()
        assertFalse(result)
    }

    @Test
    fun markOnboardingCompleteDoesNotThrow() {
        MarkOnboardingComplete(FakeUserRepository())()
    }

    @Test
    fun getUserSavedColorsReturnsEmptyListByDefault() {
        val result = GetUserSavedColors(FakeUserRepository())()
        assertTrue(result.isEmpty())
    }
}
