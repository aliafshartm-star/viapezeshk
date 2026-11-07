package com.viapezeshk.app.data

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("banners") val banners: List<Banner>?
)

data class Banner(
    @SerializedName("id") val id: String,
    @SerializedName("position") val position: String, // مثلا home_main
    @SerializedName("type") val type: String, // image, text_only, image_text
    @SerializedName("active") val active: Boolean,
    @SerializedName("priority") val priority: Int = 99,
    @SerializedName("targeting") val targeting: Targeting?,
    @SerializedName("content") val content: BannerContent,
    @SerializedName("action") val action: BannerAction?
)

data class Targeting(
    @SerializedName("cities") val cities: List<String>?
)

data class BannerContent(
    @SerializedName("title") val title: String?,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("bgColor") val bgColor: String?,
    @SerializedName("textColor") val textColor: String?,
    @SerializedName("image") val image: String?
)

data class BannerAction(
    @SerializedName("type") val type: String, // url, tel, internal_page
    @SerializedName("value") val value: String
)