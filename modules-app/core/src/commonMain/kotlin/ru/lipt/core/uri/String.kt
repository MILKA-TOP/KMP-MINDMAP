package ru.lipt.core.uri

import com.eygraber.uri.Uri

fun String.uri() = Uri.parse(this)
