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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link FloatWriter.CompressedInfinity} and {@link FloatReader.CompressedInfinity}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Float_Wr_CompressedInfinity_Test {

    private static IntStream bitsStream() {
        return Float_Wr_CompressedZero_Test.bitsStream();
    }

    static Stream<Float> valueStream() {
        return Float_Wr_CompressedZero_Test.valueStream();
    }

    static void validate(final Float written, final Float read) {
        assertThat(read).isInfinite();
        final var valueBits = Float.floatToRawIntBits(written);
        final var actualBits = Float.floatToRawIntBits(read);
        if (valueBits >= 0) { // read.floatValue() >= .0f 는 -.0f 와 +.0f 를 구분하지 못한다.
            assertThat(read).isEqualTo(Float.POSITIVE_INFINITY);
            assertThat(actualBits).isEqualTo(FloatTestConstants.POSITIVE_INFINITY_BITS);
        } else {
            assertThat(read).isEqualTo(Float.NEGATIVE_INFINITY);
            assertThat(actualBits).isEqualTo(FloatTestConstants.NEGATIVE_INFINITY_BITS);
        }
    }

    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__(final Float value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            FloatWriter.CompressedInfinity.getInstance().write(o, value);
            return i -> FloatReader.CompressedInfinity.getInstance().read(i);
        });
        validate(value, actual);
    }

    @DisplayName("nullable().write(nonnull) -> nullable().read()expected")
    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr_NonNull_Nullable(final Float value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            FloatWriter.CompressedInfinity.getInstanceNullable().write(o, value);
            return i -> FloatReader.CompressedInfinity.getInstanceNullable().read(i);
        });
        validate(value, actual);
    }

    @DisplayName("nullable().write(null) -> nullable().read()null")
    @Test
    void wr_Null_Nullable() throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            FloatWriter.CompressedInfinity.getInstanceNullable().write(o, null);
            return i -> FloatReader.CompressedInfinity.getInstanceNullable().read(i);
        });
        assertThat(actual).isNull();
    }
}
