package com.mine.musharing.audio;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.User;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.requestTasks.SendTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static android.support.constraint.Constraints.TAG;

public class HotLineRecorder {

    // STATUS
    private final int NOTHING = 0;
    private final int INITED = 1;
    private final int RECORDING = 2;
    private final int RECORDED = 3;
    private final int SENDING = 4;

    private int status = NOTHING;

    private final MediaRecorder mediaRecorder = new MediaRecorder();    // Promise there is ONLY ONE MediaRecorder to avoid hanging

    private File dir;

    private String path;

    /**
     * 当前登录的用户
     */
    private User user;

    private HotLineRecorder() {
        path = initFilePath(System.currentTimeMillis() + ".amr");
    }

    /**
     * 设置当前用户
     *
     * <em>必须在适当位置设置这个，然后 HotLineRecorder 才能正常工作</em>
     * @param user 当前用户
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 单例Holder
     */
    private static class SingletonHolder {
        private static HotLineRecorder instance = new HotLineRecorder();
    }

    /**
     * 获取 HotLineRecorder 的单例
     * @return HotLineRecorder 的单例
     */
    public static HotLineRecorder getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Init file & path
     * @param fileName file name
     * @return path
     */
    private String initFilePath(String fileName) {
        dir = new File(Environment.getExternalStorageDirectory(),"musharing_hotline_recorder");
        if(!dir.exists()){
            dir.mkdirs();
        }

        File soundFile = new File(dir, fileName);
        if(!soundFile.exists()){
            try {
                soundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return soundFile.getAbsolutePath();
    }

    /**
     * set MediaRecorder
     * @param path 要储存录音文件的地址
     *
     * status: NOTHING -> INITED
     */
    private void resetMediaRecorder(String path) {
        if (status == NOTHING) {
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);   //设置输出格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);   //设置编码格式
            mediaRecorder.setOutputFile(path);

            status = INITED;
        } else {
            throw new RuntimeException("HotLineRecorder cannot resetMediaRecorder on status " + status);
        }

    }

    /**
     * 开始录制<br/>
     *
     * status: INITED -> RECORDING
     */
    public void startRecord() throws RuntimeException {
        if (status == INITED) {
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();  //开始录制

                status = RECORDING;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("HotLineRecorder cannot startRecord on status " + status);
        }

    }

    /**
     * 停止录制，资源释放<br/>
     *
     * status: RECORDING -> RECORDED
     */
    public void stopRecord() throws RuntimeException {
        if (status == RECORDING) {
            mediaRecorder.stop();
            // mediaRecorder.release();

            status = RECORDED;
        } else {
            throw new RuntimeException("HotLineRecorder cannot stopRecord on status " + status);
        }
    }

    /**
     * 把已录音转化为 Msg
     *
     * @return 转化出的 Msg
     */
    private Msg popRecordMsg() {
        try {
            String content;

            File file = new File(path);
            InputStream istream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(istream, "iso-8859-1");
            try {
                StringBuilder sb = new StringBuilder();
                while (reader.ready()) {
                    sb.append((char)reader.read());
                }

                content = sb.toString();

                Msg msg = new Msg(Msg.TYPE_RECORD, user, content);
                return msg;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) reader.close();
                if (istream != null) istream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Msg();
    }

    /**
     * 把已录音转化为 Msg 发送<br/>，然后自动调用reset
     *
     * status: RECORDED -> SENDING -> NOTHING -> (Then call reset(), which will take it to INITED)
     */
    public void publishRecord() throws RuntimeException {
        if (status == RECORDED) {
            try {
                Msg msg = popRecordMsg();

                new SendTask(new RequestTaskListener<String>() {
                    @Override
                    public void onStart() { status = SENDING; }

                    @Override
                    public void onSuccess(String s) {}

                    @Override
                    public void onFailed(String error) {}

                    @Override
                    public void onFinish(String s) {
                        status = NOTHING;
                        reset();
                    }
                }).execute(user.getUid(), msg.toString());

            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("HotLineRecorder cannot publishRecord on status " + status);
        }
    }

    /**
     * 播放录音<br/>
     *
     * 这个模块相对独立, 不影响 status
     * @param path 要播放的文件的地址
     */
    private void playRecord(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            // File file = new File(path);
            // mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理收到的语音消息，自动播放之
     * @param msg
     */
    public void handleRecordMsg(Msg msg) {
        try {
            String fileName = System.currentTimeMillis() + ".amr";

            String content = msg.getContent();

            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "iso-8859-1");

            try {
                writer.append(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) writer.close();
                if (outputStream != null) outputStream.close();
            }

            playRecord(file.getAbsolutePath());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置，准备下一次录制-发送流程
     *
     * 也期待在出错后被调用，防止整个HotLineRecorder陷入某个尴尬的状态（status链条断裂）无法自拔使之再不可用
     *
     * status: NOTHING -> INITED
     */
    public void reset() {
        // 删除所有录音缓存
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }

        status = NOTHING;
        resetMediaRecorder(path);
    }

    public void onDestroy() {
        status = NOTHING;
        mediaRecorder.release();

        // 删除所有录音缓存
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }
    }
}
