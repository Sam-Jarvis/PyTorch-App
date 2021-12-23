package com.example.pytorch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import org.pytorch.MemoryFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BirdDetector {
    private Module model;

    public BirdDetector(String modelPath)
    {
        try {
            model = LiteModuleLoader.load(modelPath);
        }
        catch (Exception e) {
            Log.e("PyTorchBirdDetector", "Error reading assets", e);
        }
    }

    public Tensor PrepareInput(Bitmap inputImage)
    {
        return TensorImageUtils.bitmapToFloat32Tensor(
                inputImage,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST
        );
    }

    public float[] Predict(Tensor inputTensor)
    {
        Tensor outputTensor =  model.forward(IValue.from(inputTensor)).toTensor();
        return getMaxScoreAndIndex(outputTensor.getDataAsFloatArray());
    }

    private float[] getMaxScoreAndIndex(float[] scores)
    {
        float[] maxCombo = new float[2];
        float maxScore = -1;
        int maxScoreIdx = -1;

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        maxCombo[0] = maxScore;
        maxCombo[1] = maxScoreIdx;
        return maxCombo;
    }
}
