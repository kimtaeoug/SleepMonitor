package ted.gun0912.sleep.device.enums

internal enum class HapticTypeModel(val timings: LongArray, val amplitudes: IntArray) {
    SUCCESS(longArrayOf(0, 20, 80, 30), intArrayOf(0, 48, 0, 80)),
    WARNING(longArrayOf(0, 20, 40, 20), intArrayOf(0, 96, 0, 64)),
    FAILURE(
        longArrayOf(0, 20, 40, 20, 40, 30, 30, 60),
        intArrayOf(0, 64, 0, 64, 0, 120, 0, 64)
    ),
    INTERACT(longArrayOf(0, 10), intArrayOf(0, 72));
}
