package pages

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize


@Parcelize
sealed class BottomNavigationPage : Parcelable {

    data object HomePage : BottomNavigationPage()

    data object MorePage : BottomNavigationPage()
}