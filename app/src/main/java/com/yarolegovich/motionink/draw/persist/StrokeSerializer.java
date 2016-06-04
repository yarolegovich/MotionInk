package com.yarolegovich.motionink.draw.persist;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wacom.ink.serialization.InkDecoder;
import com.wacom.ink.serialization.InkEncoder;
import com.wacom.ink.utils.Utils;
import com.yarolegovich.motionink.draw.Stroke;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class StrokeSerializer extends Serializer<List<Stroke>, List<Stroke>> {

    private static final int DEFAULT_DECIMAL_PRECISION = 2;

    private int decimalPrecision;

    public StrokeSerializer(Context context) {
        super(context);

        decimalPrecision = DEFAULT_DECIMAL_PRECISION;
    }

    @Override
    public void save(int slidePosition, List<Stroke> data) {
        File slideFile = new File(getDir(), getFileName(slidePosition));

        InkEncoder inkEncoder = new InkEncoder();

        for (Stroke stroke : data) {
            inkEncoder.encodePath(
                    decimalPrecision, stroke.getPoints(), stroke.getSize(),
                    stroke.getStride(), stroke.getWidth(), stroke.getColor(),
                    stroke.getStartValue(), stroke.getEndValue(),
                    stroke.getBlendMode());
        }

        ByteBuffer encodedData = inkEncoder.getEncodedData();
        int encodedSize = inkEncoder.getEncodedDataSizeInBytes();

        byte[] bytes = new byte[encodedSize];
        if (encodedSize > 0) {
            encodedData.position(0);
            encodedData.get(bytes);
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(bytes);

        Uri slideUri = Uri.fromFile(slideFile);

        Utils.saveBinaryFile(slideUri, buffer, 0, encodedSize);
    }

    @Override
    public List<Stroke> load(int slidePosition) {
        File slideFile = new File(getDir(), getFileName(slidePosition));
        Uri slideUri = Uri.fromFile(slideFile);

        ByteBuffer buffer = Utils.loadBinaryFile(slideUri);

        if (buffer == null) {
            return new LinkedList<>();
        }

        buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);

        InkDecoder decoder = new InkDecoder(buffer);
        List<Stroke> result = new LinkedList<>();

        while (decoder.decodeNextPath()) {
            Stroke stroke = new Stroke(decoder.getDecodedPathSize());
            stroke.setColor(decoder.getDecodedPathIntColor());
            stroke.setStride(decoder.getDecodedPathStride());
            stroke.setInterval(decoder.getDecodedPathTs(), decoder.getDecodedPathTf());
            stroke.setWidth(decoder.getDecodedPathWidth());
            stroke.setBlendMode(stroke.getBlendMode());
            Utils.copyFloatBuffer(
                    decoder.getDecodedPathData(), stroke.getPoints(), 0, 0,
                    decoder.getDecodedPathSize());
            stroke.calculateBounds();
            result.add(stroke);
        }

        return result;
    }

    @Override
    protected File getDir() {
        return new File(context.getFilesDir() + "/strokes");
    }

    @Override
    protected String getFileName(int slidePosition) {
        return "slide" + slidePosition;
    }


    @Override
    protected File[] getFiles() {
        return getDir().listFiles((dir, filename) -> !filename.contains("-"));
    }

    public void setDecimalPrecision(int decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
    }
}
