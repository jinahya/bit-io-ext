package io.github.jinahya.bit.io.ext;

import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.FilterBitReader;

import java.util.Objects;
import java.util.function.Function;

/*-
 * #%L
 * bit-io-ext
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

/**
 * A {@link FilterBitReader} extension with factory methods for mapped readers.
 *
 * @param <T> value type parameter
 * @param <U> delegate value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ExtendedFilterBitWriter
 */
public abstract class ExtendedFilterBitReader<T, U>
        extends FilterBitReader<T, U> {

    /**
     * Creates a new reader that applies specified mapper to values read from specified delegate.
     * <p>
     * {@snippet lang = java:
     * final BitReader<Integer> source = input -> 3;
     * final var reader = mapping(source, value -> Integer.toString(value));
     *}
     *
     * <p>Mappings can be composed by nesting calls.</p>
     * <p>
     * {@snippet lang = java:
     * final BitReader<Integer> source = input -> 3;
     * final var reader = mapping(mapping(source, value -> value + 4), value -> Integer.toString(value));
     *}
     *
     * @param delegate a reader for reading original values.
     * @param mapper   a mapper for mapping values.
     * @param <U>      original value type parameter
     * @param <T>      mapped value type parameter
     * @return a new reader.
     * @throws NullPointerException if {@code delegate} or {@code mapper} is {@code null}.
     * @see ExtendedFilterBitWriter#mapping(BitWriter, Function)
     */
    public static <U, T> ExtendedFilterBitReader<T, U> mapping(final BitReader<? extends U> delegate,
                                                               final Function<? super U, ? extends T> mapper) {
        Objects.requireNonNull(delegate, "delegate is null");
        Objects.requireNonNull(mapper, "mapper is null");
        return new ExtendedFilterBitReader<>(delegate) {
            @Override
            protected T apply(final U value) {
                return mapper.apply(value);
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping specified delegate.
     *
     * @param delegate the reader to wrap.
     * @throws NullPointerException if {@code delegate} is {@code null}.
     */
    protected ExtendedFilterBitReader(final BitReader<? extends U> delegate) {
        super(delegate);
    }
}
