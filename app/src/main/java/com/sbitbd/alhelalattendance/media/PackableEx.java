package com.sbitbd.alhelalattendance.media;

public interface PackableEx extends Packable{
    void unmarshal(ByteBuf in);
}
