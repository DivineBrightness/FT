package com.yijing.divination.algorithm

import com.yijing.divination.data.model.CoinTossResult
import com.yijing.divination.data.model.YaoType
import com.yijing.divination.domain.algorithm.CoinTossAlgorithm
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * 铜钱投掷算法测试
 */
class CoinTossAlgorithmTest {

    private lateinit var algorithm: CoinTossAlgorithm

    @Before
    fun setup() {
        algorithm = CoinTossAlgorithm()
    }

    @Test
    fun `投掷结果应该在6到9之间`() {
        // 执行1000次投掷
        repeat(1000) {
            val result = algorithm.toss()
            assertTrue(
                "投掷结果应该在 6-9 之间，实际值：${result.value}",
                result.value in 6..9
            )
        }
    }

    @Test
    fun `老阴（6）应该是阴爻且为变爻`() {
        // 使用固定种子来产生老阴
        var foundOldYin = false
        for (seed in 0L..10000L) {
            val result = algorithm.tossWithSeed(seed)
            if (result.value == CoinTossResult.OLD_YIN) {
                assertEquals("老阴应该是阴爻", YaoType.YIN, result.yaoType)
                assertTrue("老阴应该是变爻", result.isChanging)
                foundOldYin = true
                break
            }
        }
        assertTrue("应该能产生老阴结果", foundOldYin)
    }

    @Test
    fun `少阳（7）应该是阳爻且不变`() {
        var foundYoungYang = false
        for (seed in 0L..10000L) {
            val result = algorithm.tossWithSeed(seed)
            if (result.value == CoinTossResult.YOUNG_YANG) {
                assertEquals("少阳应该是阳爻", YaoType.YANG, result.yaoType)
                assertFalse("少阳不应该是变爻", result.isChanging)
                foundYoungYang = true
                break
            }
        }
        assertTrue("应该能产生少阳结果", foundYoungYang)
    }

    @Test
    fun `少阴（8）应该是阴爻且不变`() {
        var foundYoungYin = false
        for (seed in 0L..10000L) {
            val result = algorithm.tossWithSeed(seed)
            if (result.value == CoinTossResult.YOUNG_YIN) {
                assertEquals("少阴应该是阴爻", YaoType.YIN, result.yaoType)
                assertFalse("少阴不应该是变爻", result.isChanging)
                foundYoungYin = true
                break
            }
        }
        assertTrue("应该能产生少阴结果", foundYoungYin)
    }

    @Test
    fun `老阳（9）应该是阳爻且为变爻`() {
        var foundOldYang = false
        for (seed in 0L..10000L) {
            val result = algorithm.tossWithSeed(seed)
            if (result.value == CoinTossResult.OLD_YANG) {
                assertEquals("老阳应该是阳爻", YaoType.YANG, result.yaoType)
                assertTrue("老阳应该是变爻", result.isChanging)
                foundOldYang = true
                break
            }
        }
        assertTrue("应该能产生老阳结果", foundOldYang)
    }

    @Test
    fun `tossSixTimes应该返回6个结果`() {
        val results = algorithm.tossSixTimes()
        assertEquals("应该返回6个结果", 6, results.size)
    }

    @Test
    fun `所有四种结果都应该能产生`() {
        val results = mutableSetOf<Int>()

        // 投掷足够多次以覆盖所有可能结果
        repeat(10000) {
            results.add(algorithm.toss().value)
        }

        assertTrue("应该产生老阴（6）", results.contains(6))
        assertTrue("应该产生少阳（7）", results.contains(7))
        assertTrue("应该产生少阴（8）", results.contains(8))
        assertTrue("应该产生老阳（9）", results.contains(9))
    }

    @Test
    fun `使用相同种子应该产生相同结果`() {
        val seed = 12345L
        val result1 = algorithm.tossWithSeed(seed)
        val result2 = algorithm.tossWithSeed(seed)

        assertEquals("相同种子应该产生相同结果", result1.value, result2.value)
        assertEquals("爻类型应该相同", result1.yaoType, result2.yaoType)
        assertEquals("变爻状态应该相同", result1.isChanging, result2.isChanging)
    }

    @Test
    fun `测试getDescription方法`() {
        // 测试各个结果的描述
        for (seed in 0L..10000L) {
            val result = algorithm.tossWithSeed(seed)
            val description = result.getDescription()

            assertNotNull("描述不应该为null", description)
            assertTrue("描述不应该为空", description.isNotEmpty())

            // 验证描述内容
            when (result.value) {
                6 -> assertTrue(description.contains("老阴"))
                7 -> assertTrue(description.contains("少阳"))
                8 -> assertTrue(description.contains("少阴"))
                9 -> assertTrue(description.contains("老阳"))
            }
        }
    }

    @Test
    fun `统计概率分布应该合理`() {
        val counts = mutableMapOf(6 to 0, 7 to 0, 8 to 0, 9 to 0)
        val total = 10000

        repeat(total) {
            val result = algorithm.toss()
            counts[result.value] = counts[result.value]!! + 1
        }

        // 理论概率：
        // 6 (三背): 1/8 = 12.5%
        // 7 (两背一字): 3/8 = 37.5%
        // 8 (两字一背): 3/8 = 37.5%
        // 9 (三字): 1/8 = 12.5%

        val p6 = counts[6]!!.toDouble() / total
        val p7 = counts[7]!!.toDouble() / total
        val p8 = counts[8]!!.toDouble() / total
        val p9 = counts[9]!!.toDouble() / total

        // 允许5%的误差
        assertTrue("老阴概率应该接近 12.5%", p6 in 0.10..0.15)
        assertTrue("少阳概率应该接近 37.5%", p7 in 0.35..0.40)
        assertTrue("少阴概率应该接近 37.5%", p8 in 0.35..0.40)
        assertTrue("老阳概率应该接近 12.5%", p9 in 0.10..0.15)

        println("概率分布:")
        println("  老阴(6): ${String.format("%.2f%%", p6 * 100)}")
        println("  少阳(7): ${String.format("%.2f%%", p7 * 100)}")
        println("  少阴(8): ${String.format("%.2f%%", p8 * 100)}")
        println("  老阳(9): ${String.format("%.2f%%", p9 * 100)}")
    }
}
