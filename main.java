import java.util.HashMap;
import java.util.Map;

class Node{
    public char symbol;
    public int weight;
    Node left;
    Node right;

    Node(int weight, char symbol){
        this.symbol = symbol;
        this.weight = weight;
    }
}
class huffmanBinaryTree{
    private Node root;
    HashMap<Character, Integer> frequencyTable;

    public huffmanBinaryTree(String data){
        frequencyTable = new HashMap<Character, Integer>();
    }
    public void encode(String data){
        //Making the frequency table 
        for(int i = 0; i < data.length(); i++){
            char c = data.charAt(i);
            if(frequencyTable.containsKey(c)){
                frequencyTable.put(c, frequencyTable.get(c) + 1);
            }
            else{
                frequencyTable.put(c, 1);
            }
        }

    }
    public void printFreqTable(){
        for(Map.Entry<Character, Integer>entry : frequencyTable.entrySet()){
            System.out.println("Key: " +entry.getKey() + "\tValue: " + entry.getValue());
        }
    } 
}



public class main {
    public static void main(String[] args) {
        huffmanBinaryTree tree = new huffmanBinaryTree("AAA");
        tree.encode("Noo ei kai tas sen kummenpaaä");
        tree.printFreqTable();
    } 
}
