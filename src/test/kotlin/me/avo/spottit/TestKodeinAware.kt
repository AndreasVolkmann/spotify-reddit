package me.avo.spottit

import me.avo.spottit.config.prodKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

interface TestKodeinAware : KodeinAware {

    override val kodein: Kodein get() = prodKodein

}