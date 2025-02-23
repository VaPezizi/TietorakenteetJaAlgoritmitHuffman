import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Comparator;

/*
 * Apuna käytetyt koodit: https://www.geeksforgeeks.org/huffman-coding-java/
 * Otin myös ohjeita Robert Laforen kirjoittamasta "Data structures & algorithms in Java 2nd edition" kirjasta. Esimerkiksi PriorityQuen käyttöön sain idean sieltä
 * Aluksi en halunnut mistään ottaa mallia, mutta jäin pahasti jumiin ja tarvitsin apua. 
 * 
 */

//Luokka solmuille
class Node{
    public char symbol;     //Solmun edustama merkki
    public int weight;      //Montako kertaa merkki näkyy
    Node left;              //En nyt varma miten javassa tämä menee, mutta taitaa viittauksena tulla tänne
    Node right;             //Oli miten oli, tässä vasen- ja oikea lapsisolmu

    Node(int weight, char symbol){
        this.symbol = symbol;
        this.weight = weight;
    }
}


class huffmanBinaryTree{
    private Node root;      //Määritellään puun juurisolmu
    private String originalString;
    HashMap<Character, String> codedTable;  //Hajautus taulu kertomaan jokaiselta kirjaimelta niiden koodatun muodon
    HashMap<Character, Integer> freqTable;  //Hajautus taulu kertomaan montako kertaa mikäkin kirjain löytyy merkkijonosta

    public huffmanBinaryTree(String data){
        originalString = data;
        codedTable = new HashMap<Character, String>();
        freqTable = new HashMap<Character, Integer>();
       
        //Luodaan "taajuustaulukko" annetusta merkkijonosta
        for (char ch : data.toCharArray()) {
            if(freqTable.get(ch) == null){      //Nähdään kirjain ensimmäistä kertaa, lisätään se hajautustauluun
                freqTable.put(ch, 1);
            }else{
                freqTable.put(ch, freqTable.get(ch) + 1);   // Kirjain nähty uudestaan ja inkrementoidaan arvoa
            }
        }
        PriorityQueue<Node> priorityQue = new PriorityQueue<>(Comparator.comparingInt(node -> node.weight));    //Tehdään prioriteetti jono, jolla saadaan järjestys solmujen käsittelyyn
                                                                                                                //Jono järjestetään solmujen "painon" mukaan
        for(Map.Entry<Character, Integer>entry : freqTable.entrySet()){
            priorityQue.add(new Node(entry.getValue(), entry.getKey()));        //Lisätään arvot jonoon
        }

//------------------------------
        //Puun rakennus
//------------------------------

        //Kirjassa ohjeissa oli, että tehdään erikseen jokin "tree" olio joita lisätään priority Queen, mutta "Puita":han nämä solmutkin ovat, ehkä toteutus on kirjan tekijällä,
        //                                                                                                                                      vain erilainen, en tiedä.
        while(priorityQue.size() > 1){      //Suoritetaan kunnes jono on käyty läpi (Juuri solmua lukuunottamatta)
            Node left = priorityQue.poll(); //Otetaan kaksi pienimmän painon solmua, ja lisätään ne uuden solmun lapsiksi (En ole varma onko oikeat Suomenkieliset termit, mutta menkööt) 
            Node right = priorityQue.poll();

            Node parent = new Node(left.weight + right.weight, '\0');   //Tässä lisäys uuteen "sisäiseen solmuun", symbooli on '\0', koska se ei esitä mitään merkkiä
            parent.left = left;
            parent.right = right;

            priorityQue.add(parent);    //Lisätään uusi solmu takaisin jonoon
        }
        root = priorityQue.poll();      //Asetetaan jonoon jäänyt solmu, juuri solmuksi

        //Luodaan koodit, argumentiksi juurisolmu ja tyhjä stringi, johon "bitit" kasataan
        generateCodes(root, "");

        printEncoded();     //Tulostetaan muutettu Stringi
        printCodes(root, new StringBuilder());

    }

    //Rekursiivinen funktio, ottaa aina solmun ja stringin, johon kasataan koodia jokaisella kierroksella rekursiota
    private void generateCodes(Node node, String code) {
        if (node == null) return;   //Jos puu on tyhjä, poistu

        if (node.left == null && node.right == null) {      //Jos käsiteltävä solmu on lehti solmu, lisätään sen koodi listaan.
            codedTable.put(node.symbol, code);
        }

        //System.out.println(node.symbol + " : " + code );  <- ihan mielenkiintoinen katsoa miten puuta käydään läpi :D

        //Rekursio molempiin solmuihin.
        //Vasemmalle liikkuessa puussa, lisätään stringiin 0, oikealle liikkuessa lisätään 1
        generateCodes(node.left, code + "0");       
        generateCodes(node.right, code + "1");
    }


    //Tästä nyt ei niin paljon sanottavaa. Käy annetun merkkijono läpi, ja jokaisen merkin kohdalla lisää uuteen merkkijonoon sen merkin koodatun version 
    public String encode(){
        String encodedString = "";          //Olisi voinut käyttää StringBuilder:ia tähän myös, mutta ei mielestäni ole tarpeellinen tässä tilanteessa
        for(char c : originalString.toCharArray()){
            encodedString += codedTable.get(c);     //Haetaan kooditaulusta merkin binääri muotoa vastaavan version ja liitetään rakennettavaan merkkijonoon
        }
        return encodedString;
    }
    public void printEncoded(){
        System.out.println(encode());
    }

    public String decode(String encodedString){
        Node current = root;    //Asetetaan käsiteltäväksi solmuksi juurisolmu
        String decodedString = "";      //Merkkijono johon kasataan purettu koodi
        for(char bit : encodedString.toCharArray()){
            current = (bit == '0') ? current.left : current.right;  //Jos käsiteltävä bitti on 0, asetetaan käsiteltävä solmu vasempaan lapsi solmuun, muuten oikeaan.

            if(current.left == null && current.right == null){      //Jos käsiteltävä solmu on lehtisolmu, lisätään sen symbooli purettuun merkkijonoon
                decodedString += current.symbol; 
                current = root;
            }
        }
        return decodedString;
    }

    public void printCodes(Node root, StringBuilder code){
        if(root == null)
            return;
        if(root.symbol != '\0'){
            System.out.println(root.symbol + ": " + code);
        }
        if(root.left != null){
            printCodes(root.left, code.append('0'));
            code.deleteCharAt(code.length() - 1);
        }
        if(root.right != null){
            printCodes(root.right, code.append('1'));
            code.deleteCharAt(code.length() -1);
        }
    }

    public void printFreqTable(){
        for(Map.Entry<Character, Integer>entry : freqTable.entrySet()){
            System.out.println("Key: " +entry.getKey() + "\tValue: " + entry.getValue());
        }
    } 
}



public class main {
    public static void main(String[] args) {
        huffmanBinaryTree tree = new huffmanBinaryTree("SUSIE SAYS IT IS EASY");

        tree.printFreqTable();
        System.out.println(tree.encode());
        System.out.println(tree.decode(tree.encode()));
    } 
}
