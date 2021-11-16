package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*

sealed interface WeiboHelperCommand : Command {

    companion object : Collection<WeiboHelperCommand> {
        private val commands by lazy {
            WeiboHelperCommand::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = commands.size

        override fun contains(element: WeiboHelperCommand): Boolean = commands.contains(element)

        override fun containsAll(elements: Collection<WeiboHelperCommand>): Boolean = commands.containsAll(elements)

        override fun isEmpty(): Boolean = commands.isEmpty()

        override fun iterator(): Iterator<WeiboHelperCommand> = commands.iterator()

        operator fun get(name: String): WeiboHelperCommand = commands.first { it.primaryName.equals(name, true) }
    }
}