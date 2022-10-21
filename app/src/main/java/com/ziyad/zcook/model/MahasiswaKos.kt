package com.ziyad.zcook.model

data class MahasiswaKos(
    val id:String,
    val name:String,
    val listSaved:ArrayList<Recipe>,
    val listPersonalNote:List<PersonalNote>
)
