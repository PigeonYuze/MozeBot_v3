package com.pigeonyuze.account

object ExperienceUtils {
    private const val level1Data: Long = 1000

    private const val level2Data: Long = 3000

    private const val level3Data: Long = 5000

    private const val level4Data: Long = 8000

    private const val level5Data: Long = 12000

    private const val level6Data: Long = 50000

    private const val level7Data: Long = 100000

    fun getLevel(experience: Long): Int {
        return if (experience > level7Data) {
            7
        } else if (experience > level6Data) {
            6
        } else if (experience > level5Data) {
            5
        } else if (experience > level4Data) {
            4
        } else if (experience > level3Data) {
            3
        } else if (experience > level2Data) {
            2
        } else if (experience > level1Data) {
            1
        } else {
            0
        }
    }

    fun differentialExp(expData: Long): Long {
        when (val level = getLevel(expData)) {
            1 -> {
                return level2Data - expData
            }
            2 -> {
                return level3Data - expData
            }
            3 -> {
                return level4Data - expData
            }
            4 -> {
                return level5Data - expData
            }
            5 -> {
                return level6Data - expData
            }
            6 -> {
                return 0
            }
            else -> return if (level == 0) {
                level1Data - expData
            } else -1L
        }
    }

    fun getLevelExpData(level: Int): Long {
        return when (level) {
            0 -> 0
            1 -> level1Data
            2 -> level2Data
            3 -> level3Data
            4 -> level4Data
            5 -> level5Data
            6 -> level6Data
            else -> level7Data
        }
    }
}