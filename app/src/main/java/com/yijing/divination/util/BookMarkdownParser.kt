package com.yijing.divination.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * BOOK.md 解析器
 *
 * 将易经六十四卦数据手册（BOOK.md）解析为结构化的 JSON 数据
 */
object BookMarkdownParser {

    private val json = Json { prettyPrint = true }

    /**
     * 解析 BOOK.md 内容
     *
     * @param content BOOK.md 的完整文本内容
     * @return HexagramDataRoot 包含所有卦象的数据结构
     */
    fun parse(content: String): HexagramDataRoot {
        val hexagrams = mutableListOf<HexagramData>()

        // 按 "---" 分割各卦
        val sections = content.split("\n---\n")
            .filter { it.contains("## 第") }

        sections.forEachIndexed { index, section ->
            try {
                val hexagram = parseSection(section.trim(), index + 1)
                hexagrams.add(hexagram)
            } catch (e: Exception) {
                println("解析第 ${index + 1} 卦时出错: ${e.message}")
                e.printStackTrace()
            }
        }

        return HexagramDataRoot(hexagrams)
    }

    /**
     * 解析单个卦的数据
     */
    private fun parseSection(section: String, expectedId: Int): HexagramData {
        val lines = section.split("\n").map { it.trim() }
        var currentLine = 0

        // 解析卦标题：## 第1卦 乾卦(乾为天) ䷀
        val titleLine = lines[currentLine++]
        val titleRegex = """##\s*第(\d+)卦\s+(\S+)\(([^)]+)\)\s*(\S*)""".toRegex()
        val titleMatch = titleRegex.find(titleLine)
            ?: throw IllegalArgumentException("无法解析卦标题: $titleLine")

        val id = titleMatch.groupValues[1].toInt()
        val shortName = titleMatch.groupValues[2]
        val fullName = titleMatch.groupValues[3]
        val unicode = titleMatch.groupValues[4].ifEmpty { "" }

        currentLine++ // 跳过空行

        // 解析卦象：**卦象**: 上乾☰下乾☰
        val trigramLine = findNextValueLine(lines, currentLine, "**卦象**:")
        val trigramRegex = """上([^☰☷☵☲☳☴☶☱]+)[☰☷☵☲☳☴☶☱]下([^☰☷☵☲☳☴☶☱]+)""".toRegex()
        val trigramMatch = trigramRegex.find(trigramLine)
        val (upperTrigram, lowerTrigram) = if (trigramMatch != null) {
            trigramMatch.groupValues[1] to trigramMatch.groupValues[2]
        } else {
            // 尝试简单解析
            val parts = trigramLine.replace("**卦象**:", "").trim()
            val upper = parts.substringBefore("下").removePrefix("上").trim()
            val lower = parts.substringAfter("下").trim()
            upper to lower
        }

        currentLine++

        // 解析卦辞
        val guaCi = findNextValueLine(lines, currentLine, "**卦辞**:")
            .replace("**卦辞**:", "").trim()
        currentLine++

        // 解析象辞
        val xiangCi = findNextValueLine(lines, currentLine, "**象辞**:")
            .replace("**象辞**:", "").trim()
        currentLine++

        // 解析彖辞
        val tuanCi = findNextValueLine(lines, currentLine, "**彖辞**:")
            .replace("**彖辞**:", "").trim()
        currentLine++

        // 跳过 "**爻辞**:"
        while (currentLine < lines.size && !lines[currentLine].startsWith("**爻辞**:")) {
            currentLine++
        }
        currentLine++ // 跳过 "**爻辞**:"
        currentLine++ // 跳过空行

        // 解析6爻
        val yaos = mutableListOf<YaoData>()
        var yaoPosition = 1

        while (currentLine < lines.size && yaoPosition <= 6) {
            val yaoLine = lines[currentLine]

            // 跳过空行
            if (yaoLine.isBlank()) {
                currentLine++
                continue
            }

            // 检查是否是 "**应用**:" 开始
            if (yaoLine.startsWith("**应用**:") || yaoLine.startsWith("**用")) {
                break
            }

            // 解析爻名和爻辞：**初九**: 潜龙勿用。
            if (yaoLine.startsWith("**")) {
                val yaoRegex = """\*\*([^*]+)\*\*:\s*(.+)""".toRegex()
                val yaoMatch = yaoRegex.find(yaoLine)

                if (yaoMatch != null) {
                    val yaoName = yaoMatch.groupValues[1].trim()
                    val yaoText = yaoMatch.groupValues[2].trim()

                    currentLine++

                    // 解析象曰
                    var xiangText = ""
                    if (currentLine < lines.size && lines[currentLine].startsWith("- 象曰:")) {
                        xiangText = lines[currentLine].replace("- 象曰:", "").trim()
                        currentLine++
                    }

                    // 解析译文
                    var meaning = ""
                    if (currentLine < lines.size && lines[currentLine].startsWith("- 译:")) {
                        meaning = lines[currentLine].replace("- 译:", "").trim()
                        currentLine++
                    }

                    // 只添加六爻（初、二、三、四、五、上），跳过"用九"和"用六"
                    if (!yaoName.startsWith("用")) {
                        yaos.add(
                            YaoData(
                                position = yaoPosition++,
                                name = yaoName,
                                text = yaoText,
                                xiangText = xiangText,
                                meaning = meaning
                            )
                        )
                    } else {
                        currentLine++  // 可能有额外的象曰
                        currentLine++  // 可能有译文
                    }
                } else {
                    currentLine++
                }
            } else {
                currentLine++
            }
        }

        // 解析应用
        var applicationText = ""
        while (currentLine < lines.size) {
            val line = lines[currentLine]
            if (line.startsWith("**应用**:")) {
                applicationText = line.replace("**应用**:", "").trim()
                break
            }
            currentLine++
        }

        // 从应用文本中提取事业、财运、感情、健康
        val career = extractFromApplication(applicationText, listOf("事业", "创业", "工作"))
        val wealth = extractFromApplication(applicationText, listOf("财运", "财富", "投资"))
        val love = extractFromApplication(applicationText, listOf("感情", "爱情", "婚姻"))
        val health = extractFromApplication(applicationText, listOf("健康", "身体"))

        // 生成爻组合（根据卦名推断）
        val yaoLines = generateLinesFromTrigrams(upperTrigram, lowerTrigram)



        // 生成简单的卦义和吉凶（可以后续完善）

        val meaning = "（待完善）"

        val fortune = extractFortune(applicationText)



        return HexagramData(

            id = id,

            name = fullName,

            upperTrigram = upperTrigram,

            lowerTrigram = lowerTrigram,

            lines = yaoLines,
            unicode = unicode,
            guaCi = guaCi,
            xiangCi = xiangCi,
            tuanCi = tuanCi,
            meaning = meaning,
            fortune = fortune,
            career = career,
            love = love,
            health = health,
            wealth = wealth,
            yaos = yaos
        )
    }

    /**
     * 查找下一个包含特定前缀的行
     */
    private fun findNextValueLine(lines: List<String>, startIndex: Int, prefix: String): String {
        for (i in startIndex until lines.size) {
            if (lines[i].startsWith(prefix)) {
                return lines[i]
            }
        }
        return ""
    }

    /**
     * 从应用文本中提取特定类型的指导
     */
    private fun extractFromApplication(text: String, keywords: List<String>): String {
        for (keyword in keywords) {
            if (text.contains(keyword)) {
                // 提取包含关键词的句子
                val sentences = text.split(Regex("[,，。.]"))
                for (sentence in sentences) {
                    if (sentence.contains(keyword)) {
                        return sentence.trim()
                    }
                }
            }
        }
        return text
    }

    /**
     * 提取吉凶判断
     */
    private fun extractFortune(text: String): String {
        return when {
            text.contains("大吉") -> "大吉"
            text.contains("吉") -> "吉"
            text.contains("凶") -> "凶"
            else -> "中平"
        }
    }

    /**
     * 根据八卦名称生成爻组合
     */
    private fun generateLinesFromTrigrams(upper: String, lower: String): String {
        val trigramMap = mapOf(
            "乾" to "111",
            "坤" to "000",
            "坎" to "010",
            "离" to "101",
            "震" to "001",
            "巽" to "110",
            "艮" to "100",
            "兑" to "011"
        )

        val upperLines = trigramMap[upper] ?: "111"
        val lowerLines = trigramMap[lower] ?: "111"

        return lowerLines + upperLines
    }

    /**
     * 将数据转换为 JSON 字符串
     */
    fun toJson(data: HexagramDataRoot): String {
        return json.encodeToString(data)
    }
}

/**
 * 卦象数据（用于序列化）
 */
@Serializable
data class HexagramData(
    val id: Int,
    val name: String,
    val upperTrigram: String,
    val lowerTrigram: String,
    val lines: String,
    val unicode: String,
    val guaCi: String,
    val xiangCi: String,
    val tuanCi: String,
    val meaning: String,
    val fortune: String,
    val career: String,
    val love: String,
    val health: String,
    val wealth: String,
    val yaos: List<YaoData>
)

/**
 * 爻数据
 */
@Serializable
data class YaoData(
    val position: Int,
    val name: String,
    val text: String,
    val xiangText: String,
    val meaning: String
)

/**
 * 根数据结构
 */
@Serializable
data class HexagramDataRoot(
    val hexagrams: List<HexagramData>
)
