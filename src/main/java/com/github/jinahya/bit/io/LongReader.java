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
 * An interface for reading {@code int} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LongWriter
 */
@FunctionalInterface
public interface LongReader
        extends IntReader {

    /**
     * Reads a value from specified input.
     *
     * @param input the input from which the value is read.
     * @return a value read.
     * @throws IOException if an I/O error occurs.
     */
    long readLong(BitInput input) throws IOException;

    /**
     * {@inheritDoc}
     *
     * @param input the input from which the value is read.
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @apiNote Default implementation invokes {@link #readLong(BitInput)} with {@code input}, and returns the result
     * as an {@code int} via {@link Math#toIntExact(long)}.
     * @see Math#toIntExact(long)
     */
    @Override
    default int readInt(final BitInput input) throws IOException {
        return Math.toIntExact(readLong(input));
    }
}
