import java.util.Iterator;
import java.util.Map;

public class CodeConverter {
    private MapNode mappingTree;
    private boolean useSubclassGood;

    public CodeConverter(String mapFile){
        mappingTree=new MapNode();
        useSubclassGood=false;
        fillTreeFromFile(mapFile);
        setSubclassGoodInTree(mappingTree);
    }

    public CodeConverter(String mapFile, boolean useSubclassGood){
        mappingTree=new MapNode();
        this.useSubclassGood=useSubclassGood;
        fillTreeFromFile(mapFile);
        if(useSubclassGood){
            setSubclassGoodInTree(mappingTree);
        }
    }

    private void fillTreeFromFile(String mapFile){

    }

    //Ricevuto MapNode, cambia convType dei Code di tutti i suoi nodi da Subclass a SubclassGood
    private void setSubclassGoodInTree(MapNode tree){
        if(tree.getNodeCode()!=null){
            if(tree.getNodeCode().getConvType()==Code.ConversionType.Subclass&&!multipleICD11InTree(tree.getNodeCode(),mappingTree)){
                tree.getNodeCode().setConversionTypeToSubclassGood();
            }
        }
        Iterator iterator= tree.childrenIterator();
        while (iterator.hasNext()){
            setSubclassGoodInTree((MapNode) iterator.next());
        }
    }

    //ritorna vero se trova lo stesso valore ICD11 di code nell'albero in un nodo diverso da quello di code.
    private boolean multipleICD11InTree(Code code, MapNode tree){
        if(tree.getNodeCode()!=null&&tree.getNodeCode().getIcd11Code().equals(code.getIcd11Code())&&tree.getNodeCode()!=code){
            return true;
        }else{
            Iterator iterator= tree.childrenIterator();
            while (iterator.hasNext()){
                if(multipleICD11InTree(code,(MapNode) iterator.next())){
                    return true;
                }
            }
            return false;
        }
    }

    public Code convert(Code code){
        return mappingTree.findAndReturnCode(code);
    }

    public boolean usesSubclassGood(){
        return useSubclassGood;
    }

}
