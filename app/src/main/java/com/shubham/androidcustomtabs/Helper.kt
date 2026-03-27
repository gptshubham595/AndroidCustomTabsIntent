package com.shubham.androidcustomtabs

import android.content.Context
import android.net.Uri
import android.widget.Toast

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun normalizeUrl(input: String): String {
    val trimmed = input.trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        trimmed
    } else {
        "https://$trimmed"
    }
}

fun buildLoginDemoUrl(input: String): String {
    val normalized = normalizeUrl(input)
    return if (normalized.endsWith("/")) normalized else "$normalized/"
}

fun extractSessionFromUri(uri: Uri?): DemoSession? {
    if (uri == null) return null
    val email = uri.getQueryParameter("email") ?: return null
    val name = uri.getQueryParameter("name") ?: email.substringBefore('@')
    val source = uri.getQueryParameter("source") ?: "custom-tabs"
    return DemoSession(
        name = name,
        email = email,
        source = source,
        transport = "deep-link"
    )
}
