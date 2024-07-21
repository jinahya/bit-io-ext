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
import java.io.UncheckedIOException;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * Constants defined for reading/writing bits.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;\
 */
public final class BitIoConstants {

    // -----------------------------------------------------------------------------------------------------------------
    static final int SIZE_8 = Byte.SIZE;

    static final int SIZE_16 = Short.SIZE;

    static final int SIZE_31 = Integer.SIZE - 1;

    static final int SIZE_63 = Long.SIZE - 1;

    // -----------------------------------------------------------------------------------------------------------------
    static final String MESSAGE_INPUT_IS_NULL = "input is null";

    static final String MESSAGE_OUTPUT_IS_NULL = "output is null";

    static final String MESSAGE_READER_IS_NULL = "reader is null";

    static final String MESSAGE_WRITER_IS_NULL = "writer is null";

    static final String MESSAGE_VALUE_IS_NULL = "value is null";

    static final String MESSAGE_INSTANTIATION_IS_NOT_ALLOWED = "instantiation is not allowed";

    static final String MESSAGE_ILLEGAL_ARGUMENT_ALREADY_NULLABLE = "illegal argument; already nullable";

    static final String MESSAGE_UNSUPPORTED_ALREADY_NULLABLE = "unsupported; already nullable";

    static final String MESSAGE_UNSUPPORTED_NOT_SUPPOSED_TO_BE_INVOKED = "unsupported; not supposed to be invoked";

    /**
     * A function for reading a {@code 31}-bit unsigned count value.
     *
     * @see BitIoUtils#readCount(BitInput)
     */
    static final ToIntFunction<? super BitInput> COUNT_READER = i -> {
        try {
            return BitIoUtils.readCount(i);
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to read a count from the input(" + i + ")", ioe);
        }
    };

    /**
     * A consumer for writing a {@code 31}-bit unsigned count value.
     *
     * @see BitIoUtils#writeCount(BitOutput, int)
     */
    static final ObjIntConsumer<? super BitOutput> COUNT_WRITER = (o, c) -> {
        try {
            BitIoUtils.writeCount(o, c);
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to write the count(" + c + ") to the output(" + o + ")", ioe);
        }
    };

    /**
     * A function reads an unsigned count value in a compressed manner.
     *
     * @see BitIoUtils#readCountCompressed(BitInput)
     */
    static final ToIntFunction<? super BitInput> COUNT_READER_COMPRESSED = i -> {
        try {
            return BitIoUtils.readCountCompressed(i);
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to read a compressed count from the input(" + i + ")", ioe);
        }
    };

    /**
     * A consumer writes given count value in a compressed manner.
     *
     * @see BitIoUtils#writeCountCompressed(BitOutput, int)
     */
    static final ObjIntConsumer<? super BitOutput> COUNT_WRITER_COMPRESSED = (o, c) -> {
        try {
            BitIoUtils.writeCountCompressed(o, c);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(
                    "failed to write the compressed count(" + c + ") to the output(" + o + ")", ioe);
        }
    };

    private BitIoConstants() {
        throw new AssertionError(MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
    }
}
