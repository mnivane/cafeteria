import java.util.List;

public class Nivel05 {
    interface MedioDePago {
        String nombre();
        boolean pagar(int valor);
    }

    static class Efectivo implements MedioDePago {
        public String nombre() { return "Efectivo"; }
        public boolean pagar(int valor) { return valor > 0; }
    }

    static class Billetera implements MedioDePago {
        private int saldo;

        Billetera(int saldo) { this.saldo = saldo; }
        public String nombre() { return "Billetera digital"; }

        public boolean pagar(int valor) {
            if (valor <= 0 || valor > saldo) return false;
            saldo -= valor;
            return true;
        }
    }

    static class Tarjeta implements MedioDePago {
        private int saldo;

        Tarjeta(int saldo) { this.saldo = saldo; }
        public String nombre() { return "Tarjeta"; }

        public boolean pagar(int valor) {
            int totalConComision = valor + (valor * 2 + 99) / 100;
            if (valor <= 0 || totalConComision > saldo) return false;
            saldo -= totalConComision;
            return true;
        }
    }

    static String cobrar(MedioDePago medio, int valor) {
        if (valor <= 0) throw new IllegalArgumentException("El cobro debe ser positivo");
        String resultado = medio.pagar(valor) ? "aprobado" : "rechazado";
        return medio.nombre() + " $" + valor + ": " + resultado;
    }

    abstract static class Entrega {
        private final String pedidoId;

        Entrega(String pedidoId) {
            this.pedidoId = pedidoId;
        }

        String pedidoId() { return pedidoId; }
        abstract int costo();

        String resumen() {
            return "Pedido " + pedidoId + " · costo de entrega $" + costo();
        }
    }

    static class EnMesa extends Entrega {
        EnMesa(String pedidoId) { super(pedidoId); }
        int costo() { return 0; }
    }

    static class ParaLlevar extends Entrega {
        private final int empaques;

        ParaLlevar(String pedidoId, int empaques) {
            super(pedidoId);
            if (empaques < 0) throw new IllegalArgumentException("Empaques inválidos");
            this.empaques = empaques;
        }

        int costo() { return 500 * empaques; }
    }

    interface Rastreable {
        String estado();
        default boolean entregado() { return estado().equals("entregado"); }
    }

    static class Domicilio extends Entrega implements Rastreable, Notificable {
        private final int kilometros;
        private String estado = "en preparación";

        Domicilio(String pedidoId, int kilometros) {
            super(pedidoId);
            if (kilometros < 0) throw new IllegalArgumentException("Distancia inválida");
            this.kilometros = kilometros;
        }

        int costo() { return 2000 + 800 * kilometros; }
        public String estado() { return estado; }
        public String notificacion() { return "Pedido " + pedidoId() + ": " + estado; }
        void avanzar() { estado = "en camino"; }
        void marcarEntregado() { estado = "entregado"; }
    }

    interface Descontable {
        int precioBase();

        default int conDescuento(int porcentaje) {
            if (porcentaje < 0 || porcentaje > 100) {
                throw new IllegalArgumentException("Descuento de 0 a 100");
            }
            return precioBase() * (100 - porcentaje) / 100;
        }
    }

    abstract static class Snack implements Descontable {
        private final int precio;

        Snack(int precio) {
            if (precio <= 0) throw new IllegalArgumentException("Precio inválido");
            this.precio = precio;
        }

        public int precioBase() { return precio; }
        abstract String nombre();
    }

    static class Galleta extends Snack {
        Galleta() { super(3000); }
        String nombre() { return "Galleta de avena"; }
    }

    interface Notificable {
        String notificacion();
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        List<MedioDePago> medios = List.of(new Efectivo(), new Billetera(12000), new Tarjeta(20000));
        for (MedioDePago medio : medios) {
            System.out.println(cobrar(medio, 9500));
            System.out.println(cobrar(medio, 9500));
        }
    }

    private static void ejercicio06() {
        Snack snack = new Galleta();
        Descontable descontable = snack;
        System.out.println(snack.nombre() + ": $" + snack.precioBase());
        System.out.println("Con 20 %: $" + descontable.conDescuento(20));

        List<Entrega> entregas = List.of(
                new EnMesa("A-1"), new ParaLlevar("A-2", 2), new Domicilio("A-3", 3));
        Domicilio domicilio = (Domicilio) entregas.getLast();
        domicilio.avanzar();
        for (Entrega entrega : entregas) {
            System.out.println(entrega.resumen());
            if (entrega instanceof Rastreable rastreable) {
                System.out.println(rastreable.estado() + " · entregado: " + rastreable.entregado());
            }
        }
        domicilio.marcarEntregado();
        System.out.println("¿Entregado? " + domicilio.entregado());
    }

    private static void ejercicio07() {
        Domicilio domicilio = new Domicilio("A-3", 3);
        System.out.println(domicilio.notificacion());
    }
}
