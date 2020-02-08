import java.util.NoSuchElementException;

/*
*   Questo oggetto rappresenta un certificato di morte.
*   Possiede i campi: ANNO SESSO ETA' oltre a tutti i codici relativi alle cause di morte.
* */

public class Certificate {

    private int index = 0;

    private String year;
    private String sex;
    private String age;

    // I Code nell'intervallo [0,59] sono i codici relativi alle parti. In posizione 60 vi è l'ucod.
    private Code[] parts = new Code[61];

    public Certificate(String str) {
        String codes = str.substring(10);
        setBaseData(str);                   // Anno, sesso, età vanno subito inseriti

        codes = codes.concat("\t");         // Modifica al file, blocca il ciclo!
        System.out.println(codes);

        readBlocks(codes);
    }

    private void readBlocks(String codes) {
        String current = "";

        for(int i=0; i<codes.length()-1; i++){
            String ch = String.valueOf(codes.charAt(i));        // Carattere attuale
            String nx = String.valueOf(codes.charAt(i+1));      // Carattere successivo

            if(!ch.equals("\t")) {
                // Il caso più banale è da saltare, anche perchè non c'è niente da copiare
                current = current.concat(ch);

                if(nx.equals("\t")) {
                    // Nel momento in cui si incontra un "\t" termina un blocco, quindi si salva nell'opportuna sezione e si resetta l'attuale valore
                    setPart(current);
                    current = "";
                    index++;        // l'indice va incrementato!
                }
                
            } else if(ch.equals("\t") && nx.equals("\t")) {
                setPart("");
                index++;            // Ogni due \t\t vi è un incremento perchè vi è una parte senza codice.
            }
        }
    }

    // Aggiorna il codice nella posizione segnata dall'indice.
    private void setPart(String str) {
        this.parts[index] = new Code(str);
    }

    public void updateParts(Code code, int i) {
        this.parts[i] = code;
    }

    public Code getParts(int number) {
        if(number < 0 || number > 59) {
            System.out.println("Esecuzione del metodo getPart(index) non riuscita: indice inserito non valido, deve essere compreso in [0,59]");
            return null;
        } else {
            return this.parts[number];
        }
     }


    private void setBaseData(String str) {
        String s = str.replace("\t", "");      // Stringa senza alcun carattere \t

        setYear(s.substring(0, 4));
        setSex(s.substring(4, 5));
        setAge(s.substring(5, 7));
    }

    public String getYear() {
        return year;
    }

    public String getSex() {
        return sex;
    }

    public String getAge() {
        return age;
    }

    public Code getUcod() {
        return parts[60];
    }

    private void setYear(String year) {
        this.year = year;
    }

    private void setSex(String sex) {
        this.sex = sex;
    }

    private void setAge(String age) {
        this.age = age;
    }

    public String toString() {
        String str = this.getYear() + "\t" + this.getSex() + "\t" + this.getAge() + "\t";
        for(int i=0; i<61; ++i) {
            String cod11 = this.getParts(i).getIcd11Code();
            str.concat(cod11 + "\t");
        }

        return str;
    }
}
