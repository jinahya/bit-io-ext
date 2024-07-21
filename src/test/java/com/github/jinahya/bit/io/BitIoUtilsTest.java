package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 - 2022 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class BitIoUtilsTest {

    @Nested
    class WriteCountTest {

        @DisplayName("writeCount(, negative) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_CountIsNegative() {
            final var count = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            assertThatThrownBy(() -> BitIoUtils.writeCount(Mockito.mock(BitOutput.class), count))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ReadCountTest {

    }

    @Nested
    class WriteCountCompressedTest {

        @DisplayName("writeCountCompressed(, negative) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_CountIsNegative() {
            final var count = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            assertThatThrownBy(() -> BitIoUtils.writeCountCompressed(Mockito.mock(BitOutput.class), count))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ReadCountCompressedTest {

    }

    @Nested
    class BitMaskSingleTest {

        @DisplayName("bitMaskSingle(negative) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_SizeNegative() {
            final var size = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            assertThatThrownBy(() -> BitIoUtils.bitMaskSingle(size))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("bitMaskSingle(> Integer.SIZE) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_SizeGtIntegerSize() {
            final var size = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) + Integer.SIZE;
            assertThatThrownBy(() -> BitIoUtils.bitMaskSingle(size))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("bitMaskSingle(1) -> 0x00000001")
        @Test
        void bitMaskSingle_0x00000001_1() {
            assertThat(BitIoUtils.bitMaskSingle(1)).isEqualTo(0x00000001);
        }

        @DisplayName("bitMaskSingle(2) -> 0x00000003")
        @Test
        void bitMaskSingle_0x00000003_2() {
            assertThat(BitIoUtils.bitMaskSingle(2)).isEqualTo(0x00000003);
        }

        @DisplayName("bitMaskSingle(31) -> 0x7FFFFFFF")
        @Test
        void bitMaskSingle_0x7FFFFFFF_31() {
            assertThat(BitIoUtils.bitMaskSingle(31)).isEqualTo(Integer.MAX_VALUE);
        }

        @DisplayName("bitMaskSingle(32) -> 0xFFFFFFFF")
        @Test
        void bitMaskSingle_0xFFFFFFFF_32() {
            assertThat(BitIoUtils.bitMaskSingle(Integer.SIZE)).isEqualTo(-1);
        }

        private static IntStream sizeStream() {
            return IntStream.range(1, Integer.SIZE);
        }

        @MethodSource({"sizeStream"})
        @ParameterizedTest(name = "[{index}] size: {0}")
        void bitMaskSingle__(final int size) {
            assertThat(BitIoUtils.bitMaskSingle(size))
                    .as("%d-bit mask", size)
                    .isPositive()
                    .extracting(v -> v >> size, InstanceOfAssertFactories.INTEGER)
                    .as("%d-bit mask right-shifted as %d", size)
                    .isZero();
        }
    }

    @Nested
    class BitMaskDoubleTest {

        @DisplayName("bitMaskDouble(negative) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_SizeNegative() {
            final var size = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            assertThatThrownBy(() -> BitIoUtils.bitMaskDouble(size))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("bitMaskDouble(> Long.SIZE) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_SizeGtLongSize() {
            final var size = (ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE) + Long.SIZE;
            assertThatThrownBy(() -> BitIoUtils.bitMaskDouble(size))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("bitMaskDouble(1) -> 0x0000000000000001")
        @Test
        void bitMaskDouble_0x0000000000000001_1() {
            assertThat(BitIoUtils.bitMaskDouble(1)).isEqualTo(0x0000000000000001);
        }

        @DisplayName("bitMaskDouble(2) -> 0x0000000000000003")
        @Test
        void bitMaskDouble_0x0000000000000003_2() {
            assertThat(BitIoUtils.bitMaskDouble(2)).isEqualTo(0x0000000000000003);
        }

        @DisplayName("bitMaskDouble(63) -> 0x7FFFFFFFFFFFFFFF")
        @Test
        void bitMaskDouble_0x7FFFFFFFFFFFFFFF_63() {
            assertThat(BitIoUtils.bitMaskDouble(63)).isEqualTo(Long.MAX_VALUE);
        }

        @DisplayName("bitMaskDouble(64) -> 0xFFFFFFFFFFFFFFFF")
        @Test
        void bitMaskDouble_0xFFFFFFFF_64() {
            assertThat(BitIoUtils.bitMaskDouble(Long.SIZE)).isEqualTo(-1L);
        }

        private static IntStream sizeStream() {
            return IntStream.range(1, Long.SIZE);
        }

        @MethodSource({"sizeStream"})
        @ParameterizedTest(name = "[{index}] size: {0}")
        void bitMaskDouble__(final int size) {
            assertThat(BitIoUtils.bitMaskDouble(size))
                    .as("%d-bit mask", size)
                    .isPositive()
                    .extracting(v -> v >> size, InstanceOfAssertFactories.LONG)
                    .as("%d-bit mask right-shifted as %d", size)
                    .isZero();
        }
    }

    @Nested
    class CompressedCountTest {

        @DisplayName("writeCompressedCount(given) -> readCompressedCount()written")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 65535, 65536})
        @ParameterizedTest
        void _Written_(final int expected) throws IOException {
            final int actual = BitIoTestUtils.write_read_1_unchecked(o -> {
                BitIoUtils.writeCountCompressed(o, expected);
                return BitIoUtils::readCountCompressed;
            });
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("writeCompressedCount(MAX_VALUE) -> readCompressedCount()MAX_VALUE")
        @Test
        void _MAX_MAX() throws IOException {
            final int actual = BitIoTestUtils.write_read_1_unchecked(o -> {
                BitIoUtils.writeCountCompressed(o, Integer.MAX_VALUE);
                return BitIoUtils::readCountCompressed;
            });
            assertThat(actual).isEqualTo(Integer.MAX_VALUE);
        }

        @DisplayName("writeCompressedCount(6854) -> readCompressedCount()6854")
        @Test
        void _6854() throws IOException {
            final int expected = 6854;
            final int actual = BitIoTestUtils.write_read_1_unchecked(o -> {
                BitIoUtils.writeCountCompressed(o, expected);
                return BitIoUtils::readCountCompressed;
            });
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("writeCompressedCount(random) -> readCompressedCount()expected")
        @RepeatedTest(8192)
        void test_Written_Random() throws IOException {
            final int expected = ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE;
            final int actual = BitIoTestUtils.write_read_1_unchecked(o -> {
                BitIoUtils.writeCountCompressed(o, expected);
                return BitIoUtils::readCountCompressed;
            });
            assertThat(actual).isEqualTo(expected);
        }
    }
}
