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

/**
 * An interface for reading values of a specific type.
 *
 * @param <T> value type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BitWriter
 */
@FunctionalInterface
public interface BitReader<T> {

    /**
     * Returns a new instance handles {@code null} values.
     *
     * @return a new instance handles {@code null} values.
     * @deprecated
     */
    @Deprecated
    default BitReader<T> nullable() {
        return FilterBitReader.nullable(this);
    }

    /**
     * Reads a value from specified input.
     *
     * @param input the input from which a value is read.
     * @return a value read.
     * @throws IOException if an I/O error occurs.
     * @see BitInput#readObject(BitReader)
     */
    T read(BitInput input) throws IOException;
}
