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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link DoubleWriter.CompressedInfinity} and {@link DoubleReader.CompressedInfinity}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Double_Wr_CompressedInfinity_Test {

    private static LongStream bitsStream() {
        return DoubleTestParameters.bitsStream();
    }

    private static Stream<Double> valueStream() {
        return DoubleTestParameters.valueStream();
    }

    static void validate(final Double written, final Double read) throws IOException {
        assertThat(read).isInfinite();
        final var valueBits = Double.doubleToRawLongBits(written);
        final var actualBits = Double.doubleToRawLongBits(read);
        if (valueBits >= 0) {
            assertThat(read).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(actualBits).isEqualTo(DoubleTestConstants.POSITIVE_INFINITY_BITS);
        } else {
            assertThat(read).isEqualTo(Double.NEGATIVE_INFINITY);
            assertThat(actualBits).isEqualTo(DoubleTestConstants.NEGATIVE_INFINITY_BITS);
        }
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__Nullable(final Double value) throws IOException {
        {
            final var actual = write_read_1_unchecked(o -> {
                DoubleWriter.CompressedInfinity.getInstance().write(o, value);
                return i -> DoubleReader.CompressedInfinity.getInstance().read(i);
            });
            validate(value, actual);
        }
        {
            final var actual = write_read_1_unchecked(o -> {
                DoubleWriter.CompressedInfinity.getInstance().nullable().write(o, value);
                return i -> DoubleReader.CompressedInfinity.getInstance().nullable().read(i);
            });
            validate(value, actual);
        }
    }

    @Test
    void wr_Null_Nullable() throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            DoubleWriter.CompressedInfinity.getInstance().nullable().write(o, null);
            return i -> DoubleReader.CompressedInfinity.getInstance().nullable().read(i);
        });
        assertThat(actual).isNull();
    }
}
