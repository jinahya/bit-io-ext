package com.github.jinahya.bit.io;

class _BlackBitOutput
        extends ByteOutputAdapter {

    public _BlackBitOutput() {
        super(new _BlackOutputStream());
    }
}
