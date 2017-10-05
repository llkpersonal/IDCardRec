package com.lilingkun.core;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KunKun on 2017/10/5.
 */
public class Splitter {
    private Mat sourceMat = null;
    private RotatedRect rect = null;

    public Splitter(Mat src, RotatedRect rect) {
        this.sourceMat = src;
        this.rect = rect;
    }

    private Mat affine() {
        double r,angle;
        angle = rect.angle;
        r = rect.size.width / rect.size.height;
        if( r<1 ) {
            angle = 90 + angle;
        }

        Mat rotmat = Imgproc.getRotationMatrix2D(rect.center, angle, 1);
        Mat img_rotated = new Mat();
        Imgproc.warpAffine(sourceMat, img_rotated, rotmat, sourceMat.size(), Imgproc.INTER_CUBIC);

        Size rect_size = rect.size;
        if( r<1 ) {
            double t = rect.size.width;
            rect.size.width = rect.size.height;
            rect.size.height = t;
        }

        Mat img_crop = new Mat();
        Imgproc.getRectSubPix(img_rotated, rect_size, rect.center, img_crop);
        Mat resultResized = new Mat(20,300, CvType.CV_8UC1);
        Imgproc.resize(img_crop, resultResized, resultResized.size(),0,0,Imgproc.INTER_CUBIC);
        return resultResized;
    }

    public List<Mat> split() {
        List<Mat> res = new ArrayList<Mat>();
        Mat affineMat = affine();
        for(int i = 0 ; i < affineMat.height() ; i++) {
            String s = "";
            for(int j = 0 ; j < affineMat.width() ; j++ ) {
                if(affineMat.get(i,j)[0] > 230) {
                    s += "0";
                } else {
                    s+= "1";
                }
            }
            System.out.println(s);
        }
        int s = -1 , t = -1;


        //System.out.println(affineMat.width());
        for(int i = 0 ; i < affineMat.width(); i++ ) {
            boolean flag = false;
            for(int j = 0 ; j < affineMat.height() ; j++ ) {
                double tt = affineMat.get(j,i)[0];
                if( tt < 230 ) {
                    if(s == -1 ) s = i;
                    flag = true;
                    break;
                }
            }
            if(!flag && s != -1) {
                t = i;
                Mat img_crop = affineMat.submat(0,affineMat.height()-1,s,t-1);
                Mat resultResized = new Mat(20,16, CvType.CV_8UC1);
                Imgproc.resize(img_crop,resultResized,resultResized.size(),0,0,Imgproc.INTER_CUBIC);
                res.add(resultResized);
                s = -1;
                t = -1;
            }
        }
        if(t==-1 && s != -1) {
            Mat img_crop = affineMat.submat(0,affineMat.height()-1,s,affineMat.width());
            Mat resultResized = new Mat(20,16, CvType.CV_8UC1);
            Imgproc.resize(img_crop,resultResized,resultResized.size(),0,0,Imgproc.INTER_CUBIC);
            res.add(resultResized);
        }
        return res;
    }
}
