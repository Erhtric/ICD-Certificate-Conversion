import java.io.*;

public class CertificateConverter {

    // path = "src/data/cert2017100K.txt";
    public CertificateConverter(String path) throws IOException{

        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        File fileOutput = new File("src/data/cert2017icd11.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOutput, true));

        String header = reader.readLine() + "\n";

        while(!reader.readLine().isEmpty()) {

            // Prima creiamo un certificato contenente i codici icd-10
            Certificate cer = new Certificate(reader.readLine());
            // Generiamo il convertitore
            CodeConverter converter = new CodeConverter("src/data/10To11MapToOneCategory.txt");

            // Convertiamoli in icd-11
            for(int i=0; i<61; ++i) {
                cer.updateParts(converter.convert(cer.getParts(i)), i);
                if(i == 60) cer.updateParts(converter.convert(cer.getUcod()), i);
            }

            // Scriviamo su file
            out.write(header);
            writeStringToFile(cer, out);
        }

        reader.close();
        out.close();
    }

    private void writeStringToFile(Certificate cer, BufferedWriter out) throws IOException {
        String str = cer.toString();
        out.write(str + "\n");
    }
}
