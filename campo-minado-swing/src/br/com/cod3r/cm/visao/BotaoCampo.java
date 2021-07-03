package br.com.cod3r.cm.visao;
// E se eu trocar o MouseListener pór ActionListener? Lembrar de verificar se o botão direito é o de valor 2
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import br.com.cod3r.cm.modelo.Campo;
import br.com.cod3r.cm.modelo.CampoEvento;
import br.com.cod3r.cm.modelo.CampoObservador;
import jdk.jfr.Experimental;
public class BotaoCampo extends JButton implements CampoObservador, MouseListener{

	private static final long serialVersionUID = 1L;
	
	/*Objeeto Color do pacote java.awt, serve para definir as cores dos componentes, os valores
	 * inteiros servem para indicar o tom de Red, Green e Blue, os valores vão de 0 até 255,
	 * caso o inteiro seja 0, significa ausência daquela cor, quanto maior o número, mais 
	 * clara é a tonalidade, e o inverso significa um tom mais escuro. Lembrando que estas 3 cores
	 * são as cores primárias, ou seja, todas as outras cores nascem da mistura de tonalidades 
	 * destas 3, a constante TEXTO_VERDE será de cor verde com um tom mais voltado para o escuro,
	 * não terá a presença ddo vermelho ou do azul, caso o inteiro seja 255, seria um verde bem
	 * claro.*/
	private final Color BG_PADRAO = new Color(184, 184, 184);
	private final Color BG_MARCAR = new Color(8, 179, 247);
	private final Color BG_EXPLODIR = new Color(189, 66, 68);
	private final Color TEXTO_VERDE = new Color(0, 100, 0);
	
	@Experimental
	private final ImageIcon imagemExplosao = new ImageIcon("recursos/icones/explosao5.jpg");
	
	private Campo campo;

	/*Usamos o método da instância campo registrarObservador(CampoObservador observador) para  
	 * registrar esta classe como um ouvinte na lista de observadores do campo.*/
	public BotaoCampo(Campo campo) {
		this.campo = campo;
		/*setBorder é herdado de JComponent, o BorderFactory é uma classe que possui o método
		 * compartilhado createBevelBorder(int type) que retorna um Border de acordo com o 
		 * número passado, aparentemente só processa 0, 1 e 2*/
		this.setBackground(BG_PADRAO);
		this.setOpaque(true);
		this.setBorder(BorderFactory.createBevelBorder(0));
		this.addMouseListener(this);
		campo.registrarObservador(this);
	}
	/*Por implementar CampoObservador, precisa sobrescrever o método eventoOcorreu(Campo campo,
	 * CampoEvento evento), nesta versão do método, chamamos os métodos privados da classe de
	 * acordo com a enum passada.
	 * O campo e o evento são recebidos e de acordo com o evento passado, é aplicado sobre o
	 * campo o devido efeito visual.
	 * Lembra de quando o campo minado chama o método eventoOcorreu para todos os Campos
	 * Observadores que ele possui, passando ele mesmo de parâmetro e o tipo do evento ?
	 * Então, agora os devidos efeitos visuais são aplicados.*/
	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		switch(evento) {
			case ABRIR : 
				this.aplicarEstiloAbrir();
				break;
			case MARCAR :
				this.aplicarEstiloMarcar();
				break;
			case EXPLODIR :
				this.aplicarEstiloExplodir();
				break;
			default : aplicarEstiloPadrao();
			break;
		}
		/*Serve para redesenhar o componente na tela.*/
		SwingUtilities.invokeLater(() -> {
			this.repaint(); // De Component
			this.validate(); //De Container
		});
	}

	private void aplicarEstiloPadrao() {
		// TODO Auto-generated method stub
		this.setBackground(BG_PADRAO);
		this.setBorder(BorderFactory.createBevelBorder(0));
		this.setText("");
	}

	private void aplicarEstiloExplodir() {
		// TODO Auto-generated method stub
		this.setBackground(BG_EXPLODIR);
		this.setForeground(Color.WHITE);
		this.setText("X");
	}

	private void aplicarEstiloMarcar() {
		// TODO Auto-generated method stub
		this.setBackground(BG_MARCAR);
		this.setForeground(Color.BLACK);
		this.setText("M");
	}

	private void aplicarEstiloAbrir() {
		// TODO Auto-generated method stub
		//Deixa a borda cinza.
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		if(campo.isMinado()) {
			this.setBackground(BG_EXPLODIR);
			return;
		}
		this.setBackground(BG_PADRAO);
		
		switch(campo.minasNaVizinhanca()) {
		
		case 1: 
			/*Seta a cor do texto do campo visual, caso fosse setBackground(), setaria a 
			 * cor do botão. Caso seja 1, será verde, caso 2 azul, caso 3 amarelo, caso 4, 5 e 6 será vermelho, se nenhum
			 * será rosa.*/
			this.setForeground(TEXTO_VERDE);
			break;
		case 2:
			this.setForeground(Color.BLUE);
			break;
		case 3:
			this.setForeground(Color.YELLOW);
			break;
		case 4:
		case 5:
		case 6:
			this.setForeground(Color.RED);
			break;
		default:
			this.setForeground(Color.PINK);
		}
		/*Como o método camposNaVizinhanca() retorna um número, concatena-se com uma String
		vazia para haver uma conversão implícita.*/
		String valor = !this.campo.vizinhancaSegura() ? this.campo.minasNaVizinhanca() + "" :
			"";
		//O valor retornado é usado para servir de texto do botão.
		this.setText(valor);
		
	}
	
	//Interface dos eventos do mouse. A diferença para o ActionListener, é que o botão seta
	//um ActionListener que sobrescreve actionPerformed, já aqui o botão é quem implementa a
	//interface MouseListner do pacote java.awt.event.MouseListener.
	
	@Override
	public void mousePressed(MouseEvent e) {
		//0 = nenhum botão, 1 = botão esquerdo, 2 = botão direito.
		if(e.getButton() == 1) this.campo.abrir();
		else if(e.getButton() == 3) campo.alternarMarcacao();
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
