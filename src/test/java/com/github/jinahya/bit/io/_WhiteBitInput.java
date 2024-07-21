package com.github.jinahya.bit.io;

class _WhiteBitInput
        extends ByteInputAdapter {

    public _WhiteBitInput() {
        super(new _WhiteInputStream());
    }
}
