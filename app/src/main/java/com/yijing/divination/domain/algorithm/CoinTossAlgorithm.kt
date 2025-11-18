package com.yijing.divination.domain.algorithm

import com.yijing.divination.data.model.CoinTossResult
import com.yijing.divination.data.model.YaoType
import javax.inject.Inject
import kotlin.random.Random

/**
 * 铜钱投掷算法
 *
 * 模拟传统的三枚铜钱起卦法：
 * - 三枚铜钱同时投掷
 * - 每枚铜钱有两面：字（正面）= 3，背（反面）= 2
 * - 三枚之和决定爻的类型：
 *   - 6 = 老阴（三背）- 变爻，阴变阳
 *   - 7 = 少阳（两背一字）- 不变
 *   - 8 = 少阴（两字一背）- 不变
 *   - 9 = 老阳（三字）- 变爻，阳变阴
 */
class CoinTossAlgorithm @Inject constructor() {

    companion object {
        private const val HEADS = 3  // 字面（正面）
        private const val TAILS = 2  // 背面（反面）

        const val OLD_YIN = 6       // 老阴（变爻）
        const val YOUNG_YANG = 7    // 少阳
        const val YOUNG_YIN = 8     // 少阴
        const val OLD_YANG = 9      // 老阳（变爻）
    }

    /**
     * 执行一次铜钱投掷
     *
     * @return CoinTossResult 投掷结果
     */
    fun toss(): CoinTossResult {
        // 投掷三枚铜钱
        val coin1 = if (Random.nextBoolean()) HEADS else TAILS
        val coin2 = if (Random.nextBoolean()) HEADS else TAILS
        val coin3 = if (Random.nextBoolean()) HEADS else TAILS

        // 计算总和
        val sum = coin1 + coin2 + coin3

        // 根据总和返回结果
        return when (sum) {
            OLD_YIN -> CoinTossResult(
                value = OLD_YIN,
                yaoType = YaoType.YIN,
                isChanging = true  // 老阴会变
            )
            YOUNG_YANG -> CoinTossResult(
                value = YOUNG_YANG,
                yaoType = YaoType.YANG,
                isChanging = false
            )
            YOUNG_YIN -> CoinTossResult(
                value = YOUNG_YIN,
                yaoType = YaoType.YIN,
                isChanging = false
            )
            OLD_YANG -> CoinTossResult(
                value = OLD_YANG,
                yaoType = YaoType.YANG,
                isChanging = true  // 老阳会变
            )
            else -> throw IllegalStateException("Invalid coin toss sum: $sum")
        }
    }

    /**
     * 执行6次投掷，生成完整的六爻
     *
     * @return List<CoinTossResult> 六爻的投掷结果（从下到上）
     */
    fun tossSixTimes(): List<CoinTossResult> {
        return List(6) { toss() }
    }

    /**
     * 使用指定的随机数种子投掷
     * 用于测试或可重现的占卜结果
     *
     * @param seed 随机数种子
     * @return CoinTossResult 投掷结果
     */
    fun tossWithSeed(seed: Long): CoinTossResult {
        val random = Random(seed)
        val coin1 = if (random.nextBoolean()) HEADS else TAILS
        val coin2 = if (random.nextBoolean()) HEADS else TAILS
        val coin3 = if (random.nextBoolean()) HEADS else TAILS
        val sum = coin1 + coin2 + coin3

        return when (sum) {
            OLD_YIN -> CoinTossResult(OLD_YIN, YaoType.YIN, true)
            YOUNG_YANG -> CoinTossResult(YOUNG_YANG, YaoType.YANG, false)
            YOUNG_YIN -> CoinTossResult(YOUNG_YIN, YaoType.YIN, false)
            OLD_YANG -> CoinTossResult(OLD_YANG, YaoType.YANG, true)
            else -> throw IllegalStateException("Invalid coin toss sum: $sum")
        }
    }

    /**
     * 模拟摇动手机的投掷
     * 可以结合加速度传感器数据来影响随机性
     *
     * @param sensorData 传感器数据（可选，用于增加随机性）
     * @return CoinTossResult 投掷结果
     */
    fun tossWithSensorData(sensorData: FloatArray? = null): CoinTossResult {
        // 如果有传感器数据，可以用它来影响随机种子
        val seed = if (sensorData != null) {
            val dataSum = sensorData.sum()
            (System.currentTimeMillis() + dataSum.toLong())
        } else {
            System.currentTimeMillis()
        }

        return tossWithSeed(seed)
    }
}
