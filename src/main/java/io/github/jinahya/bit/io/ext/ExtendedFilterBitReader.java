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
 * Extended reader filters on top of bit-io's {@link com.github.jinahya.bit.io.FilterBitReader}.
 *
 * @param <T> value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ExtendedFilterBitWriter
 */
public abstract class ExtendedFilterBitReader<T>
        extends FilterBitReader<T> {

    /**
     * Creates a new reader that maps values read from specified delegate.
     *
     * @param delegate a reader for reading original values.
     * @param mapper   a mapper for mapping values.
     * @param <SOURCE> original value type parameter
     * @param <T>      mapped value type parameter
     * @return a new reader.
     * @see ExtendedFilterBitWriter#mapping(BitWriter, Function)
     */
    public static <SOURCE, T> BitReader<T> mapping(final BitReader<? extends SOURCE> delegate,
                                                   final Function<? super SOURCE, ? extends T> mapper) {
        Objects.requireNonNull(delegate, "delegate is null");
        Objects.requireNonNull(mapper, "mapper is null");
        return i -> mapper.apply(delegate.read(i));
    }

    /**
     * Creates a new instance on top of specified delegate.
     *
     * @param delegate the delegate to wrap.
     */
    protected ExtendedFilterBitReader(final BitReader<? extends T> delegate) {
        super(delegate);
    }
}
