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

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A value reader for reading filtered values.
 *
 * @param <T>        filtered value type parameter
 * @param <SOURCE> original value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilterBitWriter
 */
@SuppressWarnings({
        "java:S119"
})
public abstract class FilterBitReader<SOURCE, T>
        implements BitReader<T> {

    private static final class Nullable<T>
            extends FilterBitReader<T, T> {

        private Nullable(final BitReader<? extends T> delegate) {
            super(delegate);
        }

        @Override
        public BitReader<T> nullable() {
            throw new UnsupportedOperationException(BitIoConstants.MESSAGE_UNSUPPORTED_ALREADY_NULLABLE);
        }

        @Override
        public T read(final BitInput input) throws IOException {
            BitIoObjects.requireNonNullInput(input);
            final int flag = input.readInt(true, 1);
            if (flag == 0) {
                return null;
            }
            return super.read(input);
        }

        @Override
        protected T filter(final T value) {
            return value;
        }
    }

    /**
     * Returns a new reader handles {@code null} values on the behalf of specified reader.
     *
     * @param reader the reader.
     * @param <T>    value type parameter
     * @return a new reader handles {@code null} values.
     * @see FilterBitWriter#nullable(BitWriter)
     */
    public static <T> BitReader<T> nullable(final BitReader<? extends T> reader) {
        return new Nullable<>(reader);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance which reads values of {@link SOURCE}, and returns values of {@link T} mapped by specified
     * mapper.
     *
     * @param delegate a reader for reading original values of {@link SOURCE}.
     * @param mapper   a mapper for mapping values to {@link T}.
     * @param <SOURCE> original value type parameter
     * @param <T>      filtered value type parameter
     * @return a new instance.
     * @see FilterBitWriter#mapping(BitWriter, Function)
     */
    public static <SOURCE, T> BitReader<T> mapping(final BitReader<? extends SOURCE> delegate,
                                                   final Function<? super SOURCE, ? extends T> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return new FilterBitReader<SOURCE, T>(delegate) {
            @Override
            protected T filter(final SOURCE value) {
                return mapper.apply(value);
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance on top of specified delegate.
     *
     * @param delegate the delegate to wrap.
     * @see FilterBitWriter#FilterBitWriter(BitWriter)
     */
    protected FilterBitReader(final BitReader<? extends SOURCE> delegate) {
        super();
        this.delegate = Objects.requireNonNull(delegate, "delegate is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public T read(final BitInput input) throws IOException {
        return filter(delegate.read(input));
    }

    /**
     * Maps specified original value.
     *
     * @param value the value to map.
     * @return a mapped value.
     */
    protected abstract T filter(final SOURCE value);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The reader for reading original values.
     */
    final BitReader<? extends SOURCE> delegate;
}
