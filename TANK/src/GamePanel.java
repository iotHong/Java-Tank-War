import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
/*
游戏界面：
    1.窗口大小
    2.玩家、基地、敌方坦克进入的位置、两种墙的位置、边界的位置以及实现到界面上
    3.界面敌方坦克数量
    4.游戏开始前界面
    5.游戏结束后界面（输、赢、退出》》》》》游戏状态state
 */
public class GamePanel extends JFrame {

    int winWidth = 650;
    int winHeight = 450;

    //定义玩家坦克, 初始位置: （x=125, y=390）
    PlayerTank player = new PlayerTank("images/Tank/my-u.png", 200, 390, this,
            "images/Tank/my-u.png", "images/Tank/my-l.png",
            "images/Tank/my-r.png", "images/Tank/my-d.png");
    //添加基地
    Base base = new Base("images/utils/base.png", 300, 375, this);
    //添加背景
    Image backgroundImg = Toolkit.getDefaultToolkit().getImage("images/utils/background.jpg");
    //敌方坦克进入位置------------------------------------------------------------------
    static int[] enemyPositionX = {75, 25, 300, 350, 575};
    static int[] enemyPositionY = {125, 100, 75, 275, 325};
    //金属墙坐标----------------------------------------------------------------
    static int[] ironWallPositionX = {100, 125, 300, 25, 400, 300};
    static int[] ironWallPositionY = {25, 375, 300, 250, 150, 300};
    //可摧毁墙坐标------------------------------------------------------------------
    static int[] wallPositionX = {
            250, 350, 75, 75, 75, 250, 250, 250,
            400, 350, 350, 350, 450, 450, 500, 500,
            500, 250, 300, 350, 250, 350, 300
    };
    static int[] wallPositionY = {
            75, 75, 175, 225, 275, 125, 175, 225,
            325, 125, 175, 225, 75, 125, 225, 275,
            325, 325, 325, 325, 375, 375, 25
    };

    Image offScreenImage = null;

    int enemyCount = 0;//敌方坦克数量
    int speedCount = 0;//敌方坦克速度
    //--------------------------------------------------------------
    //添加子弹列表
    ArrayList<Bullet> bulletArrayList = new ArrayList<>();
    //添加敌军列表
    ArrayList<EnemyTank> enemyArrayList = new ArrayList<>();
    //删除的子弹列表
    ArrayList<Bullet> removeBulletList = new ArrayList<>();
    //添加玩家列表, 游戏只有一个玩家可以不用列表, 用列表为了可扩展性
    ArrayList<Tank> playerList = new ArrayList<>();
    //添加围墙列表
    ArrayList<Wall> wallArrayList = new ArrayList<>();
    //添加边框以及不可摧毁的墙, 用铁墙做围栏
    ArrayList<Wall> borderWallList = new ArrayList<>();
    //添加基地列表
    ArrayList<Base> baseArrayList = new ArrayList<>();


    int state = 0;//表示游戏状态
    /*state        状态
         0       初始状态;
         1       游戏开始;
         2       游戏失败;
         3       游戏胜利;
         4       退出游戏
     */

    public void loadPanel() {

        setTitle("坦克大战");//窗口标题

        setSize(winWidth, winHeight);//窗口大小

        setLocationRelativeTo(null);//屏幕居中，方便进行游戏

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //关闭事件

        setResizable(false); //固定窗口大小

        setVisible(true); //窗口可见

        this.addKeyListener(new KeyMonitor());//添加键盘事件监视器

        //添加不可摧毁的墙--------------------------------------------------------------
        for(int i = 0; i < 2; i++) {
            borderWallList.add(
                    new Wall("images/Wall/ironWall-v.png", ironWallPositionX[i], ironWallPositionY[i],
                            this, false, 25, 50)
            );
        }
        for(int i = 3; i < 6; i++) {
            borderWallList.add(
                    new Wall("images/Wall/ironWall-h.png", ironWallPositionX[i], ironWallPositionY[i],
                            this, false, 50, 25)
            );
        }
        //添加可摧毁围墙--------------------------------------------------------------
        for(int i = 0; i < 23; i++) {
            wallArrayList.add(
                    new Wall("images/Wall/brickWall.png", wallPositionX[i], wallPositionY[i],
                            this, true, 50, 50)
            );
        }

        //添加基地--------------------------------------------------------------
        baseArrayList.add(base);

        //添加边界----------------------------------------------------------------
        for(int i = 0; i < 9; i++) {
            borderWallList.add(
                    new Wall("images/Wall/border-v.png",
                            0, i * 50, this, false, 25, 50)
            );
        }
        for(int i = 0; i < 9; i++) {
            borderWallList.add(
                    new Wall("images/Wall/border-v.png",
                            this.getWidth() - 25, i * 50, this, false, 25 ,50)
            );
        }
        for(int i = 0; i < 12; i++) {
            borderWallList.add(
                    new Wall("images/Wall/border-h.png",
                            i * 50 + 25, 0, this, false, 50, 25)
            );
        }
        for(int i = 0; i < 12; i++) {
            borderWallList.add(
                    new Wall("images/Wall/border-h.png",
                            i * 50 + 25, this.getHeight() - 25, this, false, 50, 25)
            );
        }

        while (true) {
            if(state == 4) break;
            //游戏胜利判定
            if(baseArrayList.size() > 0 && enemyCount == 5 && enemyArrayList.size() == 0) {
                state = 3;
            }
            //游戏失败判定
            if(state == 1 && (playerList.size() == 0 || baseArrayList.size() == 0)) {
                state = 2;
            }
            if(state == 1 && speedCount % 100 == 0 && enemyCount < 5) {
                Random random = new Random();
                int randDirection = random.nextInt(4);
                String img = switch (randDirection) {
                    case 1 -> "images/Tank/enemy-u.png";
                    case 2 -> "images/Tank/enemy-r.png";
                    case 3 -> "images/Tank/enemy-d.png";
                    default -> "images/Tank/enemy-l.png";
                };

                //添加敌军
                enemyArrayList.add(new EnemyTank(img, enemyPositionX[enemyCount], enemyPositionY[enemyCount], this,
                        "images/Tank/enemy-u.png", "images/Tank/enemy-l.png",
                        "images/Tank/enemy-r.png", "images/Tank/enemy-d.png", enemyCount % 2));
                enemyCount++;
            }
            repaint();
            speedCount++;
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //paint()
    @Override
    public void paint(Graphics graphics) {
        //创建和窗口一样大小的图片
        if(offScreenImage == null) {
            offScreenImage = this.createImage(winWidth, winHeight);
        }
        //获得画笔
        Graphics gImage = offScreenImage.getGraphics();
        //设置窗口背景颜色
//        gImage.setColor(Color.GRAY);
        gImage.fillRect(0,0, winWidth, winHeight);
        //设置首页字体颜色
        gImage.setColor(Color.CYAN);
        //设置首页显示字体以及内容
        gImage.setFont(new Font("微软雅黑", Font.BOLD, 30));
        if(state == 0) {
            Image bkgImg = Toolkit.getDefaultToolkit().getImage("images/utils/start.png");
            gImage.drawImage(bkgImg, 0, 0, null);

            gImage.setFont(new Font("微软雅黑", Font.BOLD, 50));
            gImage.drawString("坦克大战", 225, 150);

            gImage.setColor(Color.red);
            gImage.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            gImage.drawString("按enter键开始游戏", 250, 390);

        } else if (state == 1) {
            gImage.drawImage(backgroundImg, 0, 0, null);
            gImage.setColor(Color.RED);
            gImage.setFont(new Font("微软雅黑", Font.BOLD, 15));
            gImage.drawString("剩余敌军：" + enemyArrayList.size(), 25, this.getHeight() - 30);

            gImage.setColor(Color.RED);
            gImage.setFont(new Font("微软雅黑", Font.BOLD, 15));
            gImage.drawString("按shift + O键退出游戏", this.getWidth()-150, this.getHeight() - 30);

            gImage.setColor(Color.black);
            gImage.setFont(new Font("微软雅黑", Font.BOLD, 15));
            gImage.drawString("按空格发射炮弹", this.getWidth()-150, this.getHeight() - 45);

            gImage.setColor(Color.RED);
            gImage.setFont(new Font("微软雅黑", Font.BOLD, 15));
            gImage.drawString("按方向键控制方向", this.getWidth()-150, this.getHeight() - 60);


            //添加边框
            for(Wall wall : borderWallList) {
                wall.paintSelf(gImage);
            }
            //添加玩家
            for (Tank player : playerList) {
                player.paintSelf(gImage);
            }
            //添加子弹
            for (Bullet bullet : bulletArrayList) {
                bullet.paintSelf(gImage);
            }
            bulletArrayList.removeAll(removeBulletList);
            //添加爆炸效果
            //for (Explode explode : explodeArrayList) {
              //  explode.paintSelf(gImage);
            //}
            //添加敌军
            for (EnemyTank enemy : enemyArrayList) {
                enemy.paintSelf(gImage);
            }
            //添加围墙
            for (Wall wall : wallArrayList) {
                wall.paintSelf(gImage);
            }
            //添加基地
            for (Base base : baseArrayList) {
                base.paintSelf(gImage);
            }

        } else if (state == 2) {
            Image gameoverImg = Toolkit.getDefaultToolkit().getImage("images/utils/gameover.jpg");
            gImage.drawImage(gameoverImg, 0, 0, null);
            gImage.setColor(Color.red);
            gImage.setFont(new Font("微软雅黑", Font.PLAIN, 30));
            gImage.drawString("按shift + O键退出游戏", 200, 320);
            gImage.setColor(Color.white);
            gImage.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            gImage.drawString("2020141230141-朱俊宏", 270, 430);
        } else if (state == 3) {
            Image victoryImg = Toolkit.getDefaultToolkit().getImage("images/utils/victory.png");
            gImage.drawImage(victoryImg, 125, 25, null);
            gImage.setColor(Color.red);
            gImage.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            gImage.drawString("按shift + O键退出游戏", 235, 320);
            gImage.setColor(Color.white);
            gImage.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            gImage.drawString("2020141230141-朱俊宏", 270, 430);
        }
        //将图片加载到窗口中
        graphics.drawImage(offScreenImage, 0, 0, null);
    }

    //键盘事件监视器
    class KeyMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                playerList.add(player);
                player.alive = true;
                state = 1;
            } else if (keyEvent.isShiftDown() && keyEvent.getKeyCode() == KeyEvent.VK_O) {
                state = 4;
            }
            else {
                player.keyPressed(keyEvent);
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            player.keyReleased(keyEvent);
        }
    }
    //main
    public static void main(String[] args) {
        GamePanel winOfBattleCity = new GamePanel();
        winOfBattleCity.loadPanel();
        winOfBattleCity.dispose();
    }

}