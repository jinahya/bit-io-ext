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
import java.util.stream.Stream;

/**
 * Provides test parameters for {@link FloatWriter} and {@link FloatReader}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class FloatTestParameters {

    static Stream<Arguments> allSizesArgumentsStream() {
        return IntStream.rangeClosed(FloatConstants.SIZE_MIN_EXPONENT, FloatConstants.SIZE_EXPONENT)
                .mapToObj(es -> IntStream.range(FloatConstants.SIZE_MIN_SIGNIFICAND, FloatConstants.SIZE_SIGNIFICAND)
                        .mapToObj(ss -> Arguments.of(es, ss)))
                .flatMap(s -> s);
    }

    static Stream<Arguments> getExponentSizeAndSignificandSizeArgumentsStream() {
        return Stream.concat(
                IntStream.range(0, 16)
                        .mapToObj(i -> Arguments.of(BitIoRandom.nextExponentSizeForFloat(),
                                BitIoRandom.nextSignificandSizeForFloat())),
                Stream.of(Arguments.of(FloatConstants.SIZE_EXPONENT, FloatConstants.SIZE_SIGNIFICAND))
        );
    }

    static Stream<Arguments> getExponentSizeAndSignificandSizeAndValueArgumentsStream() {
        return getExponentSizeAndSignificandSizeArgumentsStream()
                .map(a -> {
                    final var got = a.get();
                    final var exponentSize = (int) got[0];
                    final var significandSize = (int) got[1];
                    final var value = BitIoRandom.nextValueForFloat(exponentSize, significandSize);
                    return Arguments.of(exponentSize, significandSize, value);
                });
    }

    static IntStream bitsStream() {
        return IntStream.of(
                Integer.MIN_VALUE,
                -1,
                0,
                +1,
                Integer.MAX_VALUE,
                ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE, // random positive
                ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE // random negative
        );
    }

    static Stream<Float> valueStream() {
        return Stream.concat(
                bitsStream().mapToObj(Float::intBitsToFloat),
                Stream.of(
                        Float.NEGATIVE_INFINITY,
                        -.5f,
                        +.5f,
                        Float.POSITIVE_INFINITY
                )
        );
    }

    static Stream<Float> valueStreamNonZeroSignificand() {
        return valueStream()
                .filter(v -> (Float.floatToRawIntBits(v) & FloatConstants.MASK_SIGNIFICAND) > 0)
                ;
    }

    private FloatTestParameters() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
