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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link DoubleWriter.CompressedSubnormal} and {@link DoubleReader.CompressedSubnormal}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Double_Wr_CompressedSubnormal_Test {

    private static Stream<Double> valueStream() {
        return DoubleTestParameters.valueStream()
                .filter(v -> (Double.doubleToRawLongBits(v) & DoubleConstants.MASK_SIGNIFICAND) > 0L);
    }

    static void verify(final Double written, final Double read) {
        DoubleTestConstraints.assertSameSignBit(written, read);
        DoubleTestConstraints.requireSubnormal(read);
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__(final Double value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new DoubleWriter.CompressedSubnormal(DoubleConstants.SIZE_SIGNIFICAND).write(o, value);
            return i -> new DoubleReader.CompressedSubnormal(DoubleConstants.SIZE_SIGNIFICAND).read(i);
        });
        verify(value, actual);
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr2__Compressed(final Double value) throws IOException {
        final var significandBits = Double.doubleToRawLongBits(value) & DoubleConstants.MASK_SIGNIFICAND;
        final int significandSize = Math.max(
                DoubleConstants.SIZE_SIGNIFICAND - Long.numberOfTrailingZeros(significandBits),
                DoubleConstants.SIZE_MIN_SIGNIFICAND
        );
        final var actual = write_read_1_unchecked(o -> {
            new DoubleWriter.CompressedSubnormal(significandSize).write(o, value);
            return i -> new DoubleReader.CompressedSubnormal(significandSize).read(i);
        });
        verify(value, actual);
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
                new DoubleWriter.CompressedSubnormal(DoubleConstants.SIZE_SIGNIFICAND).nullable().write(o, value);
                return i -> new DoubleReader.CompressedSubnormal(DoubleConstants.SIZE_SIGNIFICAND).nullable().read(i);
            });
            verify(value, actual);
        }

        @MethodSource({"valueStream_"})
        @ParameterizedTest
        void wr2__Compressed(final Double value) throws IOException {
            final var significandBits = Double.doubleToRawLongBits(value) & DoubleConstants.MASK_SIGNIFICAND;
            final int significandSize = Math.max(
                    DoubleConstants.SIZE_SIGNIFICAND - Long.numberOfTrailingZeros(significandBits),
                    DoubleConstants.SIZE_MIN_SIGNIFICAND
            );
            final var actual = write_read_1_unchecked(o -> {
                new DoubleWriter.CompressedSubnormal(significandSize).nullable().write(o, value);
                return i -> new DoubleReader.CompressedSubnormal(significandSize).nullable().read(i);
            });
            verify(value, actual);
        }

        @DisplayName("nullable().write(null) -> nullable().read()null")
        @Test
        void wr_Null_Nullable() throws IOException {
            final int significand = DoubleConstants.SIZE_SIGNIFICAND;
            final var actual = write_read_1_unchecked(o -> {
                new DoubleWriter.CompressedSubnormal(significand).nullable().write(o, null);
                return i -> new DoubleReader.CompressedSubnormal(significand).nullable().read(i);
            });
            assertThat(actual).isNull();
        }
    }
}
