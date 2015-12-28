/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package personnel;

import java.awt.Color;

/**
 *
 * @author Łukasz Spintzyk
 */
public class ColorIntTuple {
 private Integer value;
        private Color color=Color.GREEN;
        public ColorIntTuple(Integer i){
            value=i;
        }

        public Integer getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
        
        public String toString(){
            return value.toString();
        }
}
