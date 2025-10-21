package com.lanotech.xpost.data

// Post Data Class Definition
data class PostData(
    val id: Int,
    val userHandle: String,
    val userName: String,
    val time: String,
    val content: String,
    val url: String? = null, // The link associated with the post
    val urlText: String? = null // Optional URL text to display
)


// Define a sample post list for use in LazyColumn
val samplePosts = listOf(
    PostData(
        id = 1,
        userHandle = "@androiddev",
        userName = "Android Developers",
        time = "1h",
        content = "Jetpack Compose 1.7 brings better performance and Material 3 polish. Explore the release notes here:",
        url = "https://developer.android.com/jetpack/compose/releases",
        urlText = "developer.android.com/jetpack/compose/releases"
    ),
    PostData(
        id = 2,
        userHandle = "@material_design",
        userName = "Material Design",
        time = "2h",
        content = "Discover the latest Material Design 3 guidelines and examples for Android and web.",
        url = "https://m3.material.io/",
        urlText = "m3.material.io"
    ),
    PostData(
        id = 3,
        userHandle = "@wikipedia_ai",
        userName = "Wikipedia",
        time = "4h",
        content = "Learn what Large Language Models are and how they work under the hood.",
        url = "https://en.wikipedia.org/wiki/Large_language_model",
        urlText = "en.wikipedia.org/wiki/Large_language_model"
    ),
    PostData(
        id = 4,
        userHandle = "@google_research",
        userName = "Google Research",
        time = "6h",
        content = "Learn how AI is powering the next generation of creativity and reasoning tools.",
        url = "https://research.google/",
        urlText = "research.google"
    ),
    PostData(
        id = 5,
        userHandle = "@uxdesign",
        userName = "UX Design",
        time = "9h",
        content = "Explore design case studies and articles on accessibility, minimalism, and typography in modern UI.",
        url = "https://uxdesign.cc/",
        urlText = "uxdesign.cc"
    )
)
