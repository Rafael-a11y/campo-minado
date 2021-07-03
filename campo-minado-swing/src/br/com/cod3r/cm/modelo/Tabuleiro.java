package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {
	
	private final int linhas;
	private final int colunas;
	private final int minas;
	
	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();
	/*Construtor do tabuleiro, constrói um tabuleiro com quantidades de campos e minas de acordo com os dados passados*/
	public Tabuleiro(int linhas, int colunas, int minas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.minas = minas;
		
		gerarCampos();
		associarOsVizinhos();
		sortearMinas();
	}
	/*Recebe uma lambda Consumer que possui um parâmetro de tipo Campo e aplica o devido
	 * processamento para todos os campos do tabuleiro, ou seja, a lambda Consumer fornecida
	 * irá alterar todos os campos.*/
	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}
	
	public void registrarObservador(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(boolean resultado) {
		observadores.stream().forEach(o -> o.accept(new ResultadoEvento(resultado)));
	}
	
	public void abrir(int linha, int coluna) {
			campos.parallelStream().filter(c -> c.getLinha() == linha
					&& c.getColuna() == coluna).
			findFirst().ifPresent(c -> c.abrir());
	}
	
	
	public void alternarMarcacao(int linha, int coluna) {
		campos.parallelStream().filter(c -> c.getLinha() == linha
				&& c.getColuna() == coluna).
		findFirst().ifPresent(c -> c.alternarMarcacao());
	}
	/*Adiciona um campo para cada linha e coluna do tabuleiro*/
	private void gerarCampos() {
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservador(this);
				campos.add(campo);
			}
		}
	}
	/*Percorre a lista de campos e cada campo da lista percorre os outros campos, verificando se estes estão de acordo
	 * com as condições estabelecidas no método adicionarVizinho*/
	private void associarOsVizinhos() {
		for(Campo c1 : campos) {
			for(Campo c2 : campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
	/*Sorteia as minas de forma randônica*/
	private void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasArmadas = campos.stream().filter(minado).count();
		}while(minasArmadas < minas);
	}
	
	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}
	/*Reinicia todos os campos*/
	public void reiniciar() {
		campos.stream().forEach( c -> c.reiniciar());
		sortearMinas();
	}
	
	public final int getLinhas() {
		return linhas;
	}

	public final int getColunas() {
		return colunas;
	}

	public final int getMinas() {
		return minas;
	}

	/*Verifica se um evento ocorreu no campo, caso o evento seja de explosão, chama o método
	 * de mostrar todas as minas.*/
	@Override public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if(evento == CampoEvento.EXPLODIR) {
			this.mostrarMinas();
			this.notificarObservadores(false);	
		}
		else if(this.objetivoAlcancado()) {
			System.out.println("Ganhou... :)");
			notificarObservadores(true);
		}
	}
	
	/*Seta como aberto todas as minas que estão minadas neste tabuleiro*/
	private void mostrarMinas() {
		campos.stream().filter(c -> c.isMinado()).filter(c -> !c.isMarcado()).forEach(c -> c.setAberto(true));
	}
}
