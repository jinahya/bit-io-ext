package io.github.jinahya.bit.io;

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
 * Extended writer filters on top of bit-io's {@link com.github.jinahya.bit.io.FilterBitWriter}.
 *
 * @param <T> value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ExtendedFilterBitReader
 */
public abstract class ExtendedFilterBitWriter<T>
        extends FilterBitWriter<T> {

    /**
     * Creates a new writer that maps values before writing them to specified delegate.
     *
     * @param delegate a value writer for writing mapped values.
     * @param mapper   a mapper for mapping original values.
     * @param <T>      original value type parameter
     * @param <TARGET> mapped value type parameter
     * @return a new writer.
     * @see ExtendedFilterBitReader#mapping(BitReader, Function)
     */
    public static <T, TARGET> BitWriter<T> mapping(final BitWriter<? super TARGET> delegate,
                                                   final Function<? super T, ? extends TARGET> mapper) {
        Objects.requireNonNull(delegate, "delegate is null");
        Objects.requireNonNull(mapper, "mapper is null");
        return (o, v) -> delegate.write(o, mapper.apply(v));
    }

    /**
     * Creates a new instance on top of specified delegate.
     *
     * @param delegate the delegate to wrap.
     */
    protected ExtendedFilterBitWriter(final BitWriter<? super T> delegate) {
        super(delegate);
    }
}
