package com.example.playlistmaker

import android.content.res.Resources

object Helpers {
    /**
     *  конвертация dp d pix
     *  */
    fun dpToPix(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
