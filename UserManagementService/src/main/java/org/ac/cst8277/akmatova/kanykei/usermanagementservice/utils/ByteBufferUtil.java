package org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class ByteBufferUtil {
    public static ByteBuffer getByteBuffer(UUID message) {
        ByteBuffer producerId = ByteBuffer.allocate(16);
        producerId.putLong(message.getMostSignificantBits());
        producerId.putLong(message.getLeastSignificantBits());
        return producerId;
    }
}
