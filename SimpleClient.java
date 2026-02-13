import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SimpleClient {

    // Change these if needed
    private static final String URL = "http://localhost:8000/";
    private static final int TAB_COUNT = 50000; // Change this to simulate opening more/fewer tabs

    // true = print raw HTML, false = print "rendered" text (tags stripped)
    private static final boolean PRINT_RAW_HTML = true;

    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .GET()
                    .build();

            simulateTabs(1, client, request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Very basic HTML "rendering": remove tags + clean whitespace
    private static String renderToTerminalText(String html) {
        return html
                .replaceAll("(?is)<script.*?>.*?</script>", " ")
                .replaceAll("(?is)<style.*?>.*?</style>", " ")
                .replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // Recursive version of the "open tabs" loop
    private static void simulateTabs(int tab, HttpClient client, HttpRequest request)
            throws IOException, InterruptedException {

        // Base case: stop after TAB_COUNT
        if (tab > TAB_COUNT) {
            return;
        }

        // Do one request (one "tab")
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n========================================");
        System.out.println("TAB " + tab + " -> " + URL);
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("========================================\n");

        String body = response.body();

        if (PRINT_RAW_HTML) {
            System.out.println("---- Raw HTML ----\n");
            System.out.println(body);
        } else {
            System.out.println("---- Rendered (Terminal) ----\n");
            System.out.println(renderToTerminalText(body));
        }

        // Recursive call: next tab
        simulateTabs(tab + 1, client, request);
    }
}
