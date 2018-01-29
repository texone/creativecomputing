/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package cc.creativecomputing.gl.demo.font.lwjgl;

import org.lwjgl.*;

import cc.creativecomputing.core.logging.CCLog;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

import static org.lwjgl.BufferUtils.*;

public final class IOUtil {

    private IOUtil() {
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(Path path, int bufferSize) throws IOException {
        ByteBuffer buffer = null;
CCLog.info(path);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } 
        buffer.flip();
        return buffer.slice();
    }

}