/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ardufocuser.starfocusing;

/**
 *
 * @author zerjillo josemlp
 */
public enum StarFilterStatus {
    OK,
    FILTERED_BY_MARGIN,
    FILTERED_BY_CONTRAST,
    FILTERED_BY_DISTANCE,
    FILTERED_BY_NUMBER_STAR_MAX_BRIGHNESS,
    FILTERED_BY_FWHM,
    FILTERED_BY_GAUSS
    
}
