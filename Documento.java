import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

/*
  Documento de texto Processado.
  
   Cada Documento encapsula:
        - Nome do arquivo lido
        - HashTable interna que mapeia cada palavra unica do texto (pos
        normalizacao) à sua frequencia no documento
  
   Normalização:
        1. Converte tudo para minusculas
        2. Remove acentos (facilita comparacao, aumenta robustez a
           erros de digitacao)
        3. Remove pontuacao e caracteres nao-alfanumericos
        4. Tokeniza por espacos em branco
        5. Remove stop words a partir de uma lista carregada de stopwords_pt.txt
 */

public class Documento {

    private final String nome;  // Nome do arquivo (ex.: "doc1.txt")

    private final HashTable<String, Integer> frequencias;   // palavra -> contagem

    private int totalTokens;    // Total de tokens validos contados (diagnóstico p/ testeParte1)

    /**
     * Constroi um Documento a partir do caminho de um arquivo e da lista
     * de stop words carregada. A lista eh passada como parametro p/
     * evitar reler o mesmo arquivo a cada Documento criado.
     */
    public Documento(Path caminhoArquivo, HashTable<String, Boolean> stopWords) throws IOException {
        this.nome = caminhoArquivo.getFileName().toString();
        this.frequencias = new HashTable<>();
        this.totalTokens = 0;
        processar(caminhoArquivo, stopWords);
    }

    /**
     * Le o arquivo linha a linha, normaliza cada linha e popula a tabela
     * hash de frequencias. Tokens vazios ou que são stop words são descartados.
     */
    private void processar(Path caminho, HashTable<String, Boolean> stopWords) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String normalizada = removerAcentos(linha.toLowerCase());   // 1) lowercase + 2) remover acentos  
                normalizada = normalizada.replaceAll("[^a-z0-9\\s]", " ");  
                    // 3) remover qualquer caracter que nao seja letra, numero ou espaco
                String[] tokens = normalizada.split("\\s+"); // 4) Tokenizar por espacos em branco
                for (String token : tokens) {
                    if (token.isEmpty()) continue;
                    // 5) descartar stop words
                    if (stopWords.contains(token)) continue;
                    // adiciona/atualiza frequencia na hash
                    Integer freqAtual = frequencias.get(token);
                    if (freqAtual == null) {
                        frequencias.put(token, 1);
                    } else {
                        frequencias.put(token, freqAtual + 1);
                    }
                    totalTokens++;
                }
            }
        }
    }

    /**
     * Remove acentos usando o normalizador unicode do Java (NFD separa o
     * caractere base do diacritico; remover acentos com regex).
     */
    private static String removerAcentos(String texto) {
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Getters para Comparador e Main

    public String getNome() {
        return nome;
    }
    public HashTable<String, Integer> getFrequencias() {
        return frequencias;
    }
    public int getTamanhoVocabulario() {
        return frequencias.size();
    }
    public int getTotalTokens() {
        return totalTokens;
    }

    /*
     Carregando lista de stop words:
       Le o arquivo de stop words e devolve uma HashTable onde cada chave
       eh uma palavra a ser ignorada. Usamos Bool apenas como marcador
       pq a HashTable eh generica em <K,V> e precisamos de algum valor.
      
       IMPORTANTE: as stop words tambem precisam passar pela mesma
       normalizacao usada nos documentos (lowercase + sem acentos).
     */
    
    public static HashTable<String, Boolean> carregarStopWords(String caminhoArquivo) throws IOException {
        HashTable<String, Boolean> set = new HashTable<>();
        for (String linha : Files.readAllLines(Paths.get(caminhoArquivo), StandardCharsets.UTF_8)) {
            String palavra = removerAcentos(linha.trim().toLowerCase());
            if (!palavra.isEmpty()) {
                set.put(palavra, Boolean.TRUE);
            }
        }
        return set;
    }
}
