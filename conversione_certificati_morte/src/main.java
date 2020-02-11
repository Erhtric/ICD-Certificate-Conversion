import java.io.*;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

public class main {
    public static void main(String[] args) throws Exception {
        if(args.length==0){
            System.out.println("Non è stato specificato i nome del file da convertire.\nUsare \"nomeprogramma -help\" per un aiuto su come usare il programma.");
            return;
        }else if(args.length==1&&args[0].equals("-help")){
            System.out.println("Il primo parametro deve essere sempre il nome (o il percorso) del file da convertire.");
            System.out.println("In aggiunta, e' possibile usare le seguenti opzioni:");
            System.out.println("-help\t\tUtilizzato come primo parametro, mostra questa guida.");
            System.out.println("-disableSubclassGood\t\tNon utilizza il tipo di conversione \"SubclassGood\"");
            System.out.println("-outputPath <filePath>\t\tPermette di specificare il nome e la posizione del file di output.");
            System.out.println("-mappingPath <filePath>\t\tPermette di specificare il nome e la posizione del file di mapping.");
            System.out.println("-statsPath <filePath>\t\tPermette di specificare il nome e la posizione del file con l'analisi statistica.");
            System.out.println("-includeFirstLine\t\tConverte anche la prima riga del file di input, che di solito e' ignorata in quanto intestazione.");
            return;
        }

        //Crea il lettore per il file di input
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(new File(args[0])));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di input.");
            return;
        }

        //Crea lo scrittore per il file di output
        OutputStreamWriter writer;
        try{
            writer = new OutputStreamWriter(new FileOutputStream(getOutputFileName(args)));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di output.");
            return;
        }

        //Crea i converter per i certificati
        //Questo passo è l'ultimo perché la creazione di CodeConverter è costosa ed è inutile eseguirla se ci sono errori nella creazione del lettore o dello scrittore
        CodeConverter codeConv;
        try{
            codeConv=new CodeConverter(getMappingFileName(args),skipFirstLine(args));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di mapping.");
            return;
        }
        CertificateConverter certConv = new CertificateConverter(codeConv);

        //Salta l'intestazione del file di input
        if(skipFirstLine(args)){
            reader.readLine();
        }

        Certificate cert;
        /*parametri statistici
        ...
         */
        while (reader.ready()) {
            try {
                cert=new Certificate(reader.readLine());
                certConv.convert(cert);
                //calcoli statistici
            } catch (NoSuchElementException e) {
                //trovato codice inesistente
                //calcoli statistici
            }
        }
        reader.close();
        /*calcoli statistici
        ...
         */

        /*output statistici
        ...
         */
    }

    private static String getOutputFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-outputPath")){
                return args[i+1];
            }
        }
        return "out_"+ LocalTime.now() +".txt";
    }

    private static String getMappingFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-mappingPath")){
                return args[i+1];
            }
        }
        return "data\\10To11MapToOneCategory.txt";
    }

    private static boolean skipFirstLine(String[] args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-includeFirstLine")){
                return false;
            }
        }
        return true;
    }

    private static boolean includeSubclassGood(String[] args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-disableSubclassGood")){
                return false;
            }
        }
        return true;
    }

    private static String getStatsFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-statsPath")){
                return args[i+1];
            }
        }
        return "stats_"+ LocalTime.now() +".txt";
    }
}
