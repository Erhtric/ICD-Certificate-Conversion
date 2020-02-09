import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
    //Se non trova il codice lancia una NoSuchElementException
    public Code findAndReturnCode(Code code) throws NoSuchElementException {
        for(int i=0;i<childrenNodes.size();i++){
            if(code.getIcd10Code().equals(childrenNodes.get(i).getNodeCode().getIcd10Code())){
                return childrenNodes.get(i).getNodeCode().clone();
            } else if(code.icd10IsUnder(childrenNodes.get(i).getNodeCode())){
                return childrenNodes.get(i).findAndReturnCode(code);
            }
        }
        throw new NoSuchElementException();
    }

    public Iterator childrenIterator(){
        return new ChildrenIterator(this);
    }

    //iterator per i figli del nodo
    private static class ChildrenIterator implements Iterator {
        private MapNode m;
        private int n;

        ChildrenIterator(MapNode m){
            this.m=m;
            n=0;
        }

        public boolean hasNext(){
            return n<m.childrenNodes.size();
        }

        public Object next() throws NoSuchElementException {
            if(!hasNext()){
                throw new NoSuchElementException();
            }else{
                return m.childrenNodes.get(n++);
            }
        }
    }
}
