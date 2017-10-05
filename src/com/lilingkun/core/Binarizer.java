package com.lilingkun.core;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KunKun on 2017/10/5.
 */
public class Binarizer {
    private Mat sourceMat = null;
    private Mat binarizerMat = null;

    public Binarizer(Mat src) {
        this.sourceMat = src;
        binarizerMat = binary();
    }

    public Mat getBinarizerMat() {
        return this.binarizerMat;
    }

    private boolean vis[][] = null;

    private void dfs(Mat mat,int x,int y) {
        System.out.println(x+" "+y);
        if(vis[x][y]) return;
        mat.put(x,y,255);
        vis[x][y] = true;
        for(int i = -1 ; i <= 1; i++ ) {
            for(int j = -1 ; j <= 1 ; j++ ) {
                if( i == 0 && j == 0 ) continue;
                int dx = i+x;
                int dy = j+y;
                if(dx <0 || dy < 0 || dx >= mat.height() || dy >= mat.width()) continue;
                if( Math.abs(mat.get(dx,dy)[0]) < 1e-8) {
                    dfs(mat, dx, dy);
                }
            }
        }
    }

    private Mat binary() {
        Mat res = new Mat();
        double ostu_T = Imgproc.threshold(sourceMat, res, 0,255, Imgproc.THRESH_OTSU);

        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(sourceMat);
        final double CI = 0.12;
        double beta = CI*(minMaxLocResult.maxVal - minMaxLocResult.minVal + 1) / 128.0;
        double beta_lowT = (1-beta) * ostu_T;
        double beta_highT = (1+beta) * ostu_T;

        int rows = sourceMat.rows();
        int cols = sourceMat.cols();

        double tbn;

        for( int i = 0 ; i < rows; i ++ ) {
            for(int j = 0 ; j < cols; j ++ ) {
                if( i<2 || i > rows -3 || j < 2 || j > cols-3) {
                    if( sourceMat.get(i,j)[0] <= beta_lowT) {
                        res.put(i,j,0);
                    } else {
                        res.put(i,j,255);
                    }
                } else {
                    tbn = 0;
                    for(int k = i-2; k < i-2+5 ; k++ ) {
                        for(int l = j-2; l < j-2+5; l++ ) {
                            tbn += sourceMat.get(k,l)[0];
                        }
                    }
                    tbn /= 25.0;
                    double t = sourceMat.get(i,j)[0];
                    if( t <= beta_lowT || (t < tbn && (beta_lowT <= t && t >= beta_highT))) {
                        res.put(i,j,0);
                    }
                    if( t>beta_highT || (t >= tbn && (beta_lowT <= t && t >= beta_highT))){
                        res.put(i,j,255);
                    }
                }
//                System.out.println(i+" "+j);
            }
        }

//        vis = new boolean[res.height()][res.width()];
//        for(int i = 0 ; i < res.height() ; i++ ) {
//            for(int j = 0 ; j < res.width() ; j++ ) {
//                vis[i][j] = false;
//            }
//        }
//        dfs(res, 0, 0);
        return res;
    }

    public Image toImage() {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", binarizerMat, mob);
        byte[] byteArray = mob.toArray();
        BufferedImage buffImage = null;
        InputStream in = new ByteArrayInputStream(byteArray);
        try {
            buffImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableImage wr = null;
        if( buffImage != null ) {
            wr = new WritableImage(buffImage.getWidth(), buffImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for(int x = 0 ; x < buffImage.getWidth(); x++ ) {
                for(int y = 0 ; y < buffImage.getHeight() ; y++ ) {
                    pw.setArgb(x,y, buffImage.getRGB(x,y));
                }
            }
        }
        return wr;
    }

}
