import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Nivel09 {
    record Insumo(int id, String nombre, int cantidad) {
        Insumo {
            if (id < 1 || nombre == null || nombre.isBlank() || cantidad < 0) {
                throw new IllegalArgumentException("Insumo inválido");
            }
        }
    }

    static class Inventario {
        private final List<Insumo> insumos = new ArrayList<>();

        void crear(Insumo insumo) {
            if (buscar(insumo.id()).isPresent()) throw new IllegalArgumentException("Id duplicado");
            insumos.add(insumo);
        }

        List<Insumo> listar() { return List.copyOf(insumos); }

        Optional<Insumo> buscar(int id) {
            return insumos.stream().filter(insumo -> insumo.id() == id).findFirst();
        }

        boolean actualizar(int id, String nombre, int cantidad) {
            int posicion = indice(id);
            if (posicion < 0) return false;
            insumos.set(posicion, new Insumo(id, nombre, cantidad));
            return true;
        }

        boolean eliminar(int id) {
            return insumos.removeIf(insumo -> insumo.id() == id);
        }

        private int indice(int id) {
            for (int i = 0; i < insumos.size(); i++) {
                if (insumos.get(i).id() == id) return i;
            }
            return -1;
        }
    }

    static class ListaEspera {
        private final List<String> nombres = new ArrayList<>();

        void agregar(String nombre) { nombres.add(nombre); }
        int cantidad() { return nombres.size(); }
        Optional<String> primero() { return nombres.stream().findFirst(); }
        boolean cambiar(int indice, String nombre) {
            if (indice < 0 || indice >= nombres.size()) return false;
            nombres.set(indice, nombre);
            return true;
        }
        boolean borrar(String nombre) { return nombres.remove(nombre); }
        boolean contiene(String nombre) { return nombres.contains(nombre); }
        List<String> todos() { return List.copyOf(nombres); }
    }

    record Reserva(int id, String cliente, int personas, String hora) {
        Reserva {
            if (id < 1 || cliente == null || cliente.isBlank() || personas < 1 || personas > 8
                    || hora == null || hora.isBlank()) {
                throw new IllegalArgumentException("Reserva inválida: personas de 1 a 8 y datos completos");
            }
        }
    }

    static class Agenda {
        private final List<Reserva> reservas = new ArrayList<>();

        void crear(Reserva reserva) {
            if (buscar(reserva.id()).isPresent()) throw new IllegalArgumentException("Id duplicado");
            reservas.add(reserva);
        }

        List<Reserva> listar() {
            return reservas.stream().sorted(Comparator.comparing(Reserva::hora)).toList();
        }

        Optional<Reserva> buscar(int id) {
            return reservas.stream().filter(reserva -> reserva.id() == id).findFirst();
        }

        boolean cambiarHora(int id, String hora) {
            Optional<Reserva> encontrada = buscar(id);
            if (encontrada.isEmpty()) return false;
            Reserva anterior = encontrada.get();
            reservas.set(reservas.indexOf(anterior),
                    new Reserva(id, anterior.cliente(), anterior.personas(), hora));
            return true;
        }

        boolean cancelar(int id) { return reservas.removeIf(reserva -> reserva.id() == id); }

        List<Reserva> deLaHora(String hora) {
            return reservas.stream().filter(reserva -> reserva.hora().equals(hora)).toList();
        }
    }

    public static void main(String[] args) throws IOException {
        ejercicio04();
        ejercicio06();
        ejercicio07();
        if (args.length > 0 && args[0].equals("--menu")) ejecutarMenu();
    }

    private static void ejercicio04() {
        Inventario inventario = new Inventario();
        inventario.crear(new Insumo(1, "Leche", 20));
        inventario.crear(new Insumo(2, "Café", 5));
        inventario.actualizar(2, "Café en grano", 8);
        System.out.println("Inventario: " + inventario.listar());
        System.out.println("Buscar id 2: " + inventario.buscar(2));
        System.out.println("Eliminar id 9: " + inventario.eliminar(9));
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            System.out.println("Cantidad inválida: escribe un número entero.");
        }
    }

    private static void ejercicio06() {
        ListaEspera espera = new ListaEspera();
        espera.agregar("Gómez (5)");
        espera.agregar("Pérez (3)");
        espera.agregar("Rojas (4)");
        espera.cambiar(0, "Rojas (4)");
        espera.borrar("Gómez (5)");
        System.out.println("En espera: " + espera.cantidad());
        System.out.println("Sigue: " + espera.primero().orElse("Nadie"));
        System.out.println(espera.todos());
        System.out.println("¿Pérez? " + espera.contiene("Pérez (3)"));
    }

    private static void ejercicio07() {
        Agenda agenda = new Agenda();
        agenda.crear(new Reserva(1, "Sofía", 4, "18:30"));
        agenda.crear(new Reserva(2, "Martín", 2, "19:00"));
        agenda.crear(new Reserva(3, "Ana", 3, "18:30"));
        System.out.println("Reservas: " + agenda.listar());
        agenda.cambiarHora(2, "18:30");
        System.out.println("A las 18:30: " + agenda.deLaHora("18:30"));
        agenda.cancelar(1);
        System.out.println("Después de cancelar: " + agenda.listar());
    }

    private static void ejecutarMenu() throws IOException {
        Agenda agenda = new Agenda();
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1 Crear · 2 Listar · 3 Cambiar hora · 4 Cancelar · 5 Salir");
            String opcion = entrada.readLine();
            if (opcion == null) break;
            try {
                switch (opcion.strip()) {
                    case "1" -> {
                        System.out.print("Id, cliente, personas, hora (separados por coma): ");
                        String linea = entrada.readLine();
                        if (linea == null) {
                            continuar = false;
                            break;
                        }
                        String[] datos = linea.split(",", -1);
                        agenda.crear(new Reserva(Integer.parseInt(datos[0].strip()), datos[1].strip(),
                                Integer.parseInt(datos[2].strip()), datos[3].strip()));
                        System.out.println("Reserva creada.");
                    }
                    case "2" -> System.out.println(agenda.listar());
                    case "3" -> {
                        System.out.print("Id y nueva hora (separados por coma): ");
                        String linea = entrada.readLine();
                        if (linea == null) {
                            continuar = false;
                            break;
                        }
                        String[] datos = linea.split(",", -1);
                        boolean cambio = agenda.cambiarHora(Integer.parseInt(datos[0].strip()), datos[1].strip());
                        System.out.println(cambio ? "Hora actualizada." : "No existe ese id.");
                    }
                    case "4" -> {
                        System.out.print("Id a cancelar: ");
                        String linea = entrada.readLine();
                        if (linea == null) {
                            continuar = false;
                            break;
                        }
                        System.out.println(agenda.cancelar(Integer.parseInt(linea.strip()))
                                ? "Reserva cancelada." : "No existe ese id.");
                    }
                    case "5" -> continuar = false;
                    default -> System.out.println("Opción desconocida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Id o cantidad inválida: escribe un número entero.");
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                System.out.println("No se pudo guardar la reserva: " + e.getMessage());
            }
        }
    }
}
