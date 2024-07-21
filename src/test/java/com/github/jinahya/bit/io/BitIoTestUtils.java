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

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BitIoTestUtils {

    static <R> R write_1(final Function<? super BitOutput, Function<? super byte[], ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final BitOutput output = BitOutputFactory.from(stream);
        final Function<? super byte[], ? extends R> f2 = f1.apply(output);
        final long padded = output.align(1);
        assert padded >= 0L;
        final byte[] bytes = stream.toByteArray();
        assert f2 != null : "f2 is null";
        return f2.apply(bytes);
    }

    static <R> R write_1_unchecked(
            final CheckedFunction1<? super BitOutput,
                    CheckedFunction1<? super byte[], ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        return write_1(o -> {
            final CheckedFunction1<? super byte[], ? extends R> f2 = f1.unchecked().apply(o);
            assert f2 != null : "f2 is null";
            return b -> f2.unchecked().apply(b);
        });
    }

    static <R> R write_read_1(final Function<? super BitOutput, ? extends Function<? super BitInput, ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        return write_1(o -> {
            final Function<? super BitInput, ? extends R> f2 = f1.apply(o);
            assert f2 != null : "f2 is null";
            return a -> {
                final BitInput input = BitInputFactory.from(new ByteArrayInputStream(a));
                final R result = f2.apply(input);
                try {
                    final long discarded = input.align(1);
                    assert discarded >= 0L;
                    return result;
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            };
        });
    }

    public static <R> R write_read_1_unchecked(
            final CheckedFunction1<? super BitOutput, ? extends CheckedFunction1<? super BitInput, ? extends R>> f1)
            throws IOException {
        return write_read_1(o -> f1.unchecked().apply(o).unchecked());
    }

    static <R> R write_read_1_array(
            final Function<? super BitOutput, ? extends BiFunction<? super byte[], ? super BitInput, ? extends R>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        return write_1(o -> {
            final BiFunction<? super byte[], ? super BitInput, ? extends R> f2 = f1.apply(o);
            assert f2 != null : "f2 is null";
            return a -> {
                final BitInput input = BitInputFactory.from(new ByteArrayInputStream(a));
                final R result = f2.apply(a, input);
                try {
                    final long discarded = input.align(1);
                    assert discarded >= 0L;
                    return result;
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            };
        });
    }

    public static <R> R write_read_1_array_unchecked(
            final CheckedFunction1<? super BitOutput,
                    ? extends CheckedFunction2<byte[], ? super BitInput, ? extends R>> f1)
            throws IOException {
        return write_read_1_array(o -> f1.unchecked().apply(o).unchecked());
    }

    // -----------------------------------------------------------------------------------------------------------------
    static void write_2(final Function<? super BitOutput, Consumer<? super byte[]>> f1) throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        write_1(o -> {
            final var consumer = f1.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    static void write_read_2(final Function<? super BitOutput, ? extends Consumer<? super BitInput>> f1)
            throws IOException {
        Objects.requireNonNull(f1, "f1 is null");
        write_read_1(o -> {
            final var consumer = f1.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    static void write_read_2_uncheckd(
            final CheckedFunction1<? super BitOutput, ? extends CheckedConsumer<? super BitInput>> function)
            throws IOException {
        Objects.requireNonNull(function, "function is null");
        write_read_1_unchecked(o -> {
            final var consumer = function.apply(o);
            return i -> {
                consumer.accept(i);
                return null;
            };
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    static String format(final float value) {
        final String string = String.format("%32s", Integer.toBinaryString(Float.floatToRawIntBits(value)));
        return string.substring(0, 1) + ' ' + string.substring(1, 9) + ' ' + string.substring(9);
    }

    static String format(final double value) {
        final String string = String.format("%64s", Long.toBinaryString(Double.doubleToRawLongBits(value)));
        return string.substring(0, 1) + ' ' + string.substring(1, 11) + ' ' + string.substring(11);
    }

    private BitIoTestUtils() {
        throw new AssertionError(BitIoConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
