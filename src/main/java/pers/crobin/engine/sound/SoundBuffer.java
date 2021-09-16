package pers.crobin.engine.sound;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import pers.crobin.engine.util.Utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundBuffer {
    private final int         bufferId;

    private       ByteBuffer  vorbis = null;

    private       ShortBuffer pcm    = null;

    public SoundBuffer(String file) throws Exception {
        bufferId = AL10.alGenBuffers();
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(file, info);

            // Copy to buffer
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm,
                         info.sample_rate());
        }
    }

    private ShortBuffer readVorbis(String resource, STBVorbisInfo info) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vorbis = Utils.ioResourceToByteBuffer(resource, 32768);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            pcm = MemoryUtil.memAllocShort(lengthSamples);

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        }
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void cleanup() {
        alDeleteBuffers(this.bufferId);
        if (pcm != null) {
            MemoryUtil.memFree(pcm);
        }
    }

}
