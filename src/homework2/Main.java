package homework2;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class Main {

    public static void main(String[] args) throws IOException {
        String path = "../lucene-index"; // Define where to save Lucene index

        //PASAR LOS HTMLS A LA FUNCION XPATH PARA QUE EXTRAIGA EL TITULO
        Path dirPath = Paths.get("../htmls2");

        List<String> listaNombres = new ArrayList<>();
        List<String> listaTitulos = new ArrayList<>();
        List<String> listaContenido = new ArrayList<>();
        List<String> listaAutores = new ArrayList<>();
        List<String> listaAbstract = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.html")) {

            for (Path filePath : stream) {
                Xpaths extraer = new Xpaths(filePath); //llamo al archivo Xpaths
                String nombres = filePath.getFileName() + "";
                listaNombres.add(nombres);
                String titulos = extraer.titulos();
                listaTitulos.add(titulos);
                String autores = extraer.autores();
                listaAutores.add(autores);
                String contenido = extraer.contenido();
                listaContenido.add(contenido);
                String abstracto = extraer.abstracto();
                listaAbstract.add(abstracto);

            }
        } catch (IOException e){
            System.out.println("Error al leer los archivos htmls" + e.getMessage());
        }

        Indexer index = new Indexer();
        index.index(path, listaNombres, listaTitulos, listaAutores, listaAbstract, listaContenido);
    }
}
