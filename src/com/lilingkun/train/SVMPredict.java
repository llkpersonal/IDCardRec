package com.lilingkun.train;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import org.opencv.core.Mat;

import java.io.IOException;

/**
 * Created by KunKun on 2017/10/5.
 */
public class SVMPredict {
    svm_model model = null;

    public SVMPredict(String modelPath) {
        try {
            model = svm.svm_load_model(modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int predict(Mat mat) {
        svm_node[] nodes = new svm_node[mat.height()*mat.width()];
        int idx = 1;
        for(int i = 0 ; i < mat.height(); i++  ){
            for(int j = 0 ; j < mat.width() ; j++ ) {
                int q = i*mat.width()+j;
                nodes[q] = new svm_node();
                nodes[q].index = idx++;
                if( mat.get(i,j)[0] > 230 ) {
                    nodes[q].value = 1;
                } else {
                    nodes[q].value = 0;
                }
            }
        }
        return (int)svm.svm_predict(model, nodes);
    }


}
