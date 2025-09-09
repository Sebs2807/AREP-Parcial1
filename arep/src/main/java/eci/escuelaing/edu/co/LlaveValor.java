package eci.escuelaing.edu.co;

import java.net.*;
import java.util.HashMap;
import java.io.*;

public class LlaveValor {
    private static HashMap<String, String> store = new HashMap<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(40000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String outputLine = "";
            boolean firstLine = true;
            String path = "";
            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    System.out.println("Recibí: " + inputLine);
                    String[] division = inputLine.split(" ");
                    path = division[1];
                    firstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }
            URI uri = new URI(path);
            if (uri.getPath().startsWith("/setkv")) {
                String query = uri.getQuery().toString();
                String llave = query.split("&")[0].split("=")[1];
                String valor = query.split("&")[1].split("=")[1];
                store.put(llave, valor);
                String respuesta = "{ \"key\": \"" + llave + "\", \"value\": \"" + valor + "\", \"status\": \"almacenados exitosamente\" }";
                outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                        + respuesta
                        + "</body>\n"
                        + "</html>\n";
            } else if (uri.getPath().startsWith("/getkv")) {
                String query = uri.getQuery().toString();
                String llave = query.split("=")[1];
                String valor = store.get(llave);
                String respuesta = "{ \"key\": \"" + llave + "\", \"value\": \"" + valor + "\"}";
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "<meta charset=\"UTF-8\">\n"
                        + "<title>Title of the document</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + respuesta
                        + "</body>\n"
                        + "</html>\n";
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}
