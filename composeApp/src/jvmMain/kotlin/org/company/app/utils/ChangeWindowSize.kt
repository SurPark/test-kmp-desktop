package org.company.app.utils

import composeWindow
import java.awt.Dimension

actual fun changeWindowSize(width: Int, height: Int) {
    composeWindow.size = Dimension(width, height)
}