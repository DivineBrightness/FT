#!/usr/bin/env kotlin

/**
 * BOOK.md 解析脚本
 *
 * 运行此脚本将 BOOK.md 转换为 hexagram_data.json
 *
 * 使用方法：
 * kotlin parse_book.kts
 */

import java.io.File

// 读取 BOOK.md
val bookFile = File("BOOK.md")
if (!bookFile.exists()) {
    println("错误：BOOK.md 文件不存在！")
    System.exit(1)
}

println("正在读取 BOOK.md...")
val content = bookFile.readText()

println("正在解析...")

// 这里需要手动解析，因为我们不能在脚本中使用 kotlinx.serialization
// 简化版本的解析和JSON生成

val hexagrams = mutableListOf<Map<String, Any>>()

// 按 "---" 分割各卦
val sections = content.split("\n---\n").filter { it.contains("## 第") }

println("找到 ${sections.size} 个卦象")

sections.forEachIndexed { index, section ->
    try {
        val lines = section.split("\n").map { it.trim() }
        var currentLine = 0

        // 解析标题
        val titleLine = lines[currentLine++]
        val titleRegex = """##\s*第(\d+)卦\s+(\S+)\(([^)]+)\)\s*(\S*)""".toRegex()
        val titleMatch = titleRegex.find(titleLine)
        if (titleMatch == null) {
            println("  跳过：无法解析标题 - $titleLine")
            return@forEachIndexed
        }

        val id = titleMatch.groupValues[1].toInt()
        val fullName = titleMatch.groupValues[3]
        val unicode = titleMatch.groupValues[4].ifEmpty { "" }

        currentLine++ // 空行

        // 卦象
        val trigramLine = lines.find { it.startsWith("**卦象**:") } ?: ""
        val trigramRegex = """上([^☰☷☵☲☳☴☶☱]+)[☰☷☵☲☳☴☶☱]下([^☰☷☵☲☳☴☶☱]+)""".toRegex()
        val trigramMatch = trigramRegex.find(trigramLine)
        val (upperTrigram, lowerTrigram) = if (trigramMatch != null) {
            trigramMatch.groupValues[1] to trigramMatch.groupValues[2]
        } else {
            val parts = trigramLine.replace("**卦象**:", "").trim()
            val upper = parts.substringBefore("下").removePrefix("上").trim()
            val lower = parts.substringAfter("下").substringBefore("☰").substringBefore("☷").substringBefore("☵").substringBefore("☲").substringBefore("☳").substringBefore("☴").substringBefore("☶").substringBefore("☱").trim()
            upper to lower
        }

        // 卦辞、象辞、彖辞
        val guaCi = lines.find { it.startsWith("**卦辞**:") }?.replace("**卦辞**:", "")?.trim() ?: ""
        val xiangCi = lines.find { it.startsWith("**象辞**:") }?.replace("**象辞**:", "")?.trim() ?: ""
        val tuanCi = lines.find { it.startsWith("**彖辞**:") }?.replace("**彖辞**:", "")?.trim() ?: ""

        // 应用
        val applicationText = lines.find { it.startsWith("**应用**:") }?.replace("**应用**:", "")?.trim() ?: ""

        // 解析爻
        val yaos = mutableListOf<Map<String, Any>>()
        val yaoRegex = """\*\*([初九二三四五六上]+)\*\*:\s*(.+)""".toRegex()

        var i = 0
        var yaoPos = 1
        while (i < lines.size && yaoPos <= 6) {
            val line = lines[i]
            val match = yaoRegex.find(line)
            if (match != null) {
                val yaoName = match.groupValues[1]
                // 跳过"用九"和"用六"
                if (!yaoName.startsWith("用")) {
                    val yaoText = match.groupValues[2]
                    var xiangText = ""
                    var meaning = ""

                    if (i + 1 < lines.size && lines[i + 1].startsWith("- 象曰:")) {
                        xiangText = lines[i + 1].replace("- 象曰:", "").trim()
                    }
                    if (i + 2 < lines.size && lines[i + 2].startsWith("- 译:")) {
                        meaning = lines[i + 2].replace("- 译:", "").trim()
                    }

                    yaos.add(
                        mapOf(
                            "position" to yaoPos++,
                            "name" to yaoName,
                            "text" to yaoText,
                            "xiangText" to xiangText,
                            "meaning" to meaning
                        )
                    )
                }
            }
            i++
        }

        // 生成爻组合
        val trigramMap = mapOf(
            "乾" to "111", "坤" to "000", "坎" to "010", "离" to "101",
            "震" to "001", "巽" to "110", "艮" to "100", "兑" to "011"
        )
        val linesStr = (trigramMap[lowerTrigram] ?: "111") + (trigramMap[upperTrigram] ?: "111")

        val hexagram = mapOf(
            "id" to id,
            "name" to fullName,
            "upperTrigram" to upperTrigram,
            "lowerTrigram" to lowerTrigram,
            "lines" to linesStr,
            "unicode" to unicode,
            "guaCi" to guaCi,
            "xiangCi" to xiangCi,
            "tuanCi" to tuanCi,
            "meaning" to "（卦义解读）",
            "fortune" to if (applicationText.contains("大吉")) "大吉" else if (applicationText.contains("吉")) "吉" else "中平",
            "career" to applicationText,
            "love" to applicationText,
            "health" to applicationText,
            "wealth" to applicationText,
            "yaos" to yaos
        )

        hexagrams.add(hexagram)
        println("  ✓ 第 $id 卦: $fullName (${yaos.size} 爻)")

    } catch (e: Exception) {
        println("  ✗ 解析第 ${index + 1} 卦时出错: ${e.message}")
    }
}

// 生成 JSON
println("\n正在生成 JSON...")

fun Any?.toJsonValue(): String = when (this) {
    null -> "null"
    is String -> "\"${this.replace("\"", "\\\"").replace("\n", "\\n")}\""
    is Number -> this.toString()
    is Boolean -> this.toString()
    is List<*> -> "[${this.joinToString(",") { it.toJsonValue() }}]"
    is Map<*, *> -> "{${this.entries.joinToString(",") { "\"${it.key}\":${it.value.toJsonValue()}" }}}"
    else -> "\"$this\""
}

val json = """
{
  "hexagrams": ${hexagrams.toJsonValue()}
}
""".trimIndent()

// 写入文件
val outputFile = File("app/src/main/res/raw/hexagram_data.json")
outputFile.parentFile.mkdirs()
outputFile.writeText(json)

println("✓ JSON 文件已生成: ${outputFile.absolutePath}")
println("✓ 共解析 ${hexagrams.size} 个卦象")
