import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Uso: java Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]");
            System.err.println("  Modos: lista | topK <k> | busca <doc1.txt> <doc2.txt>");
            System.exit(1);
        }

        String dirDocumentos = args[0];

        double limiar;
        try {
            limiar = Double.parseDouble(args[1].replace(',', '.'));
        } catch (NumberFormatException e) {
            System.err.println("Erro: limiar deve ser um numero decimal (ex: 0.75)");
            System.exit(1);
            return;
        }

        String modo = args[2].toLowerCase();
        if (!modo.equals("lista") && !modo.equals("topk") && !modo.equals("busca")) {
            System.err.println("Erro: modo invalido. Use: lista | topK | busca");
            System.exit(1);
        }

        int k = 5;
        String docBusca1 = null;
        String docBusca2 = null;

        if (modo.equals("topk")) {
            if (args.length < 4) {
                System.err.println("Erro: modo topK requer o argumento k (ex: topK 5)");
                System.exit(1);
            }
            try {
                k = Integer.parseInt(args[3]);
                if (k <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("Erro: k deve ser um inteiro positivo");
                System.exit(1);
            }
        } else if (modo.equals("busca")) {
            if (args.length < 5) {
                System.err.println("Erro: modo busca requer dois arquivos (ex: busca doc1.txt doc2.txt)");
                System.exit(1);
            }
            docBusca1 = args[3];
            docBusca2 = args[4];
        }

        try {
            executar(dirDocumentos, limiar, modo, k, docBusca1, docBusca2);
        } catch (IOException e) {
            System.err.println("Erro de I/O: " + e.getMessage());
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------

    private static void executar(String dirDocumentos, double limiar, String modo,
                                  int k, String docBusca1, String docBusca2) throws IOException {

        // 1. Carregar stop words
        HashTable<String, Boolean> stopWords = Documento.carregarStopWords("stopwords_pt.txt");

        // 2. Carregar documentos
        Path dirPath = Paths.get(dirDocumentos);
        if (!Files.isDirectory(dirPath)) {
            System.err.println("Erro: diretorio nao encontrado: " + dirDocumentos);
            System.exit(1);
            return;
        }

        List<Documento> documentos = new ArrayList<>();

        if (modo.equals("busca")) {
            // Carrega apenas os dois documentos solicitados
            documentos.add(new Documento(dirPath.resolve(docBusca1), stopWords));
            documentos.add(new Documento(dirPath.resolve(docBusca2), stopWords));
        } else {
            // Carrega todos os .txt do diretorio em ordem alfabetica
            List<Path> arquivos = Files.list(dirPath)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .sorted()
                    .collect(Collectors.toList());
            for (Path arquivo : arquivos) {
                documentos.add(new Documento(arquivo, stopWords));
            }
        }

        if (documentos.isEmpty()) {
            System.out.println("Nenhum documento .txt encontrado em: " + dirDocumentos);
            return;
        }

        // 3. Comparar todos os pares e inserir resultados na AVL
        AVLTree arvore = new AVLTree();
        int totalPares = 0;

        for (int i = 0; i < documentos.size(); i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                Documento d1 = documentos.get(i);
                Documento d2 = documentos.get(j);
                double sim = ComparadorDeDocumentos.cosseno(d1, d2);
                arvore.inserir(sim, new Resultado(d1.getNome(), d2.getNome(), sim));
                totalPares++;
            }
        }

        // Nome da funcao hash usada internamente pelos documentos
        String nomeFuncaoHash = documentos.get(0).getFrequencias().getNomeFuncaoHash();

        // 4. Montar saida
        StringBuilder sb = new StringBuilder();
        sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
        sb.append("Total de documentos processados: ").append(documentos.size()).append('\n');
        sb.append("Total de pares comparados: ").append(totalPares).append('\n');
        // "Função" = "Função"
        sb.append("Função hash utilizada: ").append(nomeFuncaoHash).append('\n');
        // "Métrica" = "Métrica"
        sb.append("Métrica de similaridade: Cosseno\n");
        sb.append('\n');

        if (modo.equals("lista")) {
            sb.append(String.format(Locale.US, "Pares com similaridade >= %.4f:\n", limiar));
            sb.append("---------------------------------\n");
            List<Resultado> resultados = arvore.buscarAcimaDe(limiar);
            // Ordena por similaridade decrescente para exibicao
            resultados.sort((a, b) -> Double.compare(b.similaridade, a.similaridade));
            if (resultados.isEmpty()) {
                sb.append("Nenhum par encontrado acima do limiar.\n");
            } else {
                for (Resultado r : resultados) {
                    sb.append(String.format(Locale.US, "%s <-> %s = %.4f\n",
                            r.nomeDoc1, r.nomeDoc2, r.similaridade));
                }
            }

        } else if (modo.equals("topk")) {
            sb.append("Top ").append(k).append(" pares mais similares:\n");
            sb.append("---------------------------------\n");
            List<Resultado> resultados = arvore.topK(k);
            if (resultados.isEmpty()) {
                sb.append("Nenhum par encontrado.\n");
            } else {
                for (Resultado r : resultados) {
                    sb.append(String.format(Locale.US, "%s <-> %s = %.4f\n",
                            r.nomeDoc1, r.nomeDoc2, r.similaridade));
                }
            }

        } else { // busca
            sb.append("Comparação direta: ")
              .append(docBusca1).append(" <-> ").append(docBusca2).append('\n');
            sb.append("---------------------------------\n");
            List<Resultado> resultados = arvore.topK(1);
            if (!resultados.isEmpty()) {
                Resultado r = resultados.get(0);
                sb.append(String.format(Locale.US, "%s <-> %s = %.4f\n",
                        r.nomeDoc1, r.nomeDoc2, r.similaridade));
            }
        }

        sb.append('\n');
        // "Rotações" = "Rotações"
        sb.append("Rotações simples realizadas na AVL: ")
          .append(arvore.getRotacoesSimples()).append('\n');
        sb.append("Rotações duplas realizadas na AVL: ")
          .append(arvore.getRotacoesDuplas()).append('\n');

        String saida = sb.toString();

        // 5. Imprimir no terminal e gravar em resultado.txt
        System.out.print(saida);

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream("resultado.txt"), StandardCharsets.UTF_8))) {
            pw.print(saida);
        }

        System.out.println("Resultado salvo em resultado.txt");
    }
}
