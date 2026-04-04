package org.example.database;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP singleton para la API REST de Supabase.
 * Proporciona métodos para realizar peticiones GET, POST y PATCH
 * contra los endpoints de la base de datos.
 */
public class SupabaseClient {

    /** URL base del proyecto Supabase. */
    private final String url;

    /** Clave de API para autenticar las peticiones. */
    private final String key;

    /** Cliente HTTP utilizado para enviar las peticiones. */
    private final HttpClient client;

    /** Instancia única del cliente. */
    private static SupabaseClient instance;

    /**
     * Crea una nueva instancia del cliente con la URL y clave indicadas.
     *
     * @param url URL base del proyecto Supabase
     * @param key clave de API de Supabase
     */
    private SupabaseClient(String url, String key) {
        this.url = url;
        this.key = key;
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Inicializa el cliente singleton con las credenciales de Supabase.
     * Debe llamarse antes de cualquier uso de {@link #get()}.
     *
     * @param url URL base del proyecto Supabase
     * @param key clave de API de Supabase
     */
    public static void init(String url, String key) {
        instance = new SupabaseClient(url, key);
    }

    /**
     * Devuelve la instancia singleton del cliente.
     *
     * @return instancia de SupabaseClient
     */
    public static SupabaseClient get() {
        return instance;
    }

    /**
     * Realiza una petición GET al endpoint indicado.
     *
     * @param endpoint ruta relativa del endpoint (p. ej. {@code "jugadores?uuid=eq.abc"})
     * @return respuesta HTTP con el cuerpo en formato JSON
     * @throws Exception si ocurre un error durante la petición
     */
    public HttpResponse<String> get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/rest/v1/" + endpoint))
                .header("apikey", key)
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Realiza una petición POST al endpoint indicado con el cuerpo JSON proporcionado.
     *
     * @param endpoint ruta relativa del endpoint
     * @param body     cuerpo de la petición en formato JSON
     * @return respuesta HTTP con el cuerpo en formato JSON
     * @throws Exception si ocurre un error durante la petición
     */
    public HttpResponse<String> post(String endpoint, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/rest/v1/" + endpoint))
                .header("apikey", key)
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Realiza una petición PATCH al endpoint indicado con el cuerpo JSON proporcionado.
     *
     * @param endpoint ruta relativa del endpoint
     * @param body     cuerpo de la petición en formato JSON con los campos a actualizar
     * @return respuesta HTTP con el cuerpo en formato JSON
     * @throws Exception si ocurre un error durante la petición
     */
    public HttpResponse<String> patch(String endpoint, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/rest/v1/" + endpoint))
                .header("apikey", key)
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}