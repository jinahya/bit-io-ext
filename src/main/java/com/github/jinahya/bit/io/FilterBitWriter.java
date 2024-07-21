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
 * A value writer for writing filtered values.
 *
 * @param <T>      original value type parameter
 * @param <TARGET> filtered value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FilterBitReader
 */
@SuppressWarnings({
        "java:S119"
})
public abstract class FilterBitWriter<T, TARGET>
        implements BitWriter<T> {

    /**
     * Creates a new instance which writes filtered values.
     *
     * @param delegate a value writer for writing filtered values.
     * @param mapper   a mapper for mapping original values.
     * @param <T>      original value type parameter
     * @param <TARGET> filtered value type parameter
     * @return a new instance.
     * @see FilterBitReader#mapping(BitReader, Function)
     */
    public static <T, TARGET> BitWriter<T> mapping(final BitWriter<? super TARGET> delegate,
                                                   final Function<? super T, ? extends TARGET> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return new FilterBitWriter<T, TARGET>(delegate) {
            @Override
            protected TARGET filter(final T value) {
                return mapper.apply(value);
            }
        };
    }

    private static final class Nullable<T>
            extends FilterBitWriter<T, T> {

        private Nullable(final BitWriter<? super T> delegate) {
            super(delegate);
        }

        @Override
        public BitWriter<T> nullable() {
            throw new UnsupportedOperationException(BitIoConstants.MESSAGE_UNSUPPORTED_ALREADY_NULLABLE);
        }

        @Override
        public void write(final BitOutput output, final T value) throws IOException {
            final boolean flag = value == null;
            output.writeInt(true, 1, flag ? 0 : 1);
            if (!flag) {
                super.write(output, value);
            }
        }

        @Override
        protected T filter(final T value) {
            return value;
        }
    }

    /**
     * Returns a new writer handles {@code null} values on the behalf of specified writer.
     *
     * @param writer the writer.
     * @param <T>    value type parameter
     * @return a new writer handles {@code null} values.
     * @see com.github.jinahya.bit.io.FilterBitReader#nullable(BitReader)
     */
    public static <T> BitWriter<T> nullable(final BitWriter<? super T> writer) {
        return new Nullable<>(writer);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance on top of specified delegate.
     *
     * @param delegate the delegate to wrap.
     * @see FilterBitReader#FilterBitReader(BitReader)
     */
    protected FilterBitWriter(final BitWriter<? super TARGET> delegate) {
        super();
        this.delegate = Objects.requireNonNull(delegate, "delegate is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void write(final BitOutput output, final T value) throws IOException {
        delegate.write(output, filter(value));
    }

    /**
     * Maps specified original value.
     *
     * @param value the original value to filter.
     * @return a filter value.
     */
    protected abstract TARGET filter(final T value);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The writer for writing filtered values.
     */
    final BitWriter<? super TARGET> delegate;
}
