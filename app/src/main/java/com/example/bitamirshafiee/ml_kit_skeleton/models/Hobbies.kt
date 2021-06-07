package com.example.bitamirshafiee.ml_kit_skeleton.models

import java.util.*

class Hobbies {
    var hobby1: String? = null
    var hobby2: String? = null
    var hobby3: String? = null


    constructor() {}
    constructor(hobby1: String?, hobby2: String?, hobby3: String?) {
        this.hobby1 = hobby1
        this.hobby2 = hobby2
        this.hobby3 = hobby3

    }

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["hobby1"] = hobby1
        result["hobby2"] = hobby2
        result["hobby2"] = hobby3
        return result
    }

    override fun toString(): String {
        return "Hobbies{" +
                "hobby1='" + hobby1 + '\'' +
                ", hobby1='" + hobby2 + '\'' +
                ", hobby1='" + hobby3 + '\'' +
                '}'
    }
}