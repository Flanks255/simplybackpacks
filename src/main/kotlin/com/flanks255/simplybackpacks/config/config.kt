@file:Config(modid = simplybackpacks.MODID)
package com.flanks255.simplybackpacks.config

import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraftforge.common.config.Config

@Config.Name("BackpackItemBlacklist")
@Config.Comment("Items blacklisted from being inserted into backpacks(other item containing items...)")
@JvmField
var ConfigBackpackItemBlacklist : Array<String> = arrayOf(
        "rftools:storage_module_tablet",
        "thermalexpansion:satchel"
)