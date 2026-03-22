@file:Suppress("ForbiddenImport")

package com.lightfeather.masarify.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private val TealPrimary = Color(0xFF055250)

@Suppress("UnusedPrivateProperty")
private val GoldAccent = Color(0xFFD9B382)
private val SurfaceLow = Color(0xFFF6F3EE)

@Suppress("UnusedPrivateProperty")
private val SecondaryText = Color(0xFF775930)

class ExpenseCategoriesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val categories = loadCategories(context)

        provideContent {
            GlanceTheme {
                WidgetContent(context = context, categories = categories)
            }
        }
    }

    private fun loadCategories(context: Context): List<WidgetCategory> {
        val prefs =
            context.getSharedPreferences(
                WidgetDataSyncService.PREFS_NAME,
                Context.MODE_PRIVATE,
            )
        val json = prefs.getString(WidgetDataSyncService.KEY_EXPENSE_CATEGORIES, null) ?: return emptyList()
        return runCatching { Json.decodeFromString<List<WidgetCategory>>(json) }.getOrElse { emptyList() }
    }

    companion object {
        const val MAX_CATEGORIES = 5
    }
}

@Composable
private fun WidgetContent(
    context: Context,
    categories: List<WidgetCategory>,
) {
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(TealPrimary))
                .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Masarify QUICK TRANSACTION",
                    style =
                        TextStyle(
                            color = ColorProvider(SurfaceLow),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        ),
                )
//                Text(
//                    text = "QUICK TRANSACTION",
//                    style = TextStyle(
//                        color = ColorProvider(SurfaceLow),
//                        fontSize = 10.sp,
//                    ),
//                )
            }
        }

        Spacer(modifier = GlanceModifier.height(10.dp))

        if (categories.isEmpty()) {
            Text(
                text = "Open app to load categories",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant),
            )
        } else {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalAlignment = Alignment.Top,
            ) {
                categories.take(ExpenseCategoriesWidget.MAX_CATEGORIES).forEachIndexed { index, category ->
                    if (index > 0) Spacer(modifier = GlanceModifier.width(12.dp))
                    CategoryButton(context = context, category = category)
                }
            }
        }
    }
}

private fun WidgetCategory.resolveDisplayName(context: Context): String =
    if (resourceKey != null) {
        runCatching {
            context.getString(getCategoryStringResourceByKey(resourceKey!!).resourceId)
        }.getOrElse { name }
    } else {
        name
    }

@SuppressLint("RestrictedApi")
@Suppress("LongMethod")
@Composable
private fun CategoryButton(
    context: Context,
    category: WidgetCategory,
) {
    val displayName = category.resolveDisplayName(context)
    val deepLinkUri =
        Uri
            .Builder()
            .scheme("masarify")
            .authority("transactions")
            .appendQueryParameter("openAddDialog", "true")
            .appendQueryParameter("type", "expense")
            .appendQueryParameter("categoryId", category.id)
            .build()

    val intent =
        Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            setPackage(context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    val iconBitmap = remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(category.icon) {
        if (category.icon.isNotBlank()) {
            val bitmap =
                withContext(Dispatchers.IO) {
                    runCatching {
                        val request =
                            ImageRequest
                                .Builder(context)
                                .data(category.icon)
                                .allowHardware(false)
                                .build()
                        val result = ImageLoader(context).execute(request)
                        (result as? SuccessResult)?.image?.toBitmap()
                    }.getOrNull()
                }
            iconBitmap.value = bitmap
        }
    }

    Column(
        modifier =
            GlanceModifier
                .width(56.dp)
                .clickable(actionStartActivity(intent)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                GlanceModifier
                    .width(44.dp)
                    .height(44.dp)
                    .cornerRadius(22.dp)
                    .background(ColorProvider(SurfaceLow)),
            contentAlignment = Alignment.Center,
        ) {
            if (iconBitmap.value != null) {
                Image(
                    provider = ImageProvider(iconBitmap.value!!),
                    contentDescription = displayName,
                    modifier = GlanceModifier.width(22.dp).height(22.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = displayName.uppercase(),
            style =
                TextStyle(
                    color = GlanceTheme.colors.surface,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

class ExpenseCategoriesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ExpenseCategoriesWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
