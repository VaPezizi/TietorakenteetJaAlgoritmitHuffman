import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Map;
import java.util.Comparator;

/*
 * Apuna käytetyt koodit: https://www.geeksforgeeks.org/huffman-coding-java/
 * Otin myös ohjeita Robert Laforen kirjoittamasta "Data structures & algorithms in Java 2nd edition" kirjasta. Esimerkiksi PriorityQuen käyttöön sain idean sieltä
 * Aluksi en halunnut mistään ottaa mallia, mutta jäin pahasti jumiin ja tarvitsin ideoita. 
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

        
        //printCodes(root, new StringBuilder());

        //print();

        //System.out.println(data);
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

    //Alkuperäinen koodien printtaus. Rekursiivinen funktio
    //Ei ole enää tarpeellinen kun paljon helpompikin tapa printata löytyi
    //Mutta jätinpähän nyt kommentoituna pois tämän
    //Tämä tulee melekin suoraa Geeks For Geeksin sivulta joka on linkattuna koodin yläosassa, toisenlaisessa toteutuksessa, tämä olisi voinut olla tarpeellinen

    /*public void printCodes(Node root, StringBuilder code){
        if(root == null)
            return;
        else if(root.symbol != '\0'){
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
    }*/
    public void printCodes(){
        System.out.println("\n-----( Codes )-----\n");
        for(Map.Entry<Character, String>entry : codedTable.entrySet()){
            Character key = entry.getKey();
            String value = entry.getValue();

            if(key.equals('\n')){
                System.out.println("Char: " + "NL" + " Coded: " + value);
            }else if(key.equals(' ')){

                System.out.println("Char: " + "SP" + " Coded: " + value);
            }else{
                System.out.println("Char: " + entry.getKey() + "\tCoded: " + value);
            }
        }
    }

    public void printFreqTable(){
        System.out.println("\n------( Frequency Table )------\n");
        for(Map.Entry<Character, Integer>entry : freqTable.entrySet()){
            if(entry.getKey().equals('\n')){
                System.out.println("Key: NL" + " Value: " + entry.getValue());     //Laitoin vaan näin, että rivin vaihdon symbooli on NL
            }else if(entry.getKey().equals(' '))
                System.out.println("Key: SP" + " Value: " + entry.getValue());     //Sama homma tässä, mutta vain SP välilöynnille
            else
                System.out.println("Key: " +entry.getKey() + "\tValue: " + entry.getValue());
        }
    } 
    //Tein nyt tämmöisen print metodin johon tulee kaikki, kun en keksinyt mikä olisi paras tapa tehdä tulostus
    public void print(){
        printCodes();
        printFreqTable();

        System.out.println("\n-----( Encoded )-----\n");
        System.out.println(encode());

        System.out.println("\n-----( Decoded )-----\n");
        System.out.println(decode(encode()));
    }
}



public class main {
    public static void main(String[] args) {

        //Tajusin äsken tässä 1.3 lauantaina, että vaatimuksissa oli käyttäjältä inputtia, niin main funktio on aika nopeasti tehty
        //tässä jälkikäteen. Muutin myös miten arvoja printataan (Välilyönti = SP ja Rivivaihto = NL)

        Scanner scanner = new Scanner(System.in);
        StringBuilder s = new StringBuilder("");
        System.out.print("Enter message to encode (Leave empty line to confirm and quit): ");
        /* 
        while (scanner.hasNextLine()) {
            String temp = scanner.nextLine();
            if(temp.equals("c")){
                break;
            }else if(temp.equals("q"))
                return;
            else
                s.append(temp).append('\n');      //Vähän ikävän näköinen ratkaisu
        }*/
        String line;
        while(!(line = scanner.nextLine()).isEmpty()){
            s.append(line).append('\n');                    //Löysin paremman ratkaisun, tietenkin nyt ongelmana on, että tyhjiä rivejä ei voi koodata
                                                            //Silti parempi, kuin että 'c\n' tai 'q\n' ei voi koodata
        }
        //System.out.println(s);
        huffmanBinaryTree tree = new huffmanBinaryTree(s.toString().trim());
        tree.print();

        scanner.close();

        /*System.out.println("\n-----( Encoded )-----");
        tree.printEncoded();*/

        //System.out.println("\n-----( Decoded )-----");
        //System.out.println(tree.decode(tree.encode()));
        /*huffmanBinaryTree tree = new huffmanBinaryTree("Morjensta pöytään Jukka");

        tree.printFreqTable();
        System.out.println(tree.encode());
        System.out.println(tree.decode(tree.encode()));
        */
    } 
}
