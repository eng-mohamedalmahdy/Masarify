package tech.lightfeather.designsystem.shape

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun inWardTriangleCutShape(
    tailSize: Dp,
    topSpacePercentage: Float,
): Shape {
    val currentDensity = LocalDensity.current

    return GenericShape { size, _ ->
        val cut = with(currentDensity) { tailSize.toPx() }
        val triangleTopY = size.height * topSpacePercentage
        val triangleCenterY = triangleTopY + cut / 2f
        val triangleBottomY = triangleTopY + cut

        // Create a rectangle with inward triangular cut on the right side
        // Start from top-left
        moveTo(0f, 0f)
        // Draw to top-right corner
        lineTo(size.width, 0f)
        // Draw down to the start of the inward cut
        lineTo(size.width, triangleTopY)
        // Draw the inward triangular cut (pointing inward to the left)
        lineTo(size.width - cut, triangleCenterY)
        // Complete the triangle by going back to the right edge
        lineTo(size.width, triangleBottomY)
        // Draw down to bottom-right corner
        lineTo(size.width, size.height)
        // Draw to bottom-left corner
        lineTo(0f, size.height)
        // Close the path back to start
        close()
    }
}
