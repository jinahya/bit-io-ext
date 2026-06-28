package io.github.jinahya.bit.io.ext;

import com.github.jinahya.bit.io.BitReader;
import com.github.jinahya.bit.io.BitWriter;
import com.github.jinahya.bit.io.FilterBitWriter;

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
 * A {@link FilterBitWriter} extension with factory methods for mapped writers.
 *
 * @param <T> value type parameter
 * @param <U> delegate value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ExtendedFilterBitReader
 */
public abstract class ExtendedFilterBitWriter<T, U>
        extends FilterBitWriter<T, U> {

    /**
     * Creates a new writer that applies specified mapper before writing values to specified delegate.
     * <p>
     * {@snippet lang = java:
     * final BitWriter<Integer> sink = (output, value) -> {
     * };
     * final var writer = mapping(sink, value -> value.length());
     *}
     *
     * <p>Mappings can be composed by nesting calls. The inner mapping is closest to the delegate.</p>
     * <p>
     * {@snippet lang = java:
     * final BitWriter<Integer> sink = (output, value) -> {
     * };
     * final var writer = mapping(mapping(sink, value -> value.length()), value -> value.toString());
     *}
     *
     * @param delegate a value writer for writing mapped values.
     * @param mapper   a mapper for mapping original values.
     * @param <T>      original value type parameter
     * @param <U>      mapped value type parameter
     * @return a new writer.
     * @throws NullPointerException if {@code delegate} or {@code mapper} is {@code null}.
     * @see ExtendedFilterBitReader#mapping(BitReader, Function)
     */
    public static <T, U> ExtendedFilterBitWriter<T, U> mapping(final BitWriter<? super U> delegate,
                                                               final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(delegate, "delegate is null");
        Objects.requireNonNull(mapper, "mapper is null");
        return new ExtendedFilterBitWriter<>(delegate) {
            @Override
            protected U apply(final T value) {
                return mapper.apply(value);
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping specified delegate.
     *
     * @param delegate the writer to wrap.
     * @throws NullPointerException if {@code delegate} is {@code null}.
     */
    protected ExtendedFilterBitWriter(final BitWriter<? super U> delegate) {
        super(delegate);
    }
}
