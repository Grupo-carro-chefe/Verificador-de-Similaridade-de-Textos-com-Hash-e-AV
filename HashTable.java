import java.util.ArrayList;

/**
 * Tabela Hash genérica implementada manualmente.
 *
 * Suporta duas funções de dispersão:
 *   HASH_DJB2       - baseada no algoritmo DJB2 (multiplicação por 33)
 *   HASH_POLINOMIAL - baseada no método de Horner com base 31
 *
 * Tratamento de colisões: encadeamento separado (chaining).
 * Cada bucket é uma lista encadeada de entradas.
 */
public class HashTable<K, V> {

    public static final int HASH_DJB2       = 1;
    public static final int HASH_POLINOMIAL = 2;

    // -------------------------------------------------------------------------
    // Estrutura interna
    // -------------------------------------------------------------------------

    private static class Entrada<K, V> {
        K chave;
        V valor;
        Entrada<K, V> proxima;

        Entrada(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    // -------------------------------------------------------------------------
    // Atributos
    // -------------------------------------------------------------------------

    private final Entrada<K, V>[] buckets;
    private final int capacidade;
    private final int funcaoHash;
    private int tamanho;

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    /**
     * @param capacidade  número de buckets da tabela (use um primo para melhor distribuição)
     * @param funcaoHash  HASH_DJB2 ou HASH_POLINOMIAL
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacidade, int funcaoHash) {
        this.capacidade  = capacidade;
        this.funcaoHash  = funcaoHash;
        this.buckets     = (Entrada<K, V>[]) new Entrada[capacidade];
        this.tamanho     = 0;
    }

    /** Construtor padrão: capacidade 1009 (primo) e DJB2. */
    public HashTable() {
        this(1009, HASH_DJB2);
    }

    // -------------------------------------------------------------------------
    // Funções de dispersão
    // -------------------------------------------------------------------------

    /**
     * DJB2: hash = 5381; para cada char c -> hash = hash * 33 + c
     * Boa distribuição para strings curtas/médias.
     */
    private int hashDJB2(K chave) {
        String s = chave.toString();
        long hash = 5381;
        for (int i = 0; i < s.length(); i++) {
            hash = ((hash << 5) + hash) + s.charAt(i); // hash * 33 + c
        }
        return (int) (hash & 0x7fffffffL) % capacidade;
    }

    /**
     * Polinomial (Horner, base 31): hash = sum(c_i * 31^i)
     * Mesma base usada pelo String.hashCode() da JVM — útil para comparação no relatório.
     */
    private int hashPolinomial(K chave) {
        String s = chave.toString();
        long hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = hash * 31 + s.charAt(i);
        }
        return (int) (hash & 0x7fffffffL) % capacidade;
    }

    /** Seleciona a função de dispersão configurada e retorna o índice do bucket. */
    private int indice(K chave) {
        if (funcaoHash == HASH_POLINOMIAL) {
            return hashPolinomial(chave);
        }
        return hashDJB2(chave);
    }

    // -------------------------------------------------------------------------
    // Interface pública (mantida igual ao stub do Nicolas)
    // -------------------------------------------------------------------------

    /**
     * Insere ou atualiza o valor associado à chave.
     * Se a chave já existe no bucket, sobrescreve o valor.
     */
    public void put(K chave, V valor) {
        int i = indice(chave);
        Entrada<K, V> e = buckets[i];

        // Percorre o encadeamento procurando a chave
        while (e != null) {
            if (e.chave.equals(chave)) {
                e.valor = valor;
                return;
            }
            e = e.proxima;
        }

        // Não encontrou: insere no início do encadeamento (O(1))
        Entrada<K, V> nova = new Entrada<>(chave, valor);
        nova.proxima = buckets[i];
        buckets[i]   = nova;
        tamanho++;
    }

    /**
     * Retorna o valor associado à chave, ou null se não existir.
     */
    public V get(K chave) {
        int i = indice(chave);
        Entrada<K, V> e = buckets[i];
        while (e != null) {
            if (e.chave.equals(chave)) return e.valor;
            e = e.proxima;
        }
        return null;
    }

    /** Retorna true se a chave existe na tabela. */
    public boolean contains(K chave) {
        return get(chave) != null;
    }

    /** Retorna o número de entradas na tabela. */
    public int size() {
        return tamanho;
    }

    /** Retorna todas as chaves armazenadas. */
    public Iterable<K> keys() {
        ArrayList<K> lista = new ArrayList<>();
        for (Entrada<K, V> bucket : buckets) {
            Entrada<K, V> e = bucket;
            while (e != null) {
                lista.add(e.chave);
                e = e.proxima;
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // Estatísticas para o relatório técnico
    // -------------------------------------------------------------------------

    /**
     * Retorna um array com o número de elementos em cada bucket.
     * Use para gerar o gráfico de distribuição exigido no relatório.
     *
     * Exemplo de uso:
     *   int[] dist = tabela.getDistribuicaoBuckets();
     *   // dist[i] = quantidade de elementos no bucket i
     */
    public int[] getDistribuicaoBuckets() {
        int[] dist = new int[capacidade];
        for (int i = 0; i < capacidade; i++) {
            int count = 0;
            Entrada<K, V> e = buckets[i];
            while (e != null) {
                count++;
                e = e.proxima;
            }
            dist[i] = count;
        }
        return dist;
    }

    /**
     * Retorna o total de colisões (buckets com mais de um elemento,
     * contando cada elemento extra como uma colisão).
     */
    public int getTotalColisoes() {
        int colisoes = 0;
        for (Entrada<K, V> bucket : buckets) {
            if (bucket != null && bucket.proxima != null) {
                Entrada<K, V> e = bucket.proxima;
                while (e != null) {
                    colisoes++;
                    e = e.proxima;
                }
            }
        }
        return colisoes;
    }

    /** Retorna o identificador da função de hash em uso (1 = DJB2, 2 = Polinomial). */
    public int getFuncaoHash() {
        return funcaoHash;
    }

    /** Retorna o nome da função de hash em uso (para exibir na saída). */
    public String getNomeFuncaoHash() {
        return funcaoHash == HASH_POLINOMIAL ? "hashPolinomial" : "hashDJB2";
    }
}
