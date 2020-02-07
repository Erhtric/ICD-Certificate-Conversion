import java.io.IOException;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CodeConverter {
    private MapNode mappingTree;
    private boolean useSubclassGood;

    public CodeConverter(String mapFile) throws IOException {
        mappingTree=new MapNode();
        useSubclassGood=false;
        fillTreeFromFile(mapFile);
        setSubclassGoodInTree(mappingTree);
    }

    public CodeConverter(String mapFile, boolean useSubclassGood) throws IOException {
        mappingTree=new MapNode();
        this.useSubclassGood=useSubclassGood;
        fillTreeFromFile(mapFile);
        if(useSubclassGood){
            setSubclassGoodInTree(mappingTree);
        }
    }

    private void fillTreeFromFile(String mapFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mapFile), "UTF-8"));
        reader.readLine();//scarta la prima riga di legenda
        String data;
        String icd10;
        String icd11;
        String convType;
        int count, i;
        while (reader.ready()) {
            data=reader.readLine();
            icd10="";
            icd11="";
            convType="";
            count=0;
            i=0;
            //salta 10ClassKind e 10DepthInKind
            while(count<2){
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }
            //leggi icd10Code
            while(count<3){
                icd10+=data.charAt(i);
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }
            //salta icd10Chapter, icd10Title, 11ClassKind, 11DepthInKind e ICD-11 FoundationURI
            while(count<8){
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }
            //leggi Linearization (releaseURI)
            while(count<9){
                icd11+=data.charAt(i);
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }
            //salta icd11Code, icd11Chapter, icd11Title e chapterMatch
            while(count<13){
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }
            //leggi Relation
            while(count<14){
                convType+=data.charAt(i);
                i++;
                if(data.charAt(i)=='\t'){
                    count++;
                }
            }

            System.out.println(icd10+" - "+icd11+" - "+convType);
        }
        reader.close();
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
