import java.awt.*;
import java.util.ArrayList;
/*
坦克：
    1.位置、速度、方向、是否被消灭（存在
    2.位置+方向+移动=新位置+方向图
    3.发射弹药:弹药方向、图片、位置
    4.坦克到特殊位置：两种墙、边界、基地》》不能前行
 */
public abstract class Tank extends GameParent{

    public int tankWidth = 32;//坦克像素大小
    public int tankHeight = 32;
    public int speed = 2;//移动速度
    public boolean alive = false;//生命变量，是否被消灭

    public Direction direction = Direction.UP;//移动方向
    private String upImg;//定义坦克转向用的图片
    private String leftImg;
    private String rightImg;
    private String downImg;

    public void setImage(String image) {
        this.image = Toolkit.getDefaultToolkit().getImage(image);
    }

    public Tank(String image, int pointX, int pointY, GamePanel gamePanel,
                String upImg, String leftImg, String rightImg, String downImg) {
        super(image, pointX, pointY, gamePanel);
        this.upImg = upImg;
        this.leftImg = leftImg;
        this.rightImg = rightImg;
        this.downImg = downImg;
    }

    //tank不能连续不停地发射子弹，时间间隔为800ms(冷却时间
    private boolean fireCoolDown = true;//在冷却状态
    private int fireInterval = 800;


    //检测碰撞-----------------------------------------------------------------------
    //坦克撞墙检测
    public boolean tankHitWall(int x, int y) {
        ArrayList<Wall> wallArrayList = this.gamePanel.wallArrayList;
        Rectangle next = new Rectangle(x, y, tankWidth, tankHeight);
        for(Wall wall : wallArrayList) {
            if(next.intersects(wall.getRec())) {
                return true;
            }
        }
        return false;
    }
    //坦克不能直接穿过基地（相当于墙
    public boolean tankHitBase(int x, int y) {
        ArrayList<Base> baseArrayList = this.gamePanel.baseArrayList;
        Rectangle next = new Rectangle(x, y, tankWidth, tankHeight);
        for(Base base : baseArrayList) {
            if(next.intersects(base.getRec())) {
                return true;
            }
        }
        return false;
    }
    //边界和坦克的碰撞检测
    public boolean reachBorder(int x, int y) {
        //出界返回true
        ArrayList<Wall> borderArrayList = this.gamePanel.borderWallList;
        Rectangle next = new Rectangle(x, y, tankWidth, tankHeight);
        for(Wall wall : borderArrayList) {
            if(next.intersects(wall.getRec())) {
                return true;
            }
        }
        return false;
    }

    //坦克方向改变以及位置改变------------------------------------------------
    //向上
    public void tankUpWard() {
        setImage(upImg);
        direction = Direction.UP;
        if(!tankHitWall(pointX, pointY - speed) && !reachBorder(pointX, pointY - speed) && !tankHitBase(pointX, pointY - speed)) {
            pointY -= speed;
        }
    }
    //向左
    public void tankLeftWard() {
        setImage(leftImg);
        direction = Direction.LEFT;
        if(!tankHitWall(pointX - speed, pointY) && !reachBorder(pointX - speed, pointY) && !tankHitBase(pointX - speed, pointY)) {
            pointX -= speed;
        }
    }
    //向右
    public void tankRightWard() {
        setImage(rightImg);
        direction = Direction.RIGHT;
        if(!tankHitWall(pointX + speed, pointY) && !reachBorder(pointX + speed, pointY) && !tankHitBase(pointX + speed, pointY)) {
            pointX += speed;
        }
    }
    //向下
    public void tankDownWard() {
        setImage(downImg);
        direction = Direction.DOWN;
        if(!tankHitWall(pointX, pointY + speed) && !reachBorder(pointX, pointY + speed) && !tankHitBase(pointX, pointY + speed)) {
            pointY += speed;
        }
    }
    //坦克发射炮弹-----------------------------------------------------------------
    //新建一个线程，用来计算攻击冷却时间
    class FireCoolDown extends Thread {
        public void run() {
            fireCoolDown = false;
            try {
                Thread.sleep(fireInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fireCoolDown = true;
            //this.stop();
        }
    }
    //坦克发射子弹，坦克方向决定子弹方向、发射后进入冷却状态
    public void tankFire() {
        if(fireCoolDown && alive) {
            Point p = this.getTankHeadPoint();
            String bulletImg = null;
            //根据tank朝向选择子弹方向
            if (direction == Direction.UP) {
                bulletImg = "images/Bullet/bullet-u.png";
            } else if (direction == Direction.LEFT) {
                bulletImg = "images/Bullet/bullet-l.png";
            } else if (direction == Direction.RIGHT) {
                bulletImg = "images/Bullet/bullet-r.png";
            } else if (direction == Direction.DOWN) {
                bulletImg = "images/Bullet/bullet-d.png";
            }
            Bullet bullet = new Bullet(bulletImg, p.x, p.y, this.gamePanel, direction);
            this.gamePanel.bulletArrayList.add(bullet);

            new FireCoolDown().start();
        }
    }

    /*
     * 因为tank从头发射子弹，所以要计算坦克头的坐标
     * 但是坦克朝向不同时，获取的头部坐标不同，因此添加一个函数获取其坐标
     */
    public Point getTankHeadPoint() {
        return switch (direction) {
            case UP -> new Point(pointX + tankWidth / 2, pointY);
            case LEFT -> new Point(pointX, pointY + tankHeight / 2);
            case RIGHT -> new Point(pointX + tankWidth, pointY + tankHeight / 2);
            case DOWN -> new Point(pointX + tankWidth / 2, pointY + tankHeight);
        };
    }



    @Override
    public abstract void paintSelf(Graphics graphics);
    @Override
    public abstract Rectangle getRec();
}
