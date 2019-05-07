import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Fly{
	public static int flyCount;
	
	protected boolean isAlive,hasSound;
	protected int xPos,yPos;
	protected int neX,neY;
	protected int duration;
	protected BufferedImage flyIm;
	protected Thread t;

	Fly(){
		flyCount++;
		setAlive(true);
		setFlyIm("fly.png");
		setxPos(0);
		setyPos(0);
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public Image getFlyIm() {
		return flyIm;
	}

	public void setFlyIm(String flyIm) {
		try {
			this.flyIm = ImageIO.read(new File(flyIm));
		} catch (IOException e) {
		}
	}

	public void destroy(){
		setAlive(false);
		setFlyIm("blood.png");
		flyCount--;
	}
	
	
}
