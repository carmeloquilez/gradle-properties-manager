package com.cquilezg

import org.junit.jupiter.params.provider.Arguments

object GradleVersionsSupplier {

    const val METHOD_REFERENCE = "com.cquilezg.GradleVersionsSupplier#get"
    @JvmStatic
    fun get() = listOf(
        Arguments.of("5.6.4"),
        Arguments.of("6.9.3"),
        Arguments.of("7.6.2"),
        Arguments.of("8.9")
    )
}