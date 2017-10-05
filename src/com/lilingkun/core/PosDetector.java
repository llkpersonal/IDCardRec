package com.lilingkun.core;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KunKun on 2017/10/5.
 */
public class PosDetector {
    private Mat sourceMat = null;

    public PosDetector(Mat src) {
        this.sourceMat = src;
    }

    public List<RotatedRect> detect() {
        List<RotatedRect> res = new ArrayList<RotatedRect>();
        Mat matInv = new Mat();
        Core.subtract(new Mat(sourceMat.size(),sourceMat.type(), new Scalar(255)), sourceMat,matInv);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15,3));
        Imgproc.morphologyEx(matInv, matInv, Imgproc.MORPH_CLOSE, element);
        Mat hie = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(matInv, contours, hie,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        for(MatOfPoint pt : contours ) {
            MatOfPoint2f pt2f = new MatOfPoint2f(pt.toArray());
            RotatedRect rotatedRect = Imgproc.minAreaRect(pt2f);
            if(isEligible(rotatedRect)) {
                res.add(rotatedRect);
            }
        }
        return res;
    }

    boolean isEligible(RotatedRect rect) {
        float error = 0.2f;
        float aspect = 4.5f/0.3f;
        int min = (int)(10*aspect*10);
        int max = (int)(50*aspect*50);
        float rmin = aspect - aspect* error;
        float rmax = aspect+ aspect * error;
        int area = (int)(rect.size.height * rect.size.width);
        float r = (float)rect.size.width / (float)rect.size.height;
        if( r<1 ) {
            r = 1/r;
        }
        if( (area < min || area > max) || (r<rmin || r> rmax)) {
            return false;
        }
        return true;
    }
}
