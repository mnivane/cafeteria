import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Nivel08 {
    record Producto(String nombre, int precio) implements Comparable<Producto> {
        public int compareTo(Producto otro) { return Integer.compare(precio, otro.precio); }
    }

    static <T extends Comparable<? super T>> T minimo(List<T> elementos) {
        if (elementos.isEmpty()) throw new NoSuchElementException("La lista está vacía");
        T resultado = elementos.getFirst();
        for (T elemento : elementos) {
            if (elemento.compareTo(resultado) < 0) resultado = elemento;
        }
        return resultado;
    }

    static <T extends Comparable<? super T>> T maximo(List<T> elementos) {
        if (elementos.isEmpty()) throw new NoSuchElementException("La lista está vacía");
        T resultado = elementos.getFirst();
        for (T elemento : elementos) {
            if (elemento.compareTo(resultado) > 0) resultado = elemento;
        }
        return resultado;
    }

    static class Bandeja<T> {
        private final List<T> cosas = new ArrayList<>();
        private final int capacidad;

        Bandeja(int capacidad) {
            if (capacidad < 1) throw new IllegalArgumentException("Capacidad inválida");
            this.capacidad = capacidad;
        }

        boolean poner(T cosa) {
            if (cosas.size() == capacidad) return false;
            return cosas.add(cosa);
        }

        T primera() {
            if (cosas.isEmpty()) throw new NoSuchElementException("Bandeja vacía");
            return cosas.getFirst();
        }

        int cantidad() { return cosas.size(); }
    }

    static class Historial<T extends Comparable<? super T>> {
        private final List<T> elementos = new ArrayList<>();
        private final int capacidad;

        Historial(int capacidad) {
            if (capacidad < 1) throw new IllegalArgumentException("Capacidad inválida");
            this.capacidad = capacidad;
        }

        void agregar(T elemento) {
            if (elementos.size() == capacidad) elementos.removeFirst();
            elementos.add(elemento);
        }

        T masReciente() {
            if (elementos.isEmpty()) throw new NoSuchElementException("Historial vacío");
            return elementos.getLast();
        }

        List<T> todos() { return List.copyOf(elementos); }

        T mayor() {
            if (elementos.isEmpty()) throw new NoSuchElementException("Historial vacío");
            return maximo(elementos);
        }
    }

    static <T> int contar(List<T> lista, T buscado) {
        int cantidad = 0;
        for (T elemento : lista) if (java.util.Objects.equals(elemento, buscado)) cantidad++;
        return cantidad;
    }

    static int sumar(List<? extends Number> lista) {
        int suma = 0;
        for (Number numero : lista) suma += numero.intValue();
        return suma;
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        List<Integer> precios = List.of(9500, 7000, 12000);
        System.out.println("Mínimo: " + minimo(precios));
        System.out.println("Máximo: " + maximo(precios));
        List<Producto> productos = List.of(
                new Producto("Latte", 9500), new Producto("Brownie", 7000));
        System.out.println("Más económico: " + minimo(productos));
    }

    private static void ejercicio06() {
        Bandeja<String> bandeja = new Bandeja<>(2);
        bandeja.poner("latte");
        bandeja.poner("moka");
        System.out.println("¿Cabe un tinto? " + bandeja.poner("tinto"));
        System.out.println(bandeja.primera() + " · " + bandeja.cantidad());
    }

    private static void ejercicio07() {
        Historial<String> pedidos = new Historial<>(3);
        pedidos.agregar("Café");
        pedidos.agregar("Latte");
        pedidos.agregar("Té");
        pedidos.agregar("Moka");
        Historial<Integer> cobros = new Historial<>(3);
        cobros.agregar(9500);
        cobros.agregar(7000);
        cobros.agregar(12000);
        cobros.agregar(5000);
        System.out.println("Últimos pedidos: " + pedidos.todos());
        System.out.println("Más reciente: " + pedidos.masReciente());
        System.out.println("Menciones de Moka: " + contar(pedidos.todos(), "Moka"));
        System.out.println("Cobros: " + cobros.todos() + " · suma: $" + sumar(cobros.todos()));
        System.out.println("Mayor cobro: $" + cobros.mayor());
    }
}
