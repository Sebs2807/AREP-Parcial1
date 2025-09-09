package eci.escuelaing.edu.co;

import java.net.*;
import java.io.*;

public class HttpServer {
	public static void main(String[] args) throws IOException, URISyntaxException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(36000);
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
				if(firstLine){
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
			if(uri.getPath().startsWith("/setkv") || uri.getPath().startsWith("/getkv")){
				HttpConnectionExample.getInstance();
				String response = HttpConnectionExample.proceso(uri);
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
							+ response
							+ "</body>\n"
							+ "</html>\n";
			}else if(uri.getPath().startsWith("/cliente")){
				outputLine =	"HTTP/1.1 200 OK\r\n"
        					+ "Content-Type: text/html\r\n"
							+ "\r\n" 
							+"<!DOCTYPE html>\n"
                            + "<html>\n"
                            + "\n"
                            + "<head>\n"
                            + "    <title>Form Example</title>\n"
                            + "    <meta charset=\"UTF-8\">\n"
                            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                            + "</head>\n"
                            + "\n"
                            + "<body>\n"
                            + "    <h1>/setkv</h1>\n"
                            + "    <form action=\"/set1\">\n"
                            + "        <label for=\"name\">Key:</label><br>\n"
                            + "        <input type=\"text\" id=\"key\" name=\"key\" value=\"\"><br><br>\n"
							+ "		   <input type=\"button\" value=\"Submit\" onclick=\"loadGetKeyAndValue()\">\n"
                            + "    </form>\n"
							+ "    <div id=\"get1\"></div>\n"
							+ "    <form action=\"/set2\">\n"
							+ "		   <label for=\"name\">Value:</label><br>\n"
                            + "        <input type=\"text\" id=\"value\" name=\"value\" value=\"\"><br><br>\n"
                            + "    </form>\n"
                            + "    <div id=\"get2\"></div>\n"
							+ "	   <h1>/getkv</h1>\n"
							+ "    <form action=\"/get\">\n"
                            + "        <label for=\"name\">Key:</label><br>\n"
                            + "        <input type=\"text\" id=\"getkey\" name=\"getkey\" value=\"\"><br><br>\n"
							+ "		   <input type=\"button\" value=\"Submit2\" onclick=\"loadGetValue()\">\n"
                            + "    </form>\n"
							+ "    <div id=\"otroform\"></div>\n"
                            + "\n"
                            + "    <script>\n"
                            + "        function loadGetKeyAndValue() {\n"
                            + "            let llave = document.getElementById(\"key\").value;\n"
                            + "            let valor = document.getElementById(\"value\").value;\n"
                            + "            const xhttp = new XMLHttpRequest();\n"
                            + "            xhttp.onload = function () {\n"
                            + "                document.getElementById(\"get1\").innerHTML =\n"
                            + "                document.getElementById(\"get2\").innerHTML =\n"
                            + "                    this.responseText;\n"
                            + "            }\n"
                            + "            xhttp.open(\"GET\", \"/setkv?key=\" + llave + \"&value=\" + valor);\n"
                            + "            xhttp.send();\n"
                            + "        }\n"
							+ "        function loadGetValue() {\n"
							+ "            let getllave = document.getElementById(\"getkey\").value;\n"
							+ "            const xhttp = new XMLHttpRequest();\n"
							+ "            xhttp.onload = function () {\n"
							+ "                document.getElementById(\"otroform\").innerHTML =\n"
							+ "                    this.responseText;\n"
							+ "            }\n"
							+ "            xhttp.open(\"GET\", \"/getkv?key=\" + getllave);\n"
							+ "            xhttp.send();\n"
							+ "        }\n"
                            + "    </script>\n"
                            + "\n"
                            + "</body>\n"
                            + "\n"
                            + "</html>";
			}
			out.println(outputLine);
			out.close();
			in.close();
			clientSocket.close();
		}
		serverSocket.close();
	}
}