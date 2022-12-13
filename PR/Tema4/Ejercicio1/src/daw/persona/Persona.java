package daw.persona;
import java.time.LocalDate;

public class Persona {
    public String nombre;
    public DNI dni;
    public double sueldo;
    public LocalDate fechaNacimiento;
    public CuentaCorriente cuenta;

    public Persona(String n, DNI d, double s, LocalDate fn, CuentaCorriente c) {
        this.nombre = n;
        this.dni = d;
        this.sueldo = s;
        this.fechaNacimiento = fn;
        this.cuenta = c;
    }

    public Persona(String n, int numDNI, char letraDNI, double s, LocalDate fn) {
        this(n, new DNI(numDNI, letraDNI), s, fn, new CuentaCorriente());
    }

    public Persona(String n, DNI d) {
        this(n, d, 900, LocalDate.now().minusYears(20), new CuentaCorriente());
    }

    public Persona(String n, int numDNI, char letraDNI) {
        this(n, new DNI(numDNI, letraDNI), 900, LocalDate.now().minusYears(20), new CuentaCorriente());
    }

    public void aumentarSueldo(int porcentaje) {
        this.sueldo += this.sueldo * porcentaje / 100;
    }

    public void cobrarSueldo() {
        this.cuenta.añadirDinero((int)this.sueldo);
    }
}