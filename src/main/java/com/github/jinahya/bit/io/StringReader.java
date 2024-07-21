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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * A value reader for reading {@code String} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see StringWriter
 */
public class StringReader
        extends FilterBitReader<byte[], String>
        implements CountReader {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for reading {@link StandardCharsets#US_ASCII} decoded strings in a compressed-manner.
     *
     * @param printableOnly a flag for printable characters only; {@code true} for printable characters; {@code false}
     *                      otherwise.
     * @return a new instance.
     * @see ByteArrayReader#compressedAscii(boolean)
     */
    public static StringReader compressedAscii(final boolean printableOnly) {
        final ByteArrayReader delegate = ByteArrayReader.compressedAscii(printableOnly);
        return new StringReader(delegate, StandardCharsets.US_ASCII);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for reading {@link StandardCharsets#UTF_8} decoded strings in a compressed-manner.
     *
     * @return a new instance.
     * @see ByteArrayReader#compressedUtf8()
     */
    public static StringReader compressedUtf8() {
        final ByteArrayReader delegate = ByteArrayReader.compressedUtf8();
        return new StringReader(delegate, StandardCharsets.UTF_8);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified arguments.
     *
     * @param delegate a reader for reading encoded bytes.
     * @param charset  a charset for decoding values.
     */
    public StringReader(final ByteArrayReader delegate, final Charset charset) {
        super(delegate);
        this.charset = Objects.requireNonNull(charset, "charset is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected String filter(final byte[] value) {
        return new String(value, charset);
    }

    @Override
    public void setCountReader(final ToIntFunction<? super BitInput> countReader) {
        ((CountReader) delegate).setCountReader(countReader);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Charset charset;
}
