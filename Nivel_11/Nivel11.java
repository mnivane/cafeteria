import java.io.BufferedReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Nivel11 {
    record Insumo(int id, String nombre, int cantidad) implements Serializable {
        private static final long serialVersionUID = 1L;

        Insumo {
            if (id < 1 || nombre == null || nombre.isBlank() || nombre.contains(";")
                    || nombre.contains("\n") || nombre.contains("\r") || cantidad < 0) {
                throw new IllegalArgumentException("Insumo inválido");
            }
        }
    }

    record Turno(String barista, int bebidas) implements Serializable {
        private static final long serialVersionUID = 1L;

        Turno {
            if (barista == null || barista.isBlank() || bebidas < 0) {
                throw new IllegalArgumentException("Turno inválido");
            }
        }
    }

    record Reserva(int id, String cliente, int personas, String hora) implements Serializable {
        private static final long serialVersionUID = 1L;

        Reserva {
            if (id < 1 || cliente == null || cliente.isBlank() || personas < 1 || personas > 8
                    || hora == null || hora.isBlank()) {
                throw new IllegalArgumentException("Reserva inválida: personas de 1 a 8");
            }
        }
    }

    static class ArchivoReservas {
        private final Path archivo;

        ArchivoReservas(Path archivo) { this.archivo = archivo; }

        void guardar(List<Reserva> reservas) throws IOException {
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(archivo))) {
                salida.writeObject(new ArrayList<>(reservas));
            }
        }

        List<Reserva> cargar() throws IOException {
            if (Files.notExists(archivo)) return new ArrayList<>();
            try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
                entrada.setObjectInputFilter(filtroSeguro());
                Object objeto = entrada.readObject();
                if (!(objeto instanceof ArrayList<?> lista)) {
                    throw new InvalidObjectException("El archivo no contiene una lista de reservas");
                }
                List<Reserva> reservas = new ArrayList<>();
                for (Object elemento : lista) {
                    if (!(elemento instanceof Reserva reserva)) {
                        throw new InvalidObjectException("Tipo de reserva no permitido");
                    }
                    reservas.add(reserva);
                }
                return reservas;
            } catch (ClassNotFoundException e) {
                throw new InvalidClassException("No se reconoce una clase del archivo");
            }
        }

        private static ObjectInputFilter filtroSeguro() {
            return info -> {
                if (info.depth() > 10 || info.references() > 10000 || info.streamBytes() > 1_000_000) {
                    return ObjectInputFilter.Status.REJECTED;
                }
                Class<?> tipo = info.serialClass();
                if (tipo == null) return ObjectInputFilter.Status.UNDECIDED;
                if (tipo == ArrayList.class || tipo == Reserva.class || tipo == Insumo.class
                        || tipo == String.class
                        || (tipo.isArray() && tipo.getComponentType() == Object.class)) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                return ObjectInputFilter.Status.REJECTED;
            };
        }
    }

    static class Agenda {
        private final List<Reserva> reservas = new ArrayList<>();

        Agenda(List<Reserva> iniciales) { reservas.addAll(iniciales); }
        void crear(Reserva reserva) {
            if (buscar(reserva.id()).isPresent()) throw new IllegalArgumentException("Id duplicado");
            reservas.add(reserva);
        }
        List<Reserva> listar() { return List.copyOf(reservas); }
        Optional<Reserva> buscar(int id) {
            return reservas.stream().filter(reserva -> reserva.id() == id).findFirst();
        }
        boolean cancelar(int id) { return reservas.removeIf(reserva -> reserva.id() == id); }
        boolean cambiarHora(int id, String hora) {
            Optional<Reserva> actual = buscar(id);
            if (actual.isEmpty()) return false;
            Reserva reserva = actual.get();
            reservas.set(reservas.indexOf(reserva),
                    new Reserva(id, reserva.cliente(), reserva.personas(), hora));
            return true;
        }
        List<Reserva> deLaHora(String hora) {
            return reservas.stream().filter(reserva -> reserva.hora().equals(hora)).toList();
        }
    }

    static String aLinea(Insumo insumo) {
        return insumo.id() + ";" + insumo.nombre() + ";" + insumo.cantidad();
    }

    static Insumo deLinea(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length != 3) throw new IllegalArgumentException("Línea CSV inválida: " + linea);
        return new Insumo(Integer.parseInt(campos[0]), campos[1], Integer.parseInt(campos[2]));
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("--menu")) {
            ejecutarMenuPersistente();
            return;
        }
        Path temporal = Files.createTempDirectory("cafe-sintaxis-");
        try {
            ejercicio04(temporal);
            ejercicio06(temporal);
            ejercicio07(temporal);
            retoFinal(temporal);
        } finally {
            try (var archivos = Files.walk(temporal)) {
                for (Path archivo : archivos.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(archivo);
                }
            }
        }
    }

    private static void ejercicio04(Path temporal) throws IOException {
        List<Insumo> hoy = List.of(new Insumo(1, "Leche entera (L)", 20),
                new Insumo(2, "Café en grano (kg)", 5));
        Path csv = temporal.resolve("bodega.csv");
        List<String> lineas = new ArrayList<>();
        lineas.add("id;nombre;cantidad");
        for (Insumo insumo : hoy) lineas.add(aLinea(insumo));
        Files.write(csv, lineas);

        List<Insumo> leidos = new ArrayList<>();
        for (String linea : Files.readAllLines(csv)) {
            if (linea.equals("id;nombre;cantidad")) continue;
            try {
                leidos.add(deLinea(linea));
            } catch (IllegalArgumentException e) {
                throw new IOException("Fila de bodega inválida: " + linea, e);
            }
        }
        System.out.println(Files.readString(csv));
        System.out.println(Files.size(csv) + " bytes · ¿iguales? " + hoy.equals(leidos));
        try {
            deLinea("3;Azúcar;-3");
        } catch (IllegalArgumentException e) {
            System.out.println("Cantidad no válida detectada al leer el CSV.");
        }
    }

    private static void ejercicio06(Path temporal) throws IOException {
        Turno turno = new Turno("Martín", 42);
        Path archivo = temporal.resolve("turno.bin");
        try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(archivo))) {
            salida.writeObject(turno);
        }
        Turno leido;
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            entrada.setObjectInputFilter(info -> {
                if (info.depth() > 5 || info.references() > 100 || info.streamBytes() > 100_000) {
                    return ObjectInputFilter.Status.REJECTED;
                }
                Class<?> tipo = info.serialClass();
                if (tipo == null) return ObjectInputFilter.Status.UNDECIDED;
                return tipo == Turno.class || tipo == String.class
                        ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
            });
            leido = (Turno) entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new InvalidClassException("No se reconoce el tipo de turno");
        }
        System.out.println(leido + " · " + Files.size(archivo) + " bytes");
    }

    private static void ejercicio07(Path temporal) throws IOException {
        Path archivo = temporal.resolve("reservas.bin");
        ArchivoReservas almacenamiento = new ArchivoReservas(archivo);
        Agenda agenda = new Agenda(almacenamiento.cargar());
        agenda.crear(new Reserva(1, "Sofía", 4, "18:30"));
        agenda.crear(new Reserva(2, "Martín", 2, "19:00"));
        agenda.cambiarHora(2, "18:30");

        almacenamiento.guardar(agenda.listar());
        Agenda recuperada = new Agenda(almacenamiento.cargar());
        Path csv = temporal.resolve("reservas.csv");
        List<String> filas = new ArrayList<>(List.of("id;cliente;personas;hora"));
        for (Reserva reserva : recuperada.listar()) {
            filas.add(reserva.id() + ";" + reserva.cliente() + ";" + reserva.personas()
                    + ";" + reserva.hora());
        }
        Files.write(csv, filas);
        System.out.println("Reservas recuperadas: " + recuperada.listar());
        System.out.println("CSV exportado: " + csv.getFileName());

        try {
            new Reserva(99, "Archivo alterado", 12, "18:30");
        } catch (IllegalArgumentException e) {
            System.out.println("Reserva inválida rechazada: " + e.getMessage());
        }
        try {
            almacenamiento.cargar();
        } catch (InvalidClassException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private static void ejecutarMenuPersistente() throws IOException {
        Path archivoReservas = Path.of("reservas.bin");
        Path archivoCsv = Path.of("reservas.csv");
        ArchivoReservas almacenamiento = new ArchivoReservas(archivoReservas);
        Agenda agenda;
        try {
            agenda = new Agenda(almacenamiento.cargar());
        } catch (InvalidClassException e) {
            System.out.println("El archivo de reservas no es compatible: " + e.getMessage());
            agenda = new Agenda(List.of());
        } catch (IOException e) {
            System.out.println("No se pudieron cargar las reservas: " + e.getMessage());
            agenda = new Agenda(List.of());
        }

        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1 Crear · 2 Listar · 3 Cambiar hora · 4 Cancelar · 5 Exportar CSV · 6 Salir");
            try {
                String opcion = entrada.readLine();
                if (opcion == null) break;
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
                        System.out.println(agenda.cambiarHora(Integer.parseInt(datos[0].strip()),
                                datos[1].strip()) ? "Hora actualizada." : "No existe ese id.");
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
                    case "5" -> {
                        List<String> filas = new ArrayList<>(List.of("id;cliente;personas;hora"));
                        for (Reserva reserva : agenda.listar()) {
                            filas.add(reserva.id() + ";" + reserva.cliente() + ";"
                                    + reserva.personas() + ";" + reserva.hora());
                        }
                        Files.write(archivoCsv, filas);
                        System.out.println("CSV exportado a " + archivoCsv.toAbsolutePath());
                    }
                    case "6" -> {
                        almacenamiento.guardar(agenda.listar());
                        continuar = false;
                        System.out.println("Reservas guardadas.");
                    }
                    default -> System.out.println("Opción desconocida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Id o cantidad inválida: escribe un número entero.");
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Operación inválida: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("No se pudo leer o guardar: " + e.getMessage());
            }
        }
    }

    private enum Tamano { PEQUENO, MEDIANO, GRANDE }
    private enum Categoria { BEBIDA, POSTRE }

    private abstract static class Producto {
        private final String nombre;
        private final Categoria categoria;
        private final int precio;

        Producto(String nombre, Categoria categoria, int precio) {
            if (nombre == null || nombre.isBlank() || precio <= 0) {
                throw new IllegalArgumentException("Producto inválido");
            }
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
        }
        String nombre() { return nombre; }
        Categoria categoria() { return categoria; }
        int precio() { return precio; }
    }

    private static final class Bebida extends Producto {
        private final Tamano tamano;
        Bebida(String nombre, int precio, Tamano tamano) {
            super(nombre, Categoria.BEBIDA, precio);
            this.tamano = tamano;
        }
        Tamano tamano() { return tamano; }
    }

    private static final class Postre extends Producto {
        Postre(String nombre, int precio) { super(nombre, Categoria.POSTRE, precio); }
    }

    private record Linea(Producto producto, int cantidad) {
        private Linea {
            if (cantidad < 1) throw new IllegalArgumentException("Cantidad inválida");
        }
        int subtotal() { return producto.precio() * cantidad; }
    }

    private record Recibo(List<Linea> lineas, int total) {
        private Recibo { lineas = List.copyOf(lineas); }
    }

    private interface Repositorio<T> {
        void crear(int id, T elemento);
        Optional<T> buscar(int id);
        List<T> listar();
        boolean actualizar(int id, T elemento);
        boolean eliminar(int id);
    }

    private static final class RepositorioLista<T> implements Repositorio<T> {
        private final java.util.Map<Integer, T> elementos = new java.util.LinkedHashMap<>();
        public void crear(int id, T elemento) {
            if (elementos.putIfAbsent(id, elemento) != null) throw new IllegalArgumentException("Id duplicado");
        }
        public Optional<T> buscar(int id) { return Optional.ofNullable(elementos.get(id)); }
        public List<T> listar() { return List.copyOf(elementos.values()); }
        public boolean actualizar(int id, T elemento) {
            if (!elementos.containsKey(id)) return false;
            elementos.put(id, elemento);
            return true;
        }
        public boolean eliminar(int id) { return elementos.remove(id) != null; }
    }

    private interface MedioPago {
        boolean pagar(int total);
    }

    private interface DescuentoFinal {
        int aplicar(int subtotal);
    }

    private record PorcentajeFinal(int porcentaje) implements DescuentoFinal {
        private PorcentajeFinal {
            if (porcentaje < 0 || porcentaje > 100) throw new IllegalArgumentException("Descuento inválido");
        }
        public int aplicar(int subtotal) { return subtotal * porcentaje / 100; }
    }

    private interface NotificadorFinal {
        void enviar(String mensaje);
    }

    private static final class NotificadorMemoriaFinal implements NotificadorFinal {
        private String ultimo;
        public void enviar(String mensaje) { ultimo = mensaje; }
        String ultimo() { return ultimo; }
    }

    private static final class CajaFinal {
        private final DescuentoFinal descuento;
        private final NotificadorFinal notificador;

        CajaFinal(DescuentoFinal descuento, NotificadorFinal notificador) {
            this.descuento = descuento;
            this.notificador = notificador;
        }

        int cobrar(Recibo recibo, MedioPago medio) {
            int total = recibo.total() - descuento.aplicar(recibo.total());
            if (!medio.pagar(total)) throw new IllegalStateException("Pago rechazado");
            notificador.enviar("TICKET · total $" + total);
            return total;
        }
    }

    private static final class Efectivo implements MedioPago {
        public boolean pagar(int total) { return total > 0; }
    }

    private static void retoFinal(Path temporal) throws IOException {
        Repositorio<Producto> menu = new RepositorioLista<>();
        menu.crear(1, new Bebida("Latte", 9500, Tamano.MEDIANO));
        menu.crear(2, new Postre("Brownie", 7000));
        List<Linea> lineas = List.of(new Linea(menu.buscar(1).orElseThrow(), 2),
                new Linea(menu.buscar(2).orElseThrow(), 1));
        int subtotal = lineas.stream().mapToInt(Linea::subtotal).sum();
        Recibo recibo = new Recibo(lineas, subtotal);

        Path inventario = temporal.resolve("inventario.bin");
        List<Insumo> stock = List.of(new Insumo(1, "Leche", 20));
        try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(inventario))) {
            salida.writeObject(new ArrayList<>(stock));
        }
        List<Insumo> stockLeido;
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(inventario))) {
            entrada.setObjectInputFilter(ArchivoReservas.filtroSeguro());
            Object datos = entrada.readObject();
            if (!(datos instanceof ArrayList<?> lista)) {
                throw new InvalidObjectException("El archivo no contiene una lista de insumos");
            }
            stockLeido = new ArrayList<>();
            for (Object elemento : lista) {
                if (!(elemento instanceof Insumo insumo)) {
                    throw new InvalidObjectException("Tipo de insumo no permitido");
                }
                stockLeido.add(insumo);
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidClassException("No se reconoce el inventario");
        }
        Path csv = temporal.resolve("inventario.csv");
        List<String> filas = new ArrayList<>(List.of("id;nombre;cantidad"));
        for (Insumo insumo : stockLeido) filas.add(aLinea(insumo));
        Files.write(csv, filas);

        NotificadorMemoriaFinal notificador = new NotificadorMemoriaFinal();
        CajaFinal caja = new CajaFinal(new PorcentajeFinal(10), notificador);
        int totalCobrado = caja.cobrar(recibo, new Efectivo());
        System.out.println("Inventario persistido: " + stockLeido + " · CSV: " + csv.getFileName());
        System.out.println("Reto final · artículos: " + recibo.lineas().size()
                + " · total: $" + totalCobrado + " · pago: " + notificador.ultimo());
    }
}
