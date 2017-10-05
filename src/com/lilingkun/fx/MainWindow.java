package com.lilingkun.fx;

import com.lilingkun.core.Binarizer;
import com.lilingkun.core.PosDetector;
import com.lilingkun.core.Splitter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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

    private final static String picUrl = "/Users/KunKun/projects/idcards/id_card.jpg";

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
        for(Mat mat:lstMat) {
            for(int i = 0 ; i < mat.height(); i++ ) {
                String s = "";
                for(int j = 0 ; j < mat.width() ; j++ ) {
                    if(mat.get(i,j)[0] > 150) {
                        s += "1";
                    } else {
                        s += "0";
                    }
                }
                System.out.println(s);
            }
            System.out.println();
        }
        primaryStage.show();
    }
}
