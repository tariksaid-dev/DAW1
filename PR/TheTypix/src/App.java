import juegonavidad.ieshlanz.clases.matrix.*;
import juegonavidad.ieshlanz.clases.juego.*;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.*;
import bpc.daw.consola.*;
import bpc.daw.reproductor.*;

public class App {
    public static void main(String[] args) {
        Consola c = new Consola(
                "Typix",
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);

        Teclado t = c.getTeclado();
        CapaTexto ct = c.getCapaTexto();
        CapaCanvas canvas = c.getCapaCanvas();
        CapaSprites sprites = c.getCapaSprites();
        Graphics g = canvas.getGraphics();
        Rain r = new Rain();
        Title the = null;
        Title matrix = null;
        PressAnyButton pab = null;
        String jugador = null;
        int dificultad = 1;
        boolean juego = true;
        ArchivoMP3 musica = new ArchivoMP3("musica/clubbedToDeath.mp3");
        Reproductor rep = new Reproductor(musica, false, false);

        while (juego) {
            rep.play();

            // ALGORITMO MATRIX
            Thread hilo = new Thread() {
                public void run() {
                    try {
                        r.dibujar(g);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            hilo.start();

            // TITULOS
            try {
                Image foto = ImageIO.read(new File("img/title.png"));
                the = new Title(sprites, foto, 0, ct);
                matrix = new Title(sprites, foto, 1, ct);
                while (the.getX() < Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 100
                        || matrix.getX() > Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 300) {
                    the.actuarThe();
                    matrix.actuarMatrix();
                    Thread.sleep(15);
                }
                Image foto2 = ImageIO.read(new File("img/paraContinuar.png"));
                pab = new PressAnyButton(sprites, foto2.getScaledInstance(700, 100, Image.SCALE_FAST));

                boolean seguir = false;
                if (t.leerCaracter() == (KeyEvent.VK_ENTER)) {
                    Thread.sleep(150);
                    c.esperarSiguienteFrame();
                    seguir = true;
                    System.out.println("traza2");
                    pab.borrar(sprites);
                    the.borrar(sprites);
                    matrix.borrar(sprites);
                }
                ;

                // MENU PRINCIPAL
                Image foto3 = ImageIO.read(new File("img/mainMenu.png"));
                Image foto4 = ImageIO.read(new File("img/selector.png"));
                MainMenu mm = new MainMenu(sprites, foto3);
                Selector s = new Selector(sprites, foto4);
                while (seguir) {
                    mm.actuar(t, sprites, s);

                    c.esperarSiguienteFrame();

                    if (mm.getState() == false) {
                        mm.borrar(sprites);
                        s.borrar(sprites);
                        seguir = false;
                    }
                }
                Thread.sleep(200);

                if (mm.getOption() == 0) {
                    // Nueva Partida
                    Image foto5 = ImageIO.read(new File("img/introduceNombre.png"));
                    NuevoJuego nj = new NuevoJuego(sprites, foto5);

                    Image foto6 = ImageIO.read(new File("img/letrasGreen.png"));
                    Letras l = new Letras(sprites, foto6);

                    while (l.isDone() == false) {
                        c.esperarSiguienteFrame();
                        l.actuar(t, foto6, sprites);
                    }
                    if (l.isDone() == true) {
                        l.borrar(sprites);
                        nj.borrar(sprites);
                        l.borrarTodasLasLetras(sprites);
                    }
                    Guardar.guardar(l.getNombre(), 1);
                    g.setColor(Color.green);
                    jugador = l.getNombre();
                    dificultad = 1;
                    // hilo.stop();

                } else if (mm.getOption() == 1) {
                    // Cargar Partida
                    Guardar.leer();
                    hilo.stop();
                    g.clearRect(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                    g.setColor(Color.green);
                    Selector s3 = new Selector(sprites, foto4);
                    Image foto8 = ImageIO.read(new File("img/loadScreen.png"));
                    MenuCargar mc = new MenuCargar(sprites, foto8);
                    mc.generarPantallaCarga(g, s3, sprites);
                    while (!mc.isDone()) {
                        // si le damos a enter en nueva partida cuando no hay partidas creadas, nos saca
                        // del juego (mirar func
                        // volver())
                        Thread.sleep(130);
                        c.esperarSiguienteFrame();
                        mc.actuar(t, s3, sprites);

                        // Aquí se supone que nos hemos llevado el nombre y la dificultad de la partida
                        // a las variables arriba declaradas.
                    }
                    g.clearRect(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                    Thread.sleep(150);

                    // despues de seleccionar la partida, elegimos el nivel que queremos jugar

                    Selector s2 = new Selector(sprites, foto4);
                    ElegirNivel ed = new ElegirNivel(sprites, s2);
                    while (ed.isDone() == false) {
                        // Thread.sleep(130);
                        c.esperarSiguienteFrame();
                        ed.actuar(t, sprites, s2);
                    }
                    if (ed.isDone() == true) {
                        ed.borrar(sprites);
                        s2.borrar(sprites);
                    }

                    jugador = Guardar.nombre.get(MenuCargar.getOption());
                    dificultad = Guardar.dificultad.get(MenuCargar.getOption()).intValue();

                }
                hilo.stop();
                System.out.println("hilo stop post menu principal");
                g.clearRect(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                        (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ElegirNivel.getOption() == 1 || dificultad == 1) {
                try {
                    // NIVEL 1
                    Cinematica1.primeraPantalla(g, jugador);
                    Cinematica1.segundaPantalla(g, jugador, sprites);

                    NivelBase nb = new NivelBase();
                    System.out.println(Thread.activeCount() + "hilos activos, línea 178");
                    nb.añadirSpritesMapa(sprites);
                    nb.contador(g, nb, t);
                    nb.FondoVidas(g);
                    nb.cazaLetras(t, sprites);
                    while (nb.getVidas() > 0 && !nb.isAcabado()) {
                        if (nb.getVidas() == 2) {
                            nb.FondoVidas(g);
                        } else if (nb.getVidas() == 1) {
                            nb.FondoVidas(g);
                        }
                        // System.out.println(Thread.activeCount());
                        nb.actuar(sprites, t);
                        c.esperarSiguienteFrame();
                    }
                    if (nb.getVidas() > 0) {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasGanado.png")), 500, 400, 30);
                        // sube nivel desbloqueado a nivel2 y autosave
                        if (dificultad == 1) {
                            dificultad++;
                            Guardar.guardar(jugador, dificultad);
                        } else {
                            ElegirNivel.option = 2;
                        }
                    } else {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasPerdido.png")), 500,
                                400, 30);
                    }
                    Thread.sleep(200);
                    nb.clearScreen(g, sprites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // NIVEL2
            if (ElegirNivel.getOption() == 2 || dificultad == 2) {
                try {
                    Cinematica2.primeraPantalla(g, jugador, sprites);

                    Nivel2 nb = new Nivel2();
                    nb.añadirSpritesMapa(sprites);
                    nb.contador(g, nb, t);
                    nb.FondoVidas(g);
                    nb.cazaLetras(t, sprites);
                    while (nb.getVidas() > 0 && !nb.isAcabado()) {
                        if (nb.getVidas() == 2) {
                            nb.FondoVidas(g);
                        } else if (nb.getVidas() == 1) {
                            nb.FondoVidas(g);
                        }
                        nb.actuar(sprites, t);
                        c.esperarSiguienteFrame();
                    }
                    if (nb.getVidas() > 0) {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasGanado.png")), 500, 400, 30);
                        // sube nivel desbloqueado a nivel3 y autosave
                        if (dificultad == 2) {
                            dificultad++;
                            Guardar.guardar(jugador, dificultad);
                        } else {
                            ElegirNivel.option = 3;
                        }
                    } else {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasPerdido.png")), 500,
                                400, 30);
                    }
                    Thread.sleep(200);
                    nb.clearScreen(g, sprites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // NIVEL3
            if (ElegirNivel.getOption() == 3 || dificultad == 3) {
                try {
                    Cinematica3.primeraPantalla(g, jugador, sprites);

                    Nivel3 nb = new Nivel3();
                    nb.añadirSpritesMapa(sprites);
                    nb.contador(g, nb, t);
                    nb.FondoVidas(g);
                    nb.cazaLetras(t, sprites);
                    while (nb.getVidas() > 0 && !nb.isAcabado()) {
                        if (nb.getVidas() == 2) {
                            nb.FondoVidas(g);
                        } else if (nb.getVidas() == 1) {
                            nb.FondoVidas(g);
                        }
                        nb.actuar(sprites, t);
                        c.esperarSiguienteFrame();
                    }
                    if (nb.getVidas() > 0) {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasGanado.png")), 500, 400, 30);
                        // sube nivel desbloqueado a nivel4 y autosave
                        if (dificultad == 3) {
                            dificultad++;
                            Guardar.guardar(jugador, dificultad);
                        } else {
                            ElegirNivel.option = 4;
                        }
                    } else {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasPerdido.png")), 500,
                                400, 30);
                    }
                    Thread.sleep(200);
                    nb.clearScreen(g, sprites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ElegirNivel.getOption() == 4 || dificultad == 4) {
                try {
                    // NIVEL4
                    Cinematica4.primeraPantalla(g, jugador, sprites);

                    Nivel4 nb = new Nivel4();
                    nb.añadirSpritesMapa(sprites);
                    nb.contador(g, nb, t);
                    nb.FondoVidas(g);
                    nb.cazaLetras(t, sprites);
                    while (nb.getVidas() > 0 && !nb.isAcabado()) {
                        if (nb.getVidas() == 2) {
                            nb.FondoVidas(g);
                        } else if (nb.getVidas() == 1) {
                            nb.FondoVidas(g);
                        }
                        nb.actuar(sprites, t);
                        c.esperarSiguienteFrame();
                    }
                    if (nb.getVidas() > 0) {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasGanado.png")), 500, 400, 30);
                        // sube nivel desbloqueado a nivel5 y autosave
                        if (dificultad == 4) {
                            dificultad++;
                            Guardar.guardar(jugador, dificultad);
                        } else {
                            ElegirNivel.option = 5;
                        }
                    } else {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasPerdido.png")), 500,
                                400, 30);
                    }
                    Thread.sleep(200);
                    nb.clearScreen(g, sprites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ElegirNivel.getOption() == 5 || dificultad == 5) {
                try {
                    // NIVEL5
                    Cinematica5.primeraPantalla(g, jugador, sprites);

                    Nivel5 nb = new Nivel5();
                    nb.añadirSpritesMapa(sprites);
                    nb.contador(g, nb, t);
                    nb.FondoVidas(g);
                    nb.cazaLetras(t, sprites);
                    while (nb.getVidas() > 0 && !nb.isAcabado()) {
                        if (nb.getVidas() == 2) {
                            nb.FondoVidas(g);
                        } else if (nb.getVidas() == 1) {
                            nb.FondoVidas(g);
                        }
                        nb.actuar(sprites, t);
                        c.esperarSiguienteFrame();
                    }
                    if (nb.getVidas() > 0) {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasGanado.png")), 500, 400, 30);
                        CinematicaFinal.primeraPantalla(g, jugador, sprites);
                        CinematicaFinal.segundaPantalla(g, jugador, sprites);
                    } else {
                        FadeIn.efectoFadein(g, ImageIO.read(new File("img/hasPerdido.png")), 500,
                                400, 30);
                    }
                    Thread.sleep(200);
                    nb.clearScreen(g, sprites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                FadeIn.efectoFadein(g, ImageIO.read(new File("img/gameOver.png")), 0, 0, 30);
                Thread.sleep(5000);
                g.clearRect(0, 0, 1920, 1080);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}