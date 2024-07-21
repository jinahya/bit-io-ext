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
import java.util.function.ToIntFunction;

/**
 * A reader for reading arrays of bytes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ByteArrayWriter
 */
public class ByteArrayReader
        implements BitReader<byte[]>,
                   CountReader {

    // -----------------------------------------------------------------------------------------------------------------
    static class Unsigned
            extends ByteArrayReader {

        @SuppressWarnings({
                "java:S1700" // UNSIGNED <> Unsigned
        })
        private static final boolean UNSIGNED = true; // NOSONAR

        private Unsigned(final int elementSize) {
            super(BitIoConstraints.requireValidSizeForByte(UNSIGNED, elementSize));
        }

        @Override
        byte readElement(final BitInput input) throws IOException {
            return input.readByte(UNSIGNED, getElementSize());
        }
    }

    /**
     * Creates a new instance for reading an array of unsigned bytes.
     *
     * @param elementSize a number of bits for each element in the array; between {@code 1} (inclusive) and
     *                    {@value Byte#SIZE} (exclusive).
     * @return a new instance.
     * @see ByteArrayWriter#unsigned(int)
     */
    public static ByteArrayReader unsigned(final int elementSize) {
        return new Unsigned(elementSize);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * .
     *
     * @see ByteArrayWriter.CompressedAscii
     */
    static class CompressedAscii
            extends Unsigned {

        /**
         * An ASCII bytes reader for printable characters.
         *
         * @see <a href="https://en.wikipedia.org/wiki/ASCII#Printable_characters">Printable characters</a>
         * @see ByteArrayWriter.CompressedAscii.PrintableOnly
         */
        static class PrintableOnly
                extends CompressedAscii {

            private PrintableOnly() {
                super();
            }

            @Override
            byte readElement(final BitInput input) throws IOException {
                if (input.readBoolean()) {
                    return (byte) (input.readInt(true, 6) + 0x20);
                }
                return (byte) (input.readInt(true, 5) + 0x60);
            }
        }

        private CompressedAscii() {
            super(7);
        }

        @Override
        byte readElement(final BitInput input) throws IOException {
            return input.readByte(true, getElementSize());
        }
    }

    /**
     * Creates a new instance for reading arrays of ASCII bytes.
     *
     * @param printableOnly a flag for printable characters only; {@code true} for printable characters only;
     *                      {@code false} otherwise.
     * @return a new instance.
     */
    public static ByteArrayReader compressedAscii(final boolean printableOnly) {
        if (printableOnly) {
            return new CompressedAscii.PrintableOnly();
        }
        return new CompressedAscii();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A reader for reading an array of UTF-8 bytes as a compressed manner.
     *
     * @see ByteArrayWriter.CompressedUtf8
     */
    static class CompressedUtf8
            extends ByteArrayReader {

        private CompressedUtf8() {
            super(Byte.SIZE);
        }

        @Override
        void readElements(final BitInput input, final byte[] value) throws IOException {
            for (int i = 0; i < value.length; i++) {
                switch (input.readInt(true, 2)) {
                    case 0b00:
                        value[i] = (byte) input.readInt(true, 7);
                        break;
                    case 0b01:
                        value[i] = (byte) (0b1100_0000 | input.readInt(true, 5));
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        break;
                    case 0b10:
                        value[i] = (byte) (0b1110_0000 | input.readInt(true, 4));
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        break;
                    default: // 0b11
                        value[i] = (byte) (0b1111_0000 | input.readInt(true, 3));
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        value[++i] = (byte) (0b1000_0000 | input.readInt(true, 6)); // NOSONAR
                        break;
                }
            }
        }
    }

    /**
     * Creates a new instance writes UTF-8 byte arrays in a compressed manner.
     *
     * @return a new instance.
     * @see ByteArrayWriter#compressedUtf8()
     */
    public static ByteArrayReader compressedUtf8() {
        return new CompressedUtf8();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     *
     * @param elementSize a number of bits for each element in the array; between {@code 1} and {@value Byte#SIZE}, both
     *                    inclusive.
     */
    ByteArrayReader(final int elementSize) {
        super();
        this.elementSize = BitIoConstraints.requireValidSizeForByte(false, elementSize);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public byte[] read(final BitInput input) throws IOException {
        final int length = countReader.applyAsInt(input);
        final byte[] value = new byte[length];
        readElements(input, value);
        return value;
    }

    void readElements(final BitInput input, final byte[] elements) throws IOException {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = readElement(input);
        }
    }

    byte readElement(final BitInput input) throws IOException {
        return input.readByte(false, elementSize); // signed elementSize-bit byte
    }

    int getElementSize() {
        return elementSize;
    }

    @Override
    public void setCountReader(final ToIntFunction<? super BitInput> countReader) {
        this.countReader = Objects.requireNonNull(countReader, "countReader is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final int elementSize;

    private ToIntFunction<? super BitInput> countReader = CountReader.COUNT_READER_31;
}
