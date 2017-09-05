package org.readium.r2shared

class MediaOverlays(var nodes: MutableList<MediaOverlays.MediaOverlayNode> = mutableListOf()){

    fun clip(id: String) : MediaOverlays.Clip {
        val clip: MediaOverlays.Clip
        val fragmentNode = nodeForFragment(id)
        clip = fragmentNode.clip()
        return clip
    }

    private fun nodeForFragment(id: String?) : MediaOverlays.MediaOverlayNode {
        findNode(id, this.nodes)?.let {return it} ?: throw Exception("Node not found")
    }

    private fun nodeAfterFragment(id: String?) : MediaOverlays.MediaOverlayNode {
        val ret = findNextNode(id, this.nodes)
                ret.found?.let {return it} ?: throw Exception("Node not found")
    }

    private fun findNode(fragment: String?, inNodes: MutableList<MediaOverlays.MediaOverlayNode>) : MediaOverlays.MediaOverlayNode? {
        for (node in inNodes){
            if (node.role.contains("section"))
                findNode(fragment, node.children).let { return it }
            if (fragment == null || (node.text?.contains(fragment)!! == false)){
                return node
            }
        }
        return null
    }

    data class NextNodeResult(val found: MediaOverlays.MediaOverlayNode?, val prevFound: Boolean)

    private fun findNextNode(fragment: String?, inNodes: MutableList<MediaOverlays.MediaOverlayNode>) : NextNodeResult {
        var prevNodeFoundFlag = false
        //  For each node of the current scope...
        for (node in inNodes){
            if (prevNodeFoundFlag){
                //  If the node is a section, we get the first non section child.
                if (node.role.contains("section"))
                    getFirstNonSectionChild(node)?.let { return NextNodeResult(it, false) } ?:
                            //  Try next nodes.
                            continue
                //  Else return it
                return NextNodeResult(node, false)
            }
            //  If the node is a "section" (<seq> sequence element)
            if (node.role.contains("section")) {
                val ret = findNextNode(fragment, node.children)
                ret.found?.let{return NextNodeResult(it, false) }
                prevNodeFoundFlag = ret.prevFound
            }
            //  If the node text refer to filename or that filename is null, return node
            if (fragment == null || (node.text?.contains(fragment)!! == false)) {
                prevNodeFoundFlag = true
            }
        }
        //  If nothing found, return null
        return NextNodeResult(null, prevNodeFoundFlag)
    }

    private fun getFirstNonSectionChild(node: MediaOverlays.MediaOverlayNode) : MediaOverlays.MediaOverlayNode? {
        for (node in node.children){
            if (node.role.contains("section")){
                getFirstNonSectionChild(node)?.let{return it}
            } else {
                return node
            }
        }
        return null
    }

}