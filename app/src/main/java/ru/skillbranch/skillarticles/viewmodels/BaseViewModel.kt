package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*
import java.lang.IllegalArgumentException

abstract class BaseViewModel<T>(initState: T) : ViewModel() {

    protected val notifications = MutableLiveData<Event<Notify>>()

    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    // not null current state
    protected val currentState
        get() = state.value!!

    //лямбда выражение принимает в качестве аргумента лямбду, в которую передается текущее состояние
    //и она возвращает модифицированное состояние, которое присваивается текущему состоянию
    @UiThread
    protected inline fun updateState(update: (currentState:T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    @UiThread
    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    //более компактная форма записи observe принимает последним аргументом лямбда выражение, обрабатывающее
    //изменение текущего состояния
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer{ onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver{ onNotify(it) })
    }

    //функция принимает источник данных и лямбда выражение, обрабатывающее поступающие данные
    //лямбда принимает новые данные и текущее состояние, изменяет его и возвращает
    //модифицированное состояние устанавливается как текущее
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newState: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }
}

class ViewModelFactory(private val params: String): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit): Observer<Event<E>> {
    override fun onChanged(t: Event<E>?) {
        t?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify(val message: String) {
    data class TextMessage(val msg: String): Notify(msg)

    data class ActionMessage(
        val msg: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)?
    ): Notify(msg)

    data class ErrorMessage(
        val msg: String,
        val errorLabel: String,
        val errorHandler: (() -> Unit)?
    ): Notify(msg)
}