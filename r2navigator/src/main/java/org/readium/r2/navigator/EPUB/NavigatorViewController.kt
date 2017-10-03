package org.readium.r2.navigator.EPUB

interface NavigatorDelegate {
    fun middleTapHandler()
    fun willExitPublication(documentIndex: Int, progression: Double?)
}