package com.hellmod.sailplanner.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val isAuthenticated: Boolean
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = if (isAuthenticated) Config.TripList else Config.Auth,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(config: Config, context: ComponentContext): Child =
        when (config) {
            Config.Auth -> Child.Auth(context)
            Config.TripList -> Child.TripList(context)
            is Config.TripDetail -> Child.TripDetail(context, config.tripId)
            is Config.Shopping -> Child.Shopping(context, config.tripId)
            is Config.Watches -> Child.Watches(context, config.tripId)
            is Config.Route -> Child.Route(context, config.tripId)
            is Config.Photos -> Child.Photos(context, config.tripId)
            is Config.Expenses -> Child.Expenses(context, config.tripId)
            is Config.Collage -> Child.Collage(context, config.tripId)
        }

    fun navigateTo(config: Config) = navigation.push(config)
    fun navigateBack() = navigation.pop()
    fun replaceWithAuth() = navigation.replaceAll(Config.Auth)
    fun replaceWithTripList() = navigation.replaceAll(Config.TripList)

    @Serializable
    sealed interface Config {
        @Serializable data object Auth : Config
        @Serializable data object TripList : Config
        @Serializable data class TripDetail(val tripId: String) : Config
        @Serializable data class Shopping(val tripId: String) : Config
        @Serializable data class Watches(val tripId: String) : Config
        @Serializable data class Route(val tripId: String) : Config
        @Serializable data class Photos(val tripId: String) : Config
        @Serializable data class Expenses(val tripId: String) : Config
        @Serializable data class Collage(val tripId: String) : Config
    }

    sealed interface Child {
        data class Auth(val context: ComponentContext) : Child
        data class TripList(val context: ComponentContext) : Child
        data class TripDetail(val context: ComponentContext, val tripId: String) : Child
        data class Shopping(val context: ComponentContext, val tripId: String) : Child
        data class Watches(val context: ComponentContext, val tripId: String) : Child
        data class Route(val context: ComponentContext, val tripId: String) : Child
        data class Photos(val context: ComponentContext, val tripId: String) : Child
        data class Expenses(val context: ComponentContext, val tripId: String) : Child
        data class Collage(val context: ComponentContext, val tripId: String) : Child
    }
}
