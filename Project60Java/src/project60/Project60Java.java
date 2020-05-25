package project60;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamPanel;


//oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
public class Project60Java {
	public static void main(String[] args) {
		new Frame();
	}
}
//oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo

//ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg
class Frame extends JFrame implements WebcamPanel.Painter{
	
	static final long serialVersionUID = 1L;
	private WebcamPanel webcampanel;
	private CameraHandler cameraHandler;
	private JLabel[] etq;
	private int e;
	
	public Frame() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		setBounds(0, 0, 658, 525);
		setResizable(false);

		cameraHandler=new CameraHandler();
		webcampanel = new WebcamPanel(cameraHandler.dameCamara());
		webcampanel.setPainter(this);
		webcampanel.start();
		
		etq=new JLabel[16];	
		for(int i=0; i<=15; i++){
			etq[i]=new JLabel();
			etq[i].setFont(new Font("Arial", Font.PLAIN, 32));
			etq[i].setForeground(Color.RED);
		}
		webcampanel.setLayout(null);
		for(int y=40; y<=400; y=y+120){
			for(int x=110; x<=480;x=x+120){
				webcampanel.add(etq[e]).setBounds(x, y, 70, 40);
				etq[e].setText("--.-");
				e++;		
			}			
		}
		add(webcampanel);
		setVisible(true);
		
		new ReceiveString(new LookingPortsConfigure().getSelectedPort(), etq);
	}
	public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {		
		Graphics2D g = image.createGraphics();
		
		g.setColor(new Color(0,0,100,100));
		g.fillRect(0, 0, 80, 480);
		g.fillRect(560,0,80,480);
		
		g.setColor(Color.GREEN);
		for(int h=120; h<480; h=h+120){
			g.drawLine(80, h, 560, h);
		}		 
		for(int v=80; v<=560; v=v+120){
			g.drawLine(v, 0, v, 480);
		} 
		g.dispose();
		panel.getDefaultPainter().paintImage(panel, image, g2);
	}
	public void paintPanel(WebcamPanel panel, Graphics2D g2) {
		panel.getDefaultPainter().paintPanel(panel, g2);		
	}
}
//ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg

//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
class CameraHandler{
	private Webcam camara;
	private ArrayList<Webcam> camaraLista;
	private WebcamPicker picker;
	
	private Object[] objetos ;
	private int numeroCamara;
	private Object objetoSeleccion;
	
	public CameraHandler(){
		objetos = ListaCamaras().toArray();
		
		objetoSeleccion = JOptionPane.showInputDialog(null,"Select a camera",
				"CAMERA", JOptionPane.QUESTION_MESSAGE, null, objetos,"Your selection");
		
		if(objetoSeleccion==null){System.exit(0);}
		
		for(numeroCamara=0; numeroCamara<objetos.length; numeroCamara++){
			if(objetoSeleccion==objetos[numeroCamara]){
				break;
			}
		}	
		seleccionaCamara(numeroCamara);
	}	
	public  ArrayList<Webcam> ListaCamaras(){
		int numeroDeCamaras;
		picker = new WebcamPicker();
		numeroDeCamaras=picker.getItemCount();
		for(int g=0; g<numeroDeCamaras; g++){
			picker.getItemAt(g);
		}			
		camaraLista=new ArrayList<Webcam>(Webcam.getWebcams());
		return camaraLista;
	}
	public void seleccionaCamara(int seleccion){
		camara = camaraLista.get(seleccion);
		camara.setViewSize(new Dimension(640,480));		
		camara.open();		
	}
	public Webcam dameCamara(){
		return camara;
	}
}
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class LookingPortsConfigure{
	
	private SerialPort[] serialPortArray;	
	private String[] dataPort;	
	private Object selectedPortObject;	
	private SerialPort serialPort;
	private String selection;

	public LookingPortsConfigure() {	
		serialPortArray= SerialPort.getCommPorts();			
		dataPort = new String[serialPortArray.length];
				
		if(serialPortArray.length==0) {
			JOptionPane.showMessageDialog(null, "No busy comm port", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		for (int i = 0; i < serialPortArray.length; ++i) {
			dataPort[i]=i + "  "+
			serialPortArray[i].getSystemPortName()+ "  " +
			serialPortArray[i].getDescriptivePortName()+ "  " +
			serialPortArray[i].getPortDescription();
		}
		
		selectedPortObject = JOptionPane.showInputDialog(null,"Choose port", "PORTS", JOptionPane.QUESTION_MESSAGE, null, dataPort,"Seleccione");
				
		if(selectedPortObject==null){
			JOptionPane.showMessageDialog(null, "You did not select port", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else {
			selection=selectedPortObject.toString().substring(0,1);			
			serialPort=SerialPort.getCommPort(serialPortArray[Integer.parseInt(selection)].getSystemPortName());		
			serialPort.setComPortParameters(9600, 8, 1, 0);		//port configuration
			serialPort.openPort();	
		}
	}		
	public SerialPort getSelectedPort() {
		return serialPort;
	}
}
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class ReceiveString implements SerialPortPacketListener{

	private StringBuilder stringBuilder;
	private String stringReceived;	
	private JLabel[] etq;
	private int i=0;
	
	public ReceiveString(SerialPort serialPort, JLabel[] etq) {			
		serialPort.addDataListener(this);
		this.etq=etq;
	}	
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}		
	public void serialEvent(SerialPortEvent event) {
		stringBuilder = new StringBuilder();
		byte[] newData = event.getReceivedData();

		for (int i = 0; i < newData.length; ++i) {
			if((char)newData[i]!='\n') {
				stringBuilder.append((char)newData[i]);
			}else {
				break;
			}
		}	
		stringReceived=stringBuilder.toString();
		stringReceived.trim();
		try {
			double d=Double.parseDouble(stringReceived);
			if(d==999) {
				i=-1;	
			}else {
				if(d==888) {
					i=i+1;
					etq[i].setText("ERR");	
				}else {
					i=i+1;
					etq[i].setText("" + d/10);	
				}
			}
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Disconnect and reconnect Arduino", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}	
		stringBuilder=null;
	}	
	public int getPacketSize() {
		return 4;							//Number of characters you receive from Arduino. Use a fixed number of characters always.
	}						
}
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh

