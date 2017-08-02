package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.model.ChessAdapter;
import com.example.lgh.lgh_blue.presenter.DragGridView;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;

import static com.example.lgh.lgh_blue.view.Connect.SER_KEY;

public class Game extends Activity {
    private ChessAdapter mChessAdapter;//棋盘适配器
    private DragGridView mGridView;//显示棋盘的GridView
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
        setLayout();
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
                            newGame(getWindow().getDecorView());
                        }
                        if (str.equals("backGame")) {
                            backChess();
                        } else {
                            int from, to;
                            String s[] = str.split(",");
                            from = Integer.parseInt(s[0]);
                            to = Integer.parseInt(s[1]);
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

        mGridView.setOnChangeListener(new DragGridView.OnChangeListener() {
            @Override
            public void onChange(int from, int to) {
                if (each) {
                    //
                    chessChange(from, to);
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
                        from = 89 - from;
                        to = 89 - to;
                        socket.Chat_Sent(from + "," + to);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    /*
    * 重开一局
    * */
    public void newGame(View view) {
//        for (int i = 0; i<mChess.length; i++){
//            mmChess[i]=mChess[i];
//        }
//        before=-1;after=-1;
//        //发送一个新局的信号
//        String str="newGame";
//        try {
//            socket.Chat_Sent(str);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        each=true;

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

    private void win() {
        Toast.makeText(Game.this, "真棒，陛下赢啦！", Toast.LENGTH_SHORT).show();
    }

    private void fail() {
        Toast.makeText(Game.this, "很遗憾，陛下离成功只差一步！", Toast.LENGTH_SHORT).show();
    }
}
