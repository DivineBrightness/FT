package com.yijing.divination

import android.app.Application
import com.yijing.divination.data.DatabaseInitializer

import dagger.hilt.android.HiltAndroidApp

import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.SupervisorJob

import kotlinx.coroutines.launch

import javax.inject.Inject



/**

 * 易经占卜应用主类

 *

 * 使用 Hilt 进行依赖注入

 */

@HiltAndroidApp

class YiJingApplication : Application() {



    @Inject

    lateinit var databaseInitializer: DatabaseInitializer



    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)



    override fun onCreate() {

        super.onCreate()



        // 初始化数据库

        applicationScope.launch {

            databaseInitializer.initializeIfNeeded()

        }
    }
}
