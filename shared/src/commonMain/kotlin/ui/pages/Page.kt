package ui.pages

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
sealed class Page : Parcelable {
    data object SplashPage : Page()
    data object AppHostPage : Page()
    data object CreateBankAccountPageModel : Page()
    data object FillUserPrimaryDataPageModel : Page()
}
