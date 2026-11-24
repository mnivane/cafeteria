import java.util.List;

public class Nivel10 {
    sealed interface Pago permits Efectivo, Tarjeta, Billetera, Bono { }

    record Efectivo(int recibido) implements Pago { }
    record Tarjeta(String franquicia, int cuotas) implements Pago { }
    record Billetera(String telefono) implements Pago { }
    record Bono(int valor) implements Pago { }

    static String describir(Pago pago, int total) {
        return switch (pago) {
            case Efectivo(int recibido) ->
                    "Efectivo · vuelto $" + (recibido - total);
            case Tarjeta(String franquicia, int cuotas) when cuotas > 1 ->
                    franquicia + " a " + cuotas + " cuotas";
            case Tarjeta(String franquicia, int cuotas) ->
                    franquicia + " de contado";
            case Billetera(String telefono) -> "Billetera " + telefono;
            case Bono(int valor) -> "Bono · saldo restante $" + (valor - total);
        };
    }

    record Calificacion(String cliente, int estrellas) {
        Calificacion {
            if (cliente == null || cliente.isBlank()) throw new IllegalArgumentException("Cliente vacío");
            if (estrellas < 1 || estrellas > 5) throw new IllegalArgumentException("Estrellas de 1 a 5");
        }

        boolean esBuena() { return estrellas >= 4; }
    }

    enum Categoria {
        BEBIDA, POSTRE, BOTELLA
    }

    record ItemMenu(String nombre, Categoria categoria, int precio) {
        ItemMenu {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
            if (categoria == null) throw new IllegalArgumentException("Categoría requerida");
            if (precio <= 0) throw new IllegalArgumentException("El precio debe ser positivo");
        }

        ItemMenu conPrecio(int nuevo) {
            return new ItemMenu(nombre, categoria, nuevo);
        }
    }

    record MenuDelDia(String fecha, List<ItemMenu> items) {
        MenuDelDia {
            if (fecha == null || fecha.isBlank()) throw new IllegalArgumentException("Fecha vacía");
            items = List.copyOf(items);
        }

        int promedioPrecio() {
            return (int) items.stream().mapToInt(ItemMenu::precio).average().orElse(0);
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        int total = 26000;
        List<Pago> pagos = List.of(
                new Efectivo(30000), new Tarjeta("Débito", 1),
                new Tarjeta("Crédito", 3), new Billetera("300 555 0110"),
                new Bono(40000));
        for (Pago pago : pagos) System.out.println(describir(pago, total));
    }

    private static void ejercicio06() {
        Calificacion calificacion = new Calificacion("Sofía", 5);
        Calificacion igual = new Calificacion("Sofía", 5);
        System.out.println(calificacion.cliente() + ": " + calificacion.estrellas());
        System.out.println("¿Buena? " + calificacion.esBuena());
        System.out.println("¿Iguales? " + calificacion.equals(igual));
        System.out.println(calificacion);
    }

    private static void ejercicio07() {
        MenuDelDia menu = new MenuDelDia("2026-11-23", List.of(
                new ItemMenu("Latte", Categoria.BEBIDA, 9500),
                new ItemMenu("Brownie", Categoria.POSTRE, 7000)));
        ItemMenu original = menu.items().getFirst();
        ItemMenu actualizado = original.conPrecio(original.precio() * 110 / 100);
        System.out.println("Promedio: $" + menu.promedioPrecio());
        System.out.println("Versión nueva: " + actualizado);
        System.out.println("El original no cambió: " + original.equals(menu.items().getFirst()));
        for (Object item : menu.items()) {
            if (item instanceof ItemMenu(var nombre, var categoria, var precio)
                    && categoria == Categoria.BEBIDA) {
                System.out.println(nombre + " · " + categoria + " · $" + precio);
            }
        }
        try {
            menu.items().add(new ItemMenu("Té", Categoria.BEBIDA, 4000));
        } catch (UnsupportedOperationException e) {
            System.out.println("La lista del menú es inmutable.");
        }
    }
}
