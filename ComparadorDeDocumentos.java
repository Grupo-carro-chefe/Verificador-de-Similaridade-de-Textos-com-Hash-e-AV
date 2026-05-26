/**
Calcula a similaridade entre dois Documentos via  Métrica deSimilaridade do Cosseno.
  
     Formula:
  
                         sum_i (a_i * b_i)
            sim = -------------------------------
                 sqrt(sum_i a_i^2) * sqrt(sum_i b_i^2)
  
   a_i e b_i: frequencias da palavra i em cada documento.
   Palavras que aparecem em apenas um dos documentos contribuem com zero (0)
   para o produto escalar (pq a freq. no outro eh zero), entao podemos 
   iterar apenas pelas palavras de um dos documentos para calcular
   o numerador.
 */

public class ComparadorDeDocumentos {

    /** Construtor privado: classe usada apenas via metodos estaticos. */
    private ComparadorDeDocumentos() {}

    /**
     * Calcula a similaridade do cosseno entre dois documentos.
     * Retorna um valor entre 0.0 e 1.0.
     */
    public static double cosseno(Documento d1, Documento d2) {
        HashTable<String, Integer> freq1 = d1.getFrequencias();
        HashTable<String, Integer> freq2 = d2.getFrequencias();

        // 1) produto escalar: iteramos pelas palavras do menor vocabulario
        //    e somamos f1 * f2 quando a palavra existe em ambos.
        HashTable<String, Integer> menor;
        HashTable<String, Integer> maior;
        if (freq1.size() <= freq2.size()) {
            menor = freq1; maior = freq2;
        } else {
            menor = freq2; maior = freq1;
        }

        long produtoEscalar = 0L;
        for (String palavra : menor.keys()) {
            Integer fMenor = menor.get(palavra);
            Integer fMaior = maior.get(palavra);
            if (fMaior != null) {
                produtoEscalar += (long) fMenor * (long) fMaior;
            }
        }

        // 2) normas: raiz da soma dos quadrados das frequencias
        double norma1 = norma(freq1);
        double norma2 = norma(freq2);

        // 3) caso degenerado: documento totalmente vazio apos a
        //    normalizacao (so tinha stop words ou pontuacao). Tratamos
        //    como similaridade zero para evitar divisao por zero.
        if (norma1 == 0.0 || norma2 == 0.0) {
            return 0.0;
        }

        return produtoEscalar / (norma1 * norma2);
    }

    /**
     * Calcula a norma euclidiana de um vetor de frequencias representado
     * por uma HashTable<String, Integer>.
     */
    private static double norma(HashTable<String, Integer> freq) {
        long soma = 0L;
        for (String palavra : freq.keys()) {
            int f = freq.get(palavra);
            soma += (long) f * (long) f;
        }
        return Math.sqrt((double) soma);
    }
}
