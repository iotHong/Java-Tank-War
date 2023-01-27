import java.awt.*;
import java.util.ArrayList;
/*
    在Bullet的基础上加：
                玩家：玩家输，玩家坦克消失
                基地：玩家输，基地消失
             …………
 */
public class EnemyBullet extends Bullet{

    public EnemyBullet(String image, int pointX, int pointY, GamePanel gamePanel, Direction direction) {
        super(image, pointX, pointY, gamePanel, direction);
    }

    //子弹打到基地
    public void hitBase() {
        ArrayList<Base> baseArrayList = this.gamePanel.baseArrayList;
        for(Base base : baseArrayList) {
            if(this.getRec().intersects(base.getRec())) {
                this.gamePanel.baseArrayList.remove(base);
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }
    //子弹打到玩家坦克
    public void hitPlayer() {
        ArrayList<Tank> playerList = this.gamePanel.playerList;
        for(Tank player : playerList) {
            if(this.getRec().intersects(player.getRec())) {
                this.gamePanel.playerList.remove(player);
                player.alive = false;
                this.gamePanel.removeBulletList.add(this);
                break;
            }
        }
    }



    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, null);
        this.bulletMove();
        this.hitPlayer();
        this.hitBase();
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY, bulletWidth, bulletHeight);
    }
}
