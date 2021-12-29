package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import org.koin.test.junit5.AutoCloseKoinTest
import kotlin.random.Random

internal abstract class BaseConfigTest : AutoCloseKoinTest() {

    internal class EmptyConfigCallback : ConfigChangeCallback {
        override fun onConfigChange(properties: Map<String, Any?>) {}
    }

    internal fun <T : Config> createTestConfig(
        configBuilder: (ConfigChangeCallback) -> T
    ): T {
        val testCallback = TestConfigLoader()
        val config = configBuilder(testCallback)
        testCallback.loadConfigPropertyValues(config)
        return config
    }

    internal class TestConfigLoader(private val amount: Int = 5) : ConfigChangeCallback, ConfigLoader {
        internal val properties: MutableMap<String, Any?> = generateList()

        fun loadConfigPropertyValues(config: Config) = config.setPropertyValues(properties)

        fun storeConfigPropertyValues(properties: Map<String, Any?>) {
            this.properties.clear()
            this.properties.putAll(properties)
        }

        override fun onConfigChange(properties: Map<String, Any?>) {
            storeConfigPropertyValues(properties)
        }

        override fun loadPropertyValues(): PropertyValuesWrapper? {
            return PropertyValuesWrapper(properties = properties)
        }

        override fun storePropertyValues(valuesWrapper: PropertyValuesWrapper) {
            storeConfigPropertyValues(valuesWrapper.properties)
        }

        override fun getSource() = "Test Config Loader"

        private fun generateList(): MutableMap<String, Any?> {
            val map = buildMap<String, Any?>(amount) {
                for (i in 0 until amount-1) {
                    put("test_$i", getRandomValue())
                }
            }.toMutableMap()
            if (amount > 0) {
                map["test"] = "last"
            }
            return map
        }

        private fun getRandomValue(): Any {
            return when (Random.nextInt(4)) {
                0 -> "Test String ${Random.nextLong()}"
                1 -> (System.currentTimeMillis() % 2L) == 0L
                2 -> Random.nextDouble()
                else -> Random.nextInt()
            }
        }
    }

}