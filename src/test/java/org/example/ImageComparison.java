package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImageComparison {

    static {
        // Carga la librería nativa de OpenCV (.dll)
        System.load("C:\\opencv\\build\\java\\x64\\opencv_java481.dll"); // ⚠ Cambia esta ruta si es diferente
    }

    public static void main(String[] args) {
        String imgPath1 = "C:\\ruta\\a\\imagen1.png"; //  Cambia la ruta
        String imgPath2 = "C:\\ruta\\a\\imagen2.png"; //  Cambia la ruta

        Mat img1 = Imgcodecs.imread(imgPath1);
        Mat img2 = Imgcodecs.imread(imgPath2);

        if (img1.empty() || img2.empty()) {
            System.out.println("No se pudo cargar una o ambas imágenes.");
            return;
        }

        // Redimensionamos las imágenes al mismo tamaño si es necesario
        Imgproc.resize(img1, img1, new Size(500, 500));
        Imgproc.resize(img2, img2, new Size(500, 500));

        // Comparamos las imágenes con método de diferencia absoluta
        Mat diff = new Mat();
        Core.absdiff(img1, img2, diff);
        Mat gray = new Mat();
        Imgproc.cvtColor(diff, gray, Imgproc.COLOR_BGR2GRAY);
        int nonZeroPixels = Core.countNonZero(gray);

        if (nonZeroPixels == 0) {
            System.out.println("Las imagenes son iguales.");

        } else {
            System.out.println("Las imagenes son diferentes. Diferencias detectadas: " + nonZeroPixels);

        }
    }
}
