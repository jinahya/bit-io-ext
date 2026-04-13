package com.github.jinahya.bit.io.miscellaneous;

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

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.LongReader;

import java.io.IOException;
import java.util.Objects;

/**
 * A reader for reading values using <a href="https://en.wikipedia.org/wiki/LEB128">LEB128</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Leb128Writer
 */
@SuppressWarnings({
        "java:S1610" // empty abstract class
})
public abstract class Leb128Reader // NOSONAR
        implements LongReader {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A reader for reading <a href="https://en.wikipedia.org/wiki/LEB128#Unsigned_LEB128">unsigned LEB128</a> values.
     */
    public static class OfUnsigned
            extends Leb128Reader {

        private static final class InstanceHolder {

            private static final OfUnsigned INSTANCE = new OfUnsigned();

            private InstanceHolder() {
                throw new AssertionError(BitIoMiscellaneousConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class. The {@code OfUnsigned} class is singleton.
         *
         * @return the instance.
         */
        public static OfUnsigned getInstance() {
            return InstanceHolder.INSTANCE;
        }

        /**
         * Creates a new instance.
         */
        private OfUnsigned() {
            super();
        }

        @Override
        public long readLong(final BitInput input) throws IOException {
            Objects.requireNonNull(input, "input is null");
            long value = 0L;
            for (int shift = 0; ; shift += 7) {
                final int group = input.readInt(true, Byte.SIZE);
                value |= ((group & 0x7FL) << shift);
                if ((group & 0x80) == 0) {
                    break;
                }
            }
            return value;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A reader for reading <a href="https://en.wikipedia.org/wiki/LEB128#Signed_LEB128">signed LEB128</a> values.
     */
    public static class OfSigned
            extends Leb128Reader {

        private static final class InstanceHolder {

            private static final OfSigned INSTANCE = new OfSigned();

            private InstanceHolder() {
                throw new AssertionError(BitIoMiscellaneousConstants.MESSAGE_INSTANTIATION_IS_NOT_ALLOWED);
            }
        }

        /**
         * Returns the instance of this class. The {@code OfSigned} class is singleton.
         *
         * @return the instance.
         */
        public static OfSigned getInstance() {
            return InstanceHolder.INSTANCE;
        }

        /**
         * Creates a new instance.
         */
        private OfSigned() {
            super();
        }

        @Override
        public long readLong(final BitInput input) throws IOException {
            Objects.requireNonNull(input, "input is null");
            long value = 0L;
            for (int shift = 0; ; ) {
                final int group = input.readInt(true, Byte.SIZE);
                value |= ((group & 0x7FL) << shift);
                shift += 7;
                if ((group & 0x80) == 0) {
                    if (shift < Long.SIZE && (group & 0x40) != 0) {
                        value |= (~0L << shift);
                    }
                    break;
                }
            }
            return value;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the instance of {@link OfUnsigned} class.
     *
     * @return the instance of {@link OfUnsigned} class.
     */
    public static Leb128Reader getInstanceUnsigned() {
        return OfUnsigned.getInstance();
    }

    /**
     * Returns the instance of {@link OfSigned} class.
     *
     * @return the instance of {@link OfSigned} class.
     */
    public static Leb128Reader getInstanceSigned() {
        return OfSigned.getInstance();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected Leb128Reader() {
        super();
    }
}
