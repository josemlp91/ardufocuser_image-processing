
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

import java.io.*;
import org.eso.fits.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FitsImage {

    // Nombre del archivo que almacena la imagen.
    private String filename;

    // Binario con el archivo abierto.
    private FitsFile fitsFile;

    // Matrix bidimensional con los pixeles de la imagen.
    private int[][] imageMatrix;

    // Número de pixeles totales.
    private int nPixels;

    // Numero de filas.
    private int width;

    // Numero de columnas.
    private int height;

    // Media de los valores de la imagen.
    private double mean;

    // Maximo de los valores de la imagen.
    private int max;

    // Minimo
    private int min;

    /**
     * Constructor, genera y iniclializa imagen dado la ruta del archivo.
     *
     * @param filename Ruta del archivo con la imagen en formato fits Flexible
     * Image Transport System. See {
     * @linktourl http://es.wikipedia.org/wiki/FITS}
     *
     */
    public FitsImage(String filename) {
        FitsFile file = null;
        this.filename = filename;

        try {
            // Abrimos archivo fits 
            file = new FitsFile(filename);
            this.fitsFile = file;

            // Get HDUnit in FitsFile by its position. 
            FitsHDUnit hdu = fitsFile.getHDUnit(0);

            // Cabeceras con metadatos de la imagen.
            FitsHeader hdr = hdu.getHeader();

            int type = hdr.getType();

            //     System.out.println(Fits.getType(type));
            FitsMatrix fitsmatrix = (FitsMatrix) hdu.getData();

            // Numero de dimensiones 
            int naxis[] = fitsmatrix.getNaxis();

            // Numero total de pixeles.
            this.nPixels = fitsmatrix.getNoValues();
            try {
                if (nPixels > 0) {

                    // Calculamos numero de filas y de columnas.
                    this.width = naxis[0];
                    this.height = nPixels / width;

                    //     System.out.println(" Npixel,width,height: " + nPixels + ", " + width + ", " + height);
                }
            } catch (SecurityException ex) {
                Logger.getLogger("FitsImage").log(Level.SEVERE, "Imagen no valida.", ex);

                System.out.println("Imagen no valida.");
            }

            // Iniciamos arrays.
            int[] imgArray = new int[this.nPixels];
            this.imageMatrix = new int[width][height];

            try {
                fitsmatrix.getIntValues(0, this.nPixels, imgArray);
            } catch (FitsException ex) {
                Logger.getLogger("FitsImage").log(Level.SEVERE, "No es posible acceder a los pixeles de la imagen.", ex);

            }

            min = Integer.MAX_VALUE;
            max = Integer.MIN_VALUE;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int val = imgArray[x + y * width];
                    imageMatrix[x][y] = val;

                    if (min > val) {
                        min = val;
                    }

                    if (max < val) {
                        max = val;
                    }

                    mean += val;
                }
            }

            // Calculo de media.
            this.mean = this.mean / (width * height);

        } catch (IOException ex) {
            Logger.getLogger("FitsImage").log(Level.SEVERE, null, ex);
        } catch (FitsException ex) {
            Logger.getLogger("FitsImage").log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Informa del normbre del archivo 
     * @return nombre archivo 
     */
    public String getFilename() {
        return filename;
    }


    /*
     * Acedemos matrix bidimensional  con pixels de la imagen.
     * @return array[][]
     */
    public int[][] getImageMatrix() {
        return imageMatrix;
    }

    /*
     * Numero de pixeles de la imagen.
     */
    public int getNPixels() {
        return nPixels;
    }

    /*
     * Numero de filas de la imagen.
     */
    public int getHeight() {
        return height;
    }

    /*
     * Numero de columnas de la imagen.
     */
    public int getWidth() {
        return width;
    }

    /*
     * Media
     */
    public double getMean() {
        return mean;
    }

    /*
     * Maximo valor
     */
    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    /*
     * Valor del pixel x, y
     */
    public int getValue(int x, int y) {
        return imageMatrix[x][y];
    }

    /*
     * Convierte matrix de pixeles en un buffered Image.
     */
    public BufferedImage asBufferedImage() {
        return asBufferedImage(1.0, 0, 65535);
    }

    public BufferedImage asBufferedImage(double gamma) {
        return asBufferedImage(gamma, 0, 65535);
    }

    public BufferedImage asBufferedImage(double gamma, int minimumHistogram, int maximumHistogram) {
        return asBufferedImage(gamma, minimumHistogram, maximumHistogram, false);
    }

    public BufferedImage asBufferedImage(double gamma, int minimumHistogram, int maximumHistogram, boolean inverted) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int pixel = imageMatrix[x][y];

                double aux = ((double) pixel - minimumHistogram) / ((double) (maximumHistogram - minimumHistogram));
                if (aux < 0) {
                    aux = 0;
                }
                if (aux > 1.0) {
                    aux = 1.0;
                }

                aux = Math.pow(aux, gamma);

                if (inverted) {
                    aux = 1.0 - aux;
                }

                int mod = (int) (aux * 255);
                bufferedImage.setRGB(x, y, 0xff000000 | ((mod & 0xff) << 16) | ((mod & 0xff) << 8) | (mod & 0xff));
            }
            //  System.out.println("");
        }

        return bufferedImage;
    }

    /*
     * Extrae un fragmento de la matrix de la imagnen dado la esquina superior izquierda y las dimensiones.
     * @param cornerX: coordenada x de la esquina superior izquierda
     * @param cornerY: coordenada y de la esquina superior derecha.
     */
    public int[][] getSubMatrix(int cornerX, int cornerY, int dim) {

        int[][] subMatrix = new int[dim][dim];

        for (int x = cornerX; x < cornerX + dim; x++) {
            for (int y = cornerY; y < cornerY + dim; y++) {
                if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
                    subMatrix[x - cornerX][y - cornerY] = 0;
                } else {
                    subMatrix[x - cornerX][y - cornerY] = imageMatrix[x][y];
                }
            }
        }

        return subMatrix;

    }
    /*
     * Extrae un fragmento de la matrix de la imagnen dado el centro.
     */

    public int[][] getSubMatrixCenter(int coorX, int coorY, int radius) {
        return getSubMatrix(coorX - radius, coorY - radius, radius * 2 + 1);
    }

    /*
     * Imprime por pantalla información interesante de la imagen
     * Metodo interesante para depurar.
     * Use: Debug.
     */
    public void verbose() {

        System.out.println("Max value: " + this.max);
        System.out.println("Mean value: " + this.mean);
        System.out.println("Width: " + this.width);
        System.out.println("Height: " + this.height);
        System.out.println("Number of Pixels: " + this.nPixels);

    }

    /**
     * Imprime por pantalla la representación de la matrix. Use : Debug.
     */
    public void printImageMatrix() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                System.out.print(this.imageMatrix[x][y] + "  ");
            }
            System.out.println();
        }

    }

    /**
     * Imprime por pantalla la representación de la matrix. Use : Debug.
     */
    public static void printImageMatrix(int[][] matrix, int dim) {

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {

                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }

    }

    /* Gets the coordinates of the maximum pixel areound a location */
    public int[] getMaximumCoordinatesAround(int centerX, int centerY, int radius) {
        int max = Integer.MIN_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                if ((x >= 0) && (y >= 0) && (x < width) && (y < height)) {
                    if (imageMatrix[x][y] > max) {
                        max = imageMatrix[x][y];
                        maxX = x;
                        maxY = y;
                    }
                }
            }
        }
        return new int[]{maxX, maxY};
    }

}
