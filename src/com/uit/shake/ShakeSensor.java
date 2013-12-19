
package com.uit.shake;

/**   
 *
 * @Title: ShakeListener.java
 * @Package com.uit.shake
 * @Description: 
 *        摇一摇的功能实现类,需要用户注册onShareListener来事件回调 .  
 *           
 * @author Honghui He  
 * @version V1.0   
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @ClassName: UMShakeSensor
 * @Description: 摇一摇传感器，内部原理为加速度传感器，摇一摇到达速度阀值则出发相应的动作， 回调onShakeComplete方法
 * @author Mr.Simple
 * @date Oct 5, 2013 2:51:37 PM
 */
public class ShakeSensor implements SensorEventListener {

    /**
     * App Context
     */
    protected Context mContext = null;
    /**
     * 传感器管理器
     */
    protected SensorManager mSensorManager = null;
    /**
     * 目标传感器
     */
    protected Sensor mSensor = null;
    /**
     * 传感器监听器, 摇一摇以后回调onShakeComplete方法
     */
    protected OnShakeListener mShakeListener = null;
    /**
     * 类的tag
     */
    protected final String TAG = this.getClass().getName();

    /**
     * 速度阈值，当摇晃速度达到这值后产生作用
     */
    private int mSpeedShreshold = DEFAULT_SHAKE_SPEED;
    /**
     * 传感器检测变化的时间间隔
     */
    private static final int UPTATE_INTERVAL_TIME = 100;
    /**
     * 默认的摇一摇传感器阀值
     */
    public static final int DEFAULT_SHAKE_SPEED = 2000;

    /**
     * 传感器是否启动
     */
    private boolean isStart = false;

    /**
     * 手机上一个位置时重力感应坐标
     */
    private float mLastX = 0.0f;
    private float mLastY = 0.0f;
    private float mLastZ = 0.0f;
    /**
     * 上次检测时间
     */
    private long mLastUpdateTime;

    /**
     * @Title: ShakeListener Constructor
     * @Description: UMShakeSensor Constructor
     * @param context
     */
    public ShakeSensor(Context context) {
        this(context, ShakeSensor.DEFAULT_SHAKE_SPEED);
    }

    /**
     * @Title: UMShakeSensor
     * @Description: UMShakeSensor Constructor
     * @param context App的context
     * @param speedShreshold 摇一摇的速度阀值，默认为2000
     */
    public ShakeSensor(Context context, int speedShreshold) {
        mContext = context;
        mSpeedShreshold = speedShreshold;
    }

    /**
     * @Title: register
     * @Description: 注册传感器，返回是否注册成功
     * @return boolean 注册是否成功的标识
     * @throws
     */
    public boolean register() {
        // 获得传感器管理器
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            // 获得重力传感器
            mSensor = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册传感器
        if (mSensor != null) {
            isStart = mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.d(TAG, "### 传感器初始化失败!");
        }
        return isStart;
    }

    /**
     * @Title: stop
     * @Description: 在传感器没有被锁住的情况下,注销传感器，并且清理一些对象和状态
     * @throws
     */
    public void unregister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            isStart = false;
            mShakeListener = null;
        }
    }

    /**
     * (非 Javadoc)
     * 
     * @Title: onAccuracyChanged
     * @Description:
     * @param sensor
     * @param accuracy
     * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor,
     *      int)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "### onAccuracyChanged,  accuracy = " + accuracy);
    }

    /**
     * (非 Javadoc)
     * 
     * @Title: onSensorChanged
     * @Description: 重力感应器感应获得变化数据
     * @param event
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - mLastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }
        // 现在的时间变成last时间
        mLastUpdateTime = currentUpdateTime;

        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // 获得x,y,z的变化值
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;

        // 将现在的坐标变成last坐标
        mLastX = x;
        mLastY = y;
        mLastZ = z;

        // 获取摇晃速度
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
        // 达到速度阀值，回调给开发者
        if (speed >= mSpeedShreshold && mShakeListener != null) {
            mShakeListener.onShakeComplete(event);
        }

    } // end of onSensorChanged

    /**
     * 获取 mShakeListener
     * 
     * @return 返回 mShakeListener
     */
    public OnShakeListener getShakeListener() {
        return mShakeListener;
    }

    /**
     * 设置 mShakeListener
     * 
     * @param 对mShakeListener进行赋值
     */
    public void setShakeListener(OnShakeListener listener) {
        this.mShakeListener = listener;
    }

    /**
     * 获取摇一摇的速度阀值 mSpeedShreshold
     * 
     * @return 返回 mSpeedShreshold
     */
    public int getSpeedShreshold() {
        return mSpeedShreshold;
    }

    /**
     * 设置摇一摇的速度阀值 mSpeedShreshold
     * 
     * @param 对mSpeedShreshold进行赋值
     */
    public void setSpeedShreshold(int speedShreshold) {
        if (speedShreshold < 0) {
            speedShreshold = 0;
            Log.e(TAG, "speedShreshold速度阀值不能小于0，自动重置为0.");
        }
        this.mSpeedShreshold = speedShreshold;
    }

    /**
     * @Title: getSensor
     * @Description:获取传感器
     * @return
     * @throws
     */
    public Sensor getSensor() {
        return mSensor;
    }

    /**
     * @ClassName: OnSensorBaseListener
     * @Description: 空接口, 继承自DirectShareListener， 用于传感器动作结束后的动作和分享完成后的动作回调
     * @author Honghui He
     */
    public interface OnShakeListener {
        public void onShakeComplete(SensorEvent event);
    }

}
