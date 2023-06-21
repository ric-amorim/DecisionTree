import java.util.*;
import java.io.*;

public class ID32 {
    public List<String> state; // guardar os diferentes estados(possiveis) de cada coluna
    public double[] number;// numero de vezes que os atributes ocorrem
    public List<List<String>> csv;
    public int rows;
    public int cols;
    public int targetAt;

    ID32(List<List<String>> read, int line, int col, int targetAt) {
        rows = line;
        csv = read;
        cols = col;

    }

    public DNode<String> id3(ID32 dados) {
        DNode<String> root = null;
        dados.values(dados.cols - 1);
        for (int i = 0; i < dados.rows; i++) {
            if (dados.state.size() == 1) {
                return root = new DNode<String>(dados.state.get(i), 0);
            }
        }
        if (dados.cols <= 2) {
            return root = new DNode<String>(dados.getMostCommonValue(dados), 0);
        } else {
            int bestAtribute = dados.bestAttribute(dados);
            dados.values(bestAtribute);
            root = new DNode<String>(dados.csv.get(0).get(bestAtribute), dados.state.size());
            for (int i = 0; i < dados.state.size(); i++) {
                root.addFilho(new DNode<String>(dados.state.get(i), 1));
                List<List<String>> temp = dados.MakeChildren(dados.csv, dados.state.get(i), bestAtribute);
                ID32 novosdados = new ID32(temp, temp.size(), temp.get(0).size(), targetAt);
                if (temp.size() == 1) {
                    DNode<String> novo = new DNode<String>(dados.getMostCommonValue(dados), 0);
                    ((DNode<String>) root.filhos[i]).addFilho(novo);
                } else {
                    temp = novosdados.removeColumn(temp, bestAtribute);
                    ID32 novosdados2 = new ID32(temp, temp.size(), temp.get(0).size(), targetAt);
                    ((DNode<String>) root.filhos[i]).addFilho(id3(novosdados2));
                }
            }
        }
        return root;
    }

    public int bestAttribute(ID32 data) {
        double max = Integer.MIN_VALUE;
        int pos = 0;
        for (int i = 1; i <= data.cols - 2; i++) {
            double tmp = data.infogain(data,cols-1, i);
            if (tmp>= max) {
                pos = i;
                max = tmp;
            }
    }
        return pos;
    }

    public String getMostCommonValue(ID32 examples) {
        int maior = 0;
        int index = 0;
        values(cols - 1);
        int count[] = new int[state.size()];
        for (List<String> example : examples.csv) {
            for (int i = 0; i < state.size(); i++) {
                if (example.get(cols - 1).equals(state.get(i))) {
                    count[i]++;
                }
            }
        }
        for (int i = 0; i < state.size(); i++) {
            if (count[i] > maior) {
                maior = count[i];
                index = i;
            }
        }
        return state.get(index);
    }

    public void values(int columns) {
        state = new ArrayList<>();
        number = new double[rows];
        int n = 1;
        for (int i = 1; i < rows; i++) { // for para obter os valores dos diferentes estados e o numero de vezes que
                                         // ocorre
            String value = csv.get(i).get(columns);
            if (!state.contains(value)) {
                state.add(value);
                number[n] = 1;
                n++;
            } else {
                number[state.indexOf(value) + 1]++;
            }

        }

    }

    public double entropy(int columns) {
        double res = 0.0;
        values(columns);
        double max = (double) rows - 1;
        for (int i = 1; i <= state.size(); i++) { // formula para calcular a entropry
            double probrability = number[i] / max;
            // System.out.println(number[i] +" "+ max+" "+probrability);
                res -= probrability * (Math.log(probrability) / Math.log(2));
        }
        return Math.abs(res);
    }

    public double infogain(ID32 data,int target, int columns) {
        double res = 0.0;
        double f = 0.0;
        double e = data.entropy(target);
        data.values(columns-1);
        double max = (double) rows - 1;
        for (int j = 0; j < state.size(); j++) {
            List<List<String>> split = MakeChildren(data.csv, state.get(j), columns);
            ID32 newSplit = new ID32(split, split.size(), split.get(0).size(), target);
            newSplit.values(columns-1);


            double probrability = data.number[j+1]/ max;
            if(probrability>0)
                res += probrability * newSplit.entropy(columns-1);
        }
        f = e - res;
        return Math.abs(f);
    }

    public List<List<String>> removeColumn(List<List<String>> csv, int columnToRemove) {
        List<List<String>> modifiedCsv = new ArrayList<>();

        for (List<String> row : csv) {
            List<String> modifiedRow = new ArrayList<>(row);
            modifiedRow.remove(columnToRemove);
            modifiedCsv.add(modifiedRow);
        }

        return modifiedCsv;
    }

    public List<List<String>> MakeChildren(List<List<String>> csv, String goal, int classAttribute) {
        List<List<String>> subTree = new ArrayList<>();
        subTree.add(csv.get(0));
        for (int i = 0; i < rows; i++) {
            if (csv.get(i).get(classAttribute).equals(goal))
                subTree.add(csv.get(i));
        }
        return subTree;
    }

    public void printTree(DNode<String> root, String prefix) {
        if (root == null) {
            return;
        }

        // System.out.println(prefix+ "| ");
        System.out.println(prefix + "|__ " + root.valor);

        if (root.filhos != null) {
            for (Object filho : root.filhos) {
                if (filho instanceof DNode) {
                    printTree((DNode<String>) filho, prefix + "   ");
                }
            }
        }
    }

    public static void roundNumber(List<List<String>> csv, int lines, int columns) {
        for (int i = 1; i < lines; i++) {
            try {
                for (int j = 1; j < columns; j++) {
                    Double doub = Double.parseDouble(csv.get(i).get(j));
                    int temp = doub.intValue();
                    String s = String.valueOf(temp);
                    csv.get(i).set(j, s);
                }
            } catch (NumberFormatException ex) {
                continue;
            }
        }
    }

    public static String classifyExample(DNode<String> root, List<String> exemplo, List<List<String>> read) {
        int count = 0;
        for (Object filho : root.filhos) {
            if (filho instanceof DNode) {
                count++;
            }
        }
        if (count == 0) {
            return root.valor; // Return the label/class when there are no more children
        }

        String attributeName = root.valor; // Attribute at the current node
        int attributeIndex = -1;

        // Encontrar o index do
        for (int i = 0; i < read.get(0).size(); i++) {
            if (read.get(0).get(i).equals(attributeName)) {
                attributeIndex = i;
                break;
            }
        }

        if (attributeIndex == -1) {
            return null; // Attribute not found in the example
        }

        String attributeValue = exemplo.get(attributeIndex); // Get the attribute value from the example

        try{
            double attributeNumericValue = Double.parseDouble(attributeValue);
            int roundedValue = (int) Math.round(attributeNumericValue);
            attributeValue = String.valueOf(roundedValue);
        } catch (NumberFormatException ex) {
            
        }
        // Traverse the children nodes based on the attribute value
        for (Object filho : root.filhos) {
            if (filho instanceof DNode) {
                DNode<String> childNode = (DNode<String>) filho;
                if (childNode.valor.equals(attributeValue)) {
                    return classifyExample((DNode<String>) childNode.filhos[0], exemplo, read); // Recursively classify
                                                                                                // the example using the
                    // child node
                }
            }
        }

        return null; // Unable to classify the example
    }

    public static void main(String args[]) throws FileNotFoundException,
            IOException {
        Scanner in = new Scanner(System.in);
        List<List<String>> read = new ArrayList<>();
        System.out.print("file.csv: ");
        String file = in.next();
        try (BufferedReader buf = new BufferedReader(new FileReader(file))) { // ler o csv
            String s;
            while ((s = buf.readLine()) != null) {
                String[] values = s.split(",");
                read.add(Arrays.asList(values));
            }

        }

        int lines = read.size();
        int columns = read.get(0).size();
        roundNumber(read, lines, columns);
        // for(List<String> i : read)
        // System.out.println(i);
        ID32 temp = new ID32(read, lines, columns, columns);
        DNode<String> root = temp.id3(temp);
        temp.printTree(root, "");
        System.out.println();
        System.out.println("Adicionar exemplo à base de dados? (s/n)");
        String awns = in.next();
        while (awns.equals("s") || awns.equals("sim") || awns.equals("Sim")) {
            System.out.println("Exemplo:");
            String input = in.next();
            String[] values = input.split(",");
            List<String> exemplo = new ArrayList<>();
            for (int i = 0; i < values.length; i++) {
                exemplo.add(values[i]);
            }
            // List<String> example = Arrays.asList("15", "rainy", "65", "70", "TRUE");
            String classification = classifyExample(root, exemplo, read);
            System.out.println("Classification: " + classification);
            System.out.println("Outro Exemplo?");
            awns = in.next();
        }
        // --------------------------------------------------------------------------------------
        // System.out.println(temp.id3(lines,11,xd));
        // List<List<String>> subTree = temp.removeColumn(read, 9);
        // for (List<String> s : subTree)
        // System.out.println(s + " " + subTree.size());
        // System.out.println(temp.bestAttribute(temp));
        // System.out.println(read.get(0).get(temp.bestAttribute(temp)));
        // System.out.println(temp.getMostCommonValue(temp, columns - 1));
        // temp.values(5);
        // for (int i = 0; i < temp.state.size(); i++) {
        // System.out.println(temp.state.get(i) + " " + temp.number[i + 1]);
        // }
        // List<List<String>> subTree =temp.MakeChildren(read,"No",1);
        // for(List<String> s : subTree)
        // System.out.println(s);
        // System.out.println(" ");
        // List<List<String>> subTree2 =temp.MakeChildren(read,"Yes",1);
        // for(List<String> s : subTree2)
        // System.out.println(s);
        // ID3 temp2 = new ID3(subTree, subTree.size(),subTree.get(0).size());
        // int ola = temp.bestAttribute(temp);
        // System.out.println(ola);
        // for (int i = 1; i < columns-1; i++)
        // System.out.println(read.get(0).get(i) + " " + temp.infogain(12,i));

    }
}
