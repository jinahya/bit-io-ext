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
import org.junit.jupiter.params.provider.Arguments;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A class for testing {@link DoubleWriter} and {@link DoubleReader}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class DoubleTestParameters {

    static IntStream exponentSizeStream() {
        return IntStream.rangeClosed(DoubleConstants.SIZE_MIN_EXPONENT, DoubleConstants.SIZE_EXPONENT);
    }

    static IntStream significandSizeStream() {
        return IntStream.rangeClosed(DoubleConstants.SIZE_MIN_SIGNIFICAND, DoubleConstants.SIZE_SIGNIFICAND);
    }

    static Stream<Arguments> sizesArgumentsStream() {
        return Stream.concat(
                IntStream.range(0, 16)
                        .mapToObj(i -> Arguments.of(BitIoRandom.nextExponentSizeForDouble(),
                                                    BitIoRandom.nextSignificandSizeForDouble())),
                Stream.of(Arguments.of(FloatConstants.SIZE_EXPONENT, FloatConstants.SIZE_SIGNIFICAND))
        );
    }

    static Stream<Arguments> getExponentSizeAndSignificandSizeArgumentsStream() {
        return sizesArgumentsStream()
                .map(a -> {
                    final var got = a.get();
                    final var exponentSize = (int) got[0];
                    final var significandSize = (int) got[1];
                    final var value = BitIoRandom.nextValueForDouble(exponentSize, significandSize);
                    return Arguments.of(exponentSize, significandSize, value);
                });
    }

    static LongStream bitsStream() {
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

    static LongStream bitsStreamNonZeroSignificand() {
        return bitsStream()
                .filter(b -> (b & DoubleConstants.MASK_SIGNIFICAND) > 0L)
                ;
    }

    static Stream<Double> valueStream() {
        return Stream.concat(
                bitsStream().mapToObj(Double::longBitsToDouble),
                Stream.of(
                        Double.NEGATIVE_INFINITY,
                        -.5d,
                        +.5d,
                        Double.POSITIVE_INFINITY
                )
        );
    }

    static Stream<Double> valueStreamNonZeroSignificand() {
        return valueStream()
                .filter(v -> (Double.doubleToRawLongBits(v) & DoubleConstants.MASK_SIGNIFICAND) > 0L);
    }

    private DoubleTestParameters() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
