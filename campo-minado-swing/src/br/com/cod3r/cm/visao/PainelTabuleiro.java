package br.com.cod3r.cm.visao;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;
import br.com.cod3r.cm.modelo.Tabuleiro;
import br.com.cod3r.cm.visao.BotaoCampo;
public class PainelTabuleiro extends JPanel{

	public PainelTabuleiro(Tabuleiro tabuleiro) {
		
		/*JPanel que tem setado o LayoutManager GridLayout que recebe de parâmetro as mesmas
		 * quantidades de linhas e colunas, ou seja, a quantidadede linhas e colunas desta
		 * grid será de acordo com as linhas e colunas do tabuleiro.*/
		this.setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));
		
		/*o this aponta para o próprio objeto painel que adiciona um BotaoCampo que recebe de parâmetro
		 * um Campo. */
		tabuleiro.paraCadaCampo(c -> this.add(new BotaoCampo(c)));
		tabuleiro.registrarObservador(e -> {
			//TODO mostrar resultado para o usuário.
			/*A PRINCÍPIO NÃO VI NENHUMA DIFERENÇA AO ASSOCIAR A CAIXA DE DIÁLOGO AO JPANEL. A princípio executa a lambda
			 * somente após o método aplicarEstiloExplodir() ser executado, por isso invokeLater, recebe um Runnable que é
			 * uma interface funcional, sobrescreve o run() por uma lambda, sempre em uma lambda o objeto retratado é o mais
			 * interno, neste caso, o método invokeLater(Runnable doRun) recebe um Runnbale anônimo que tem o run() sobrescrito
			 * por lambda.*/
				SwingUtilities.invokeLater(() -> {
					if(e.isGanhou()) {
						JOptionPane.showMessageDialog(this, "Campos Minados marcados e Não Minados Abertos", "Vitória", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(this, "O Campo Expoldiu", "Derrota :(", JOptionPane.WARNING_MESSAGE);
					}
					tabuleiro.reiniciar();
				});
				
		});
	}
}
