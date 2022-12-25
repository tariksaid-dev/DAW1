package juegonavidad.ieshlanz.clases.matrix;
import bpc.daw.consola.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;
import java.awt.*;
public class PressAnyButton {
    private Sprite sprite;
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    public PressAnyButton(CapaSprites sprites, Image imagen) {
        this.sprite = sprites.crearSprite(
            imagen, 
            new Rectangle (0, 0, imagen.getWidth(null), imagen.getHeight(null)),
            this.width/3,
            this.height - 200);
    }

    public void borrar(CapaSprites sprites) {
        sprites.eliminarSprite(this.sprite);
    }
}
