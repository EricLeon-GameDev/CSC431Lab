package Lab5;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleWebServer {

    public static void main(String[] args) throws IOException {
        // Create an HTTP server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Define a context that listens for requests at the root path "/"
        server.createContext("/", new RootHandler());

        // Start the server
        server.setExecutor(null); // Creates a default executor
        server.start();
        System.out.println("Server is running on http://10.0.101.74:8000/");
    }

    // Handler to process incoming HTTP requests
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // index.html is inside: C:\Repos\CSC431\CSC431Lab\Lab5\index.html
            Path filePath = Paths.get("Lab5", "index.html");

            // Helpful debug prints
            System.out.println("Working dir = " + System.getProperty("user.dir"));
            System.out.println("Looking for = " + filePath.toAbsolutePath());
            System.out.println("Request: " + exchange.getRequestURI());

            // If the file doesn't exist, return a clear 404 instead of an empty response
            if (!Files.exists(filePath)) {
                String msg = "404 Not Found: " + filePath.toAbsolutePath();
                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return;
            }

            // Read the HTML file and send it back
            byte[] fileBytes = Files.readAllBytes(filePath);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }
    }
}
