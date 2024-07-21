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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 * An interface for writers need to write a number of sub-values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see CountReader
 */
public interface CountWriter {

    /**
     * Returns a new instance reads {@code count} values of specified bits.
     *
     * @param size the number of bits to read for {@code count} values.
     * @return a new instance.
     */
    static ObjIntConsumer<BitOutput> newCountWriter(final int size) {
        BitIoConstraints.requireValidSizeForInt(true, size);
        return (o, v) -> {
            try {
                o.writeInt(true, size, v);
            } catch (final IOException ioe) {
                throw new RuntimeException("failed to write count(" + v + ") to " + o, ioe);
            }
        };
    }

    ObjIntConsumer<BitOutput> COUNT_WRITER_8 = newCountWriter(BitIoConstants.SIZE_8);

    ObjIntConsumer<BitOutput> COUNT_WRITER_16 = newCountWriter(BitIoConstants.SIZE_16);

    ObjIntConsumer<BitOutput> COUNT_WRITER_31 = newCountWriter(BitIoConstants.SIZE_31);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Configures this writer to use specified consumer for writing the count of elements.
     *
     * @param countWriter the consumer accepts an {@code output} and a {@code count}, and writes the {@code count} to
     *                    the {@code output}.
     */
    @SuppressWarnings({
            "java:S1874", // isAccessible
            "java:S3011" // setAccessible, set
    })
    default void setCountWriter(final ObjIntConsumer<? super BitOutput> countWriter) {
        Objects.requireNonNull(countWriter, "countWriter is null");
        try {
            final Field field = getClass().getDeclaredField("countWriter");
            if (!field.isAccessible()) { // NOSONAR
                field.setAccessible(true); // NOSONAR
            }
            field.set(this, countWriter); // NOSONAR
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to set 'countWriter", roe); // NOSONAR
        }
    }
}
