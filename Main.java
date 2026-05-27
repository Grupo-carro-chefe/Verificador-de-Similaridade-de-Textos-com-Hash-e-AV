/*
 * ============================================================
 *  Projeto Pratico 2 - Estrutura de Dados II
 *  Verificador de Similaridade de Textos com Hash e AVL
 *
 *  Integrantes do grupo (PREENCHER nome completo e RA):
 *    - Nome Completo - RA: 000000
 *    - Nome Completo - RA: 000000
 *    - Nome Completo - RA: 000000
 * ============================================================
 */

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

        HashTable<String, Boolean> stopWords = Documento.carregarStopWords("stopwords_pt.txt");

        Path dirPath = Paths.get(dirDocumentos);
        if (!Files.isDirectory(dirPath)) {
            System.err.println("Erro: diretorio nao encontrado: " + dirDocumentos);
            System.exit(1);
            return;
        }

        // ---------------------------------------------------------------------
        // Modo busca: formato proprio e enxuto (conforme edital).
        // Compara apenas os dois documentos informados.
        // ---------------------------------------------------------------------
        if (modo.equals("busca")) {
            Documento d1 = new Documento(dirPath.resolve(docBusca1), stopWords);
            Documento d2 = new Documento(dirPath.resolve(docBusca2), stopWords);
            double sim = ComparadorDeDocumentos.cosseno(d1, d2);

            StringBuilder sb = new StringBuilder();
            sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
            sb.append("Comparando: ").append(d1.getNome())
              .append(" <-> ").append(d2.getNome()).append('\n');
            sb.append(String.format(Locale.US, "Similaridade calculada: %.2f\n", sim));
            sb.append("Métrica utilizada: Cosseno\n");

            gravarEImprimir(sb.toString());
            return;
        }

        // ---------------------------------------------------------------------
        // Modos lista / topK: carrega todos os .txt do diretorio
        // ---------------------------------------------------------------------
        List<Path> arquivos = Files.list(dirPath)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                .sorted()
                .collect(Collectors.toList());

        List<Documento> documentos = new ArrayList<>();
        for (Path arquivo : arquivos) {
            documentos.add(new Documento(arquivo, stopWords));
        }

        if (documentos.isEmpty()) {
            System.out.println("Nenhum documento .txt encontrado em: " + dirDocumentos);
            return;
        }

        // Compara todos os pares e insere os resultados na AVL
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

        String nomeFuncaoHash = documentos.get(0).getFrequencias().getNomeFuncaoHash();

        // Cabecalho comum
        StringBuilder sb = new StringBuilder();
        sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
        sb.append("Total de documentos processados: ").append(documentos.size()).append('\n');
        sb.append("Total de pares comparados: ").append(totalPares).append('\n');
        sb.append("Função hash utilizada: ").append(nomeFuncaoHash).append('\n');
        sb.append("Métrica de similaridade: Cosseno\n");
        sb.append('\n');

        if (modo.equals("lista")) {
            sb.append(String.format(Locale.US, "Pares com similaridade >= %.2f:\n", limiar));
            sb.append("---------------------------------\n");
            List<Resultado> resultados = arvore.buscarAcimaDe(limiar);
            resultados.sort((a, b) -> Double.compare(b.similaridade, a.similaridade));
            if (resultados.isEmpty()) {
                sb.append("Nenhum par encontrado acima do limiar.\n");
            } else {
                for (Resultado r : resultados) {
                    sb.append(formatarPar(r));
                }
            }

            sb.append('\n');
            sb.append("Pares com menor similaridade:\n");
            sb.append("---------------------------------\n");
            List<Resultado> menores = arvore.menorSimilaridade();
            for (Resultado r : menores) {
                sb.append(formatarPar(r));
            }

        } else { // topk
            sb.append("Top ").append(k).append(" pares mais similares:\n");
            sb.append("---------------------------------\n");
            List<Resultado> resultados = arvore.topK(k);
            if (resultados.isEmpty()) {
                sb.append("Nenhum par encontrado.\n");
            } else {
                for (Resultado r : resultados) {
                    sb.append(formatarPar(r));
                }
            }
        }

        // Dados de rotacoes da AVL (coletados para o relatorio tecnico)
        sb.append('\n');
        sb.append("Rotações simples realizadas na AVL: ")
          .append(arvore.getRotacoesSimples()).append('\n');
        sb.append("Rotações duplas realizadas na AVL: ")
          .append(arvore.getRotacoesDuplas()).append('\n');

        gravarEImprimir(sb.toString());
    }

    /** Formata uma linha de par no padrao "docA.txt <-> docB.txt = 0.82". */
    private static String formatarPar(Resultado r) {
        return String.format(Locale.US, "%s <-> %s = %.2f\n",
                r.nomeDoc1, r.nomeDoc2, r.similaridade);
    }

    /** Imprime no terminal e grava o mesmo conteudo em resultado.txt (UTF-8). */
    private static void gravarEImprimir(String saida) throws IOException {
        System.out.print(saida);
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream("resultado.txt"), StandardCharsets.UTF_8))) {
            pw.print(saida);
        }
        System.out.println("Resultado salvo em resultado.txt");
    }
}
