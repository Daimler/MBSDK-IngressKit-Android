package com.daimler.mbingresskit.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.daimler.mbingresskit.BuildConfig
import java.io.File

class IngressFileProvider : FileProvider() {

    companion object {

        fun getUriForFile(context: Context, file: File): Uri {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getUriForFile(context, authority(context), file)
            } else {
                Uri.fromFile(file)
            }
        }

        private fun authority(context: Context) =
            "${context.applicationContext.packageName}.${BuildConfig.PROVIDER_AUTHORITY_SUFFIX}"
    }
}