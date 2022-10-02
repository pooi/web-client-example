package com.example.webclient.utils

import kotlinx.coroutines.runBlocking

object CoroutineTestUtils {
    @JvmStatic
    fun <T> executeSuspendFun(suspendMethod: suspend () -> T): T = runBlocking {
        suspendMethod()
    }
}
