
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

import java.util.ArrayList;

/**
 *
 * @author zerjillo josemlp
 */
public class Processing {

    /**
     *  Calcula  conjunto de estrellas candidatas aplicando búsqueda de picos locales.
     * @param image Imagen.
     * @return
     */
    public static ArrayList<Star> detectStars(FitsImage image, int radius) {
        ArrayList<Star> stars = new ArrayList<>();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (isBrightnessPeak(image, x, y, radius)) {
                    stars.add(new Star(image, x, y));
                }
            }
        }

        return stars;
    }

    /**
     * Copmprueba que un pixel sobresale en luminocidad de su entorno.
     *
     * @param img Imagen.
     * @param x Coordenada x
     * @param y Coordenada y
     * @param radius Radio del entorno
     * @return
     */
    public static Boolean isBrightnessPeak(FitsImage img, int x, int y, int radius) {
        int p = img.getValue(x, y);

        for (int xx = x - radius; xx <= x + radius; xx++) {
            for (int yy = y - radius; yy <= y + radius; yy++) {
                if ((xx >= 0) && (yy >= 0) && (xx < img.getWidth()) && (yy < img.getHeight())) {
                    if ((xx != x) || (yy != y)) {
                        if (p <= img.getValue(xx, yy)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    //  Elimina estrellas proximas al límite de la imagen.
    public static void filterStarByMargin(ArrayList<Star> stars, int margin, int width, int height) {
        for (Star star : stars) {
            if ((star.getxPos() < margin) || (star.getyPos() < margin)) {
                star.setFilterStatus(StarFilterStatus.FILTERED_BY_MARGIN);
            }

            if ((star.getxPos() > width - margin) || (star.getyPos() > height - margin)) {
                star.setFilterStatus(StarFilterStatus.FILTERED_BY_MARGIN);
            }
        }
    }

    // Filtrado por contraste, comprueba que la zona de la estrella tiene un valor de contraste por encima de un umbral.
    public static void filterByContrast(FitsImage image, ArrayList<Star> stars, int radius, double minQuotient) {
        double quotient;

        for (int i = 0; i < stars.size(); i++) {
            Star s = stars.get(i);

            if (s.getFilterStatus() == StarFilterStatus.OK) {
                int[][] submatrix = image.getSubMatrixCenter(s.getxPos(), s.getyPos(), radius);

                double minimum = s.getLuminosity();

                for (int ii = 0; ii < radius; ii++) {
                    for (int jj = 0; jj < radius; jj++) {
                        if (submatrix[ii][jj] < minimum) {
                            minimum = submatrix[ii][jj];
                        }
                    }
                }

                quotient = (double) s.getLuminosity() / minimum;

                if (quotient < minQuotient) {
                    s.setFilterStatus(StarFilterStatus.FILTERED_BY_CONTRAST);
                }
            }
        }
    }
    
    // Busca estrella más luminosa dentro de un conjunto conjunto.
    public static int getStarWithMaxBrighness(FitsImage image, ArrayList<Star> stars, int maxival) {

        int max = Integer.MIN_VALUE;
        int maxid = Integer.MIN_VALUE;
        int lux;

        for (int i = 0; i < stars.size(); i++) {
            lux = stars.get(i).getLuminosity();
            if (lux > max && lux < maxival) {
                max = lux;
                maxid = i;
            }
        }
        return maxid;

    }

    // Reduce conjunto de estrellas a la ns estrellas más luminosas.
    public static void filterByMaximunBrighness(FitsImage image, ArrayList<Star> stars, int ns) {

        ArrayList<Integer> max_star_id = new ArrayList<>();
        int max = Integer.MAX_VALUE;
        int maxid;
        Star s;
        for (int i = 0; i < ns; i++) {
            maxid = getStarWithMaxBrighness(image, stars, max);
            max = stars.get(maxid).getLuminosity();
            max_star_id.add(maxid);

        }

        for (int sr = 0; sr < stars.size(); sr++) {
            if (max_star_id.indexOf(sr) == -1) {
                stars.get(sr).setFilterStatus(StarFilterStatus.FILTERED_BY_NUMBER_STAR_MAX_BRIGHNESS);
            }

        }

    }


    /*
     * Comprueba si la estrella almacenada en el indice i, esta a una distancia menor de mindis
     * de la estrella actual.
     */
    private static void stars_too_near(ArrayList<Star> stars, int idstar, double mindis) {

        Star s, s1;
        double dis;

        s = stars.get(idstar);
        if (s.getFilterStatus() == StarFilterStatus.OK) {

            for (int j = 0; (j < (stars.size() - 1)) && (idstar != j); j++) {
                s1 = stars.get(j);
                if (s1.getFilterStatus() == StarFilterStatus.OK) {
                    dis = Utils.computeDistance(s.getxPos(), s.getyPos(), s1.getxPos(), s1.getyPos());
                    if (dis < mindis) {
                        s.setFilterStatus(StarFilterStatus.FILTERED_BY_DISTANCE);
                        s1.setFilterStatus(StarFilterStatus.FILTERED_BY_DISTANCE);
                        //System.out.println(s); System.out.println(s1);
                    }
                }
            }
        }

    }

    public static void filterByMinDistance(FitsImage image, ArrayList<Star> stars, double mindis) {

        for (int i = 0; i < stars.size(); i++) {
            stars_too_near(stars, i, mindis);
        }

    }

    /* Reduce conjunto de entrellas a aquellas cuyo ecuación gaussiana se ajuste a los párametros dados.
     Ojo, metodo pesado, aplicar a un conjunto muy reducido. 
     */
    public static void filterByGaussianParam(FitsImage image, ArrayList<Star> stars, int radius, double sigma, double normal, double mean) {

        Star s;
        double[] parameters;
        for (int i = 0; i < stars.size(); i++) {
            s = stars.get(i);
            if (s.getFilterStatus() == StarFilterStatus.OK) {

                parameters = Utils.computeGaussianParams(image.getImageMatrix(), s.getxPos(), s.getyPos(), radius);
                System.out.println(parameters[0] + "--" + parameters[1] + "---" + parameters[2]);
                if (parameters[2] > sigma) {
                    s.setFilterStatus(StarFilterStatus.FILTERED_BY_GAUSS);
                }

            }
        }

    }
    
    // Filtra estrellas cuyos FWHM se encuentre en el umbral dado.
    public static void filterByFWHM(FitsImage image, ArrayList<Star> stars, int radius, double minfwhm) {

        Star s;
        int[][] subImage;
        double fwhmcalculate;
        for (int i = 0; i < stars.size(); i++) {

            s = stars.get(i);

            if (s.getFilterStatus() == StarFilterStatus.OK) {
                subImage = image.getSubMatrixCenter(s.getxPos(), s.getyPos(), radius);
                fwhmcalculate = Utils.computeFWHM_v1(subImage);
                //System.out.println(fwhmcalculate);

                if (fwhmcalculate < minfwhm) {
                    s.setFilterStatus(StarFilterStatus.FILTERED_BY_FWHM);
                }

            }
        }

    }

     // Main para pruebas.
    public static void main(String[] args) {

        FitsImage fimg;
        ArrayList<Star> stars = new ArrayList<>();
        fimg = new FitsImage("/home/josemlp/workspace/pruebasEnfoque/nucleo24880_042.fit");

        stars = detectStars(fimg, 10);
        filterByMaximunBrighness(fimg, stars, 10);
        filterByMinDistance(fimg, stars, 20);
        filterStarByMargin(stars, 20, fimg.getWidth(), fimg.getHeight());
        filterByContrast(fimg, stars, 10, 2);
        filterByFWHM(fimg, stars, 10, 10);
        //filterByGaussianParam(fimg, stars, 5, 10);

    }

}
