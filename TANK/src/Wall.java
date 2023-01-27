import java.awt.*;
/*
墙：
    1.大小、两种类型、位置
 */
public class Wall extends GameParent {


    int wallWidth;//墙像素大小
    int wallHeight;

    boolean destroyableWall = true;//墙的类型       true是砖墙

    public Wall(String image, int pointX, int pointY, GamePanel gamePanel, boolean destroyable, int width, int height) {
        super(image, pointX, pointY, gamePanel);
        this.destroyableWall = destroyable;
        this.wallWidth = width;
        this.wallHeight = height;
    }

    public int getWallWidth() {
        return wallWidth;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, gamePanel);
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY,this.getWallWidth(), this.getWallHeight());
    }
}
