import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
/*
敌方坦克的一系列行为：
        1.位置、移动方向
        2.发射弹药
        3.是否撞到玩家
 */
public class EnemyTank extends Tank {

    int moveTimes = 0;//移动次数
    int fireType = 0;//
    private boolean fireCoolDown = true;
    //敌军坦克
    public EnemyTank(String image, int pointX, int pointY, GamePanel gamePanel,
                     String upImg, String leftImg, String rightImg, String downImg, int fireType) {
        super(image, pointX, pointY, gamePanel, upImg, leftImg, rightImg, downImg);
        this.fireType = fireType;
    }

    //敌军坦克移动方向-》随机
    public Direction getRandomDirection() {
        Random random = new Random();
        int randDirection = random.nextInt(4);
        return switch (randDirection) {
            case 0 -> Direction.UP;
            case 1 -> Direction.LEFT;
            case 2 -> Direction.RIGHT;
            default -> Direction.DOWN;
        };
    }
    //移动与改变方向
    public void enemyMove() {
        enemyFire();
        if(moveTimes > 30) {
            direction = this.getRandomDirection();
            moveTimes = 0;
        } else  {
            moveTimes++;
        }

        switch (direction) {
            case UP -> tankUpWard();
            case LEFT -> tankLeftWard();
            case RIGHT -> tankRightWard();
            case DOWN -> tankDownWard();
        }
        crashPlayer();
    }

    //敌军坦克攻击
    public void enemyFire() {
        Point p = getTankHeadPoint();
        if(fireType == 0) {
            Random random = new Random();
            int rand = random.nextInt(400);
            if (rand < 8) {
                this.gamePanel.bulletArrayList.add(new EnemyBullet("images/Bullet/enemyBullet.png",
                        p.x, p.y, this.gamePanel, direction));
            }
        } else if (fireType == 1) {
            if(fireCoolDown) {
                this.gamePanel.bulletArrayList.add(new EnemyBullet("images/Bullet/enemyBullet.png",
                        p.x, p.y, this.gamePanel, direction));
                new FireCoolDown().start();
            }
        }
    }

    //若敌军坦克撞上玩家，玩家判为输
    public void crashPlayer() {
        ArrayList<Tank> playerList = this.gamePanel.playerList;
        for(Tank player : playerList) {
            if(this.getRec().intersects(player.getRec())) {
                this.gamePanel.playerList.remove(player);
                player.alive = false;
                break;
            }
        }
    }
    //敌军坦克发射炮弹间隙，不能不间断发炮，发一次得停8000ms
    class FireCoolDown extends Thread {
        public void run() {
            fireCoolDown = false;
            try {
                int fireInterval = 8000;
                Thread.sleep(fireInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fireCoolDown = true;
            //this.stop();
        }
    }

    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, null);
        enemyMove();
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY, tankWidth, tankHeight);
    }
}
