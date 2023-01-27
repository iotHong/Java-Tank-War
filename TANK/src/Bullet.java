import java.awt.*;
import java.util.ArrayList;
/*
实现对玩家弹药的控制：
    1.大小、速度、位置、方向
    2.弹药发射后的移动（位置）
    3.弹药打到各个组件的行为：（击中/出界都要消失
                敌方坦克：坦克消失，计数-1
                普通墙：墙消失
                铁墙：墙不消失
                基地：相当铁墙（玩家不能基地）
                边界：相当于铁墙
                …………
 */
public class Bullet extends GameParent {

    int bulletWidth = 2;//弹药像素大小
    int bulletHeight = 3;

    Direction direction;//弹药飞行方向（和坦克头方向一致
    int speed = 8;//弹药飞行速度

    public Bullet(String image, int pointX, int pointY, GamePanel gamePanel, Direction direction) {
        super(image, pointX, pointY, gamePanel);
        this.direction = direction;
    }

    public void bulletMove() {
        switch (direction) {
            case UP -> bulletUpWard();
            case LEFT -> bulletLeftWard();
            case RIGHT -> bulletRightWard();
            case DOWN -> bulletDownWard();
        }
        this.hitWall();//击墙
        this.hitBorderWall();//边界
        this.playerHitBase(); //玩家的子弹不能摧毁基地
        this.outOfBorder();//离开界面
    }

    //子弹移动（所在位置x、y坐标加减速度
    public void bulletUpWard() {pointY -= speed;}

    public void bulletDownWard() {
        pointY += speed;
    }

    public void bulletLeftWard() {
        pointX -= speed;
    }

    public void bulletRightWard() {
        pointX += speed;
    }

    //检测弹药打到敌方坦克
    public void hitEnemy() {
        ArrayList<EnemyTank> enemyArrayList = this.gamePanel.enemyArrayList;
        for(EnemyTank enemy : enemyArrayList) {
            if(this.getRec().intersects(enemy.getRec())) {
               this.gamePanel.enemyArrayList.remove(enemy);
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }

    //检测弹药打到墙（可摧毁就remove）
    public void hitWall() {
        ArrayList<Wall> wallArrayList = this.gamePanel.wallArrayList;
        for(Wall wall : wallArrayList) {
            if(this.getRec().intersects(wall.getRec()) && wall.destroyableWall) {
                this.gamePanel.wallArrayList.remove(wall);
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }
    //打到边界
    public void hitBorderWall() {
        ArrayList<Wall> borderWallList = this.gamePanel.borderWallList;
        for(Wall wall : borderWallList) {
            if(this.getRec().intersects(wall.getRec())) {
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }
    //玩家打到基地
    public void playerHitBase() {
        ArrayList<Base> baseArrayList = this.gamePanel.baseArrayList;
        for(Base base : baseArrayList) {
            if(this.getRec().intersects(base.getRec())) {
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }

    //删除已出屏幕的子弹,防止overflow
    public void outOfBorder() {
        if(pointX < 0 || pointX + bulletWidth > this.gamePanel.getWidth()
                || pointY < 0 || pointY + bulletHeight > this.gamePanel.getHeight()) {
            this.gamePanel.removeBulletList.add(this);
        }
    }

    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, null);
        this.bulletMove();
        this.hitEnemy();
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY, bulletWidth, bulletHeight);
    }
}
