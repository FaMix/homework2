import java.io.*;

public class search_engine {
    public static void main(String[] args) {
        try {
            // Leer las cadenas desde 'input.txt'
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

            String line1 = reader.readLine();
            String line2 = reader.readLine();
            String line3 = reader.readLine();
            reader.close();

            // Verificar si las líneas son válidas antes de procesarlas
            if (line1 == null || line2 == null || line3 == null) {
                System.err.println("Error: El archivo input.txt no tiene el formato esperado.");
                return;
            }
            
            // SEARCH ENGINE --------------------------------------------------------------------------------------------
            // Separar las cadenas y extraer solo los valores (después de ": ")
            String cadena1 = line1.split(": ")[1];
            String cadena2 = line2.split(": ")[1];
            String cadena3 = line3.split(": ")[1];
            
            // Invertir las cadenas
            String inverted1 = new StringBuilder(cadena1).reverse().toString();
            String inverted2 = new StringBuilder(cadena2).reverse().toString();
            String inverted3 = new StringBuilder(cadena3).reverse().toString();
            //SEARCH ENGINE ----------------------------------------------------------------------------------------------

            // Escribir las cadenas invertidas en 'output.txt'
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("Cadena 1 invertida: " + inverted1 + "\n");
            writer.write("Cadena 2 invertida: " + inverted2 + "\n");
            writer.write("Cadena 3 invertida: " + inverted3 + "\n");
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: El archivo input.txt no tiene el formato esperado.");
        }
    }
}
