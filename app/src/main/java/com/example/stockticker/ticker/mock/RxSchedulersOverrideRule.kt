package com.example.stockticker.ticker.mock

import com.github.mikephil.charting.components.Description
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import java.sql.Statement
import java.util.concurrent.Executor

class RxSchedulersOverrideRule : TestRule {

    private val immediate = object : Scheduler() {

        override fun createWorker(): Worker {
            return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class) override fun evaluate() {
                RxJavaPlugins.setInitIoSchedulerHandler { _ -> immediate }
                RxJavaPlugins.setInitComputationSchedulerHandler { _ -> immediate }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { _ -> immediate }
                RxJavaPlugins.setInitSingleSchedulerHandler { _ -> immediate }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> immediate }

                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }

}