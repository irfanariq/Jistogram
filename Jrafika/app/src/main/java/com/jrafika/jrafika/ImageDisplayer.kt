package com.jrafika.jrafika

import com.jrafika.jrafika.core.Image

interface ImageDisplayer {

    fun setInput(image: Image)

    fun setImage(image: Image)

    fun setLoading()

}