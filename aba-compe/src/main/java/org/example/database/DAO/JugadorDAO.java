package org.example.database.DAO;

import org.bukkit.Bukkit;
import org.example.Main;
import org.example.database.SupabaseClient;
import org.example.entity.jugador.JugadorEntity;

import java.net.http.HttpResponse;

/**
 * DAO singleton para la persistencia de jugadores en Supabase.
 * Todas las operaciones se ejecutan de forma asíncrona para no bloquear el hilo principal.
 */
public class JugadorDAO {

    /** Instancia única del DAO. */
    private static JugadorDAO instance;

    /**
     * Devuelve la instancia única del DAO, creándola si no existe.
     *
     * @return instancia singleton de JugadorDAO
     */
    public static JugadorDAO get() {
        if (instance == null) instance = new JugadorDAO();
        return instance;
    }

    /**
     * Busca el jugador en Supabase por UUID. Si no existe lo crea,
     * si existe actualiza su nombre de usuario por si ha cambiado.
     *
     * @param jugador entidad del jugador a buscar o crear
     */
    public void findOrCreate(JugadorEntity jugador) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.get(), () -> {
            try {
                HttpResponse<String> response = SupabaseClient.get()
                        .get("jugadores?uuid=eq." + jugador.getUuid() + "&select=*");

                if (response.body().equals("[]")) {
                    String body = String.format(
                            "{\"uuid\":\"%s\",\"username\":\"%s\"}",
                            jugador.getUuid(), jugador.getName()
                    );
                    SupabaseClient.get().post("jugadores", body);
                    System.out.println("Jugador creado en Supabase: " + jugador.getName());
                } else {
                    String body = String.format("{\"username\":\"%s\"}", jugador.getName());
                    SupabaseClient.get().patch("jugadores?uuid=eq." + jugador.getUuid(), body);
                    System.out.println("Jugador actualizado en Supabase: " + jugador.getName());
                }
            } catch (Exception e) {
                System.out.println("Error Supabase findOrCreate: " + e.getMessage());
            }
        });
    }

    /**
     * Persiste las estadísticas actuales del jugador en Supabase.
     * Actualiza kills, muertes, lanas colocadas, victorias, derrotas y tiempo jugado.
     *
     * @param jugador entidad del jugador cuyas estadísticas se guardan
     */
    public void saveStats(JugadorEntity jugador) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.get(), () -> {
            try {
                String body = String.format(
                        "{\"kills\":%d,\"deaths\":%d,\"wools_placed\":%d,\"wins\":%d,\"losses\":%d,\"time_played\":%d,\"updated_at\":\"now()\"}",
                        jugador.getKills(),
                        jugador.getDeaths(),
                        jugador.getWoolsPlaced(),
                        jugador.getWins(),
                        jugador.getLosses(),
                        jugador.getTimePlayed()
                );
                SupabaseClient.get().patch("jugadores?uuid=eq." + jugador.getUuid(), body);
                System.out.println("Stats guardadas para: " + jugador.getName());
            } catch (Exception e) {
                System.out.println("Error Supabase saveStats: " + e.getMessage());
            }
        });
    }
}