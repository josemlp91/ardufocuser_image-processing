
/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package ardufocuser.starfocusing;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author zerjillo josemlp
 */
public class Utils {

    /**
     * Calcula valores de luz maximos y minimos de la matriz imagen.
     */
    public static int[] computeMinMax(int[][] pixels) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                if (min > pixels[x][y]) {
                    min = pixels[x][y];
                }
                if (max < pixels[x][y]) {
                    max = pixels[x][y];
                }
            }
        }

        return new int[]{min, max};
    }
    
    /**
     * Calcula valores de luz maximos y minimos de la región de la matriz imagen  delimitada por startX - endX  y startY- endY ..
     */

    public static int[] computeMinMax(int[][] pixels, int startX, int startY, int endX, int endY) {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (min > pixels[x][y]) {
                    min = pixels[x][y];
                }
                if (max < pixels[x][y]) {
                    max = pixels[x][y];
                }
            }
        }

        return new int[]{min, max};
    }

    /**
     * Calcula media de luminocidad de la matriz imagen.
     */
    public static double computeMean(int[][] pixels) {
        double means;
        double sum = 0;
        int npix = pixels.length * pixels[0].length;

        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                sum += pixels[x][y];

            }
        }
        means = sum / npix;

        return means;
    }

    /**
     * Calcula la distancia euclidea de dos puntos, dado sus coordenadas.
     */
    public static double computeDistance(int p1x, int p1y, int p2x, int p2y) {

        int cat1 = Math.abs(p1x - p2x);
        int cat2 = Math.abs(p1y - p2y);
        double dis = Math.sqrt((Math.pow(cat1, 2) + Math.pow(cat2, 2)));
        return dis;

    }

    
    /*
       Dada una imagen realiza un ajuste a la ecuación gaussiana, indicando los paŕametros de tal ecuación.
       Return:
        private final double norm; Parametros normal de la ecuación gaussiana.
        private final double mean; Parametro media de la ecuación gaussiana.
        private final double sigma; Parametro sigma de la ecuación gaussiana.
    */
    public static double[] computeGaussianParams(int[][] image, int starCenterX, int starCenterY, int radius) {

        int min = computeMinMax(image, starCenterX - radius, starCenterY - radius, starCenterX + radius, starCenterY + radius)[0];

        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int y = starCenterY - radius; y <= starCenterY + radius; y++) {
            for (int x = starCenterX - radius; x <= starCenterX + radius; x++) {
                double d = computeDistance(starCenterX, starCenterY, x, y);
                obs.add(d, image[x][y] - min);
            }
        }

        double[] parameters = GaussianCurveFitter.create().withStartPoint(new double[]{image[starCenterX][starCenterY], 0.0, 1.0}).fit(obs.toList());
        return parameters;
    }
    
    // Primera versión del cálculo del parámetro FWHM. (Sin utilizar ajuste gausiano)
    public static double computeFWHM_v1(int[][] image) {

        int npix = image.length * image[0].length;
        int pixel_value;
        double fwhm_value = 0.0;
        double x2_sum = 0;

        double mean = computeMean(image);

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                pixel_value = image[i][j];
                x2_sum += pow((double) pixel_value - mean, 2);
            }
        }

        x2_sum /= (double) npix;
        fwhm_value = sqrt(x2_sum) * 2.3548;
        fwhm_value = sqrt(fwhm_value / Math.PI) * 2;

        return fwhm_value;
    }
    
     // Segunda versión del cálculo del parámetro FWHM. (Sin utilizar ajuste gausiano)
     // Siguiendo la ecuación::  https://es.wikipedia.org/wiki/Anchura_a_media_altura
    public static double computeFWHM(int[][] image, int starCenterX, int starCenterY, int radius ) {

        double[] gaussparam;
        double sigma;
        double fwhm;
        
        gaussparam=computeGaussianParams(image, starCenterX, starCenterY, radius);
        
        double gfactor=2.0*Math.sqrt(2*Math.log(2));
        sigma=gaussparam[2];
        fwhm=gfactor*sigma;
        
        return fwhm;
    }
    
}
