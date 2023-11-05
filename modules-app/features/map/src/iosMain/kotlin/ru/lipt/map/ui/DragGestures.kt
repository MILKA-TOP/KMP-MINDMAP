package ru.lipt.map.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope

actual suspend fun PointerInputScope.dragGestures(
    onDragStart: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) = this.detectDragGesturesAfterLongPress(
    onDragStart = onDragStart,
    onDragEnd = onDragEnd,
    onDragCancel = onDragCancel,
    onDrag = onDrag,
)
