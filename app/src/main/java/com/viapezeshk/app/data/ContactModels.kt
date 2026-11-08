package com.viapezeshk.app.data

import com.google.gson.annotations.SerializedName

data class ContactConfig(
    @SerializedName("contacts") val contacts: List<ContactItem>?
)

data class ContactItem(
    @SerializedName("id") val id: Long,
    @SerializedName("icon") val icon: String,
    @SerializedName("title") val title: String,
    @SerializedName("value") val value: String,
    @SerializedName("type") val type: String // "url" یا "tel"
)