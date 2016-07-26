/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package focusersimulator;

import ardufocuser.starfocusing.FitsImage;
import static ardufocuser.starfocusing.Processing.detectStars;
import static ardufocuser.starfocusing.Processing.filterByContrast;
import static ardufocuser.starfocusing.Processing.filterStarByMargin;
import ardufocuser.starfocusing.Star;
import ardufocuser.starfocusing.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author josemlp
 */
public class FocuserSimulator {

    private String path;

    private ArrayList<ArrayList<String>> imagename_focus_levels;
    private ArrayList<Integer> focus_levels;
    private String general_name;

    private int min_focus_value;
    private int max_focus_value;
    private int size;

    private FitsImage current_image;
    private String current_image_name;
    private int current_focus_value;
    private int index_current_focus_value;

    private Boolean limitinware;
    private Boolean limitoutware;

    private ArrayList<Star> stars;
    private double last_fwhm;
    private double new_fwhm;
    private Boolean is_first;
    private FocuserDirection direction;

    /*
     *  Inicia el enfocador, seleccionando un foco aleatorio,  calculando los limites inferiores 
     *   y superiores.
     *
     */
    public FocuserSimulator(String images_path) {

        focus_levels = new ArrayList<Integer>();
        ArrayList<String> brute_image_names = new ArrayList<String>();
        Integer fimagefocus;
        String fimageid;

        this.path = images_path;

        File f = new File(this.path);
        String fname;

        if (f.exists()) {
            File[] ficheros = f.listFiles();
            for (int x = 0; x < ficheros.length; x++) {

                fname = ficheros[x].getName();
                brute_image_names.add(fname);
                String[] parts = fname.split("_");
                this.general_name = parts[0];
                fimagefocus = Integer.parseInt(parts[1]);

                if (focus_levels.indexOf(fimagefocus) == -1) {
                    focus_levels.add(fimagefocus);
                }

            }
            Collections.sort(focus_levels);

            this.min_focus_value = focus_levels.get(0);
            this.max_focus_value = focus_levels.get(focus_levels.size() - 1);

            System.out.println(focus_levels);
            this.size = focus_levels.size();
            imagename_focus_levels = new ArrayList<ArrayList<String>>(this.size);

            for (int i = 0; i < this.size; i++) {
                ArrayList aux = new ArrayList(20);
                imagename_focus_levels.add(aux);
            }

            for (int i = 0; i < brute_image_names.size(); i++) {

                String[] parts = brute_image_names.get(i).split("_");
                fimagefocus = Integer.parseInt(parts[1]);
                fimageid = parts[2];

                Integer id = focus_levels.indexOf(fimagefocus);
                imagename_focus_levels.get(id).add(this.general_name + "_" + fimagefocus + "_" + fimageid);

            }

            Random r = new Random();
            int randomfocus = (int) (Math.random() * this.size);
            index_current_focus_value = randomfocus;
            current_focus_value = focus_levels.get(randomfocus);
            current_image_name = imagename_focus_levels.get(randomfocus).get(0);
            current_image = new FitsImage(this.path + "/" + this.current_image_name);

            System.out.println(imagename_focus_levels);
            System.out.println(this.min_focus_value);
            System.out.println(this.max_focus_value);
            System.out.println(current_focus_value);
            System.out.println(current_image_name);

        } else {
            System.out.println("Directorio no existe");
        }

        is_first = true;

        this.limitoutware = false;
        this.limitinware = false;
        this.new_fwhm = 100;
        this.autofocus_process_image();
        direction = FocuserDirection.CLOCKWISE;
        last_fwhm = this.autofocus_process_image();
        

    }

    /*
     Avanza una posición el foco, capturando una imagen.
     */
    public void go_to_next_focus_point() {

        Random r = new Random();

        if (this.index_current_focus_value + 1 < this.size) {
            index_current_focus_value++;
            int s = imagename_focus_levels.get(index_current_focus_value).size();
            int rs = (int) (Math.random() * s);
            current_focus_value = focus_levels.get(index_current_focus_value);
            current_image_name = imagename_focus_levels.get(index_current_focus_value).get(rs);
            current_image = new FitsImage(this.path + "/" + this.current_image_name);
            System.out.println(current_image_name);
        } else {

            this.limitoutware = true;
        }

        new_fwhm = this.autofocus_process_image();
        this.direction = FocuserDirection.CLOCKWISE;

    }

    /*
     Retrocede  una posición el foco, capturando una imagen.
     */
    public void go_to_previous_focus_point() {

        Random r = new Random();

        if (index_current_focus_value > 0) {
            index_current_focus_value--;
            int s = imagename_focus_levels.get(index_current_focus_value).size();
            int rs = (int) (Math.random() * s);
            current_focus_value = focus_levels.get(index_current_focus_value);
            current_image_name = imagename_focus_levels.get(index_current_focus_value).get(rs);
            current_image = new FitsImage(this.path + "/" + this.current_image_name);
            System.out.println(current_image_name);
        } else {
            limitinware = true;
        }

        new_fwhm = this.autofocus_process_image();
        this.direction = FocuserDirection.CLOCKWISE;

    }

    public void stop_focus() {
        System.out.println("FOCO ENCONTRADO:  " + this.current_focus_value);
        return;

    }

    /*
     Mueve el enfocador directamente a un foco determinado, capturando una imagen.
     */
    public void go_to_select_focus(int focus_point) {

        int s = imagename_focus_levels.get(focus_point).size();
        int rs = (int) (Math.random() * s);
        current_focus_value = focus_levels.get(focus_point);
        current_image_name = imagename_focus_levels.get(focus_point).get(rs);
        current_image = new FitsImage(this.path + "/" + this.current_image_name);

    }

    public int get_current_focus_value() {
        return this.current_focus_value;
    }

    public String get_current_image_name() {
        return this.current_image_name;
    }

    public FitsImage get_fits_image() {
        return this.current_image;
    }

    public int get_size() {
        return size;
    }

    public int get_current_index() {
        return this.index_current_focus_value;
    }

    /**
     * Para probar la clase.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        FocuserSimulator focuser = new FocuserSimulator("../images/nucleo");
        /*
         focuser.go_to_next_focus_point();
         System.out.println(focuser.get_current_image_name());

         focuser.go_to_next_focus_point();
         System.out.println(focuser.get_current_image_name());

         focuser.go_to_previous_focus_point();
         System.out.println(focuser.get_current_image_name());

         focuser.go_to_previous_focus_point();
         System.out.println(focuser.get_current_image_name());
         */

        focuser.autofocus();

    }

    public double autofocus_process_image() {

        // Realizamos filtrado 
        ArrayList<Star> detectStars = detectStars(current_image, 10);
        filterStarByMargin(detectStars, 20, current_image.getWidth(), current_image.getHeight());
        filterByContrast(current_image, detectStars, 20, 1.9);

        double fwhm_mean = 0;
        double aux_fwhm;
        int[][] frame;

        for (int i = 0; i < detectStars.size(); i++) {
            Star s = detectStars.get(i);
            frame = current_image.getSubMatrixCenter(s.getxPos(), s.getyPos(), 20);
            //aux_fwhm = Utils.computeFWHM(frame);   ARREGLAR.
            //fwhm_mean += aux_fwhm;                       ARREGLAR.
        }

        double fwhm = fwhm_mean /= detectStars.size();
        return fwhm;

    }

    public void autofocus_logic(double last_fwhm) {

        Boolean focus = false;
        while (!focus) {
            System.out.println(last_fwhm);
            System.out.println(new_fwhm);

            if (this.limitoutware) {
                System.out.println("retrocede sss");
                this.go_to_previous_focus_point();
                this.limitoutware=false;

            }

            if (this.limitinware) {
                System.out.println("avanza sss");
                this.go_to_next_focus_point();
                this.limitinware=false;

            }
            /*
             if (is_first) {
             this.go_to_next_focus_point();
             is_first=false;
             System.out.println("avanza fist");
             }*/
            // Si la tendencia es positiva y vamos avanzando, entonces  avanzando.
            if ((new_fwhm >= last_fwhm) && (this.direction == FocuserDirection.CLOCKWISE) && !this.limitoutware) {
                System.out.println("avanza 1");
                this.go_to_next_focus_point();

                // Si la tendencia es negativa y vamos avanzando, entonces cambiamos a retroceder..
            } else if ((new_fwhm <= last_fwhm) && (this.direction == FocuserDirection.CLOCKWISE) && !this.limitinware) {
                System.out.println("retrocede");
                this.go_to_previous_focus_point();

                // Si la tendencia es negativa y vamos retrocediendo, pues cambiamos a avanzar.
            } else if ((new_fwhm <= last_fwhm) && (this.direction == FocuserDirection.COUNTERCLOCKWISE) && !this.limitoutware) {
                System.out.println("avanza 2");
                this.go_to_next_focus_point();

                // Si la tendencia es positiva   y vamos retrocediendo entonces paramos.
            } else if ((new_fwhm >= last_fwhm) && (this.direction == FocuserDirection.COUNTERCLOCKWISE)) {
                this.stop_focus();
                focus = true;

            }
        }
    }

    public void autofocus() {
        //
        autofocus_logic(last_fwhm);
        //         

    }

}
