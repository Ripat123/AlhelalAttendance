package com.sbitbd.alhelalacademy.media;

public interface PackableEx extends Packable{
    void unmarshal(ByteBuf in);
}
