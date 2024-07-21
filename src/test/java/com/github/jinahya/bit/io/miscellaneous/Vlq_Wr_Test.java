package com.github.jinahya.bit.io.miscellaneous;

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

import com.github.jinahya.bit.io.BitIoRandom;
import com.github.jinahya.bit.io.BitIoTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Vlq_Wr_Test {

    @DisplayName("0")
    @Nested
    class _0Test {

        @Test
        void __int() throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeInt(o, 0);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return VlqReader.getInstance().readInt(i);
                };
            });
            assertThat(actual).isZero();
        }

        @Test
        void __long() throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeLong(o, 0L);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return VlqReader.getInstance().readLong(i);
                };
            });
            assertThat(actual).isZero();
        }
    }

    @DisplayName("106093 (Wikipedia)")
    @Nested
    class _106903 {

        @Test
        void vlq__106903() throws IOException {
            final int expected = 106903;
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeInt(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(134, 195, 23);
                    return VlqReader.getInstance().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void vlqLong__106903() throws IOException {
            final long expected = 106903L;
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeLong(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(134, 195, 23);
                    return VlqReader.getInstance().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class RandomTest {

        private static int[] _ints() {
            return BitIoRandom.nextUnsignedIntArray();
        }

        private static long[] _longs() {
            return BitIoRandom.nextUnsignedLongArray();
        }

        @MethodSource({"_ints"})
        @ParameterizedTest
        void __int(final int expected) throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeInt(o, expected);
                return (a, i) -> VlqReader.getInstance().readInt(i);
            });
            assertThat(actual).isEqualTo(expected);
        }

        @MethodSource({"_longs"})
        @ParameterizedTest
        void __long(final long expected) throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeLong(o, expected);
                return (a, i) -> VlqReader.getInstance().readLong(i);
            });
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class TestVectorTest {

        private static Stream<Arguments> testVector() {
            return Stream.of(
                    Arguments.of(0x00000000, new int[]{0x00}),
                    Arguments.of(0x0000007F, new int[]{0x7F}),
                    Arguments.of(0x00000080, new int[]{0x81, 0x00}),
                    Arguments.of(0x00002000, new int[]{0xC0, 0x00}),
                    Arguments.of(0x00003FFF, new int[]{0xFF, 0x7F}),
                    Arguments.of(0x00004000, new int[]{0x81, 0x80, 0x00}),
                    Arguments.of(0x001FFFFF, new int[]{0xFF, 0xFF, 0x7F}),
                    Arguments.of(0x00200000, new int[]{0x81, 0x80, 0x80, 0x00}),
                    Arguments.of(0x08000000, new int[]{0xC0, 0x80, 0x80, 0x00}),
                    Arguments.of(0x0FFFFFFF, new int[]{0xFF, 0xFF, 0xFF, 0x7F})
            );
        }

        @MethodSource({"testVector"})
        @ParameterizedTest
        void wr__TestVector(final int expected, final int[] bytes) throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeInt(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(bytes);
                    return VlqReader.getInstance().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }

        @MethodSource({"testVector"})
        @ParameterizedTest
        void wr__TestVectorL(final long expected, final int[] bytes) throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                VlqWriter.getInstance().writeLong(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(bytes);
                    return VlqReader.getInstance().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }
    }
}
