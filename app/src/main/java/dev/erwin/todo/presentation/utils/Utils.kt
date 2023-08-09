package dev.erwin.todo.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.Date
import kotlin.random.Random

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

typealias OnItemClickListener = (id: String?) -> Unit

val randomRatio get() = Random.nextInt(from = 1, until = 4)

fun createSpannableText(text: String, isDarkModeActive: Int, start: Int, end: Int): Spannable {
    val spannableText = SpannableString(text)
    when (isDarkModeActive) {
        Configuration.UI_MODE_NIGHT_NO -> {
            spannableText.setSpan(
                ForegroundColorSpan(Color.BLUE),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        Configuration.UI_MODE_NIGHT_YES -> {
            spannableText.setSpan(
                ForegroundColorSpan(Color.CYAN),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    return spannableText
}

fun String.isEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
    return Regex(emailRegex).matches(this)
}

fun String.isPasswordSecure(): Boolean = this.trim().length >= 8

fun Long.withDateFormat(): String {
    val date = Date(this)
    return DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
}