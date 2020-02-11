/*
*   Questo oggetto rappresenta un certificato di morte.
*   Possiede i campi: ANNO SESSO ETA' oltre a tutti i codici relativi alle cause di morte.
* */

public class Certificate {

    private int index = 0;

    private String year;
    private String sex;
    private String age;

    private Code[] parts = new Code[61];

    public Certificate(String str) {
        String codes = str.substring(9);
        setBaseData(str);                   // Anno, sesso, età vanno subito inseriti
        codes =  codes + "\n";
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
                    setPart(normalization(current));
                    current = "";
                    index++;        // l'indice va incrementato!
                } else if(nx.equals("\n")) {
                    // Quando si incontra un carattere newline termina una riga
                    setPart(normalization(current));
                }
                
            } else if(ch.equals("\t") && nx.equals("\t")) {
                setPart("");
                index++;            // Ogni due \t\t vi è un incremento perchè vi è una parte senza codice.
            }
        }
    }

    // Questo metodo serve per poter rendere i certificati utilizzabili dal convertitore
    // - ANM e i numeri romani rimarranno tali e quali;
    // - ANMZ -> ANM.Z
    private String normalization(String str) {
        String res = str;
        if(!isRomanNumber(str) && str.length() > 3) {
            res = str.substring(0, 3) + '.' + str.substring(3);
        }
        return res;
    }

    private boolean isRomanNumber(String s){
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)!='I'&&s.charAt(i)!='V'&&s.charAt(i)!='X'){
                return false;
            }
        }
        return true;
    }

    private void setPart(String str) {
        this.parts[index] = new Code(str);
    }

    public void updateParts(Code code, int number) {
        this.parts[number] = code;
    }

    public Code getCodeFromIndex(int number) {
        if(number < 0 || number > 61) {
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

    private void setYear(String year) {
        this.year = year;
    }

    private void setSex(String sex) {
        this.sex = sex;
    }

    private void setAge(String age) {
        this.age = age;
    }

    public String toStringICD11() {
        String str = "YEAR: " + this.getYear() + "\t" + "SEX: " + this.getSex() + "\t" + "AGE: " + this.getAge();
        for(int i=0; i<61; i++) {
            String cod11 = this.getCodeFromIndex(i).getIcd11Code();
            str = str + " " + "P" + (i+1) + ": " + cod11 + " ";
        }

        return str;
    }

    public String toStringICD10() {
        String str = this.getYear() + "\t" + this.getSex() + "\t" + this.getAge();
        for(int i=0; i<61; i++) {
            String cod10 = this.getCodeFromIndex(i).getIcd10Code();
            str = str + " " + "P" + (i+1) + ": " + cod10 + " ";
        }

        return str;
    }
}
