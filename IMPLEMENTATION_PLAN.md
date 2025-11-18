# 易经占卜 Android 应用 - 实现计划

## 📋 项目概览

**项目名称**: 易经占卜 (Yi Jing Divination)
**平台**: Android
**开发语言**: Kotlin
**架构模式**: MVVM + Clean Architecture
**最低 SDK**: API 24 (Android 7.0)
**目标 SDK**: API 34 (Android 14)

---

## 🎯 核心目标

构建一个完全离线的易经占卜应用，包含：
1. 铜钱卜卦功能
2. 六十四卦完整数据库（基于 BOOK.md）
3. 卦象解析和现代化解读
4. 历史记录管理
5. 易经学习模块

---

## 🏗️ 项目架构设计

### 技术栈

```
核心技术:
├── Kotlin 1.9+
├── Jetpack Compose (UI)
├── Kotlin Coroutines + Flow (异步)
├── Room Database (本地存储)
├── Hilt (依赖注入)
├── ViewModel + StateFlow (状态管理)
└── Material Design 3 (设计系统)
```

### 项目目录结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/yijing/divination/
│   │   │   ├── YiJingApplication.kt              # Application 类
│   │   │   │
│   │   │   ├── di/                                # 依赖注入模块
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   └── RepositoryModule.kt
│   │   │   │
│   │   │   ├── data/                              # 数据层
│   │   │   │   ├── local/                         # 本地数据源
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── YiJingDatabase.kt     # Room 数据库
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── HexagramDao.kt    # 卦象 DAO
│   │   │   │   │   │   │   └── RecordDao.kt      # 记录 DAO
│   │   │   │   │   │   └── entity/
│   │   │   │   │   │       ├── HexagramEntity.kt # 卦象实体
│   │   │   │   │   │       ├── YaoEntity.kt      # 爻辞实体
│   │   │   │   │   │       └── RecordEntity.kt   # 占卜记录实体
│   │   │   │   │   └── preferences/
│   │   │   │   │       └── AppPreferences.kt     # SharedPreferences 封装
│   │   │   │   │
│   │   │   │   ├── model/                         # 数据模型
│   │   │   │   │   ├── Hexagram.kt               # 卦象模型
│   │   │   │   │   ├── Yao.kt                    # 爻模型
│   │   │   │   │   ├── DivinationRecord.kt       # 占卜记录模型
│   │   │   │   │   ├── Trigram.kt                # 八卦模型
│   │   │   │   │   └── CoinTossResult.kt         # 投掷结果模型
│   │   │   │   │
│   │   │   │   └── repository/                    # 数据仓库
│   │   │   │       ├── HexagramRepository.kt     # 卦象仓库
│   │   │   │       ├── RecordRepository.kt       # 记录仓库
│   │   │   │       └── DivinationRepository.kt   # 占卜仓库
│   │   │   │
│   │   │   ├── domain/                            # 业务逻辑层
│   │   │   │   ├── algorithm/                     # 核心算法
│   │   │   │   │   ├── CoinTossAlgorithm.kt      # 铜钱投掷算法
│   │   │   │   │   ├── HexagramGenerator.kt      # 卦象生成器
│   │   │   │   │   └── YaoCalculator.kt          # 爻计算器
│   │   │   │   │
│   │   │   │   └── usecase/                       # 用例
│   │   │   │       ├── PerformDivinationUseCase.kt    # 执行占卜
│   │   │   │       ├── GetHexagramUseCase.kt          # 获取卦象
│   │   │   │       ├── SaveRecordUseCase.kt           # 保存记录
│   │   │   │       └── SearchHexagramUseCase.kt       # 搜索卦象
│   │   │   │
│   │   │   ├── ui/                                # UI 层
│   │   │   │   ├── MainActivity.kt
│   │   │   │   │
│   │   │   │   ├── navigation/                    # 导航
│   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   └── Screen.kt
│   │   │   │   │
│   │   │   │   ├── theme/                         # 主题
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   │
│   │   │   │   ├── components/                    # 通用组件
│   │   │   │   │   ├── HexagramView.kt           # 卦象可视化组件
│   │   │   │   │   ├── YaoLine.kt                # 爻线组件
│   │   │   │   │   ├── CoinAnimation.kt          # 铜钱动画
│   │   │   │   │   └── LoadingIndicator.kt       # 加载指示器
│   │   │   │   │
│   │   │   │   ├── screen/                        # 页面
│   │   │   │   │   ├── home/                      # 主页
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── divination/                # 占卜
│   │   │   │   │   │   ├── DivinationScreen.kt
│   │   │   │   │   │   ├── DivinationViewModel.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── CoinTossView.kt
│   │   │   │   │   │       └── QuestionInput.kt
│   │   │   │   │   │
│   │   │   │   │   ├── result/                    # 结果展示
│   │   │   │   │   │   ├── ResultScreen.kt
│   │   │   │   │   │   ├── ResultViewModel.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── HexagramCard.kt
│   │   │   │   │   │       ├── InterpretationView.kt
│   │   │   │   │   │       └── YaoDetailView.kt
│   │   │   │   │   │
│   │   │   │   │   ├── hexagram/                  # 卦象浏览
│   │   │   │   │   │   ├── HexagramListScreen.kt
│   │   │   │   │   │   ├── HexagramDetailScreen.kt
│   │   │   │   │   │   └── HexagramViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── history/                   # 历史记录
│   │   │   │   │   │   ├── HistoryScreen.kt
│   │   │   │   │   │   ├── HistoryViewModel.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       └── RecordCard.kt
│   │   │   │   │   │
│   │   │   │   │   └── learning/                  # 学习模块
│   │   │   │   │       ├── LearningScreen.kt
│   │   │   │   │       ├── LearningViewModel.kt
│   │   │   │   │       └── components/
│   │   │   │   │           └── KnowledgeCard.kt
│   │   │   │   │
│   │   │   │   └── util/                          # UI 工具
│   │   │   │       ├── HexagramDrawer.kt         # 卦象绘制工具
│   │   │   │       └── DateFormatter.kt          # 日期格式化
│   │   │   │
│   │   │   └── util/                              # 通用工具
│   │   │       ├── Constants.kt                   # 常量
│   │   │       ├── DataParser.kt                  # BOOK.md 数据解析器
│   │   │       └── Logger.kt                      # 日志工具
│   │   │
│   │   ├── res/
│   │   │   ├── raw/
│   │   │   │   └── hexagram_data.json            # 从 BOOK.md 转换的 JSON
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── drawable/
│   │   │       ├── ic_coin.xml                    # 铜钱图标
│   │   │       ├── ic_hexagram.xml                # 卦象图标
│   │   │       └── ic_history.xml                 # 历史图标
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── test/                                       # 测试代码
│       ├── java/com/yijing/divination/
│       │   ├── algorithm/
│       │   │   ├── CoinTossAlgorithmTest.kt
│       │   │   └── HexagramGeneratorTest.kt
│       │   ├── repository/
│       │   │   └── HexagramRepositoryTest.kt
│       │   └── viewmodel/
│       │       └── DivinationViewModelTest.kt
│       └── resources/
│
├── build.gradle.kts                                # 模块级构建配置
└── proguard-rules.pro                              # 混淆规则
```

---

## 📊 数据模型设计

### 1. 卦象实体 (HexagramEntity)

```kotlin
@Entity(tableName = "hexagrams")
data class HexagramEntity(
    @PrimaryKey val id: Int,                    // 1-64
    val name: String,                           // 卦名：如 "乾为天"
    val upperTrigram: String,                   // 上卦：如 "乾"
    val lowerTrigram: String,                   // 下卦：如 "乾"
    val lines: String,                          // 爻组合：如 "111111"
    val unicode: String,                        // Unicode 符号：如 "䷀"

    // 经文内容
    val guaCi: String,                          // 卦辞
    val xiangCi: String,                        // 象辞
    val tuanCi: String,                         // 彖辞

    // 现代解读
    val meaning: String,                        // 卦义
    val fortune: String,                        // 吉凶

    // 应用指导
    val career: String,                         // 事业
    val love: String,                           // 爱情
    val health: String,                         // 健康
    val wealth: String                          // 财富
)
```

### 2. 爻辞实体 (YaoEntity)

```kotlin
@Entity(
    tableName = "yaos",
    foreignKeys = [ForeignKey(
        entity = HexagramEntity::class,
        parentColumns = ["id"],
        childColumns = ["hexagramId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class YaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hexagramId: Int,                        // 所属卦象 ID
    val position: Int,                          // 爻位：1-6
    val name: String,                           // 爻名：如 "初九"
    val text: String,                           // 爻辞原文
    val xiangText: String,                      // 象曰
    val meaning: String                         // 译文/解释
)
```

### 3. 占卜记录实体 (RecordEntity)

```kotlin
@Entity(tableName = "divination_records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,                        // 占卜时间戳
    val question: String?,                      // 占卜问题

    // 本卦信息
    val originalHexagramId: Int,                // 本卦 ID
    val originalHexagramName: String,           // 本卦名
    val yaoResults: String,                     // 六次投掷结果 JSON

    // 变卦信息（如有）
    val changedHexagramId: Int?,                // 变卦 ID
    val changedHexagramName: String?,           // 变卦名
    val changingYaoPositions: String?,          // 变爻位置 JSON

    // 用户笔记
    val note: String?,                          // 用户笔记
    val isFavorite: Boolean = false             // 是否收藏
)
```

### 4. 投掷结果模型 (CoinTossResult)

```kotlin
data class CoinTossResult(
    val value: Int,                             // 6(老阴) 7(少阳) 8(少阴) 9(老阳)
    val yaoType: YaoType,                       // 爻类型
    val isChanging: Boolean                     // 是否为变爻
)

enum class YaoType {
    YANG,           // 阳爻 —
    YIN             // 阴爻 - -
}
```

---

## 🔧 核心算法实现

### 1. 铜钱投掷算法 (CoinTossAlgorithm.kt)

**功能**: 模拟三枚铜钱投掷，生成爻

**算法原理**:
- 三枚铜钱，每枚有正反两面
- 正面（字）= 3，反面（背）= 2
- 三枚之和：6(老阴)、7(少阳)、8(少阴)、9(老阳)

```kotlin
object CoinTossAlgorithm {
    private const val HEADS = 3  // 字面
    private const val TAILS = 2  // 背面

    fun toss(): CoinTossResult {
        val coin1 = if (Random.nextBoolean()) HEADS else TAILS
        val coin2 = if (Random.nextBoolean()) HEADS else TAILS
        val coin3 = if (Random.nextBoolean()) HEADS else TAILS

        val sum = coin1 + coin2 + coin3

        return when (sum) {
            6 -> CoinTossResult(6, YaoType.YIN, true)    // 老阴，变
            7 -> CoinTossResult(7, YaoType.YANG, false)  // 少阳
            8 -> CoinTossResult(8, YaoType.YIN, false)   // 少阴
            9 -> CoinTossResult(9, YaoType.YANG, true)   // 老阳，变
            else -> throw IllegalStateException()
        }
    }
}
```

### 2. 卦象生成器 (HexagramGenerator.kt)

**功能**: 根据六次投掷结果生成本卦和变卦

```kotlin
class HexagramGenerator(
    private val hexagramRepository: HexagramRepository
) {
    suspend fun generateHexagram(
        tossResults: List<CoinTossResult>
    ): DivinationResult {
        require(tossResults.size == 6) { "需要 6 次投掷结果" }

        // 生成本卦（从下到上）
        val originalLines = tossResults.map {
            if (it.yaoType == YaoType.YANG) 1 else 0
        }
        val originalHexagram = findHexagram(originalLines)

        // 找出变爻
        val changingPositions = tossResults
            .mapIndexedNotNull { index, result ->
                if (result.isChanging) index else null
            }

        // 生成变卦（如有变爻）
        val changedHexagram = if (changingPositions.isNotEmpty()) {
            val changedLines = originalLines.toMutableList()
            changingPositions.forEach { pos ->
                changedLines[pos] = 1 - changedLines[pos]
            }
            findHexagram(changedLines)
        } else null

        return DivinationResult(
            originalHexagram = originalHexagram,
            changedHexagram = changedHexagram,
            changingYaoPositions = changingPositions,
            tossResults = tossResults
        )
    }

    private suspend fun findHexagram(lines: List<Int>): Hexagram {
        val linesStr = lines.joinToString("")
        return hexagramRepository.getHexagramByLines(linesStr)
    }
}
```

### 3. 八卦映射表

```kotlin
object TrigramMapper {
    // 八卦与二进制的映射
    private val trigramMap = mapOf(
        "111" to "乾",  // ☰
        "000" to "坤",  // ☷
        "010" to "坎",  // ☵
        "101" to "离",  // ☲
        "001" to "震",  // ☳
        "110" to "巽",  // ☴
        "100" to "艮",  // ☶
        "011" to "兑"   // ☱
    )

    fun getTrigramName(lines: String): String {
        return trigramMap[lines] ?: "未知"
    }
}
```

---

## 📝 数据转换流程

### BOOK.md → JSON → SQLite

#### 步骤 1: 创建数据解析工具 (DataParser.kt)

**功能**: 解析 BOOK.md 并转换为 JSON 格式

```kotlin
object BookMarkdownParser {
    fun parse(content: String): List<HexagramData> {
        val hexagrams = mutableListOf<HexagramData>()
        val sections = content.split("---").filter { it.isNotBlank() }

        sections.forEach { section ->
            val hexagram = parseSection(section.trim())
            hexagram?.let { hexagrams.add(it) }
        }

        return hexagrams
    }

    private fun parseSection(section: String): HexagramData? {
        // 解析逻辑...
        // 提取卦名、卦象、卦辞、象辞、彖辞、爻辞、应用等
    }
}
```

#### 步骤 2: 生成 hexagram_data.json

**JSON 结构示例**:

```json
{
  "hexagrams": [
    {
      "id": 1,
      "name": "乾为天",
      "unicode": "䷀",
      "upperTrigram": "乾",
      "lowerTrigram": "乾",
      "lines": "111111",
      "guaCi": "乾,元亨利贞。",
      "xiangCi": "天行健,君子以自强不息。",
      "tuanCi": "大哉乾元,万物资始,乃统天...",
      "meaning": "乾卦象征刚健纯粹的阳性力量",
      "fortune": "大吉",
      "career": "正是大展宏图的好时机",
      "love": "感情发展顺利",
      "health": "精力充沛",
      "wealth": "财运亨通",
      "yaos": [
        {
          "position": 1,
          "name": "初九",
          "text": "潜龙勿用。",
          "xiangText": "潜龙勿用,阳在下也。",
          "meaning": "龙潜伏在水中,暂时不要有所作为。"
        }
        // ... 其他爻
      ]
    }
    // ... 其他 63 卦
  ]
}
```

#### 步骤 3: 首次启动时导入数据库

```kotlin
class DatabaseInitializer @Inject constructor(
    private val database: YiJingDatabase,
    private val context: Context
) {
    suspend fun initializeIfNeeded() {
        if (database.hexagramDao().getCount() == 0) {
            val json = context.resources
                .openRawResource(R.raw.hexagram_data)
                .bufferedReader()
                .use { it.readText() }

            val data = Json.decodeFromString<HexagramDataRoot>(json)

            // 插入数据库
            data.hexagrams.forEach { hexagram ->
                database.hexagramDao().insert(hexagram.toEntity())
                hexagram.yaos.forEach { yao ->
                    database.yaoDao().insert(yao.toEntity(hexagram.id))
                }
            }
        }
    }
}
```

---

## 🎨 UI 设计要点

### 1. 主题配色

```kotlin
// Color.kt
val ChineseRed = Color(0xFFDC143C)
val InkBlack = Color(0xFF2C3E50)
val PaperWhite = Color(0xFFFFFAF0)
val BambooGreen = Color(0xFF6B8E23)
val GoldYellow = Color(0xFFFFD700)
```

### 2. 核心 UI 组件

#### HexagramView - 卦象可视化

```kotlin
@Composable
fun HexagramView(
    lines: List<Int>,  // 1 = 阳爻, 0 = 阴爻
    modifier: Modifier = Modifier,
    showAnimation: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 从上往下显示（第6爻到第1爻）
        lines.reversed().forEach { line ->
            YaoLine(isYang = line == 1)
        }
    }
}

@Composable
fun YaoLine(isYang: Boolean) {
    if (isYang) {
        // 阳爻：实线 ————
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.Black)
        )
    } else {
        // 阴爻：断线 —— ——
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(Color.Black)
            )
        }
    }
}
```

#### CoinAnimation - 铜钱翻转动画

```kotlin
@Composable
fun CoinAnimation(
    isFlipping: Boolean,
    result: CoinTossResult?,
    onFlipComplete: () -> Unit
) {
    // Lottie 动画或自定义动画
}
```

---

## 🚀 开发实施步骤

### Phase 1: 项目初始化与基础架构 (第 1-2 周)

#### 任务清单

- [ ] **1.1 创建 Android 项目**
  - 文件: `settings.gradle.kts`, `build.gradle.kts`
  - 配置 Kotlin、Compose、Hilt

- [ ] **1.2 设置依赖项**
  ```kotlin
  dependencies {
      // Compose
      implementation("androidx.compose.ui:ui:1.5.4")
      implementation("androidx.compose.material3:material3:1.1.2")

      // Room
      implementation("androidx.room:room-runtime:2.6.0")
      kapt("androidx.room:room-compiler:2.6.0")
      implementation("androidx.room:room-ktx:2.6.0")

      // Hilt
      implementation("com.google.dagger:hilt-android:2.48")
      kapt("com.google.dagger:hilt-compiler:2.48")

      // Coroutines
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

      // Navigation
      implementation("androidx.navigation:navigation-compose:2.7.5")
  }
  ```

- [ ] **1.3 创建数据模型**
  - 文件: `HexagramEntity.kt`
  - 文件: `YaoEntity.kt`
  - 文件: `RecordEntity.kt`

- [ ] **1.4 设置 Room 数据库**
  - 文件: `YiJingDatabase.kt`
  - 文件: `HexagramDao.kt`
  - 文件: `YaoDao.kt`
  - 文件: `RecordDao.kt`

- [ ] **1.5 配置 Hilt 依赖注入**
  - 文件: `YiJingApplication.kt`
  - 文件: `di/AppModule.kt`
  - 文件: `di/DatabaseModule.kt`

---

### Phase 2: 数据准备 (第 2-3 周)

#### 任务清单

- [ ] **2.1 编写 BOOK.md 解析器**
  - 文件: `util/BookMarkdownParser.kt`
  - 功能: 读取 BOOK.md，解析每一卦的结构
  - 提取: 卦名、卦象、卦辞、象辞、彖辞、爻辞、应用

- [ ] **2.2 生成 JSON 数据**
  - 文件: `res/raw/hexagram_data.json`
  - 运行解析器，生成标准 JSON
  - 验证 64 卦数据完整性

- [ ] **2.3 创建数据库初始化器**
  - 文件: `data/DatabaseInitializer.kt`
  - 功能: 首次启动时从 JSON 导入数据到 SQLite
  - 包含数据校验逻辑

- [ ] **2.4 创建 Repository 层**
  - 文件: `data/repository/HexagramRepository.kt`
  - 文件: `data/repository/RecordRepository.kt`
  - 实现数据访问接口

---

### Phase 3: 核心算法开发 (第 3-4 周)

#### 任务清单

- [ ] **3.1 实现铜钱投掷算法**
  - 文件: `domain/algorithm/CoinTossAlgorithm.kt`
  - 功能: 模拟三枚铜钱投掷
  - 生成老阴(6)、少阳(7)、少阴(8)、老阳(9)

- [ ] **3.2 实现卦象生成器**
  - 文件: `domain/algorithm/HexagramGenerator.kt`
  - 功能: 根据六爻生成本卦
  - 功能: 计算变爻，生成变卦

- [ ] **3.3 实现八卦映射**
  - 文件: `domain/algorithm/TrigramMapper.kt`
  - 功能: 二进制 → 八卦名称映射

- [ ] **3.4 编写单元测试**
  - 文件: `test/algorithm/CoinTossAlgorithmTest.kt`
  - 文件: `test/algorithm/HexagramGeneratorTest.kt`
  - 测试边界情况和随机性

---

### Phase 4: 占卜功能 UI 开发 (第 4-6 周)

#### 任务清单

- [ ] **4.1 创建主界面**
  - 文件: `ui/MainActivity.kt`
  - 文件: `ui/navigation/NavGraph.kt`
  - 设置 Navigation Compose

- [ ] **4.2 开发占卜流程界面**
  - 文件: `ui/screen/divination/DivinationScreen.kt`
  - 文件: `ui/screen/divination/DivinationViewModel.kt`
  - 功能: 问题输入框
  - 功能: 铜钱投掷按钮/摇一摇

- [ ] **4.3 开发铜钱动画组件**
  - 文件: `ui/components/CoinAnimation.kt`
  - 使用 Lottie 或自定义动画
  - 展示投掷过程

- [ ] **4.4 开发爻线显示组件**
  - 文件: `ui/components/YaoLine.kt`
  - 显示阳爻 / 阴爻
  - 支持变爻标记

- [ ] **4.5 开发卦象可视化组件**
  - 文件: `ui/components/HexagramView.kt`
  - 显示完整卦象（6爻）
  - 标注上卦下卦

- [ ] **4.6 状态管理**
  - ViewModel 管理投掷状态
  - StateFlow 驱动 UI 更新

---

### Phase 5: 结果展示模块 (第 6-7 周)

#### 任务清单

- [ ] **5.1 开发结果展示界面**
  - 文件: `ui/screen/result/ResultScreen.kt`
  - 文件: `ui/screen/result/ResultViewModel.kt`
  - 展示本卦和变卦

- [ ] **5.2 卦辞解读组件**
  - 文件: `ui/screen/result/components/InterpretationView.kt`
  - 显示: 卦辞、象辞、彖辞
  - 可折叠/展开设计

- [ ] **5.3 爻辞详情组件**
  - 文件: `ui/screen/result/components/YaoDetailView.kt`
  - 显示六爻各自解释
  - 高亮变爻

- [ ] **5.4 现代解读展示**
  - 分类显示: 事业、爱情、健康、财富
  - Material Design Card 布局

- [ ] **5.5 保存记录功能**
  - 添加"保存到历史"按钮
  - 允许用户添加笔记

---

### Phase 6: 卦象浏览模块 (第 7-8 周)

#### 任务清单

- [ ] **6.1 开发六十四卦列表**
  - 文件: `ui/screen/hexagram/HexagramListScreen.kt`
  - 文件: `ui/screen/hexagram/HexagramViewModel.kt`
  - LazyColumn 展示所有卦

- [ ] **6.2 搜索功能**
  - 支持卦名搜索
  - 支持关键词搜索

- [ ] **6.3 分类导航**
  - 按八宫卦分类
  - Tab 布局

- [ ] **6.4 卦象详情页**
  - 文件: `ui/screen/hexagram/HexagramDetailScreen.kt`
  - 完整显示卦象信息
  - 可从此页面发起占卜

---

### Phase 7: 历史记录模块 (第 8-9 周)

#### 任务清单

- [ ] **7.1 开发历史记录列表**
  - 文件: `ui/screen/history/HistoryScreen.kt`
  - 文件: `ui/screen/history/HistoryViewModel.kt`
  - 时间倒序显示

- [ ] **7.2 记录卡片组件**
  - 文件: `ui/screen/history/components/RecordCard.kt`
  - 显示: 日期、问题、卦象、简要解读

- [ ] **7.3 记录详情**
  - 点击查看完整占卜记录
  - 支持编辑笔记

- [ ] **7.4 管理功能**
  - 删除记录
  - 收藏功能
  - 导出功能（可选）

---

### Phase 8: 学习模块 (第 9-10 周)

#### 任务清单

- [ ] **8.1 知识库内容准备**
  - 易经基础知识
  - 卜卦方法说明
  - 阴阳五行理论
  - 八卦基础知识

- [ ] **8.2 开发学习界面**
  - 文件: `ui/screen/learning/LearningScreen.kt`
  - 文件: `ui/screen/learning/LearningViewModel.kt`

- [ ] **8.3 知识卡片组件**
  - 文件: `ui/screen/learning/components/KnowledgeCard.kt`
  - 支持图文混排

---

### Phase 9: 优化与完善 (第 10-12 周)

#### 任务清单

- [ ] **9.1 性能优化**
  - 数据库查询优化
  - 图片资源压缩
  - 启动速度优化

- [ ] **9.2 UI/UX 优化**
  - 过渡动画
  - 加载状态
  - 错误处理

- [ ] **9.3 深色模式**
  - 适配 Dark Theme
  - 动态主题切换

- [ ] **9.4 多语言支持**
  - 简体中文
  - 繁体中文（可选）

- [ ] **9.5 离线功能验证**
  - 确保无网络权限
  - 测试完全离线可用

- [ ] **9.6 添加引导页**
  - 首次启动教程
  - 功能介绍

- [ ] **9.7 免责声明**
  - 关于页面
  - 免责声明文本

---

### Phase 10: 测试与发布 (第 12-13 周)

#### 任务清单

- [ ] **10.1 单元测试**
  - 算法测试覆盖率 > 80%
  - Repository 测试

- [ ] **10.2 UI 测试**
  - Compose UI 测试
  - 关键流程测试

- [ ] **10.3 集成测试**
  - 端到端流程测试

- [ ] **10.4 手动测试**
  - 多设备测试
  - 多版本 Android 测试

- [ ] **10.5 性能测试**
  - 内存泄漏检测
  - ANR 检测

- [ ] **10.6 打包发布**
  - 配置签名
  - 生成 Release APK
  - 准备应用商店素材

---

## 📋 关键文件清单

### 需要创建的核心文件 (按优先级)

#### 优先级 P0 (核心功能)

| 文件路径 | 功能描述 | 预计代码量 |
|---------|---------|-----------|
| `data/local/database/entity/HexagramEntity.kt` | 卦象数据实体 | 50 行 |
| `data/local/database/entity/YaoEntity.kt` | 爻辞数据实体 | 40 行 |
| `data/local/database/entity/RecordEntity.kt` | 占卜记录实体 | 60 行 |
| `data/local/database/YiJingDatabase.kt` | Room 数据库配置 | 40 行 |
| `data/local/database/dao/HexagramDao.kt` | 卦象数据访问 | 80 行 |
| `data/local/database/dao/YaoDao.kt` | 爻辞数据访问 | 50 行 |
| `data/local/database/dao/RecordDao.kt` | 记录数据访问 | 100 行 |
| `domain/algorithm/CoinTossAlgorithm.kt` | 铜钱投掷算法 | 60 行 |
| `domain/algorithm/HexagramGenerator.kt` | 卦象生成器 | 120 行 |
| `util/BookMarkdownParser.kt` | BOOK.md 解析器 | 300 行 |
| `res/raw/hexagram_data.json` | 六十四卦 JSON 数据 | 自动生成 |

#### 优先级 P1 (UI 核心)

| 文件路径 | 功能描述 | 预计代码量 |
|---------|---------|-----------|
| `ui/screen/divination/DivinationScreen.kt` | 占卜主界面 | 200 行 |
| `ui/screen/divination/DivinationViewModel.kt` | 占卜业务逻辑 | 150 行 |
| `ui/screen/result/ResultScreen.kt` | 结果展示界面 | 250 行 |
| `ui/components/HexagramView.kt` | 卦象可视化组件 | 100 行 |
| `ui/components/YaoLine.kt` | 爻线组件 | 50 行 |
| `ui/components/CoinAnimation.kt` | 铜钱动画 | 120 行 |

#### 优先级 P2 (扩展功能)

| 文件路径 | 功能描述 | 预计代码量 |
|---------|---------|-----------|
| `ui/screen/hexagram/HexagramListScreen.kt` | 卦象列表 | 180 行 |
| `ui/screen/history/HistoryScreen.kt` | 历史记录 | 200 行 |
| `ui/screen/learning/LearningScreen.kt` | 学习模块 | 150 行 |

---

## 🧪 测试策略

### 单元测试

```kotlin
// CoinTossAlgorithmTest.kt
class CoinTossAlgorithmTest {
    @Test
    fun `投掷结果应该在6-9之间`() {
        repeat(1000) {
            val result = CoinTossAlgorithm.toss()
            assertTrue(result.value in 6..9)
        }
    }

    @Test
    fun `老阴老阳应该标记为变爻`() {
        // 测试逻辑
    }
}
```

### UI 测试

```kotlin
// DivinationScreenTest.kt
class DivinationScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `点击投掷按钮6次后应该显示结果`() {
        // UI 测试逻辑
    }
}
```

---

## 📦 依赖库清单

```gradle
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")

    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Lottie (动画)
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.room:room-testing:2.6.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

---

## 🎯 里程碑检查点

| 里程碑 | 完成标准 | 预计时间 |
|-------|---------|---------|
| M1: 项目搭建 | 数据库、依赖注入配置完成 | Week 2 |
| M2: 数据就绪 | 64 卦数据导入数据库成功 | Week 3 |
| M3: 算法完成 | 铜钱投掷和卦象生成通过测试 | Week 4 |
| M4: 占卜流程 | 可完整执行一次占卜并显示结果 | Week 6 |
| M5: 基础功能 | 占卜、浏览、历史三大模块可用 | Week 9 |
| M6: 完整版本 | 所有功能开发完成，开始测试 | Week 11 |
| M7: 发布就绪 | 测试通过，APK 打包完成 | Week 13 |

---

## ⚠️ 注意事项

### 1. 数据准确性
- BOOK.md 的解析必须确保无误
- 建议人工核对前 10 卦的数据
- 卦象顺序、爻位方向不能错

### 2. 隐私保护
- **不申请网络权限**
- **不申请位置权限**
- 所有数据本地存储
- 不使用任何分析 SDK

### 3. 文化尊重
- 解读内容客观理性
- 避免封建迷信倾向
- 添加"仅供参考"免责声明

### 4. 性能优化
- 数据库查询添加索引
- 大量文本使用分页加载
- 图片使用矢量图（SVG/VectorDrawable）

### 5. 代码规范
- 遵循 Kotlin 编码规范
- 使用 ktlint 格式化代码
- 添加必要注释

---

## 📚 参考资料

- [易经原文权威版本]
- [Android Jetpack Compose 官方文档](https://developer.android.com/jetpack/compose)
- [Room 数据库指南](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)

---

## 🔄 后续扩展方向

完成基础版本后，可考虑以下扩展:

1. **梅花易数**: 添加时间起卦、数字起卦
2. **六爻占卜**: 更复杂的六爻断卦系统
3. **社区功能**: (需网络) 用户分享心得
4. **AI 解读**: (需网络) AI 辅助解卦
5. **日历功能**: 黄历、节气提示
6. **提醒功能**: 每日一卦推送

---

**文档版本**: v1.0
**最后更新**: 2025-11-18
**维护者**: 开发团队

---

## ✅ 下一步行动

1. **确认技术栈**: 是否同意使用 Kotlin + Compose + Room？
2. **审核数据结构**: 数据模型是否满足需求？
3. **开始 Phase 1**: 创建项目，搭建基础架构
4. **准备开发环境**: Android Studio、Kotlin 插件

**准备好开始开发了吗？** 🚀
