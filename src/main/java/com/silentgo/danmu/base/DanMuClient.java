package com.silentgo.danmu.base;

import cn.hutool.core.thread.ThreadUtil;
import com.silentgo.danmu.client.douyu.DouyuDanMuSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public abstract class DanMuClient {

    public static final Logger logger = LoggerFactory.getLogger(DanMuClient.class);

    private String url;
    //单位s
    private int maxNoDanMuWait = 180;
    private int anchorStatusRescanTime = 30;

    private boolean deprecated = false;

    private boolean live = false;


    private DouyuDanMuSocket danMuSocket;

    private List<String> msgPipe = new LinkedList<>();
    private long danmuWaitTime = -1;

    private Thread danmuThread;
    private Thread heartThread;

    public DanMuClient(String url) {
        this.url = url;
    }

    public void start() {
        do {
            try {


                while (!deprecated) {
                    if (getLiveStatus()) break;
                    ThreadUtil.safeSleep(anchorStatusRescanTime);
                }
                prepareEnv();

                if (danMuSocket != null)
                    danMuSocket.close();

                danmuWaitTime = -1;
                initSocket();

                createThreadFn();

                startService();

            } catch (Exception e) {
                logger.error("wait retry", e);
                continue;
            }
            break;
        } while (!deprecated);

        if (danmuThread != null) {
            try {
                danmuThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        if (heartThread != null) {
//            try {
//                heartThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }


    public abstract void initSocket();

    public abstract void prepareEnv();

    public abstract void createThreadFn();

    public abstract void startService();

    public boolean getLiveStatus() {
        return false;
    }


    public DouyuDanMuSocket getDanMuSocket() {
        return danMuSocket;
    }

    public void setDanMuSocket(DouyuDanMuSocket danMuSocket) {
        this.danMuSocket = danMuSocket;
    }


    public String getUrl() {
        return url;
    }

    public Thread getDanmuThread() {
        return danmuThread;
    }

    public void setDanmuThread(Thread danmuThread) {
        this.danmuThread = danmuThread;
    }

    public Thread getHeartThread() {
        return heartThread;
    }

    public void setHeartThread(Thread heartThread) {
        this.heartThread = heartThread;
    }


    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean isLive() {
        return live;
    }

    public long getDanmuWaitTime() {
        return danmuWaitTime;
    }

    public void setDanmuWaitTime(long danmuWaitTime) {
        this.danmuWaitTime = danmuWaitTime;
    }

    public int getMaxNoDanMuWait() {
        return maxNoDanMuWait;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
