package com.example.project9.model

import android.net.Uri
import java.net.URL

data class Image(
    var name: String = "",
    var uri: Uri = Uri.EMPTY,
    var url: String = ""
)