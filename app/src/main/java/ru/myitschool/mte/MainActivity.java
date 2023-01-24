package ru.myitschool.mte;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.myitschool.mte.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button btnStart, btnStop;

    FragmentManager fm;
    FragmentTransaction ft;
    FirstFragment ff;
    ProceedingFragment pf;
    Handler handler;

    ChangeFragment changeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnStart = binding.content.startBtn;
        btnStop = binding.content.stopBtn;

        fm = getSupportFragmentManager();
        ff = new FirstFragment();
        pf = new ProceedingFragment();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String msgStr = msg.obj.toString();
                ft = fm.beginTransaction();
                if(msgStr.equals("true")){
                    ft.replace(R.id.output_fragment, ff);
                    ft.commit();
                }else{
                    ft.replace(R.id.output_fragment, pf);
                    ft.commit();
                }
            }
        };

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment = new ChangeFragment(handler);
                changeFragment.setRun(true);
                changeFragment.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean retry = true;
                changeFragment.setRun(false);
                while (retry) {
                    try {
                        changeFragment.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
class ChangeFragment extends Thread{
    Handler handler;
    boolean isRun = false;
    boolean isChange = true;
    public ChangeFragment(Handler handler){
        this.handler = handler;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    @Override
    public void run() {
        while (isRun){
            Message msg = new Message();

            msg.obj = Boolean.toString(isChange);
            handler.sendMessage(msg);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isChange = !isChange;
        }
    }
}