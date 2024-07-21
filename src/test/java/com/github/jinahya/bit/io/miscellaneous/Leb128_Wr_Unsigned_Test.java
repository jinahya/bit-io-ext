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
class Leb128_Wr_Unsigned_Test {

    @DisplayName("0")
    @Nested
    class _0 {

        @Test
        void __int() throws IOException {
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceUnsigned().writeInt(o, 0);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return Leb128Reader.getInstanceUnsigned().readInt(i);
                };
            });
            assertThat(actual).isZero();
        }

        @Test
        void __long() throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceUnsigned().writeLong(o, 0L);
                return (a, i) -> {
                    assertThat(a).containsExactly(0x00);
                    return Leb128Reader.getInstanceUnsigned().readLong(i);
                };
            });
            assertThat(actual).isZero();
        }
    }

    @DisplayName("624485 (Wikipedia)")
    @Nested
    class _624485 {

        @Test
        void __int() throws IOException {
            final int expected = 624485;
            final int actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceUnsigned().writeInt(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(0xE5, 0x8E, 0x26);
                    return Leb128Reader.getInstanceUnsigned().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void __long() throws IOException {
            final long expected = 624485L;
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceUnsigned().writeLong(o, expected);
                return (a, i) -> {
                    assertThat(a).containsExactly(0xE5, 0x8E, 0x26);
                    return Leb128Reader.getInstanceUnsigned().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class Random {

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
                Leb128Writer.getInstanceUnsigned().writeInt(o, expected);
                return (a, i) -> {
//                    log.debug("{} -> {}", String.format("%10d", expected), HexFormat.ofDelimiter(" ").formatHex(a));
                    return Leb128Reader.getInstanceUnsigned().readInt(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }

        @MethodSource({"_longs"})
        @ParameterizedTest
        void __long(final long expected) throws IOException {
            final long actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
                Leb128Writer.getInstanceUnsigned().writeLong(o, expected);
                return (a, i) -> {
//                    log.debug("{} -> {}", String.format("%19d", expected), HexFormat.ofDelimiter(" ").formatHex(a));
                    return Leb128Reader.getInstanceUnsigned().readLong(i);
                };
            });
            assertThat(actual).isEqualTo(expected);
        }
    }
}
