import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class main {
    public static void main(String[] args) throws Exception {

        // Prima carichiamo il file e creiamo un opportuno Reader
        File file = new File("src/data/cert2017100K.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine();

        Certificate certificate = Tools.createCertificate(reader);

    }
}
