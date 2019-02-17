package me.avo.spottit

import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein

fun main(args: Array<String>) =
    Spottit(
        arguments = Arguments(args),
        kodein = prodKodein
    ).run()
