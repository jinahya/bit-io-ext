package com.github.jinahya.bit.io;

/*-
 * #%L
 * bit-io2
 * %%
 * Copyright (C) 2020 Jinahya, Inc.
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link DoubleWriter.CompressedZero} and {@link DoubleReader.CompressedZero}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Double_Wr_CompressedZero_Test {

    private static LongStream bitsStream() {
        return LongStream.of(
                Long.MIN_VALUE,
                -1,
                0,
                +1,
                Long.MAX_VALUE,
                ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE, // random positive
                ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE // random negative
        );
    }

    static Stream<Double> valueStream() {
        return Stream.concat(
                bitsStream().mapToObj(Double::longBitsToDouble),
                Stream.of(
                        Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY
                )
        );
    }

    static void validate(final Double written, final Double read) throws IOException {
        assertThat(read + .0d).isZero();
        assertThat(read.doubleValue()).isZero();
        assertThat(read.doubleValue()).isGreaterThanOrEqualTo(+.0d); // 이래서 .0d 로 비교하면 안된다!
        assertThat(read.doubleValue()).isLessThanOrEqualTo(+.0d);
        assertThat(read.doubleValue()).isGreaterThanOrEqualTo(-.0d);
        assertThat(read.doubleValue()).isLessThanOrEqualTo(-.0d);
        final var valueBits = Double.doubleToRawLongBits(written);
        final var actualBits = Double.doubleToRawLongBits(read);
        if (valueBits >= 0L) {
            assertThat(read).isEqualTo(+.0d);
            assertThat(actualBits).isEqualTo(DoubleTestConstants.POSITIVE_ZERO_BITS);
        } else {
            assertThat(read).isEqualTo(-.0d);
            assertThat(actualBits).isEqualTo(DoubleTestConstants.NEGATIVE_ZERO_BITS);
        }
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void rw__(final Double value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            DoubleWriter.CompressedZero.getInstance().write(o, value);
            return i -> DoubleReader.CompressedZero.getInstance().read(i);
        });
        validate(value, actual);
    }

    @Nested
    class NullableTest {

        private static Stream<Double> valueStream_() {
            return valueStream();
        }

        @MethodSource({"valueStream_"})
        @ParameterizedTest
        void wr__(final Double value) throws IOException {
            final var actual = write_read_1_unchecked(o -> {
                DoubleWriter.CompressedZero.getInstanceNullable().write(o, value);
                return i -> DoubleReader.CompressedZero.getInstanceNullable().read(i);
            });
            validate(value, actual);
        }

        @Test
        void wr_Null_Null() throws IOException {
            final var actual = write_read_1_unchecked(o -> {
                DoubleWriter.CompressedZero.getInstanceNullable().write(o, null);
                return i -> DoubleReader.CompressedZero.getInstanceNullable().read(i);
            });
            assertThat(actual).isNull();
        }
    }
}
