package com.github.jinahya.bit.io;

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

import io.vavr.CheckedFunction1;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.concurrent.ThreadLocalRandom.current;

public final class BitIoRandom {

    // ------------------------------------------------------------------------------------------------------------ byte
    static int nextSizeForByte(final boolean unsigned) {
        final var size = current().nextInt(1, Byte.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForByte(unsigned, size);
    }

    static <R> R applyRandomSizeForByte(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextSizeForByte(unsigned));
    }

    static byte nextValueForByte(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        final byte value;
        if (unsigned) {
            value = (byte) (current().nextInt() >>> (Integer.SIZE - size));
            assert value >= 0;
            assert value >> size == 0;
        } else {
            value = (byte) (current().nextInt() >> (Integer.SIZE - size));
            if (size < Byte.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForByte(final boolean unsigned, final int size,
                                         final IntFunction<? extends R> function) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextValueForByte(unsigned, size));
    }

    static <R> R applyRandomValueForByteUnchecked(final boolean unsigned, final int size,
                                                  final CheckedFunction1<? super Integer, ? extends R> function) {
        BitIoConstraints.requireValidSizeForByte(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForByte(unsigned, size, v -> function.unchecked().apply(v));
    }

    static <R> R applyRandomSizeAndValueForByte(final boolean unsigned,
                                                final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForByte(
                unsigned,
                s -> applyRandomValueForByte(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForByteUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeAndValueForByte(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    // ----------------------------------------------------------------------------------------------------------- short
    static int nextSizeForShort(final boolean unsigned) {
        final int size = current().nextInt(1, Short.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForShort(unsigned, size);
    }

    static <R> R applyRandomSizeForShort(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextSizeForShort(unsigned));
    }

    static short nextValueForShort(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForShort(unsigned, size);
        final short value;
        if (unsigned) {
            value = (short) (current().nextInt() >>> (Integer.SIZE - size));
            assert value >> size == 0;
        } else {
            value = (short) (current().nextInt() >> (Integer.SIZE - size));
            if (size < Short.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForShort(final boolean unsigned, final int size,
                                          final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextValueForShort(unsigned, size));
    }

    static <R> R applyRandomValueForShortUnchecked(final boolean unsigned, final int size,
                                                   final CheckedFunction1<? super Integer, ? extends R> function) {
        return applyRandomValueForShort(
                unsigned, size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomValueForShort(final boolean unsigned,
                                          final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForShort(
                unsigned,
                s -> applyRandomValueForShort(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForShortUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomValueForShort(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    // ------------------------------------------------------------------------------------------------------------- int
    static int nextSizeForInt(final boolean unsigned) {
        final var size = current().nextInt(1, Integer.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForInt(unsigned, size);
    }

    static <R> R nextSizeForInt(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextSizeForInt(unsigned));
    }

    static int nextValueForInt(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        final int value;
        if (unsigned) {
            value = (current().nextInt() >>> (Integer.SIZE - size));
            assert value >> size == 0;
        } else {
            value = (current().nextInt() >> (Integer.SIZE - size));
            if (size < Integer.SIZE) {
                assert value >> size == 0 || value >> size == -1;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForInt(final boolean unsigned, final int size,
                                        final IntFunction<? extends R> function) {
        BitIoConstraints.requireValidSizeForInt(unsigned, size);
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextValueForInt(unsigned, size));
    }

    static <R> R applyRandomValueForIntUnchecked(final boolean unsigned, final int size,
                                                 final CheckedFunction1<? super Integer, ? extends R> function) {
        return applyRandomValueForInt(unsigned, size, v -> function.unchecked().apply(v));
    }

    static <R> R applyRandomSizeAndValueForInt(final boolean unsigned,
                                               final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return nextSizeForInt(
                unsigned,
                s -> applyRandomValueForInt(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForIntUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomSizeAndValueForInt(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    // ------------------------------------------------------------------------------------------------------------ long
    static int nextSizeForLong(final boolean unsigned) {
        final var size = current().nextInt(1, Long.SIZE + (unsigned ? 0 : 1));
        return BitIoConstraints.requireValidSizeForLong(unsigned, size);
    }

    static <R> R applyRandomSizeForLong(final boolean unsigned, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextSizeForLong(unsigned));
    }

    static long nextValueForLong(final boolean unsigned, final int size) {
        BitIoConstraints.requireValidSizeForLong(unsigned, size);
        final long value;
        if (unsigned) {
            value = (current().nextLong() >>> (Long.SIZE - size));
            assert value >= 0L;
            assert value >> size == 0L;
        } else {
            value = (current().nextLong() >> (Long.SIZE - size));
            if (size < Long.SIZE) {
                assert value >> size == 0L || value >> size == -1L;
            }
        }
        return value;
    }

    static <R> R applyRandomValueForLong(final boolean unsigned, final int size,
                                         final LongFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextValueForLong(unsigned, size));
    }

    static <R> R applyRandomValueForLongUnchecked(final boolean unsigned, final int size,
                                                  final CheckedFunction1<? super Long, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForLong(
                unsigned,
                size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomSizeAndValueForLong(final boolean unsigned,
                                                final IntFunction<? extends LongFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForLong(
                unsigned,
                s -> applyRandomValueForLong(unsigned, s, v -> function.apply(s).apply(v))
        );
    }

    static <R> R applyRandomSizeAndValueForLongUnchecked(
            final boolean unsigned,
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<Long, ? extends R>> function) {
        return applyRandomSizeAndValueForLong(
                unsigned,
                s -> v -> function.unchecked().apply(s).unchecked().apply(v)
        );
    }

    // ------------------------------------------------------------------------------------------------------------ char
    static int nextSizeForChar() {
        final int size = current().nextInt(1, Character.SIZE + 1);
        return BitIoConstraints.requireValidSizeForChar(size);
    }

    static <R> R applyRandomSizeForChar(final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextSizeForChar());
    }

    static char nextValueForChar(final int size) {
        BitIoConstraints.requireValidSizeForChar(size);
        final char value = (char) (current().nextInt() >>> (Integer.SIZE - size));
        assert value >> size == 0;
        return value;
    }

    static <R> R applyRandomValueForChar(final int size, final IntFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(nextValueForChar(size));
    }

    static <R> R applyRandomValueForCharUnchecked(final int size,
                                                  final CheckedFunction1<? super Integer, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomValueForChar(
                size,
                v -> function.unchecked().apply(v)
        );
    }

    static <R> R applyRandomValueForChar(final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomSizeForChar(s -> applyRandomValueForChar(s, v -> function.apply(s).apply(v)));
    }

    static <R> R applyRandomValueForCharUnchecked(
            final CheckedFunction1<? super Integer, ? extends CheckedFunction1<? super Integer, ? extends R>> function) {
        return applyRandomValueForChar(s -> v -> function.unchecked().apply(s).unchecked().apply(v));
    }

    // ----------------------------------------------------------------------------------------------------------- float
    static int nextExponentSizeForFloat() {
        return FloatConstraints.requireValidExponentSize(
                ThreadLocalRandom.current().nextInt(
                        FloatConstants.SIZE_MIN_EXPONENT,
                        FloatConstants.SIZE_EXPONENT + 1
                )
        );
    }

    static int nextExponentBitsForFloat(final int exponentSize) {
        FloatConstraints.requireValidExponentSize(exponentSize);
        return nextValueForInt(true, exponentSize) << FloatConstants.SIZE_SIGNIFICAND;
    }

    static int nextSignificandSizeForFloat() {
        return FloatConstraints.requireValidSignificandSize(
                ThreadLocalRandom.current().nextInt(
                        FloatConstants.SIZE_MIN_SIGNIFICAND,
                        FloatConstants.SIZE_SIGNIFICAND + 1
                )
        );
    }

    static int nextSignificandBitsForFloat(final int significandSize) {
        FloatConstraints.requireValidSignificandSize(significandSize);
        return nextValueForInt(true, significandSize) << (FloatConstants.SIZE_SIGNIFICAND - significandSize);
    }

    static int nextBitsForFloat(final int exponentSize, final int significandSize) {
        return (nextValueForInt(true, 1) << FloatConstants.SHIFT_SIGN_BIT)
               | nextExponentBitsForFloat(exponentSize)
               | nextSignificandBitsForFloat(significandSize);
    }

    static float nextValueForFloat(final int exponentSize, final int significandSize) {
        return Float.intBitsToFloat(nextBitsForFloat(exponentSize, significandSize));
    }

    static <R> R applyRandomValueBitsForFloat(
            final IntFunction<? extends IntFunction<? extends IntFunction<? extends R>>> function) {
        final var exponentSize = nextExponentSizeForFloat();
        final var significantSize = nextSignificandSizeForFloat();
        final var valueBits = nextBitsForFloat(exponentSize, significantSize);
        return function.apply(exponentSize)
                .apply(significantSize)
                .apply(valueBits);
    }

    static int nextSignificandBitsForFloatNaN(final int size) {
        FloatConstraints.requireValidSignificandSize(size);
        return nextSignificandBitsForFloat(size) | 0b01;
    }

    // ---------------------------------------------------------------------------------------------------------- double
    static int nextExponentSizeForDouble() {
        return DoubleConstraints.requireValidExponentSize(
                ThreadLocalRandom.current().nextInt(
                        DoubleConstants.SIZE_MIN_EXPONENT,
                        DoubleConstants.SIZE_EXPONENT + 1
                )
        );
    }

    static long nextExponentBitsForDouble(final int exponentSize) {
        DoubleConstraints.requireValidExponentSize(exponentSize);
        return nextValueForLong(true, exponentSize) << DoubleConstants.SIZE_SIGNIFICAND;
    }

    static int nextSignificandSizeForDouble() {
        return DoubleConstraints.requireValidSignificandSize(
                ThreadLocalRandom.current().nextInt(
                        DoubleConstants.SIZE_MIN_SIGNIFICAND,
                        DoubleConstants.SIZE_SIGNIFICAND + 1
                )
        );
    }

    static long getSignificandBitsForDouble(final int significandSize) {
        DoubleConstraints.requireValidSignificandSize(significandSize);
        return nextValueForLong(true, significandSize) << (DoubleConstants.SIZE_SIGNIFICAND - significandSize);
    }

    static long nextBitsForDouble(final int exponentSize, final int significandSize) {
        return (nextValueForLong(true, 1) << DoubleConstants.SHIFT_SIGN_BIT)
               | nextExponentBitsForDouble(exponentSize)
               | getSignificandBitsForDouble(significandSize);
    }

    static double nextValueForDouble(final int exponentSize, final int significandSize) {
        return Double.longBitsToDouble(nextBitsForDouble(exponentSize, significandSize));
    }

    static <R> R applyNextLongBitsForDouble(
            final LongFunction<? extends LongFunction<? extends LongFunction<? extends R>>> function) {
        final var exponentSize = nextExponentSizeForDouble();
        final var significantSize = nextSignificandSizeForDouble();
        final var valueBits = nextBitsForDouble(exponentSize, significantSize);
        return function.apply(exponentSize)
                .apply(significantSize)
                .apply(valueBits);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static int nextUnsignedInt() {
        return ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE;
    }

    public static int[] nextUnsignedIntArray(final int length) {
        return IntStream.range(0, length)
                .map(i -> nextUnsignedInt())
                .toArray();
    }

    public static int[] nextUnsignedIntArray() {
        return nextUnsignedIntArray(ThreadLocalRandom.current().nextInt(1, 128));
    }

    public static long nextUnsignedLong() {
        return ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE;
    }

    public static long[] nextUnsignedLongArray(final int length) {
        return LongStream.range(0, length)
                .map(i -> nextUnsignedLong())
                .toArray();
    }

    public static long[] nextUnsignedLongArray() {
        return nextUnsignedLongArray(ThreadLocalRandom.current().nextInt(1, 128));
    }

    public static int nextSignedInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int[] nextSignedIntArray(final int length) {
        return IntStream.range(0, length)
                .map(i -> nextSignedInt())
                .toArray();
    }

    public static int[] nextSignedIntArray() {
        return nextSignedIntArray(ThreadLocalRandom.current().nextInt(1, 128));
    }

    public static long nextSignedLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static long[] nextSignedLongArray(final int length) {
        return LongStream.range(0, length)
                .map(i -> nextSignedLong())
                .toArray();
    }

    public static long[] nextSignedLongArray() {
        return nextSignedLongArray(ThreadLocalRandom.current().nextInt(1, 128));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private BitIoRandom() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
