package com.lilingkun.train;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

/**
 * Created by KunKun on 2017/10/5.
 */
public class Trainer {
    private static String path = "./digit/";
    private static int id = 72+72+72;

    public static void toPicFile(Mat mat) {
        for(int i = 0 ; i < mat.height(); i++ ) {
            for(int j = 0 ; j < mat.width() ;j++ ) {
                if( mat.get(i,j)[0] > 150 ) {
                    mat.put(i,j,255);
                } else {
                    mat.put(i,j,0);
                }
            }
        }
        Imgcodecs.imwrite(path+id+".jpg", mat);
        id++;
    }
}
