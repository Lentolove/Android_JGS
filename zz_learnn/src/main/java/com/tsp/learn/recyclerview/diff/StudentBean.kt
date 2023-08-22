package com.tsp.learn.recyclerview.diff

data class StudentBean(val name : String, val age : Int, val  id : Int, val gender : Int) {
    object GENDER_GRIL {
        const val GENDER_GRIL : Int = 1
        const val GENDER_BOY : Int = 2
    }
}
