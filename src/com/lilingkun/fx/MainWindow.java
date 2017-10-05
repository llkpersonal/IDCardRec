package com.lilingkun.fx;

import com.lilingkun.core.Binarizer;
import com.lilingkun.core.PosDetector;
import com.lilingkun.core.Splitter;
import com.lilingkun.train.SVMPredict;
import com.lilingkun.train.Trainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.List;

/**
 * Created by KunKun on 2017/10/5.
 */
public class MainWindow extends Application{

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 600;
    private final static String WINDOW_TITLE = "身份证号码识别";

    private final static String picUrl = "/Users/KunKun/projects/idcards/id_card11.jpg";

    private AnchorPane pane = null;
    private Scene scene = null;

    public MainWindow() {
        pane = new AnchorPane();
        scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(this.scene);

        Mat image = Imgcodecs.imread(picUrl, 0);


        Mat resultResized = new Mat(600*image.height()/image.width(),600, CvType.CV_8UC1);
        Imgproc.resize(image,resultResized,resultResized.size(),0,0,Imgproc.INTER_CUBIC);
        image = resultResized;

        Binarizer binarizer = new Binarizer(image);
        ImageView imageView = new ImageView();
        pane.getChildren().add(imageView);


        imageView.setX(0);
        imageView.setY(0);
        imageView.setImage(binarizer.toImage());
        PosDetector detector = new PosDetector(binarizer.getBinarizerMat());
        List<RotatedRect> listOfRotatedRect = detector.detect();
        for(int i = 0 ; i < listOfRotatedRect.size(); i++ ) {
            Point[] pts = new Point[4];
            listOfRotatedRect.get(i).points(pts);
            Line line = new Line(pts[0].x,pts[0].y, pts[1].x,pts[1].y);
            pane.getChildren().add(line);
            line = new Line(pts[1].x,pts[1].y, pts[2].x,pts[2].y);
            pane.getChildren().add(line);
            line = new Line(pts[2].x,pts[2].y, pts[3].x,pts[3].y);
            pane.getChildren().add(line);
            line = new Line(pts[3].x,pts[3].y, pts[0].x,pts[0].y);
            pane.getChildren().add(line);
        }
        Splitter splitter = new Splitter(binarizer.getBinarizerMat(), listOfRotatedRect.get(0));
        List<Mat> lstMat = splitter.split();
        SVMPredict predictor = new SVMPredict("/Users/KunKun/projects/IDCardRec/svm.model");
        String idNum = "";
        for(Mat mat:lstMat) {
            Trainer.toPicFile(mat);
            int x = predictor.predict(mat);
            //System.out.println(x);
            idNum += Integer.toString(x);
        }
        Text text = new Text();
        text.setX(550);
        text.setY(50);
        text.setText("身份证号："+idNum);
        pane.getChildren().add(text);
        primaryStage.show();

    }
}
