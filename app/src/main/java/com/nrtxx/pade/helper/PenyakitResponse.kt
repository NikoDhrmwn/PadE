package com.nrtxx.pade.helper

import com.google.gson.annotations.SerializedName

data class PenyakitResponse(

	@field:SerializedName("createTime")
	val createTime: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("updateTime")
	val updateTime: String,

	@field:SerializedName("fields")
	val fields: Fields
)

data class Fields(

	@field:SerializedName("Nama")
	val nama: Nama,

	@field:SerializedName("Gejala")
	val gejala: Gejala,

	@field:SerializedName("HAD")
	val HAD: HAD,

	@field:SerializedName("HAV")
	val HAV: HAV,

	@field:SerializedName("Info")
	val info: Info,

	@field:SerializedName("Penyebab")
	val penyebab: Penyebab
)

data class HAV(

	@field:SerializedName("stringValue")
	val stringValue: String
)

data class HAD(

	@field:SerializedName("stringValue")
	val stringValue: String
)

data class Nama(

	@field:SerializedName("stringValue")
	val stringValue: String
)

data class Gejala(

	@field:SerializedName("stringValue")
	val stringValue: String
)

data class Penyebab(

	@field:SerializedName("stringValue")
	val stringValue: String
)

data class Info(

	@field:SerializedName("stringValue")
	val stringValue: String
)
