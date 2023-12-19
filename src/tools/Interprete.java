package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interprete {
    static boolean existenErrores = false;
    static TablaSimbolos ts = new TablaSimbolos();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Uso correcto: interprete [archivo.txt]");
            System.exit(64);
        } else if (args.length == 1) {
            ejecutarArchivo(args[0]);
        } else {
            ejecutarPrompt();
        }
    }

    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));
        if (existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String conjunto = "";

        while (true) {
            System.out.print(">>> ");
            String linea = reader.readLine();
            if (linea == null || linea.equals("salir"))
                break; // Ctrl + D
            ejecutar(linea);
            existenErrores = false;
        }
    }

    private static void ejecutar(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

//        for(Token token : tokens){
//        System.out.println(token);
//        }

        try {
            Parser parser = new Parser(tokens);
            parser.parse();
            if (parser.esValida) {
//                System.out.print("\n>>>Cadena valida<<<\n\n");
                GeneradorPostfija gpf = new GeneradorPostfija(tokens);
                List<Token> postfija = gpf.convertir();
                for(Token token : postfija){
                    System.out.println(token);
                }
                GeneradorAST gast = new GeneradorAST(postfija);
                Arbol programa = gast.generarAST();
                programa.recorrer(ts);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    static void error(int linea, String mensaje) {
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String donde, String mensaje) {
        System.err.println(
                "[linea " + linea + "] Error " + donde + ": " + mensaje);
        existenErrores = true;
    }
}
