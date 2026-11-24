import java.util.ArrayList;
import java.util.List;

public class Nivel07 {
    interface Notificador {
        void enviar(String destino, String mensaje);
    }

    static class NotificadorMemoria implements Notificador {
        private final List<String> enviados = new ArrayList<>();

        public void enviar(String destino, String mensaje) {
            enviados.add(destino + " → " + mensaje);
        }

        List<String> enviados() { return List.copyOf(enviados); }
    }

    static class CorreoSimulado implements Notificador {
        public void enviar(String destino, String mensaje) {
            System.out.println("Correo a " + destino + ": " + mensaje);
        }
    }

    interface Descuento {
        int aplicar(int subtotal);
    }

    record Porcentaje(int valor) implements Descuento {
        Porcentaje {
            if (valor < 0 || valor > 100) throw new IllegalArgumentException("Descuento inválido");
        }

        public int aplicar(int subtotal) {
            return subtotal * valor / 100;
        }
    }

    static class Caja {
        private final Notificador notificador;

        Caja(Notificador notificador) { this.notificador = notificador; }

        int cobrar(List<Integer> precios, Descuento descuento, String telefono) {
            int subtotal = precios.stream().mapToInt(Integer::intValue).sum();
            int total = subtotal - descuento.aplicar(subtotal);
            notificador.enviar(telefono, "TICKET · total $" + total);
            return total;
        }
    }

    interface Impresora {
        void imprimir(String texto);
    }

    static class ImpresoraConsola implements Impresora {
        public void imprimir(String texto) { System.out.println("[impresora] " + texto); }
    }

    static class Cocina {
        private final Impresora impresora;

        Cocina(Impresora impresora) { this.impresora = impresora; }

        void enviarComanda(String plato, int mesa) {
            impresora.imprimir("Mesa " + mesa + ": " + plato);
        }
    }

    record Totales(int total, int ventas) {
        static Totales calcular(List<Integer> importes) {
            return new Totales(importes.stream().mapToInt(Integer::intValue).sum(), importes.size());
        }
    }

    interface Formato {
        String dar(int total, int ventas);
    }

    static class FormatoTexto implements Formato {
        public String dar(int total, int ventas) {
            return "Ventas: " + ventas + " · Total: $" + total;
        }
    }

    static class FormatoCsv implements Formato {
        public String dar(int total, int ventas) {
            return "ventas,total\n" + ventas + "," + total;
        }
    }

    static class FormatoJson implements Formato {
        public String dar(int total, int ventas) {
            return "{\"ventas\":" + ventas + ",\"total\":" + total + "}";
        }
    }

    interface Destino {
        void enviar(String texto);
    }

    static class DestinoConsola implements Destino {
        public void enviar(String texto) { System.out.println(texto); }
    }

    static class DestinoMemoria implements Destino {
        private String ultimo;
        public void enviar(String texto) { ultimo = texto; }
        String ultimo() { return ultimo; }
    }

    static class ReporteDiario {
        private final Formato formato;
        private final Destino destino;

        ReporteDiario(Formato formato, Destino destino) {
            this.formato = formato;
            this.destino = destino;
        }

        void emitir(List<Integer> importes) {
            Totales totales = Totales.calcular(importes);
            destino.enviar(formato.dar(totales.total(), totales.ventas()));
        }
    }

    static class ReporteDiarioOriginal {
        String generar(List<Integer> importes) {
            int total = 0;
            for (int importe : importes) total += importe;
            return "Ventas: " + importes.size() + " · Total: $" + total;
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        NotificadorMemoria memoria = new NotificadorMemoria();
        Caja caja = new Caja(memoria);
        caja.cobrar(List.of(9500), new Porcentaje(0), "300 555 0104");
        caja.cobrar(List.of(3500), new Porcentaje(50), "300 555 0105");
        System.out.println("Mensajes enviados: " + memoria.enviados().size());
        System.out.println("Último: " + memoria.enviados().getLast());

        CorreoSimulado correo = new CorreoSimulado();
        new Caja(correo).cobrar(List.of(9500), new Porcentaje(0), "cliente@cafe.test");
    }

    private static void ejercicio06() {
        Cocina cocina = new Cocina(new ImpresoraConsola());
        cocina.enviarComanda("Sándwich de pollo", 4);
    }

    private static void ejercicio07() {
        List<Integer> ventas = List.of(9500, 3500);
        DestinoMemoria memoria = new DestinoMemoria();
        ReporteDiario texto = new ReporteDiario(new FormatoTexto(), memoria);
        texto.emitir(ventas);
        String salidaOriginal = new ReporteDiarioOriginal().generar(ventas);
        System.out.println("Salida original preservada: " + salidaOriginal.equals(memoria.ultimo()));
        new ReporteDiario(new FormatoCsv(), new DestinoConsola()).emitir(ventas);
        new ReporteDiario(new FormatoJson(), new DestinoConsola()).emitir(ventas);
    }
}
