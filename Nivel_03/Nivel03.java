public class Nivel03 {
    static class Reserva {
        private final String cliente;
        private final int personas;
        private final String hora;

        Reserva(String cliente, int personas, String hora) {
            if (cliente == null || cliente.isBlank()) throw new IllegalArgumentException("Cliente vacío");
            if (personas < 1 || personas > 8) throw new IllegalArgumentException("Mesas de 1 a 8 personas");
            if (hora == null || hora.isBlank()) throw new IllegalArgumentException("Hora vacía");
            this.cliente = cliente;
            this.personas = personas;
            this.hora = hora;
        }

        Reserva(String cliente, int personas) {
            this(cliente, personas, "19:00");
        }

        Reserva(String cliente) {
            this(cliente, 2, "19:00");
        }

        String aviso() {
            return cliente + " · " + personas + " pers. · " + hora;
        }

        String aviso(int minutosAntes) {
            return "En " + minutosAntes + " min: " + aviso();
        }
    }

    static class Postre {
        private final String nombre;
        private final int precio;
        private final boolean sinAzucar;

        Postre(String nombre, int precio, boolean sinAzucar) {
            if (nombre == null || nombre.isBlank() || precio <= 0) {
                throw new IllegalArgumentException("Postre o precio inválido");
            }
            this.nombre = nombre;
            this.precio = precio;
            this.sinAzucar = sinAzucar;
        }

        Postre(String nombre, int precio) {
            this(nombre, precio, false);
        }

        String etiqueta() {
            return nombre + " $" + precio;
        }

        String etiqueta(boolean detallada) {
            if (detallada && sinAzucar) return etiqueta() + " · sin azúcar";
            return etiqueta();
        }
    }

    static class Combo {
        private final String bebida;
        private final String postre;
        private final int precio;

        Combo(String bebida, String postre, int precio) {
            if (precio <= 0) throw new IllegalArgumentException("El precio debe ser positivo");
            if (bebida == null || bebida.isBlank()) throw new IllegalArgumentException("Bebida vacía");
            this.bebida = bebida;
            this.postre = postre;
            this.precio = precio;
        }

        Combo(String bebida, int precio) {
            if (bebida == null || bebida.isBlank()) throw new IllegalArgumentException("Bebida vacía");
            this(bebida.strip(), "sin postre", precio);
        }

        int precioFinal() {
            return precio;
        }

        int precioFinal(int propinaPct) {
            if (propinaPct < 0) throw new IllegalArgumentException("Propina inválida");
            return precioFinal() + precio * propinaPct / 100;
        }

        @Override
        public String toString() {
            return bebida + " + " + postre + ": $" + precio;
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        Reserva completa = new Reserva("Familia Rojas", 6, "18:30");
        Reserva corta = new Reserva("Andrés", 2);
        Reserva predeterminada = new Reserva("Camila");
        System.out.println(completa.aviso());
        System.out.println(corta.aviso(15));
        System.out.println(predeterminada.aviso());
        try {
            new Reserva("Fiesta", 12);
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo: " + e.getMessage());
        }
    }

    private static void ejercicio06() {
        Postre brownie = new Postre("Brownie", 7000);
        Postre cheesecake = new Postre("Cheesecake", 9000, true);
        System.out.println(brownie.etiqueta(true));
        System.out.println(cheesecake.etiqueta(true));
    }

    private static void ejercicio07() {
        Combo simple = new Combo("Latte", 9500);
        Combo completo = new Combo("Moka", "Brownie", 15000);
        System.out.println(simple);
        System.out.println(completo + " · con propina: $" + completo.precioFinal(10));
        try {
            new Combo("  ", 5000);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
