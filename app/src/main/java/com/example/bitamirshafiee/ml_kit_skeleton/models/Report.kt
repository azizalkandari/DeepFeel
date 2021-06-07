package com.example.bitamirshafiee.ml_kit_skeleton.models

import java.util.*

class Report {
    var path: String? = null
    var emotion: String? = null
    var eye: String? = null
    var happienesa: String? = null


    constructor() {}
    constructor(path: String?, emotion: String?, eye: String?, happienesa: String?) {
        this.path = path
        this.emotion = emotion
        this.eye = eye
        this.happienesa = happienesa
    }

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["path"] = path
        result["emotion"] = emotion
        result["eye"] = eye
        result["happienesa"] = happienesa
        return result
    }

    override fun toString(): String {
        return "Report{" +
                "path='" + path + '\'' +
                ", emotion='" + emotion + '\'' +
                ", eye='" + eye + '\'' +
                ", happienesa='" + happienesa + '\'' +
                '}'
    }
}