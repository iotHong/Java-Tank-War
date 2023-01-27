import java.awt.*;
/*
由于提供图片素材的位置、存储路径等操作重复出现，
于是抽象为一个类，构建抽象类GameParent
之后其他继承即可
 */
public abstract class GameParent {

    public Image image;//图片素材路径
    public int pointX; //像素坐标
    public int pointY;
    public GamePanel gamePanel;//接口

    public GameParent(String image, int pointX, int pointY, GamePanel gamePanel) {
            this.image = Toolkit.getDefaultToolkit().getImage(image);
            this.pointX = pointX;
            this.pointY = pointY;
            this.gamePanel = gamePanel;
    }

    public abstract void paintSelf(Graphics graphics);

    public abstract Rectangle getRec();
}
