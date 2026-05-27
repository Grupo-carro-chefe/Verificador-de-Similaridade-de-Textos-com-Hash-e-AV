import java.util.ArrayList;
import java.util.List;

/**
 * Arvore AVL com chave double (similaridade) e lista de Resultado por no.
 * Trata empates de similaridade acumulando resultados no mesmo no.
 */
public class AVLTree {

    private static class No {
        double chave;
        ArrayList<Resultado> pares;
        No esquerdo, direito;
        int altura;

        No(double chave, Resultado resultado) {
            this.chave = chave;
            this.pares = new ArrayList<>();
            this.pares.add(resultado);
            this.altura = 1;
        }
    }

    private No raiz;
    private int rotacoesSimples;
    private int rotacoesDuplas;

    public AVLTree() {
        raiz = null;
        rotacoesSimples = 0;
        rotacoesDuplas = 0;
    }

    // -------------------------------------------------------------------------
    // Utilitarios de altura e balanco
    // -------------------------------------------------------------------------

    private int altura(No no) {
        return no == null ? 0 : no.altura;
    }

    private int fatorBalanco(No no) {
        return no == null ? 0 : altura(no.esquerdo) - altura(no.direito);
    }

    private void atualizarAltura(No no) {
        if (no != null) {
            no.altura = 1 + Math.max(altura(no.esquerdo), altura(no.direito));
        }
    }

    // -------------------------------------------------------------------------
    // Rotacoes (nao contam sozinhas — contagem feita em balancear)
    // -------------------------------------------------------------------------

    /** Rotacao simples a direita: caso Left-Left */
    private No rotacaoDireita(No z) {
        No y  = z.esquerdo;
        No t3 = y.direito;
        y.direito  = z;
        z.esquerdo = t3;
        atualizarAltura(z);
        atualizarAltura(y);
        return y;
    }

    /** Rotacao simples a esquerda: caso Right-Right */
    private No rotacaoEsquerda(No z) {
        No y  = z.direito;
        No t2 = y.esquerdo;
        y.esquerdo = z;
        z.direito  = t2;
        atualizarAltura(z);
        atualizarAltura(y);
        return y;
    }

    // -------------------------------------------------------------------------
    // Balanceamento
    // -------------------------------------------------------------------------

    private No balancear(No no) {
        atualizarAltura(no);
        int fb = fatorBalanco(no);

        // Left-Left: rotacao simples a direita
        if (fb > 1 && fatorBalanco(no.esquerdo) >= 0) {
            rotacoesSimples++;
            return rotacaoDireita(no);
        }

        // Left-Right: rotacao dupla (esquerda no filho, depois direita no no)
        if (fb > 1 && fatorBalanco(no.esquerdo) < 0) {
            rotacoesDuplas++;
            no.esquerdo = rotacaoEsquerda(no.esquerdo);
            return rotacaoDireita(no);
        }

        // Right-Right: rotacao simples a esquerda
        if (fb < -1 && fatorBalanco(no.direito) <= 0) {
            rotacoesSimples++;
            return rotacaoEsquerda(no);
        }

        // Right-Left: rotacao dupla (direita no filho, depois esquerda no no)
        if (fb < -1 && fatorBalanco(no.direito) > 0) {
            rotacoesDuplas++;
            no.direito = rotacaoDireita(no.direito);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    // -------------------------------------------------------------------------
    // Insercao
    // -------------------------------------------------------------------------

    private No inserirRec(No no, double chave, Resultado resultado) {
        if (no == null) {
            return new No(chave, resultado);
        }

        int cmp = Double.compare(chave, no.chave);
        if (cmp < 0) {
            no.esquerdo = inserirRec(no.esquerdo, chave, resultado);
        } else if (cmp > 0) {
            no.direito = inserirRec(no.direito, chave, resultado);
        } else {
            // Empate de similaridade: acumula no mesmo no
            no.pares.add(resultado);
            return no; // estrutura nao mudou, sem balanceamento necessario
        }

        return balancear(no);
    }

    public void inserir(double chave, Resultado resultado) {
        raiz = inserirRec(raiz, chave, resultado);
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    /**
     * Retorna todos os pares com similaridade >= limiar.
     * Aproveita a propriedade BST para podar ramos irrelevantes.
     */
    public List<Resultado> buscarAcimaDe(double limiar) {
        List<Resultado> lista = new ArrayList<>();
        buscarAcimaDe(raiz, limiar, lista);
        return lista;
    }

    private void buscarAcimaDe(No no, double limiar, List<Resultado> lista) {
        if (no == null) return;

        int cmp = Double.compare(no.chave, limiar);
        if (cmp >= 0) {
            // Este no qualifica; a subarvore esquerda pode conter outros >= limiar
            buscarAcimaDe(no.esquerdo, limiar, lista);
            lista.addAll(no.pares);
            buscarAcimaDe(no.direito, limiar, lista);
        } else {
            // Este no nao qualifica; apenas a subarvore direita pode conter valores maiores
            buscarAcimaDe(no.direito, limiar, lista);
        }
    }

    /**
     * Retorna os K pares mais similares em ordem decrescente de similaridade.
     * Se houver menos de K pares na arvore, retorna todos.
     */
    public List<Resultado> topK(int k) {
        List<Resultado> todos = new ArrayList<>();
        traversalDescendente(raiz, todos);
        if (todos.size() <= k) {
            return new ArrayList<>(todos);
        }
        return new ArrayList<>(todos.subList(0, k));
    }

    /** Percurso em ordem decrescente de chave (direita -> raiz -> esquerda). */
    private void traversalDescendente(No no, List<Resultado> lista) {
        if (no == null) return;
        traversalDescendente(no.direito, lista);
        lista.addAll(no.pares);
        traversalDescendente(no.esquerdo, lista);
    }

    /**
     * Retorna os pares com a menor similaridade (no mais a esquerda da arvore).
     * Pode haver mais de um par caso ocorra empate na menor similaridade.
     */
    public List<Resultado> menorSimilaridade() {
        List<Resultado> lista = new ArrayList<>();
        No no = raiz;
        if (no == null) return lista;
        while (no.esquerdo != null) {
            no = no.esquerdo;
        }
        lista.addAll(no.pares);
        return lista;
    }

    // -------------------------------------------------------------------------
    // Estatisticas para o relatorio
    // -------------------------------------------------------------------------

    public int getRotacoesSimples() {
        return rotacoesSimples;
    }

    public int getRotacoesDuplas() {
        return rotacoesDuplas;
    }
}
