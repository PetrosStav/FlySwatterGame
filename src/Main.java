import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends JFrame implements ActionListener,MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int points;
	public static boolean endGame = false;
	public static boolean swatterisdown = false;
	private AudioPlayer bgMusic;
	private AudioPlayer swatterSound;
	private AudioPlayer triplekill;
	private AudioPlayer doublekill;
	
	static ArrayList<Fly> flies = new ArrayList<>();
	
	Timer tm = new Timer(40, this);

	public Main(){
		setTitle("Fly Swatter v1.0");
		//background
		try {
			BufferedImage img = ImageIO.read(new File("bg.jpg"));
			JLabel background = new JLabel(new ImageIcon(img));
			add(background);
		} catch (IOException e) {
			setBackground(Color.WHITE);
			e.printStackTrace();
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		setVisible(true);
		setResizable(false);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon("swatter.png").getImage(),
				new Point(0,0),"custom cursor"));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();	//the dimension of the user's screen
		setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);	//center the window in the user's screen
		addMouseListener(this);
		loadSounds();
		tm.start();
	}
	
	private void loadSounds() {
		bgMusic = new AudioPlayer("fly.wav");
		swatterSound = new AudioPlayer("swatter.wav");
		doublekill = new AudioPlayer("doublekill.wav");
		triplekill = new AudioPlayer("holyshit.wav");
		bgMusic.play(true);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		synchronized (flies) {
			for(Fly f : flies){
				if(!f.isAlive){ //draw blood first so that is at the bottom layer
					g2d.drawImage(f.getFlyIm(), f.getxPos(), f.getyPos(), f.getFlyIm().getWidth(null)/4, f.getFlyIm().getHeight(null)/4, null);
				}
			}
			for(Fly f : flies){ //draw alive flies so that they are at the top layer
				if(f.isAlive){
					g2d.drawImage(f.getFlyIm(), f.getxPos(), f.getyPos(), f.getFlyIm().getWidth(null)/4, f.getFlyIm().getHeight(null)/4, null);
				}
			}
		}
		g2d.drawString("Score : " + points, 50, 550);
		if(swatterisdown){
			setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					new ImageIcon("swatter.png").getImage(),
					new Point(0,0),"custom cursor"));
			swatterisdown = false;
		}
		if(endGame){
			g2d.drawString("You won!", 50, 575);
		}
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	public static void main(String[] args) {
		new Main();
		synchronized (flies) {
			setUpFlies();
		}
	}
	
	public static void setUpFlies(){
		Random r = new Random();
		Fly f;
		for(int i = 0 ; i<20 ; i++){
			f = new Fly();
			f.setxPos(r.nextInt(600) + 100);
			f.setyPos(r.nextInt(400) + 100);
			flies.add(f);
		}
	}
	
	public void startFlying(){
		//TODO make better moving for flies -- remove f.duration?
		Random r = new Random();
		int currX = -1;
		int currY = -1;
		int direction = -1;
		synchronized (flies) {
			for(Fly f : flies){
				if(f.isAlive){
					currX = f.getxPos();
					currY = f.getyPos();
					if(f.duration == 0){
						direction = r.nextInt(4);
						f.duration = r.nextInt(9) + 1;
						if(direction == 0){
							f.neX = 0;
							f.neY = -5;
						}else if(direction == 1){
							f.neX = 5;
							f.neY = 0;
						}else if(direction == 2){
							f.neX = 0;
							f.neY = 5;
						}else if(direction == 3){
							f.neX = -5;
							f.neY = 0;
						}
						if(f.neX + currX > 700 || currX + f.neX < 100){
							f.setxPos(currX - f.neX);
							f.duration = 0;
						}else{
							f.setxPos(currX + f.neX);
						}
						if(f.neY + currY > 500 || currY + f.neY < 100){
							f.setyPos(currY - f.neY);
							f.duration = 0;
						}else{
							f.setyPos(currY + f.neY);
						}
					}else{
						if(f.neX + currX > 700 || currX + f.neX < 100){
							f.setxPos(currX - f.neX);
						}else{
							f.setxPos(currX + f.neX);
						}
						if(f.neY + currY > 500 || currY + f.neY < 100){
							f.setyPos(currY - f.neY);
						}else{
							f.setyPos(currY + f.neY);
						}
						f.duration--;
					}
				}
			}
			repaint();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!endGame){
			startFlying();
			if(Fly.flyCount == 0){
				bgMusic.close();
				swatterSound.close();
				doublekill.close();
				triplekill.close();
				if(!endGame){
					new AudioPlayer("unstoppable.wav").play(false);
				}
				endGame = true;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!swatterisdown && !endGame){
			setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					new ImageIcon("swatter_2.png").getImage(),
					new Point(0,0),"custom cursor"));
			swatterisdown = true;
		}
		swatterSound.play(false);
		int currPoints = points;
		for(Fly f : flies){
			if(f.getxPos() > e.getX() - 30 && f.getxPos() < e.getX() + 30 && f.getyPos() > e.getY() - 30 && f.getyPos() < e.getY() + 30){
				if(f.isAlive){
					f.destroy();
					points++;
				}
			}
		}
		if(points - currPoints == 2){
			doublekill.play(false);
		}else if(points - currPoints >= 3){
			triplekill.play(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
