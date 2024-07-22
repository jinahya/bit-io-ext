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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Leb128_Wr_Signed_Test {

    @DisplayName("0")
    @Nested
    class _0 {

        @Test
        void __int() throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeInt(o, 0);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return Leb128Reader.getInstanceSigned().readInt(i);
                };
            });
            assertThat(actual).isZero();
        }

        @Test
        void __long() throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeLong(o, 0L);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return Leb128Reader.getInstanceSigned().readLong(i);
                };
            });
            assertThat(actual).isZero();
        }
    }

    @DisplayName("-1")
    @Nested
    class __1 {

        @Test
        void __int() throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeInt(o, -1);
                return (a, i) -> {
                    return Leb128Reader.getInstanceSigned().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(-1);
        }

        @Test
        void __long() throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeLong(o, -1L);
                return (a, i) -> {
                    return Leb128Reader.getInstanceSigned().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(-1L);
        }
    }

    @DisplayName("-123456 (Wikipedia)")
    @Nested
    class __123456 {

        @Test
        void __int() throws IOException {
            final int expected = -123456;
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeInt(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(0xC0, 0xBB, 0x78);
                    return Leb128Reader.getInstanceSigned().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void __long() throws IOException {
            final long expected = -123456L;
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeLong(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(0xC0, 0xBB, 0x78);
                    return Leb128Reader.getInstanceSigned().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class Random {

        private static int[] _ints() {
            return BitIoRandom.nextSignedIntArray();
        }

        private static long[] _longs() {
            return BitIoRandom.nextSignedLongArray();
        }

        @MethodSource({"_ints"})
        @ParameterizedTest
        void __int(final int expected) throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeInt(o, expected);
                return (a, i) -> Leb128Reader.getInstanceSigned().readInt(i);
            });
            assertThat(actual).isEqualTo(expected);
        }

        @MethodSource({"_longs"})
        @ParameterizedTest
        void __long(final long expected) throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceSigned().writeLong(o, expected);
                return (a, i) -> Leb128Reader.getInstanceSigned().readLong(i);
            });
            assertThat(actual).isEqualTo(expected);
        }
    }
}
