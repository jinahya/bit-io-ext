package com.github.jinahya.bit.io.recipe;

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

import com.github.jinahya.bit.io.BitIoTestUtils;
import com.github.jinahya.bit.io.ListReader;
import com.github.jinahya.bit.io.ListWriter;
import com.github.jinahya.bit.io.User;
import com.github.jinahya.bit.io.UserReader;
import com.github.jinahya.bit.io.UserWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class List_Wr_User_Test {

    private static Stream<List<User>> randomValueStream() {
        return Stream.of(
                IntStream.range(0, 16)
                        .mapToObj(i -> User.newRandomInstance())
                        .collect(Collectors.toList())
        );
    }

    @MethodSource({"randomValueStream"})
    @ParameterizedTest
    void wr__(final List<User> expected) throws IOException {
        final var actual = BitIoTestUtils.write_read_1_array_unchecked(o -> {
            new ListWriter<>(new UserWriter()).write(o, expected);
            return (a, i) -> {
                log.debug("a.length: {}", a.length);
                return new ListReader<>(new UserReader()).read(i);
            };
        });
        assertThat(actual).isEqualTo(expected);
    }
}
