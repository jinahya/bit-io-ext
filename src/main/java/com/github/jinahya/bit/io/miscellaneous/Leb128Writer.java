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

import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.LongWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * A writer for writing values using <a href="https://en.wikipedia.org/wiki/LEB128">LEB128</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Leb128Reader
 */
@SuppressWarnings({
        "java:S1610" // empty abstract class
})
public abstract class Leb128Writer // NOSONAR
        implements LongWriter {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A writer for writing <a href="https://en.wikipedia.org/wiki/LEB128#Unsigned_LEB128">unsigned LEB128</a> values.
     */
    public static class OfUnsigned
            extends Leb128Writer {

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
        public void writeLong(final BitOutput output, long value) throws IOException {
            Objects.requireNonNull(output, BitIoMiscellaneousConstants.MESSAGE_OUTPUT_IS_NULL);
            if (value < 0L) {
                throw new IllegalArgumentException("negative value: " + value);
            }
            while (true) {
                final int group = (int) (value & 0x7FL);
                final boolean last = (value >>= 7) == 0L;
                output.writeInt(true, 1, last ? 0 : 1);
                output.writeInt(true, 7, group);
                if (last) {
                    break;
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A writer for writing <a href="https://en.wikipedia.org/wiki/LEB128#Signed_LEB128">signed LEB128</a> values.
     */
    public static class OfSigned
            extends Leb128Writer {

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
        public void writeLong(final BitOutput output, long value) throws IOException {
            Objects.requireNonNull(output, BitIoMiscellaneousConstants.MESSAGE_OUTPUT_IS_NULL);
            while (true) {
                final int group = (int) (value & 0x7FL);
                value >>= 7;
                final boolean clear = (group & 0x40) == 0;
                if ((value == 0 && clear) || (value == -1 && !clear)) {
                    output.writeInt(true, Byte.SIZE, group);
                    return;
                }
                output.writeInt(true, Byte.SIZE, (group | 0x80));
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the instance of {@link OfUnsigned} class.
     *
     * @return the instance of {@link OfUnsigned} class.
     */
    public static Leb128Writer getInstanceUnsigned() {
        return OfUnsigned.getInstance();
    }

    /**
     * Returns the instance of {@link OfSigned} class.
     *
     * @return the instance of {@link OfSigned} class.
     */
    public static Leb128Writer getInstanceSigned() {
        return OfSigned.getInstance();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected Leb128Writer() {
        super();
    }
}
