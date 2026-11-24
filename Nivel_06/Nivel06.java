import java.util.EnumMap;
import java.util.Map;

public class Nivel06 {
    enum Categoria {
        BEBIDAS("Bebida"),
        POSTRES("Postre"),
        BOTELLAS("Bebida embotellada");

        private final String descripcion;

        Categoria(String descripcion) { this.descripcion = descripcion; }
        String descripcion() { return descripcion; }
    }

    enum Leche {
        ENTERA(false, 0),
        DESLACTOSADA(false, 500),
        AVENA(true, 1500),
        ALMENDRAS(true, 1800);

        private final boolean vegetal;
        private final int recargo;

        Leche(boolean vegetal, int recargo) {
            this.vegetal = vegetal;
            this.recargo = recargo;
        }

        boolean vegetal() { return vegetal; }
        int recargo() { return recargo; }
    }

    enum EstadoMesa {
        LIBRE("Mesa disponible"),
        OCUPADA("Atendiendo"),
        POR_LIMPIAR("Pendiente de limpieza"),
        RESERVADA("Mesa reservada"),
        FUERA_DE_SERVICIO("No disponible");

        private final String aviso;

        EstadoMesa(String aviso) { this.aviso = aviso; }
        String aviso() { return aviso; }
        boolean disponible() { return this == LIBRE; }

        EstadoMesa siguiente() {
            return switch (this) {
                case LIBRE -> OCUPADA;
                case OCUPADA -> POR_LIMPIAR;
                case POR_LIMPIAR -> LIBRE;
                case RESERVADA -> OCUPADA;
                case FUERA_DE_SERVICIO -> FUERA_DE_SERVICIO;
            };
        }
    }

    public static void main(String[] args) {
        ejercicio04();
        ejercicio06();
        ejercicio07();
    }

    private static void ejercicio04() {
        Map<Categoria, Integer> conteo = new EnumMap<>(Categoria.class);
        for (Categoria categoria : Categoria.values()) conteo.put(categoria, 0);
        conteo.merge(Categoria.BEBIDAS, 2, Integer::sum);
        conteo.merge(Categoria.POSTRES, 3, Integer::sum);
        conteo.merge(Categoria.BOTELLAS, 1, Integer::sum);
        conteo.forEach((categoria, cantidad) ->
                System.out.println(categoria + " (" + categoria.descripcion() + "): " + cantidad));
    }

    private static void ejercicio06() {
        for (Leche leche : Leche.values()) {
            if (leche.vegetal()) System.out.println(leche + " +$" + leche.recargo());
        }
    }

    private static void ejercicio07() {
        EstadoMesa[] mesas = {
                EstadoMesa.LIBRE, EstadoMesa.OCUPADA, EstadoMesa.POR_LIMPIAR, EstadoMesa.RESERVADA,
                EstadoMesa.LIBRE, EstadoMesa.OCUPADA, EstadoMesa.LIBRE, EstadoMesa.FUERA_DE_SERVICIO
        };
        imprimirConteo(mesas);
        for (int i = 0; i < mesas.length; i++) mesas[i] = mesas[i].siguiente();
        imprimirConteo(mesas);
        for (EstadoMesa estado : EstadoMesa.values()) {
            System.out.println(estado + ": " + estado.aviso() + " · disponible: " + estado.disponible());
        }
    }

    private static void imprimirConteo(EstadoMesa[] mesas) {
        Map<EstadoMesa, Integer> conteo = new EnumMap<>(EstadoMesa.class);
        for (EstadoMesa estado : EstadoMesa.values()) conteo.put(estado, 0);
        for (EstadoMesa mesa : mesas) conteo.merge(mesa, 1, Integer::sum);
        System.out.println(conteo);
    }
}
