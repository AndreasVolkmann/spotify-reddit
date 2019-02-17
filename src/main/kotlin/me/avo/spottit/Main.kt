package me.avo.spottit

import com.apurebase.arkenv.parse
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein

fun main(args: Array<String>) {
    val arks = Arguments().parse(args)
    Spottit(arks, prodKodein).run()
}
