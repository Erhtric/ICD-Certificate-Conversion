import java.util.ArrayList;

public class MapNode {
    private Code nodeCode;
    private ArrayList<MapNode> childrenNodes;

    public MapNode(){
        this.nodeCode=null;
        this.childrenNodes=new ArrayList<>();
    }

    public MapNode(Code nodeCode){
        this.nodeCode=nodeCode;
        this.childrenNodes=new ArrayList<>();
    }

    public Code getNodeCode() {
        return nodeCode;
    }

    //Aggiunge un nuovo oggetto Code all'albero usato per la conversione
    public void addCode(Code newCode){
        for(int i=0;i<childrenNodes.size();i++){
            if(newCode.icd10IsUnder(childrenNodes.get(i).getNodeCode())){
                childrenNodes.get(i).addCode(newCode);
                return;
            }
        }
        childrenNodes.add(new MapNode(newCode));
    }

    //Riceve un oggetto Code contente un codice icd10, ne ritorna uno nuovo contente il codice in icd10, il codice in icd11 corrispondente (se esiste) ed il tipo di conversione effettuata
    //Ritorna null e manda un messaggio in output se non trova il codice (DA SOSTITUIRE CON UNA EXCEPTION)
    public Code findAndReturnCode(Code code){
        for(int i=0;i<childrenNodes.size();i++){
            if(code.getIcd10Code().equals(childrenNodes.get(i).getNodeCode().getIcd10Code())){
                return childrenNodes.get(i).getNodeCode().clone();
            } else if(code.icd10IsUnder(childrenNodes.get(i).getNodeCode())){
                return childrenNodes.get(i).findAndReturnCode(code);
            }
        }
        return null;
    }
}
