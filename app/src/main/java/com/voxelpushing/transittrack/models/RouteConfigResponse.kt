package com.voxelpushing.transittrack.models

object RouteConfigResponse {
    data class Result(val route: Route)
    data class Route(val direction: List<DirTag>)
    data class DirTag(val tag: String, val title: String)
}