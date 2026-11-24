public class Nivel02 {
    static class Producto {
        private String nombre;
        private int precio;
        private int stock;

        String getNombre() { return nombre; }
        int getPrecio() { return precio; }
        int getStock() { return stock; }

        void setNombre(String nombre) {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
            this.nombre = nombre;
        }

        void setPrecio(int precio) {
            if (precio < 0 || precio > 50000) {
                throw new IllegalArgumentException("Precio fuera de rango: " + precio);
            }
            this.precio = precio;
        }

        void reponer(int unidades) {
            if (unidades <= 0) throw new IllegalArgumentException("Las unidades deben ser positivas");
            stock += unidades;
        }

        void vender(int unidades) {
            if (unidades <= 0 || unidades > stock) {
                throw new IllegalArgumentException("Stock insuficiente o cantidad inválida");
            }
            stock -= unidades;
        }
    }

    static class TarjetaRegalo {
        private int saldo;

        int getSaldo() { return saldo; }

        void recargar(int valor) {
            if (valor <= 0) throw new IllegalArgumentException("Recarga inválida");
            saldo += valor;
        }

        boolean pagar(int valor) {
            if (valor <= 0) throw new IllegalArgumentException("El pago debe ser positivo");
            if (valor > saldo) return false;
            saldo -= valor;
            return true;
        }
    }

    static class BotePropinas {
        private int total;
        private int aportes;

        void aportar(int valor) {
            if (valor <= 0) throw new IllegalArgumentException("El aporte debe ser positivo");
            total += valor;
            aportes++;
        }

        int repartir(int personas) {
            validarPersonas(personas);
            return total / personas;
        }

        int sobrante(int personas) {
            validarPersonas(personas);
            return total % personas;
        }

        private void validarPersonas(int personas) {
            if (personas < 1) throw new IllegalArgumentException("Debe haber al menos una persona");
        }

        int getTotal() { return total; }
        int getAportes() { return aportes; }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        Producto latte = new Producto();
        latte.setNombre("Latte");
        latte.setPrecio(9500);
        latte.reponer(8);
        System.out.println(latte.getNombre() + " $" + latte.getPrecio());
        try {
            latte.setPrecio(-9500);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        try {
            latte.reponer(0);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Stock: " + latte.getStock());
    }

    private static void ejercicio06() {
        TarjetaRegalo tarjeta = new TarjetaRegalo();
        tarjeta.recargar(30000);
        System.out.println("¿Pagó latte? " + tarjeta.pagar(9500));
        System.out.println("¿Pagó torta? " + tarjeta.pagar(25000));
        System.out.println("Saldo: $" + tarjeta.getSaldo());
    }

    private static void ejercicio07() {
        BotePropinas bote = new BotePropinas();
        bote.aportar(2000);
        bote.aportar(5000);
        bote.aportar(3500);
        try {
            bote.aportar(-100);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Total: $" + bote.getTotal() + ", aportes: " + bote.getAportes());
        System.out.println("Por persona: $" + bote.repartir(3) + ", sobrante: $" + bote.sobrante(3));
    }
}
