import java.util.ArrayList;
import java.util.List;

public class Nivel04 {
    static class Producto {
        private final String nombre;
        private final int precio;

        Producto(String nombre, int precio) {
            if (nombre == null || nombre.isBlank() || precio <= 0) {
                throw new IllegalArgumentException("Producto inválido");
            }
            this.nombre = nombre;
            this.precio = precio;
        }

        String getNombre() { return nombre; }
        int getPrecio() { return precio; }
        String descripcion() { return nombre + " $" + precio; }
    }

    static class Bebida extends Producto {
        private final int mililitros;

        Bebida(String nombre, int precio, int mililitros) {
            super(nombre, precio);
            if (mililitros <= 0) throw new IllegalArgumentException("Tamaño inválido");
            this.mililitros = mililitros;
        }
    }

    static class Postre extends Producto {
        private final boolean vegano;

        Postre(String nombre, int precio, boolean vegano) {
            super(nombre, precio);
            this.vegano = vegano;
        }

        boolean esVegano() { return vegano; }
    }

    static class Temporada extends Producto {
        private final String mes;

        Temporada(String nombre, int precio, String mes) {
            super(nombre, precio);
            this.mes = mes;
        }

        @Override
        String descripcion() {
            return super.descripcion() + " (solo " + mes + ")";
        }
    }

    static class LineaPedido {
        private final Producto producto;
        private final int cantidad;

        LineaPedido(Producto producto, int cantidad) {
            if (producto == null || cantidad <= 0) throw new IllegalArgumentException("Línea inválida");
            this.producto = producto;
            this.cantidad = cantidad;
        }

        int subtotal() { return producto.getPrecio() * cantidad; }
        int cantidad() { return cantidad; }

        String texto() {
            return cantidad + " × " + producto.getNombre() + " = $" + subtotal();
        }
    }

    static class Pedido {
        private final int mesa;
        private final List<LineaPedido> lineas = new ArrayList<>();

        Pedido(int mesa) {
            if (mesa < 1) throw new IllegalArgumentException("Mesa inválida");
            this.mesa = mesa;
        }

        void agregar(Producto producto, int cantidad) {
            lineas.add(new LineaPedido(producto, cantidad));
        }

        int cantidadDeItems() {
            return lineas.stream().mapToInt(LineaPedido::cantidad).sum();
        }

        int total() {
            return lineas.stream().mapToInt(LineaPedido::subtotal).sum();
        }

        void imprimir() {
            System.out.println("Mesa " + mesa);
            for (LineaPedido linea : lineas) System.out.println("  " + linea.texto());
            System.out.println("Total: $" + total() + " · artículos: " + cantidadDeItems());
        }
    }

    static class Combo {
        private final Producto principal;
        private final Producto acompanante;

        Combo(Producto principal, Producto acompanante) {
            this.principal = principal;
            this.acompanante = acompanante;
        }

        int precio() {
            return (principal.getPrecio() + acompanante.getPrecio()) * 90 / 100;
        }
    }

    static class PostreConEtiqueta {
        private final String nombre;
        private final int precio;
        private final boolean sinAzucar;

        PostreConEtiqueta(String nombre, int precio, boolean sinAzucar) {
            if (nombre == null || nombre.isBlank() || precio <= 0) {
                throw new IllegalArgumentException("Postre inválido");
            }
            this.nombre = nombre;
            this.precio = precio;
            this.sinAzucar = sinAzucar;
        }

        PostreConEtiqueta(String nombre, int precio) {
            this(nombre, precio, false);
        }

        String etiqueta() { return nombre + " $" + precio; }

        String etiqueta(boolean detallada) {
            return detallada && sinAzucar ? etiqueta() + " · sin azúcar" : etiqueta();
        }
    }

    static class Empleado {
        private final String nombre;
        private final int salarioBase;

        Empleado(String nombre, int salarioBase) {
            this.nombre = nombre;
            this.salarioBase = salarioBase;
        }

        int pago() { return salarioBase; }
        String nombre() { return nombre; }

        @Override
        public String toString() { return nombre + ": $" + pago(); }
    }

    static class Barista extends Empleado {
        private final int bebidas;

        Barista(String nombre, int salarioBase, int bebidas) {
            super(nombre, salarioBase);
            this.bebidas = bebidas;
        }

        @Override
        int pago() { return super.pago() + 200 * bebidas; }
    }

    static class Mesero extends Empleado {
        private final int propinas;

        Mesero(String nombre, int salarioBase, int propinas) {
            super(nombre, salarioBase);
            this.propinas = propinas;
        }

        @Override
        int pago() { return super.pago() + propinas; }
    }

    static class Turno {
        private final List<Empleado> empleados = new ArrayList<>();

        void agregar(Empleado empleado) { empleados.add(empleado); }
        int totalNomina() { return empleados.stream().mapToInt(Empleado::pago).sum(); }

        void imprimir() {
            empleados.forEach(System.out::println);
            System.out.println("Nómina total: $" + totalNomina());
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        Pedido pedido = new Pedido(4);
        pedido.agregar(new Bebida("Latte", 9500, 350), 2);
        pedido.agregar(new Postre("Brownie", 7000, false), 1);
        pedido.agregar(new Postre("Tarta vegana", 8000, true), 1);
        pedido.imprimir();

        Producto ponque = new Temporada("Ponqué de calabaza", 8500, "octubre");
        Combo combo = new Combo(new Bebida("Latte", 9500, 350), ponque);
        System.out.println(ponque.descripcion());
        System.out.println("Combo: $" + combo.precio());
    }

    private static void ejercicio06() {
        Producto cafe = new Bebida("Latte", 9500, 350);
        Producto postre = new Postre("Brownie", 7000, false);
        System.out.println(cafe.descripcion() + " + " + postre.descripcion());
        PostreConEtiqueta brownie = new PostreConEtiqueta("Brownie", 7000);
        PostreConEtiqueta cheesecake = new PostreConEtiqueta("Cheesecake", 9000, true);
        System.out.println(brownie.etiqueta(true));
        System.out.println(cheesecake.etiqueta(true));
    }

    private static void ejercicio07() {
        Turno turno = new Turno();
        turno.agregar(new Barista("Martín", 100000, 20));
        turno.agregar(new Mesero("Ana", 90000, 15000));
        turno.imprimir();
    }
}
