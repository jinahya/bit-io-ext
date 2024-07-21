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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

/**
 * A class for testing {@link DoubleWriter} and {@link DoubleReader}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Double_Wr_Test {

    static Stream<Arguments> sizesArgumentsStream() {
        return DoubleTestParameters.sizesArgumentsStream();
    }

    static Stream<Arguments> sizesAndValuesArgumentsStream() {
        return DoubleTestParameters.getExponentSizeAndSignificandSizeArgumentsStream();
    }

    @DisplayName("write(nonnull) -> read()expected")
    @MethodSource({"sizesAndValuesArgumentsStream"})
    @ParameterizedTest(name = "[{index}] exponentSize: {0}, significandSize: {1}, value: {2}")
    void rw__(final int exponentSize, final int significandSize, final Double value) throws IOException {
        try (MockedStatic<DoubleConstraints> constraints
                     = Mockito.mockStatic(DoubleConstraints.class, Mockito.CALLS_REAL_METHODS)) {
            final var actual = write_read_1_unchecked(o -> {
                new DoubleWriter(exponentSize, significandSize).write(o, value);
                return i -> new DoubleReader(exponentSize, significandSize).read(i);
            });
            if (value.isNaN()) {
                assertThat(actual).isNaN();
                return;
            }
            assertThat(actual).isEqualTo(value);
            constraints.verify(() -> DoubleConstraints.requireValidExponentSize(exponentSize), times(4));
            constraints.verify(() -> DoubleConstraints.requireValidSignificandSize(significandSize), times(4));
        }
    }

    @DisplayName("nullable().write(nonnull) -> nullable().read()expected")
    @MethodSource({"sizesAndValuesArgumentsStream"})
    @ParameterizedTest(name = "[{index}] exponentSize: {0}, significandSize: {1}, value: {2}")
    void wr_Nullable(final int exponentSize, final int significandSize, final Double value) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new DoubleWriter(exponentSize, significandSize).nullable().write(o, value);
            return i -> new DoubleReader(exponentSize, significandSize).nullable().read(i);
        });
        if (value.isNaN()) {
            assertThat(actual).isNaN();
            return;
        }
        assertThat(actual).isEqualTo(value);
    }

    @DisplayName("nullable().write(nonnull) -> nullable().read()expected")
    @MethodSource({"sizesArgumentsStream"})
    @ParameterizedTest(name = "[{index}] exponentSize: {0}, significandSize: {1}, value: {2}")
    void wr_Null_Nullable(final int exponentSize, final int significandSize) throws IOException {
        final var actual = write_read_1_unchecked(o -> {
            new DoubleWriter(exponentSize, significandSize).nullable().write(o, null);
            return i -> new DoubleReader(exponentSize, significandSize).nullable().read(i);
        });
        assertThat(actual).isNull();
    }
}
