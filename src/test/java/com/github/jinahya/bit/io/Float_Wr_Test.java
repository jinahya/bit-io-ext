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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_1_unchecked;
import static com.github.jinahya.bit.io.FloatConstants.SIZE_EXPONENT;
import static com.github.jinahya.bit.io.FloatConstants.SIZE_SIGNIFICAND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atMost;

/**
 * A class for testing {@link FloatWriter} and {@link FloatReader}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Float_Wr_Test {

    static Stream<Arguments> sizesArgumentsStream() {
        return FloatTestParameters.getExponentSizeAndSignificandSizeArgumentsStream();
    }

    static Stream<Arguments> sizesAndValuesArgumentsStream() {
        return FloatTestParameters.getExponentSizeAndSignificandSizeAndValueArgumentsStream();
    }

    private static Stream<Float> valueStream() {
        return FloatTestParameters.valueStream();
    }

    @DisplayName("write(value) -> read()value")
    @MethodSource({"valueStream"})
    @ParameterizedTest
    void wr__(final Float value) throws IOException {
        try (MockedStatic<FloatConstraints> floatConstraints
                     = Mockito.mockStatic(FloatConstraints.class, Mockito.CALLS_REAL_METHODS)) {
            final var actual = write_read_1_unchecked(o -> {
                new FloatWriter(SIZE_EXPONENT, SIZE_SIGNIFICAND).write(o, value);
                return i -> new FloatReader(SIZE_EXPONENT, SIZE_SIGNIFICAND).read(i);
            });
            if (value.isNaN()) {
                assertThat(actual).isNaN();
                return;
            }
//            log.debug("w: {}", FloatTestUtils.formatBinary(value));
//            log.debug("r: {}", FloatTestUtils.formatBinary(actual));
            assertThat(actual).isEqualTo(value);
            floatConstraints.verify(() -> FloatConstraints.requireValidExponentSize(SIZE_EXPONENT), atMost(4));
            floatConstraints.verify(() -> FloatConstraints.requireValidSignificandSize(SIZE_SIGNIFICAND), atMost(4));
        }
    }

    @DisplayName("nullable().write(, , value) -> nullable().read(, )value")
    @MethodSource({"sizesAndValuesArgumentsStream"})
    @ParameterizedTest
    void wr__(final int exponentSize, final int significandSize, final Float value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new FloatWriter(exponentSize, significandSize).write(o, value);
            return i -> new FloatReader(exponentSize, significandSize).read(i);
        });
        if (value.isNaN()) {
            assertThat(actual).isNaN();
            return;
        }
        assertThat(actual).isEqualTo(value);
    }

    @DisplayName("nullable().write(, , value) -> nullable().read(, )value")
    @MethodSource({"sizesAndValuesArgumentsStream"})
    @ParameterizedTest
    void wr__Nullable(final int exponentSize, final int significandSize, final Float value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new FloatWriter(exponentSize, significandSize).nullable().write(o, value);
            return i -> new FloatReader(exponentSize, significandSize).nullable().read(i);
        });
        if (value.isNaN()) {
            assertThat(actual).isNaN();
            return;
        }
        assertThat(actual).isEqualTo(value);
    }

    @DisplayName("nullable().write(null) -> nullable().read()null")
    @MethodSource({"sizesArgumentsStream"})
    @ParameterizedTest
    void wr_Null_Nullable(final int exponentSize, final int significandSize) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new FloatWriter(exponentSize, significandSize).nullable().write(o, null);
            return i -> new FloatReader(exponentSize, significandSize).nullable().read(i);
        });
        assertThat(actual).isNull();
    }
}
