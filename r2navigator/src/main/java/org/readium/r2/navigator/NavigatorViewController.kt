package org.readium.r2.navigator

import org.readium.r2.shared.Publication

interface NavigatorDelegate {
    fun middleTapHandler()
    fun willExitPublication(documentIndex: Int, progression: Double?)
}

class NavigatorViewController (publication: Publication, initialIndex: Int, initialProgression: Double?) {
    private val delegatee: Delegatee
    lateinit private var triptychView: TriptychView
    var userSettings: UserSettings
    private var initialProgression: Double?

    val publication: Publication
    var delegate: NavigatorDelegate? = null

    init {
        this.publication = publication
        this.initialProgression = initialProgression
        userSettings = UserSettings()
        delegatee = Delegatee()
        var index = initialIndex
        if (initialIndex == -1) {
            index = publication.spine.count()
        }
    }

    fun displaySpineItem(href: String ){

    }

}

private class Delegatee {
    lateinit var parent: NavigatorViewController
    private var firstView = true
}