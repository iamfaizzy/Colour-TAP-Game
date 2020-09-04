package com.faizan.assignmentblacklight;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.faizan.assignmentblacklight.databinding.ActivityMainBinding;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import static com.faizan.assignmentblacklight.Tiles.BLUE;
import static com.faizan.assignmentblacklight.Tiles.GREEN;
import static com.faizan.assignmentblacklight.Tiles.RED;
import static com.faizan.assignmentblacklight.Tiles.YELLOW;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private int current;
    private Runnable runnableCode, runnableWait;
    private Handler handler;
    private CountDownTimer countDownTimer;
    private boolean isStart = false;
    private MutableLiveData<Integer> score = new MutableLiveData<>();
    private boolean isClicked = false;
    private MediaPlayer mpTap, mpGameOver;
    private boolean isDlgShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.gameLayout.vRed.setOnClickListener(this);
        binding.gameLayout.vYellow.setOnClickListener(this);
        binding.gameLayout.vGreen.setOnClickListener(this);
        binding.gameLayout.vBlue.setOnClickListener(this);

        handler = new Handler();
        resetScore();
        observeScore();
        mpTap = MediaPlayer.create(this, R.raw.tap);
        mpGameOver = MediaPlayer.create(this, R.raw.game_over);

        countDownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.gameLayout.tvCount.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                binding.gameLayout.tvCount.setText(R.string.go);
                binding.gameLayout.tvCount.setVisibility(View.GONE);
                binding.gameLayout.btnStart.setVisibility(View.GONE);
                isStart = true;
                isClicked = true;
                current = RED;
                binding.gameLayout.vRed.setBackgroundResource(R.color.grey);
                startTilesChanging();
            }

        };

        binding.gameLayout.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountDown();
                binding.gameLayout.btnStart.setVisibility(View.GONE);
            }
        });



    }

    private void showGameOverDialogue() {
        saveBestScore();
        resetGame();
        isDlgShown=true;
        AlertDialog.Builder builder; builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.game_over));
        builder.setMessage(String.format(getString(R.string.game_score), score.getValue(), SharedPrefUtils.getIntData(MainActivity.this, SharedPrefUtils.BEST_SCORE)));
        builder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startCountDown();
                isDlgShown=false;

            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }
        });
        builder.create().show();
    }

    private void resetScore() {
        score.setValue(0);
    }

    private void saveBestScore() {
        int best = SharedPrefUtils.getIntData(MainActivity.this, SharedPrefUtils.BEST_SCORE);
        int current = score.getValue() != null ? score.getValue() : 0;
        SharedPrefUtils.saveData(MainActivity.this, SharedPrefUtils.BEST_SCORE, Math.max(best, current));
    }

    private void observeScore() {
        score.observe(MainActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.gameLayout.tvScore.setText(String.format(getString(R.string.score), integer));
            }
        });

    }

    private void startTilesChanging() {

        runnableCode = new Runnable() {
            @Override
            public void run() {
                if (isStart && isClicked) {
                    changeToGrey();
                    handler.postDelayed(runnableCode, 1000);
                    isClicked = false;

                } else if (isStart) {
                    showDialogueWithSound();
                }
            }
        };
        handler.post(runnableCode);


    }

    private void showDialogueWithSound() {
        if (mpGameOver.isPlaying()) {
            mpGameOver.stop();
        }
        mpGameOver.start();
        if(!isDlgShown){

            showGameOverDialogue();
        }
    }

    private void startCountDown() {
        binding.gameLayout.tvCount.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }


    @Override
    public void onClick(View view) {
        if (isStart) {
            int id = view.getId();
            isClicked = true;
            int currentScore = score.getValue() != null ? score.getValue() : 0;
            if (mpTap.isPlaying()) {
                mpTap.stop();
            }
            switch (id) {
                case R.id.v_blue:
                    if (current == BLUE) {
                        score.setValue(currentScore + 1);
                        mpTap.start();
                    } else {
                        showDialogueWithSound();
                    }
                    break;
                case R.id.v_green:
                    if (current == GREEN) {
                        score.setValue(currentScore + 1);
                        mpTap.start();
                    } else {
                        showDialogueWithSound();
                    }
                    break;
                case R.id.v_yellow:
                    if (current == YELLOW) {
                        score.setValue(currentScore + 1);
                        mpTap.start();
                    } else {
                        showDialogueWithSound();
                    }
                    break;
                case R.id.v_red:
                    if (current == RED) {
                        score.setValue(currentScore + 1);
                        mpTap.start();
                    } else {
                        showDialogueWithSound();
                    }
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        mpTap.release();
        mpGameOver.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mpGameOver.isPlaying()) {

            mpGameOver.stop();

        }
        if (mpTap.isPlaying()) {
            mpTap.stop();
        }
        resetGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isDlgShown){

            binding.gameLayout.btnStart.setVisibility(View.VISIBLE);
        }

    }

    private void resetGame() {
        isStart = false;
        isClicked = false;
        resetTile();
        resetScore();
    }

    private void changeToGrey() {
        int tiles = Tiles.getRandomWithExclusion(current);
        resetTile();
        current = tiles;
        switch (tiles) {
            case RED:
                binding.gameLayout.vRed.setBackgroundResource(R.color.grey);
                break;
            case BLUE:
                binding.gameLayout.vBlue.setBackgroundResource(R.color.grey);
                break;
            case GREEN:
                binding.gameLayout.vGreen.setBackgroundResource(R.color.grey);
                break;
            case YELLOW:
                binding.gameLayout.vYellow.setBackgroundResource(R.color.grey);
                break;
        }


    }

    private void resetTile() {
        switch (current) {
            case RED:
                binding.gameLayout.vRed.setBackgroundResource(R.color.red);
                break;
            case BLUE:
                binding.gameLayout.vBlue.setBackgroundResource(R.color.blue);
                break;
            case GREEN:
                binding.gameLayout.vGreen.setBackgroundResource(R.color.green);
                break;
            case YELLOW:
                binding.gameLayout.vYellow.setBackgroundResource(R.color.yellow);
                break;
        }

    }


}