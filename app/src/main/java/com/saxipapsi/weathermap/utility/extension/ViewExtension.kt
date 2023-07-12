package com.saxipapsi.weathermap.utility.extension

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.saxipapsi.weathermap.R


fun ImageView.load(resource: String?, option : RequestOptions? = null) {
    context?.let {
        Glide.with(it).load(resource).apply(option ?: RequestOptions().placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.black)))).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                Log.d("Glide", "onLoadFailed() : ${e?.cause}")
                return false;
            }
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                Log.d("Glide", "onResourceReady()")
                return false;
            }
        }).dontAnimate().into(this)
    }
}