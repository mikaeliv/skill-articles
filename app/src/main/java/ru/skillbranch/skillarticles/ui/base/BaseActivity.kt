package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

abstract class BaseActivity<T: BaseViewModel<out IViewModelState>> : AppCompatActivity() {

    protected abstract var viewModel: T
    protected abstract val layout: Int

    abstract fun setupViews()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(layout)
        setupViews()
    }

}