import java.awt.*;
/*
继承gameparent
基地位置、大小
 */

public class Base extends GameParent {
    int size = 50;//基地图片像素大小
    public Base(String image, int pointX, int pointY, GamePanel gamePanel) {
        super(image, pointX, pointY, gamePanel);
    }
    //位置,存储。。。。
    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, null);
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY, size, size);
    }
}
