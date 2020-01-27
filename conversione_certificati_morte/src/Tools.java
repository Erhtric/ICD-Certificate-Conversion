import java.io.File;
import java.util.Scanner;

public class Tools {

    public static void reader() throws Exception{
        File file = new File("src/data/cert2017100K.txt");
        Scanner sc = new Scanner(file);

        // Prima linea con le intestazioni
        sc.nextLine();

        while (sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }
    }

}
