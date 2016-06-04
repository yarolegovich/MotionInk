package com.yarolegovich.motionink.draw.persist;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wacom.ink.serialization.InkDecoder;
import com.wacom.ink.serialization.InkEncoder;
import com.wacom.ink.utils.Utils;
import com.yarolegovich.motionink.draw.Stroke;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class StrokeSerializer {

    private static final String DIR = "/strokes";

    private static final int DEFAULT_DECIMAL_PRECISION = 2;
    private static final String FILE_NAME_BASE = "slide";

    private Context context;
    private int decimalPrecision;

    private int width;
    private int height;

    public StrokeSerializer(Context context) {
        this.context = context;

        decimalPrecision = DEFAULT_DECIMAL_PRECISION;

        File directory = new File(context.getFilesDir() + DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public boolean saveStrokes(int slidePosition, List<Stroke> strokeList) {
        return saveBinary(slidePosition, strokeList);
    }

    public List<Stroke> loadStrokes(int slidePosition) {
        return loadBinary(slidePosition);
    }

    private boolean saveBinary(int slidePosition, List<Stroke> strokeList) {
        File slideFile = new File(context.getFilesDir() + DIR, FILE_NAME_BASE + slidePosition);

        InkEncoder inkEncoder = new InkEncoder();

        for (Stroke stroke : strokeList) {
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

        return Utils.saveBinaryFile(slideUri, buffer, 0, encodedSize);
    }

    private List<Stroke> loadBinary(int slidePosition) {
        File slideFile = new File(context.getFilesDir(), FILE_NAME_BASE + slidePosition);
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

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setDecimalPrecision(int decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
    }

    public void clearProject() {
        File dir = new File(context.getFilesDir() + DIR);
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            new File(dir, children[i]).delete();
        }
    }
}
