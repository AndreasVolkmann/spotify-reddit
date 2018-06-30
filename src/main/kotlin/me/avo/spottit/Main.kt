package me.avo.spottit

import me.avo.spottit.config.Arkuments
import me.avo.spottit.config.prodKodein

fun main(args: Array<String>) {
    val arks = Arkuments(args)
    Spottit(arks, prodKodein).run()
}