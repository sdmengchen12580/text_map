package org.faqrobot.demo_0927.ui.first;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;

import org.faqrobot.demo_0927.R;
import org.faqrobot.demo_0927.config.Config;
import org.faqrobot.demo_0927.utils.StatusBarUtil;
import org.faqrobot.demo_0927.utils.Util_Log_Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**波浪线的贝塞尔曲线http://blog.csdn.net/boonya/article/details/51063057*/
public class BaseActivity extends Activity {
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton bt_close_voice;
    private FloatingActionButton bt_refresh, bt_tts;
    private final String LEADER_WORD = "请说出您当前城市的地名。";


    /**1.空闲(播报转语音)  * 2.录音中  * 3.识别解析 * 4.播报 * 5.死亡——不播报不识别  6.当前能播报状态*/
    enum AsrStatus {
        idle, recoder, recognizing, speck, dead, canspeck
    }
    /**刚进入时候为可说话状态*/
    private AsrStatus statue = AsrStatus.canspeck;
    /**对应的采样率和说话的语种*/
    private static int arraySample[] = new int[]{SpeechConstants.ASR_SAMPLING_RATE_BANDWIDTH_AUTO,
            SpeechConstants.ASR_SAMPLING_RATE_16K, SpeechConstants.ASR_SAMPLING_RATE_8K};
    private static String arrayLanguageStr[] = new String[]{SpeechConstants.LANGUAGE_MANDARIN,
            SpeechConstants.LANGUAGE_ENGLISH, SpeechConstants.LANGUAGE_CANTONESE};
    /*** 语音识别*语音合成*/
    private SpeechUnderstander mUnderstander;
    private SpeechSynthesizer mTTSPlayer;

    private String mRecognizerText = "";
    private StringBuffer mAsrResultBuffer;

    private Thread check_status_is_idel;
    private boolean checking_is_idel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**标题*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title);
        /**沉浸栏加白色的字体*/
        StatusBarUtil.transparencyBar(this);
        /**初始化控件*/
        initView();
        /**点击事件*/
        clickView();
        /**初始化语音合成*/
        initSpecker();
        /**初始化语音识别*/
        initRecognizer();
        /**检测是否空闲状态*/
        check_now_status_is_idel();
    }


    /**初始化控件*/
    private void initView() {
        floatingActionsMenu = findViewById(R.id.father_button_menu);
        mAsrResultBuffer = new StringBuffer();
        bt_refresh = findViewById(R.id.bt_refresh_location);
        bt_tts = findViewById(R.id.bt_voice_gps);
        bt_close_voice = findViewById(R.id.bt_close_voice);
    }

    /**点击事件*/
    private void clickView() {
        /**1.刷新地名*/
        bt_refresh.setOnClickListener(view -> Toast.makeText(BaseActivity.this, "no finish", Toast.LENGTH_SHORT).show());
        /**2.点击播报*/
        bt_tts.setOnClickListener(view -> {
            if(statue != AsrStatus.canspeck){
               return;
            }else if(statue == AsrStatus.canspeck){
                Util_Log_Toast.log_e("start speck hello_word");
                /**初始化语音合成*/
                mTTSPlayer.playText(LEADER_WORD);
                runOnUiThread(() -> bt_tts.setTitle(LEADER_WORD));
                /**当前正在播报*/
                statue = AsrStatus.speck;
                /**显示可以关闭播报的按钮*/
                bt_close_voice.setVisibility(View.VISIBLE);
            }
        });
        /**3.关闭播报的按钮*/
        bt_close_voice.setOnClickListener(view -> {
             if( statue == AsrStatus.speck){
                    mTTSPlayer.stop();
                    mTTSPlayer.init("");
                    statue = AsrStatus.idle;
                       /**隐藏*/
                    bt_close_voice.setVisibility(View.GONE);
                }
        });
//        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
//            @Override
//            public void onMenuExpanded() {
//                Log.e("_________", "当前状态为打开");
//                statue = AsrStatus.canspeck;
//            }
//            @Override
//            public void onMenuCollapsed() {
//                Log.e("_________", "当前状态为关闭,关闭聆听和播报，切换为死亡状态");
//                if(statue==AsrStatus.speck){
//                    Log.e("——————————","正在播报，关闭播报");
//                    mTTSPlayer.stop();
//                    mTTSPlayer.init("");
//                }
//                if(statue==AsrStatus.recoder||statue==AsrStatus.recognizing){
//                    Log.e("——————————","正在聆听或者解析，停止");
//                    mUnderstander.cancel();
//                    mUnderstander.init("");
//                }
//                statue = AsrStatus.dead;
//            }
//        });
    }

    /**
     * 检测是否为空闲状态
     */
    private void check_now_status_is_idel() {
        check_status_is_idel = new Thread(() -> {
            while (checking_is_idel) {
                if (statue == AsrStatus.idle) {
                    /**清空stringbuffer和2个edittext*/
                    mAsrResultBuffer.delete(0, mAsrResultBuffer.length());
                    /**开始识别*/
                    mUnderstander.start();
                    mUnderstander.init("");
                    /**当前正在识别*/
                    statue = AsrStatus.recoder;
                }
            }
        });
        check_status_is_idel.start();
    }

    /**
     * 初始化语音识别
     */
    private void initRecognizer() {
        /**创建语音识别对象，appKey和 secret通过 http://dev.hivoice.cn/ 网站申请*/
        mUnderstander = new SpeechUnderstander(this, Config.appKey, Config.secret);
        mUnderstander.setOption(SpeechConstants.ASR_OPT_TEMP_RESULT_ENABLE, true);
        mUnderstander.setOption(SpeechConstants.NLU_SCENARIO, "videoDefault");
        mUnderstander.setOption(SpeechConstants.ASR_SAMPLING_RATE, arraySample[0]);
        mUnderstander.setOption(SpeechConstants.ASR_LANGUAGE, arrayLanguageStr[0]);
        mUnderstander.setListener(new SpeechUnderstanderListener() {
            @Override
            public void onResult(int type, String jsonResult) {
                switch (type) {
                    /**在线识别*/
                    case SpeechConstants.ASR_RESULT_NET:
                        // 在线识别结果，通常onResult接口多次返回结果，保留识别结果组成完整的识别内容。
                        Util_Log_Toast.log_e("调用在线识别");
                        if (jsonResult.contains("net_asr")
                                && jsonResult.contains("net_nlu")) {
                            try {
                                JSONObject json = new JSONObject(jsonResult);
                                JSONArray jsonArray = json.getJSONArray("net_asr");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String status = jsonObject.getString("result_type");
                                Util_Log_Toast.log_e("jsonObject = " + jsonObject.toString());
                                if (status.equals("full")) {
                                    String result = (String) jsonObject
                                            .get("recognition_result");
                                    /**jsonResult有结果，就能解析出识别到用户说的话-并且长度大于1*/
                                    if (jsonResult != null && result.length() > 2) {
                                        Util_Log_Toast.log_e(BaseActivity.this, "用户说话为：" + result.toString());
                                        /**显示地名*/
                                        runOnUiThread(() -> bt_tts.setTitle("即将查询去"+result.trim().substring(0,result.length()-1)+"的路线"));
                                        mTTSPlayer.playText("即将查询去"+result.trim().substring(0,result.length()-1)+"的路线");
                                        statue = AsrStatus.canspeck;
                                        // TODO: 2017/10/12 请求地图，gps导航
                                    } else if (result.length() == 2) {
                                        Util_Log_Toast.log_e(BaseActivity.this, "用户说一个字，不识别");
                                        /**当前为死亡状态*/
                                        statue = AsrStatus.canspeck;
                                    } else if (result.isEmpty()) {
                                        /**当前为死亡状态*/
                                        statue = AsrStatus.canspeck;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            //取出语音识别结果
                            asrResultOperate(jsonResult);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onEvent(int type, int timeMs) {
                switch (type) {
                    /**1.用户可以说话了*/
                    case SpeechConstants.ASR_EVENT_RECORDING_START:
                        Util_Log_Toast.log_e(BaseActivity.this, "录音设备打开，开始识别，用户可以开始说话");
                        statue = AsrStatus.recoder;
                        break;
                    /**2.说话开始*/
                    case SpeechConstants.ASR_EVENT_SPEECH_DETECTED:
                        Util_Log_Toast.log_e(BaseActivity.this, "用户开始说话");
                        break;
                    /**3.音量的改变*/
                    case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
                        // 说话音量实时返回
                        int volume = (Integer) mUnderstander.getOption(SpeechConstants.GENERAL_UPDATE_VOLUME);
                        break;
                    /**4.识别完*/
                    case SpeechConstants.ASR_EVENT_NET_END:
                        Util_Log_Toast.log_e(BaseActivity.this, "识别完成");
                        break;
                    /**5.超时未说话*/
                    case SpeechConstants.ASR_EVENT_VAD_TIMEOUT:
                        Util_Log_Toast.log_e(BaseActivity.this, "长时间不说话，但是不语音识别");
//                            mUnderstander.stop();
                        break;
                    /**6.录音停止*/
                    case SpeechConstants.ASR_EVENT_RECORDING_STOP:
                        Util_Log_Toast.log_e(BaseActivity.this, "录音停止，即将进入解析用户言语");
                        statue = AsrStatus.recognizing;
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onError(int type, String errorMSG) {
                if (errorMSG != null) {
                    /**显示错误信息*/
                    Util_Log_Toast.hitErrorMsg(BaseActivity.this, errorMSG);
                }
            }
        });
        mUnderstander.init("");
    }

    /**
     * 初始化语音合成
     */
    public void initSpecker() {
        /**创建语音合成（合成就是播报）对象*/
        Util_Log_Toast.log_e(this,"start init speck");
        mTTSPlayer = new SpeechSynthesizer(this, Config.appKey, Config.secret);
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_NET);
        /**设置语音合成回调监听*/
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {
            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        /**正常播放完成的*/
                        if(statue==AsrStatus.speck){
                            /**播报完后移除按钮*/
                            bt_close_voice.setVisibility(View.GONE);
                            /**开始识别*/
                            statue = AsrStatus.idle;
                        }
                        /**处理完之后，切换到的能说话状态，不操作，让点击播报的按钮去操作*/
                        else if(statue==AsrStatus.canspeck){
                            return;
                        }
                        // 播放完成回调
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onError(int type, String errorMSG) {
                /**吐司出错*/
                Util_Log_Toast.hitErrorMsg(BaseActivity.this, errorMSG);
            }
        });
        mTTSPlayer.init("");
    }
    /**
     * 释放资源
     */
    @Override
    protected void onStop() {
        super.onStop();
        mRecognizerText = null;
        mAsrResultBuffer = null;
        if (mUnderstander != null) {
            mUnderstander.stop();
        }
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
        }
        //关闭线程
        checking_is_idel = false;
        if (check_status_is_idel != null) {
            check_status_is_idel.interrupt();
            check_status_is_idel = null;
        }
    }

    /**
     * 语音解析工具方法
     */
    private void asrResultOperate(String jsonResult) {
        JSONObject asrJson;
        try {
            asrJson = new JSONObject(jsonResult);
            JSONArray asrJsonArray = asrJson.getJSONArray("net_asr");
            JSONObject asrJsonObject = asrJsonArray.getJSONObject(0);
            String asrJsonStatus = asrJsonObject.getString("result_type");
            /**清空数据*/
//            if (asrJsonStatus.equals("change")) {
//                mRecognizerResultText.append(mAsrResultBuffer.toString());
//                mRecognizerResultText.append(asrJsonObject.getString("recognition_result"));
//            } else {
//                mAsrResultBuffer.append(asrJsonObject.getString("recognition_result"));
//                mRecognizerResultText.append(mAsrResultBuffer.toString());
//            }
            if (!asrJsonStatus.equals("change")) {
                mAsrResultBuffer.append(asrJsonObject.getString("recognition_result"));
                Util_Log_Toast.log_e(BaseActivity.this, asrJsonObject.getString("recognition_result") + "");
                Util_Log_Toast.log_e(BaseActivity.this, mAsrResultBuffer.toString() + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
