package com.tsp.learn.recyclerview.diff

data class StudentBean(
    val id: Int,
    val name: String,
    val age: Int,
    val gender: Int
) {
    companion object {
        const val GENDER_BOY = 1
        const val GENDER_GIRL = 2

        fun genderLabel(gender: Int): String = when (gender) {
            GENDER_BOY -> "男"
            GENDER_GIRL -> "女"
            else -> "未知"
        }
    }
}