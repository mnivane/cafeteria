import java.util.ArrayList;
import java.util.List;

public class Nivel01 {
    static class Cliente {
        String nombre;
        int puntos;

        void sumarPuntos(int compra) {
            if (compra < 0) throw new IllegalArgumentException("La compra no puede ser negativa");
            puntos += compra / 1000;
        }

        boolean puedeCanjear() {
            return puntos >= 50;
        }

        void canjear() {
            if (puedeCanjear()) puntos -= 50;
        }

        String tarjeta() {
            return nombre + ": " + puntos + " puntos";
        }
    }

    static class Mesa {
        int numero;
        int personas;
        boolean ocupada;

        void ocupar(int cuantas) {
            if (cuantas < 1) throw new IllegalArgumentException("Debe haber al menos una persona");
            personas = cuantas;
            ocupada = true;
        }

        void liberar() {
            personas = 0;
            ocupada = false;
        }

        String estado() {
            return ocupada ? "Mesa " + numero + " ocupada por " + personas
                    : "Mesa " + numero + " libre";
        }
    }

    static class Barista {
        String nombre;
        String turno;
        int bebidas;
        int propinas;

        Barista(String nombre, String turno) {
            this.nombre = nombre;
            this.turno = turno;
        }

        void preparar(String bebida) {
            System.out.println(nombre + " prepara un " + bebida);
            bebidas++;
        }

        String resumen() {
            return nombre + " (" + turno + "): " + bebidas + " bebidas";
        }

        boolean merecePausa() {
            return bebidas >= 20;
        }

        void recibirPropina(int valor) {
            if (valor > 0) propinas += valor;
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        Cliente sofia = new Cliente();
        sofia.nombre = "Sofía";
        sofia.puntos = 41;
        sofia.sumarPuntos(12500);
        System.out.println(sofia.tarjeta());
        System.out.println("¿Café gratis? " + sofia.puedeCanjear());
        sofia.canjear();
        System.out.println("Después del canje: " + sofia.tarjeta());

        Cliente segundo = new Cliente();
        segundo.nombre = "Luis";
        segundo.sumarPuntos(9999);
        System.out.println(segundo.tarjeta());
    }

    private static void ejercicio06() {
        Mesa mesa = new Mesa();
        mesa.numero = 4;
        mesa.ocupar(3);
        System.out.println(mesa.estado());
        mesa.liberar();
        System.out.println(mesa.estado());
    }

    private static void ejercicio07() {
        List<Barista> baristas = new ArrayList<>();
        Barista martin = new Barista("Martín", "mañana");
        Barista ana = new Barista("Ana", "tarde");
        for (int i = 0; i < 3; i++) martin.preparar("Latte");
        for (int i = 0; i < 21; i++) ana.preparar("Moka");
        baristas.add(martin);
        baristas.add(ana);
        Barista limite = new Barista("Luz", "noche");
        for (int i = 0; i < 20; i++) limite.preparar("Té");
        baristas.add(limite);

        for (Barista barista : baristas) {
            System.out.println(barista.resumen());
            System.out.println("¿Merece pausa? " + barista.merecePausa());
        }
        martin.recibirPropina(2000);
        martin.recibirPropina(-500);
        System.out.println("Propinas de Martín: $" + martin.propinas);
    }
}
