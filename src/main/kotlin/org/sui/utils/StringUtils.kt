package org.sui.utils

object StringUtils {

    /**
     * Extracts the first bracketed string from the input string.
     */
    fun cleanJsonListString(input: String): String {

        // * Checks if the given string starts with an empty JSON array ("[]").
        // * If it does, returns an empty list.
        if (input.startsWith("[]")) {
            return "[]"
        }

        var bracketCount = 0
        var inQuotes = false
        var start = -1
        var end = -1

        input.forEachIndexed { index, char ->
            when {
                char == '"' && input.getOrNull(index - 1) != '\\' -> inQuotes = !inQuotes
                char == '[' && !inQuotes -> {
                    bracketCount++
                    if (start == -1) start = index
                }

                char == ']' && !inQuotes -> {
                    bracketCount--
                    if (bracketCount == 0 && start != -1) {
                        end = index
                        return@forEachIndexed
                    }
                }
            }
        }

        return if (start != -1 && end != -1) {
            var cleanString = input.substring(start, end + 1)
            if (cleanString.endsWith("[warn]")) {
                // 去掉末尾的[warn]
                cleanString = cleanString.substring(0, cleanString.length - 6)
            }
            return cleanString
        } else {
            ""
        }
    }

    fun cleanJsonObjectString(input: String): String {
        var braceCount = 0
        var inQuotes = false
        var start = -1
        var end = -1

        input.forEachIndexed { index, char ->
            when {
                char == '"' && input.getOrNull(index - 1) != '\\' -> inQuotes = !inQuotes
                char == '{' && !inQuotes -> {
                    braceCount++
                    if (start == -1) start = index
                }

                char == '}' && !inQuotes -> {
                    braceCount--
                    if (braceCount == 0 && start != -1) {
                        end = index
                        return@forEachIndexed
                    }
                }
            }
        }

        return if (start != -1 && end != -1) {
            input.substring(start, end + 1)
        } else {
            ""
        }
    }

}