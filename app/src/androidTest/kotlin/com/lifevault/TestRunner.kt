package com.lifevault

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom test runner для использования TestLifeVaultApplication вместо основного Application.
 */
class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestLifeVaultApplication::class.java.name, context)
    }
}
