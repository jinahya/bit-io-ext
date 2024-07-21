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

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.jinahya.bit.io.BitIoTestUtils.write_read_2_uncheckd;

public class ReadmeTest {

    @Test
    void test1() throws IOException {
        write_read_2_uncheckd(o -> {
            o.writeBoolean(true);       // 1 bit   1
            o.writeInt(true, 3, 1);     // 3 bits  4
            o.writeLong(false, 37, 0L); // 37 bits 41
            final long padded = o.align(1);
            assert padded == 7L;
            assert (padded + 41) % Byte.SIZE == 0;
            return i -> {
                boolean v1 = i.readBoolean();    // 1 bit   1
                int v2 = i.readInt(true, 3);     // 3 bits  4
                assert v2 == 1;
                long v3 = i.readLong(false, 37); // 37 bits 41
                assert v3 == 0L;
                long discarded = i.align(1);
                assert discarded == 7L;
                assert (discarded + 41) % Byte.SIZE == 0;
            };
        });
    }
}
