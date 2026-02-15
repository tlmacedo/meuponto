// Arquivo: app/src/test/java/br/com/tlmacedo/meuponto/util/MainDispatcherRule.kt
package br.com.tlmacedo.meuponto.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit Rule que configura o Main dispatcher para testes.
 *
 * Substitui o Dispatchers.Main por um TestDispatcher durante os testes,
 * permitindo o controle do tempo e a execução síncrona de coroutines.
 *
 * @param testDispatcher Dispatcher de teste a ser usado (padrão: StandardTestDispatcher)
 *
 * @author Thiago
 * @since 2.0.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
