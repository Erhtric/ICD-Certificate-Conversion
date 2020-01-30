import java.util.List;

public class Certificate {

    private int index = 0;

    private String year;
    private String sex;
    private String age;

    private Code[] P = new Code[61];

    public Certificate(String str) {
        String codes = str.substring(10);
        setBaseData(str);

        // Solo per debug
        codes = codes.concat("\t");
        System.out.println(codes);

        int index = 0;
        readBlocks(codes);

    }

    private void readBlocks(String codes) {
        String current = "";

        for(int i=0; i<codes.length()-1; i++){
            String ch = String.valueOf(codes.charAt(i));
            String nx = String.valueOf(codes.charAt(i+1));

            if(!ch.equals("\t")) {
                // Altrimenti vi può essere "...\t\t..."
                current = current.concat(ch);

                if(nx.equals("\t")) {
                    // Nel momento in cui si incontra un "\t" termina un blocco, quindi si salva nell'opportuna sezione e si resetta l'attuale valore
                    setP(current);
                    current = "";
                    index++;
                }

            } else if(ch.equals("\t") && nx.equals("\t")) {
                setP("");
                index++;
            }
        }
    }

    public void setP(String str) {
        this.P[index] = new Code(str);
    }

    // Da completare...
    public Code getP1_1() {
        return this.P[0];
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
        return P[60];
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAge(String age) {
        this.age = age;
    }

}
