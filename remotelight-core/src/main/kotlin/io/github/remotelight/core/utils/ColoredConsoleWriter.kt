package io.github.remotelight.core.utils

import org.tinylog.Level
import org.tinylog.core.ConfigurationParser
import org.tinylog.core.LogEntry
import org.tinylog.core.LogEntryValue
import org.tinylog.provider.InternalLogger
import org.tinylog.writers.AbstractFormatPatternWriter
import org.tinylog.writers.Writer

// workaround to convert a java map to a kotlin accepted map
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private fun java.util.Map<String, String>.toHashMap(): HashMap<String, String> {
    val map = HashMap<String, String>()
    this.forEach { key, value ->
        map[key] = value
    }
    return map
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class ColoredConsoleWriter(properties: java.util.Map<String, String>): AbstractFormatPatternWriter(properties.toHashMap()), Writer {

    private val errorLevel: Level

    init {
        // code copied and translated to kotlin from org.tinylog.writers.ConsoleWriter
        // Set the default level for stderr logging
        var levelStream: Level = Level.WARN

        // Check stream property
        var stream = properties["stream"]
        if (stream != null) {
            // Check whether we have the err@LEVEL syntax
            val streams = stream.split("@", limit = 2).toTypedArray()
            if (streams.size == 2) {
                levelStream = ConfigurationParser.parse(streams[1], levelStream)
                if (streams[0] != "err") {
                    InternalLogger.log(
                        Level.ERROR,
                        "Stream with level must be \"err\", \"" + streams[0] + "\" is an invalid name"
                    )
                }
                stream = null
            }
        }

        errorLevel = if (stream == null) {
            levelStream
        } else if ("err".equals(stream, ignoreCase = true)) {
            Level.TRACE
        } else if ("out".equals(stream, ignoreCase = true)) {
            Level.OFF
        } else {
            InternalLogger.log(Level.ERROR, "Stream must be \"out\" or \"err\", \"$stream\" is an invalid stream name")
            levelStream
        }
    }

    override fun getRequiredLogEntryValues(): MutableCollection<LogEntryValue> {
        val logEntryValues = super.getRequiredLogEntryValues()
        logEntryValues.add(LogEntryValue.LEVEL)
        return logEntryValues
    }

    override fun write(logEntry: LogEntry) {
        val preFormatted = render(logEntry)
        val message = replacePlaceholders(logEntry, preFormatted)
        if (logEntry.level.ordinal < errorLevel.ordinal) {
            print(message)
        } else {
            System.err.print(message)
        }
    }

    private fun replacePlaceholders(logEntry: LogEntry, formatted: String): String {
        if(!formatted.contains('%')) return formatted
        var text = formatted
        // replace %level% placeholder with a color code for the current log level
        text = text.replace("%level%", codeFromLevel(logEntry.level), true)
        // replace any other color code placeholder
        Code.values().forEach { color ->
            text = text.replace("%${color.name}%", color.code, true)
        }
        return text
    }

    private fun codeFromLevel(level: Level): String = when(level) {
        Level.TRACE -> Code.DARK_GRAY.code
        Level.DEBUG -> Code.LIGHT_GRAY.code
        Level.INFO -> Code.WHITE.code
        Level.WARN -> Code.YELLOW.code
        Level.ERROR -> Code.RED.code
        else -> Code.RESET.code
    }

    override fun flush() {}

    override fun close() {}

    enum class Code(val code: String) {
        WHITE("\u001B[97m"),
        PURPLE("\u001B[34m"),
        CYAN("\u001B[36m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        RED("\u001B[31m"),
        LIGHT_GRAY("\u001B[37m"),
        DARK_GRAY("\u001B[90m"),
        RESET("\u001B[0m")
    }

}