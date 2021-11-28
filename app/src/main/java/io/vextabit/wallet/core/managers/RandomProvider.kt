package io.vextabit.wallet.core.managers

import io.vextabit.wallet.core.IRandomProvider
import java.util.*

class RandomProvider : IRandomProvider {

    override fun getRandomIndexes(count: Int, maxIndex: Int): List<Int> {
        val indexes = mutableListOf<Int>()

        val random = Random()

        while (indexes.size < count) {
            val index = random.nextInt(maxIndex)

            if (!indexes.contains(index)) {
                indexes.add(index)
            }
        }

        return indexes
    }

}
