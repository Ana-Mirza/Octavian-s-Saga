import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Rise extends Task {
    int N;
    int M;
    int P;
    int K;

    ArrayList<String> cardsOwned;
    ArrayList<String> packageCards;
    HashMap<String, Integer> hashMap;

    boolean solution;
    ArrayList<ArrayList<Integer>> InputMat;
    ArrayList<ArrayList<Integer>> referenceMat;
    ArrayList<ArrayList<Integer>> cnfExpression;
    ArrayList<Integer> subsetIndices;
    HashMap<Integer, Integer> column;

    public Rise() {
        InputMat = new ArrayList<>();
        referenceMat = new ArrayList<>();
        cnfExpression = new ArrayList<>();
        cardsOwned = new ArrayList<>();
        packageCards = new ArrayList<>();
	column = new HashMap<>();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();

        /* transform input for sat cnf */
        N = hashMap.size();
        M = InputMat.size();
        K = 0;
        solution = false;

        while (!solution && K < P) {
            K++;
            int cont = 0;
            referenceMat = new ArrayList<>();

            /* create reference matrix - rows are nr of subsets chosen
             *                          - columns are independent subsets */
            for (int i = 0; i < K; i++) {
                referenceMat.add(new ArrayList<>());
                for (int j = 0; j < M; j++) {
                    referenceMat.get(i).add(++cont);
		    column.put(cont, j);
                }
            }

            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
        }
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(System.in);
        hashMap = new HashMap<>();

        /* read input */
        N = scanner.nextInt();
        M = scanner.nextInt();
        P = scanner.nextInt();
        // scanner.next();

        /* read cards owned */
        for (int i = 0; i <= N; i++) {
            String card = scanner.nextLine();
            if (!card.isEmpty())
                cardsOwned.add(card);
        }

        /* read cards in package and place in hashmap if not owned */
        for (int i = 0; i < M; i++) {
            packageCards.add(scanner.nextLine());
            if (!cardsOwned.contains(packageCards.get(packageCards.size() - 1)))
                hashMap.put(packageCards.get(packageCards.size() - 1),
                            hashMap.size() + 1);
        }

        /* read packages given */
        for (int i = 0; i < P; i++) {
            int x = Integer.parseInt(scanner.nextLine());
            InputMat.add(new ArrayList<>());

            for (int j = 0; j < x; j++) {
                String card = scanner.nextLine();

                if (hashMap.containsKey(card))
                    InputMat.get(i).add(hashMap.get(card));
            }
        }

    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        int cont = 0;
        cnfExpression = new ArrayList<>();

        /* at least one element from each row has to be true */
        for (int i = 0; i < K; i++) {
            cnfExpression.add(new ArrayList<>());

            for (int j = 0; j < M; j++) {
                cnfExpression.get(i).add(referenceMat.get(i).get(j));
            }
            cnfExpression.get(i).add(0);
        }

        /* a subset must appear only once */
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < K; j++) {
                for (int k = j + 1; k < K; k++) {
                    cnfExpression.add(new ArrayList<>());
                    cnfExpression.get(cnfExpression.size() - 1).add(-referenceMat.get(j).get(i));
                    cnfExpression.get(cnfExpression.size() - 1).add(-referenceMat.get(k).get(i));
                    cnfExpression.get(cnfExpression.size() - 1).add(0);
                }
            }
        }

        /* a subset cannot be chosen twice */
        for (int i = 0; i < K; i ++) {
            for (int j = 0; j < M; j++) {
                for (int k = j + 1; k < M; k++) {
                    cnfExpression.add(new ArrayList<>());
                    cnfExpression.get(cnfExpression.size() - 1).add(-referenceMat.get(i).get(j));
                    cnfExpression.get(cnfExpression.size() - 1).add(-referenceMat.get(i).get(k));
                    cnfExpression.get(cnfExpression.size() - 1).add(0);
                }
            }
        }

        /* add subsets where node n is present */
        for (int i = 1; i <= N; i++) {
            cnfExpression.add(new ArrayList<>());

            /* search in subsets for variable */
            for (int j = 0; j < InputMat.size(); j++) {
                if (InputMat.get(j).contains(i)) {

                    /* add vales corresponding to subset found */
                    for (int k = 0; k < K; k++) {
                        cnfExpression.get(cnfExpression.size() - 1).add(
                                referenceMat.get(k).get(j));
                    }
                }
            }
            cnfExpression.get(cnfExpression.size() - 1).add(0);
        }

        /* write cnf expression in file */
	FileWriter fw =  new FileWriter("sat.cnf", false);
        fw.write("p cnf " + M * K  + " " + cnfExpression.size() + "\n");
        for (int i = 0; i < cnfExpression.size(); i++) {
            cnfExpression.get(i).forEach(nr -> {
                try {
                    fw.write(nr + " ");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            fw.write("\n");
        }
        fw.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        File file = new File("sat.sol");
        Scanner sc = new Scanner(file);
        int v;
        ArrayList<Integer> list = new ArrayList<>();

        /* check if there is a solution */
        solution = sc.nextBoolean();

        if (!solution)
            return;

        v = sc.nextInt();

        /* read solution */
        while (sc.hasNext())
            list.add(sc.nextInt());

        /* iterate thorough list of literals and find
         * positive literals */
        subsetIndices = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > 0) {

                /* find column on which positive literal is */
		subsetIndices.add(column.get(list.get(i)) + 1);
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {

        /* no solution found */
        if (!solution) {
            System.out.println("False");
            return;
        }

        /* subsets chosen */
        System.out.println(subsetIndices.size());
        subsetIndices.forEach(subset -> System.out.print(subset + " "));
        System.out.print("\n");
    }

    public static void main(String argv[]) throws IOException, InterruptedException {
        Rise rise = new Rise();
        rise.solve();
    }
}
