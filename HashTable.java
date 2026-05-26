import java.util.ArrayList;

/**
 * !!! STUB TEMPORARIO !!!
 *
 * Não é implementação final, apenas temporária
 * Versão final duas funcoes de dispersao, tratamento de colisoes
 * manual, etc... (Parte 2)
 *
 * Esta versao usa encadeamento separado bem simples, de tamanho fixo e
 * uma unica funcao de hash simples.
 *
 * OBSERVAÇÃO: APAGAR ESTE ARQUIVO antes de juntar com a implementacao
 * final da Parte 2. A interface publica (put/get/contains/size/keys) foi
 * combinada em grupo e deve ser mantida na versao final.
 */

public class HashTable<K, V> {

    private static class Entrada<K, V> {
        K chave;
        V valor;
        Entrada<K, V> proxima;
        Entrada(K c, V v) { chave = c; valor = v; }
    }

    private static final int CAPACIDADE = 1024;
    private final Entrada<K, V>[] buckets;
    private int tamanho;

    @SuppressWarnings("unchecked")
    public HashTable() {
        this.buckets = (Entrada<K, V>[]) new Entrada[CAPACIDADE];
        this.tamanho = 0;
    }

    private int indice(K chave) {
        return (chave.hashCode() & 0x7fffffff) % CAPACIDADE;
    }

    public void put(K chave, V valor) {
        int i = indice(chave);
        Entrada<K, V> e = buckets[i];
        while (e != null) {
            if (e.chave.equals(chave)) {
                e.valor = valor;
                return;
            }
            e = e.proxima;
        }
        Entrada<K, V> nova = new Entrada<>(chave, valor);
        nova.proxima = buckets[i];
        buckets[i] = nova;
        tamanho++;
    }

    public V get(K chave) {
        int i = indice(chave);
        Entrada<K, V> e = buckets[i];
        while (e != null) {
            if (e.chave.equals(chave)) return e.valor;
            e = e.proxima;
        }
        return null;
    }

    public boolean contains(K chave) {
        return get(chave) != null;
    }

    public int size() {
        return tamanho;
    }

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
}
