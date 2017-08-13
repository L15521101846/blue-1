package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.model.ChessAdapter;
import com.example.lgh.lgh_blue.presenter.DragGridView;
import com.example.lgh.lgh_blue.presenter.GameDialog;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;

import static com.example.lgh.lgh_blue.view.Connect.SER_KEY;

public class Game extends Activity {
    private ChessAdapter mChessAdapter;//棋盘适配器
    //private DragGridView mGridView;//显示棋盘的GridView
    private GridView mGridView;//显示棋盘的GridView
    private int fromItem = -1;//棋子起始位置
    private int toItem = -1;//棋子目标位置
    String connectTo;
    private int[] mChess = {R.mipmap.white_car_2x, R.mipmap.white_horse_2x, R.mipmap.white_ele_2x, R.mipmap.white_off_2x,
            R.mipmap.boss1_2x,
            R.mipmap.white_off_2x, R.mipmap.white_ele_2x, R.mipmap.white_horse_2x, R.mipmap.white_car_2x,
            R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank,
            R.mipmap.blank, R.mipmap.white_fire_2x, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.white_fire_2x, R.mipmap.blank,
            R.mipmap.white_weapon_2x, R.mipmap.blank, R.mipmap.white_weapon_2x, R.mipmap.blank, R.mipmap.white_weapon_2x, R.mipmap.blank, R.mipmap.white_weapon_2x, R.mipmap.blank, R.mipmap.white_weapon_2x,
            R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank,
            R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank,
            R.mipmap.red_weapon_2x, R.mipmap.blank, R.mipmap.red_weapon_2x, R.mipmap.blank, R.mipmap.red_weapon_2x, R.mipmap.blank, R.mipmap.red_weapon_2x, R.mipmap.blank, R.mipmap.red_weapon_2x,
            R.mipmap.blank, R.mipmap.red_fire_2x, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.red_fire_2x, R.mipmap.blank,
            R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank, R.mipmap.blank,
            R.mipmap.red_car_2x, R.mipmap.red_horse_2x, R.mipmap.red_ele_2x, R.mipmap.red_off_2x,
            R.mipmap.boss_2x,
            R.mipmap.red_off_2x, R.mipmap.red_ele_2x, R.mipmap.red_horse_2x, R.mipmap.red_car_2x,};

    private int[] mmChess = new int[90];
    /*
    * before拖拽前的棋子坐标，after拖拽后的棋子坐标，To用于悔棋时记录“被吃掉”的棋子，clickN限制悔棋按钮不能连续点击
    * */
    private int before = -1, after = -1, To = -1, clickN = -1;//每个用户初始化不能悔棋
    private boolean each = true;//每个用户初始化可以走棋
    private Handler handler = null;
    Socket socket;
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            mChessAdapter.notifyDataSetChanged();
        }
    };
    private TextView my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        socket = (Socket) getIntent().getSerializableExtra(SER_KEY);
        connectTo=getIntent().getStringExtra("connectTo");
        setLayout();
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
     if(keyCode == KeyEvent.KEYCODE_BACK) return  true;
        return super.onKeyDown(keyCode,event);
    }
    private void setLayout() {
        /*
        * 初始化棋盘
        * */
        for (int i = 0; i < mChess.length; i++) {
            mmChess[i] = mChess[i];
        }
        /*
        * 初始化棋盘适配器，绑定棋盘的GridView布局
        * */
        mGridView = (DragGridView) findViewById(R.id.game_chess);

        mChessAdapter = new ChessAdapter(getLayoutInflater(), mmChess);

        mGridView.setAdapter(mChessAdapter);
        handler = new Handler();
        /*
        * 接收信息的线程
        * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String str = socket.Chat_receive();
                        if (str.equals("newGame")) {
                            //newGame(getWindow().getDecorView());
                        }
                        if (str.equals("backGame")) {
                            backChess();
                        }
                        else {
                            int from, to;
                            String s[] = str.split(",");
                            from = Integer.parseInt(s[0]);
                            to = Integer.parseInt(s[1]);
                            if (mmChess[to] == R.mipmap.boss_2x) {
                                Looper.prepare();
                                chessChange(from, to);
                                handler.post(runnableUi);
                                dialog("很遗憾，您输了！");
                                Looper.loop();
                            }
                            chessChange(from, to);
                        }
                        /*
                        * 收到悔棋或者敌人走棋的信息，本人不能点击悔棋，可以走棋
                        * */
                        clickN = -1;
                        each = true;
                        handler.post(runnableUi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        mGridView.setOnChangeListener(new DragGridView.OnChangeListener() {
//            @Override
//            public void onChange(int from, int to) {
//                if (each) {
//                    //
//                    chessChange(from, to);
//                    /*
//                    * 走过棋子的用户，可以点击悔棋
//                    * */
//                    clickN++;
//                    /*
//                    * 走过棋的用户只能等待敌人下一步棋子信息来了，才能恢复走棋
//                    * */
//                    each = false;
//                    mChessAdapter.notifyDataSetChanged();
//                    //发送给好友移动棋子的坐标
//                    try {
//                        from = 89 - from;
//                        to = 89 - to;
//                        socket.Chat_Sent(from + "," + to);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (fromItem != -1) {
                    toItem = position;
                    int fromX, fromY;
                    int toX, toY;
                    int moveChessID=-1;
                    fromX = fromItem % 9;
                    fromY = fromItem / 9;
                    toX = toItem % 9;
                    toY = toItem / 9;
                    for (int j=0;j<mChess.length;j++){
                        if (mChess[j]==mmChess[fromItem]) moveChessID=j;
                    }
                    if(qzisTure(moveChessID,fromX,fromY,toX,toY)&&fromItem!=toItem){
                        if (each) {
                            if (mmChess[toItem] == R.mipmap.boss1_2x)
                                dialog("殿下，您赢啦！");
                                //
                            chessChange(fromItem, toItem);

                    /*
                    * 走过棋子的用户，可以点击悔棋
                    * */
                            clickN++;
                    /*
                    * 走过棋的用户只能等待敌人下一步棋子信息来了，才能恢复走棋
                    * */
                            each = false;

                            mChessAdapter.notifyDataSetChanged();
                            //发送给好友移动棋子的坐标
                            try {
                                fromItem = 89 - fromItem;
                                toItem = 89 - toItem;
                                socket.Chat_Sent(fromItem + "," + toItem);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    fromItem = toItem =-1;
                }
                for ( int i = 0; i < mChess.length; i++) {
                    if (mChess[i] == mmChess[position]) {
                        if (i > 50 && mmChess[position] != R.mipmap.blank) {
                            fromItem = position;
                        }
                    }
                }

            }
        });
    }

    /**
     * 判断棋子的走法的正确性
     *
     * @param
     */
    public boolean qzisTure(int moveChessID,int fromX, int fromY, int toX, int toY) {
        switch (moveChessID) {
            //走帅
            case 85: {
                if (toY < 7 || toX < 3 || toX > 5) {//出了九宫格
                    return false;
                } else if ((Math.abs(toX - fromX) + Math.abs(toY - fromY)) == 1) {
//只能走一格
                    if (isEatable(toX,toY)) return true;
                } else {
                    return false;
                }
            }
            //走士
            case 84:case 86: {
                if (toY < 7 || toX < 3 || toX > 5) { //出了九宫格
                    return false;
                } else if (Math.abs(toX - fromX) == 1 && Math.abs(toY - fromY) == 1) {
//走斜线，直走一格
                    if (isEatable(toX,toY)) return true;
                } else {
                    return false;
                }
            }
            //走象
            case 83:case 87: {
                if (toY < 5) {//过河了
                    return false;
                } else if (Math.abs(toY - fromY) == 2 && Math.abs(toX - fromX) == 2) {
//走"田"字
                    int centerX = (toY + fromY) / 2;
                    int centerY = (toX + fromX) / 2;
                    if (mmChess[centerY+centerX*9] != R.mipmap.blank) {// 象眼处有棋子
                        return false;
                    }
                    if (isEatable(toX,toY))return true;
                } else {
                    return false;
                }
            }
            //走马
            case 82:case 88: {
                if (Math.abs(toY - fromY) == 2 && Math.abs(toX - fromX) == 1) {
                    int centerY = (toY + fromY) / 2;
                    if (mmChess[centerY*9+fromX] != R.mipmap.blank) {//马蹄处有棋子
                        return false;
                    }
                    if (isEatable(toX,toY))return true;
                } else if (Math.abs(toY - fromY) == 1 && Math.abs(toX - fromX) == 2) {
                    int centerX = (toX + fromX) / 2;
                    if (mmChess[fromY*9+centerX] != R.mipmap.blank) {//马蹄处有棋子
                        return false;
                    }
                    if (isEatable(toX,toY))return true;
                } else {
                    return false;
                }
            }
            //走車
            case 81:case 89: {
                if (Math.abs(toY - fromY) > 0 && Math.abs(toX - fromX) == 0) {
//走的竖线
                    if (toY > fromY) {
                        for (int i = fromY + 1; i < toY; i++) {
                            if (mmChess[i*9+fromX] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                return false;
                            }
                        }
                    } else {
                        for (int i = fromY - 1; i > toY; i--) {//从初始位置到目标位置存在非空白棋子
                            if (mmChess[i*9+fromX] != R.mipmap.blank) {
                                return false;
                            }
                        }
                    }
                    if (isEatable(toX,toY))return true;
                    else return false;
                } else if (Math.abs(toX - fromX) > 0 && Math.abs(toY - fromY) == 0) {
//走的横线
                    if (toX > fromX) {
                        for (int i = fromX + 1; i < toX; i++) {
                            if (mmChess[fromY*9+i] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                return false;
                            }
                        }
                    } else {
                        for (int i = fromX - 1; i > toX; i--) {
                            if (mmChess[fromY*9+i] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                return false;
                            }
                        }
                    }
                    if (isEatable(toX,toY))return true;
                    else return false;
                } else {
                    return false;
                }
            }
            //走炮
            case 64:case 70: {
                int chNum = 0;//记录非白色棋子个数
                if (Math.abs(toY - fromY) > 0 && Math.abs(toX - fromX) == 0) {
//走的竖线
                    if (toY > fromY) {
                        for (int i = fromY + 1; i < toY; i++) {
                            if (mmChess[i*9+fromX] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                chNum++;
                            }
                        }
                    } else {
                        for (int i = fromY - 1; i > toY; i--) {//从初始位置到目标位置存在非空白棋子
                            if (mmChess[i*9+fromX] != R.mipmap.blank) {
                                chNum++;
                            }
                        }
                    }
                    if (chNum == 1&& isEatable(toX,toY) && mmChess[toY*9+toX] !=R.mipmap.blank)return true;
                    else if (chNum == 0 && mmChess[toY*9+toX] ==R.mipmap.blank)return true;
                    else return false;
                } else if (Math.abs(toX - fromX) > 0 && Math.abs(toY - fromY) == 0) {
//走的横线
                    if (toX > fromX) {
                        for (int i = fromX + 1; i < toX; i++) {
                            if (mmChess[fromY*9+i] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                chNum++;
                            }
                        }
                    } else {
                        for (int i = fromX - 1; i > toX; i--) {
                            if (mmChess[fromY*9+i] != R.mipmap.blank) {//从初始位置到目标位置存在非空白棋子
                                chNum++;
                            }
                        }
                    }
                    if (chNum == 1&& isEatable(toX,toY) && mmChess[toY*9+toX] !=R.mipmap.blank)return true;
                    else if (chNum == 0 && mmChess[toY*9+toX] ==R.mipmap.blank)return true;
                    else return false;
                } else {
                    return false;
                }
            }
            //走卒
            case 54:case 56:case 58:case 60:case 62: {
                if ((toY - fromY) > 0) {
//后退
                    return false;
                } else if (fromY < 5) {
                    //过了河
                   if (toY == fromY&&Math.abs(toX-fromX) == 1&&isEatable(toX,toY))return true;//向两边走一格
                   else if (toX == fromX && Math.abs(toY-fromY) ==1&&isEatable(toX,toY))return true;//向前走一格
                    return false;
                    } else {
                    //没过河
                        if (toX == fromX && Math.abs(toY-fromY)==1&&isEatable(toX,toY))return true;//只能向前走一格
                        else return false;
                    }
            }
            default:return false;
        }
    }

    /**
     * 判断目标棋子是否为“可吃”棋子；1.blank2.对方棋子
     * @param
     */
    public boolean isEatable(int toX,int toY){
        for (int j = 0; j < 50; j++) {
            if (mChess[j] == mmChess[toY * 9 + toX]) return true;
        }
        return false;
    }

    /**
     * 对弈结束弹窗显示输赢,赢：1，输：0
     * @param
     */
    private void dialog(String content){
        new GameDialog(Game.this.getWindow().getContext(), R.style.dialog_game, content, new GameDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm){
                    try {
                        closeKetAndExit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    /**
     * 关闭socket，并退出象棋对弈,发送send：true，接收send：false
     * @param
     */
    private void closeKetAndExit() throws IOException{
        if (socket.isConnected())
                socket.close();
        Intent intent =new Intent();
        intent.setClass(Game.this,Main.class);
        startActivity(intent);
    }
    /*
    * 悔棋
    * */
    public void backGame(View view) {

        //发送一个悔棋的信号

        /*
        * 判断是否已经点击过
        * */
        if (clickN != -1) {
            backChess();
            String str = "backGame";
            try {
                socket.Chat_Sent(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mChessAdapter.notifyDataSetChanged();
        }
        //重置clickN变量，点击过一次了，不能再点击。只能悔棋一步。
        clickN = -1;
        /*
        *悔棋后可以重新走棋
        * */
        each = true;
    }

    private void chessChange(int from, int to) {
        int temp;
        before = from;
        after = to;
        temp = mmChess[from];
                /*
                * 如果“吃掉”的棋子是空的
                * */
        if (mmChess[to] == mChess[10]) {
            mmChess[from] = mmChess[to];
            To = -1;
        }//否则，要将“吃掉”的棋子记录到变量To上
        else {
            To = mmChess[to];
            mmChess[from] = mChess[10];


        }
        mmChess[to] = temp;
    }

    private void backChess() {
        int temp;
        if (before != -1 && after != -1) {
            temp = mmChess[before];
            mmChess[before] = mmChess[after];
            if (To == -1)
                mmChess[after] = temp;
            else {
                mmChess[after] = To;
                To = -1;
            }
        }
    }
}
