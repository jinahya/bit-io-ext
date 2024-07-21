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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * A reader for reading lists of specific element type.
 *
 * @param <E> element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ListWriter
 */
public class ListReader<E>
        implements BitReader<List<E>>,
                   CountReader {

    /**
     * Creates a new instance for reading lists of specified element type using specified element reader.
     *
     * @param elementReader the reader for reading elements.
     */
    public ListReader(final BitReader<? extends E> elementReader) {
        super();
        this.elementReader = Objects.requireNonNull(elementReader, "elementReader is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public List<E> read(final BitInput input) throws IOException {
        BitIoObjects.requireNonNullInput(input);
        final int size = countReader.applyAsInt(input);
        final List<E> value = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            value.add(elementReader.read(input));
        }
        return value;
    }

    @Override
    public void setCountReader(final ToIntFunction<? super BitInput> countReader) {
        this.countReader = Objects.requireNonNull(countReader, "countReader is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final BitReader<? extends E> elementReader;

    private ToIntFunction<? super BitInput> countReader = CountReader.COUNT_READER_31;
}
