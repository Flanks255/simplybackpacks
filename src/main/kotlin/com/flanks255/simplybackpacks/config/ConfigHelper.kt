package com.flanks255.simplybackpacks.config

object ConfigHelper {
    fun checkBlacklist(item: String): Boolean {
        if (item in ConfigBackpackItemBlacklist)
            return true
        return false
    }
}