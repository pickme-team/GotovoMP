package org.example.project.presentation.util

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import gotovomp.composeapp.generated.resources.Res
import gotovomp.composeapp.generated.resources.cabin_bold
import gotovomp.composeapp.generated.resources.cabin_italic
import gotovomp.composeapp.generated.resources.cabin_medium
import gotovomp.composeapp.generated.resources.cabin_regular
import gotovomp.composeapp.generated.resources.cabin_semibold
import gotovomp.composeapp.generated.resources.plus_jakarta_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
fun makeTypography(): Typography {
  val baseline = Typography()
  val bodyFontFamily =
      FontFamily(
          Font(Res.font.cabin_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
          Font(Res.font.cabin_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
          Font(Res.font.cabin_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
          Font(Res.font.cabin_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
          Font(Res.font.cabin_italic, weight = FontWeight.Normal, style = FontStyle.Italic))
  val displayFontFamily =
      FontFamily(
          Font(
              Res.font.plus_jakarta_sans_regular,
              weight = FontWeight.Normal,
              style = FontStyle.Normal))
  return Typography(
      displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
      displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
      displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
      headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
      headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
      headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
      titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
      titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
      titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
      bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
      bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
      bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
      labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
      labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
      labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
  )
}

val primaryLight = Color(0xFF87521B)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDCC0)
val onPrimaryContainerLight = Color(0xFF2D1600)
val secondaryLight = Color(0xFF8F4B3A)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDAD2)
val onSecondaryContainerLight = Color(0xFF3A0A02)
val tertiaryLight = Color(0xFF406835)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFC1EFAF)
val onTertiaryContainerLight = Color(0xFF012200)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F5)
val onBackgroundLight = Color(0xFF221A14)
val surfaceLight = Color(0xFFFFF8F5)
val onSurfaceLight = Color(0xFF221A14)
val surfaceVariantLight = Color(0xFFF2DFD1)
val onSurfaceVariantLight = Color(0xFF51443A)
val outlineLight = Color(0xFF837469)
val outlineVariantLight = Color(0xFFD5C3B6)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF372F28)
val inverseOnSurfaceLight = Color(0xFFFEEEE3)
val inversePrimaryLight = Color(0xFFFEB877)
val surfaceDimLight = Color(0xFFE6D7CD)
val surfaceBrightLight = Color(0xFFFFF8F5)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1E8)
val surfaceContainerLight = Color(0xFFFBEBE1)
val surfaceContainerHighLight = Color(0xFFF5E5DB)
val surfaceContainerHighestLight = Color(0xFFEFE0D5)

val primaryDark = Color(0xFFFEB877)
val onPrimaryDark = Color(0xFF4B2700)
val primaryContainerDark = Color(0xFF6B3B03)
val onPrimaryContainerDark = Color(0xFFFFDCC0)
val secondaryDark = Color(0xFFFFB4A2)
val onSecondaryDark = Color(0xFF561F11)
val secondaryContainerDark = Color(0xFF723425)
val onSecondaryContainerDark = Color(0xFFFFDAD2)
val tertiaryDark = Color(0xFFA5D395)
val onTertiaryDark = Color(0xFF11380B)
val tertiaryContainerDark = Color(0xFF295020)
val onTertiaryContainerDark = Color(0xFFC1EFAF)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF19120C)
val onBackgroundDark = Color(0xFFEFE0D5)
val surfaceDark = Color(0xFF19120C)
val onSurfaceDark = Color(0xFFEFE0D5)
val surfaceVariantDark = Color(0xFF51443A)
val onSurfaceVariantDark = Color(0xFFD5C3B6)
val outlineDark = Color(0xFF9E8E82)
val outlineVariantDark = Color(0xFF51443A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEFE0D5)
val inverseOnSurfaceDark = Color(0xFF372F28)
val inversePrimaryDark = Color(0xFF87521B)
val surfaceDimDark = Color(0xFF19120C)
val surfaceBrightDark = Color(0xFF413730)
val surfaceContainerLowestDark = Color(0xFF130D07)
val surfaceContainerLowDark = Color(0xFF221A14)
val surfaceContainerDark = Color(0xFF261E18)
val surfaceContainerHighDark = Color(0xFF312822)
val surfaceContainerHighestDark = Color(0xFF3C332C)

val appLightScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

val appDarkScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )
