import java.awt.*;
import java.awt.event.KeyEvent;
/*
玩家坦克：继承Tank类
    额外需求：1，根据用户按键做出响应：调整方向、发射
                按方向键：调整方向+移动（按了马上松开，一直按
                按空格：发射弹药
                不按：静止（休眠
 */
public class PlayerTank extends Tank {
    private boolean up = false;
    private boolean left = false;
    private boolean right = false;
    private boolean down = false;
    public PlayerTank(String image, int pointX, int pointY, GamePanel gamePanel,
                      String upImg, String leftImg, String rightImg, String downImg) {
        super(image, pointX, pointY, gamePanel, upImg, leftImg, rightImg, downImg);
    }


    //键盘按下事件，
    public void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_SPACE:
                tankFire();
            default:
                break;
        }
    }
    //键盘松开事件
    public void keyReleased(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_DOWN -> down = false;
            default -> {
            }
        }
    }

    //tank移动响应
    public void tankMove() {
        if(up) {
            tankUpWard();
        } else if (left) {
            tankLeftWard();
        } else if (right) {
            tankRightWard();
        } else if (down) {
            tankDownWard();
        }
    }

    @Override
    public void paintSelf(Graphics graphics) {
        graphics.drawImage(image, pointX, pointY, null);
        tankMove();
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(pointX, pointY, tankWidth, tankHeight);
    }
}
