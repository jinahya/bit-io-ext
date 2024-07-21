package com.github.jinahya.bit.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

class _WhiteInputStream
        extends InputStream
        implements ByteInput {

    @Override
    public int read() throws IOException {
        return ThreadLocalRandom.current().nextInt(256);
    }
}
