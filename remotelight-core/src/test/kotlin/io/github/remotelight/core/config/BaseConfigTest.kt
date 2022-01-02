package io.github.remotelight.core.config

import io.github.remotelight.core.config.provider.PropertyProvider
import org.koin.test.junit5.AutoCloseKoinTest
import kotlin.random.Random

internal abstract class BaseConfigTest : AutoCloseKoinTest() {

    internal class TestPropertyProvider(private val amount: Int = 5) : PropertyProvider<Any?> {
        internal val properties = mutableMapOf<String, Any?>()

        override fun onInit() {
            properties.putAll(generateList())
        }

        override fun onClose() {}

        override fun storeProperties() {}

        override fun getProperties(): Map<String, *> {
            return properties
        }

        override fun getRawProperties(): Map<String, Any?> = properties

        override fun <T> getProperty(id: String, type: Class<T>): T {
            return properties[id] as T
        }

        override fun hasProperty(id: String): Boolean {
            return properties.containsKey(id)
        }

        override fun <T> setProperty(id: String, value: T) {
            properties[id] = value
        }

        override fun deleteProperty(id: String): Any? {
            return properties.remove(id)
        }

        override fun clear() {
            properties.clear()
        }

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