package com.jason.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Description: wav文件头信息
 * @author
 * @date 2018年4月16日
 */
public class WaveHeader {

    /**
     * 将文件标记为RIFF文件，每字符用1个字节表示。
     */
    public final char fileID[] = { 'R', 'I', 'F', 'F'};
    /**
     * 4个字节整数表示总体文件大小，以字节为单位（32 位整数）在创建之后即填写
     */
    public int fileLength;
    /**
     * 文件类型标记为WAVE
     */
    public char wavTag[] = { 'W', 'A', 'V', 'E'};
    /**
     * 标记格式块,描述数据格式信息
     */
    public char FmtHdrID[] = { 'f', 'm', 't', ' '};
    public int FmtHdrLeth;
    public short FormatTag;
    public short Channels;
    public int SamplesPerSec;
    public int AvgBytesPerSec;
    public short BlockAlign;
    public short BitsPerSample;
    public char DataHdrID[] = { 'd', 'a', 't', 'a'};
    public int DataHdrLeth;

    public byte[] getHeader() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeChar(bos, fileID);
        writeInt(bos, fileLength);
        writeChar(bos, wavTag);
        writeChar(bos, FmtHdrID);
        writeInt(bos, FmtHdrLeth);
        writeShort(bos, FormatTag);
        writeShort(bos, Channels);
        writeInt(bos, SamplesPerSec);
        writeInt(bos, AvgBytesPerSec);
        writeShort(bos, BlockAlign);
        writeShort(bos, BitsPerSample);
        writeChar(bos, DataHdrID);
        writeInt(bos, DataHdrLeth);
        bos.flush();
        byte[] r = bos.toByteArray();
        bos.close();
        return r;
    }

    private void writeShort(ByteArrayOutputStream bos, int s) throws IOException {
        byte[] mybyte = new byte[2];
        mybyte[1] = (byte) ((s << 16) >> 24);
        mybyte[0] = (byte) ((s << 24) >> 24);
        bos.write(mybyte);
    }

    private void writeInt(ByteArrayOutputStream bos, int n) throws IOException {
        byte[] buf = new byte[4];
        buf[3] = (byte) (n >> 24);
        buf[2] = (byte) ((n << 8) >> 24);
        buf[1] = (byte) ((n << 16) >> 24);
        buf[0] = (byte) ((n << 24) >> 24);
        bos.write(buf);
    }

    private void writeChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; i++) {
            char c = id[i];
            bos.write(c);
        }
    }

    /**
     * 初始化，获取头信息字节数组
     * 
     * @return
     */
    public static byte[] init(int PCMSize, int samples) {
        // 填入参数，比特率等等。这里用的是16位单声道 8000 hz
        WaveHeader header = new WaveHeader();
        // 长度字段 = 内容的大小（PCMSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
        header.fileLength = PCMSize + (44 - 8);
        header.FmtHdrLeth = 16;
        header.BitsPerSample = 16;
        header.Channels = 1;
        header.FormatTag = 0x0001;
        header.SamplesPerSec = samples;
        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
        header.DataHdrLeth = PCMSize;

        byte[] h = null;
        try {
            h = header.getHeader();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return h;
    }
}
