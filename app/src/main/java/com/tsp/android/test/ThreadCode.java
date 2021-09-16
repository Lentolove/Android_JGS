//package com.tsp.android.test;
//
//import android.app.ActivityManager;
//import android.app.ApplicationErrorReport;
//import android.os.DeadObjectException;
//import android.os.Process;
//
//import java.util.Objects;
//
///**
// * author : shengping.tian
// * time   : 2021/08/25
// * desc   :
// * version: 1.0
// */
//
//
//class Thread implements Runnable {
//    //1.线程组
//    private ThreadGroup group;
//
//    //2.为当前线程单独设置的异常处理器
//    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
//
//    //3.所有线程通用的默认异常处理器
//    private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;
//
//    //4.当出现未捕获的异常 --》 线程分发异常
//    public final void dispatchUncaughtException(Throwable e) {
//        getUncaughtExceptionHandler().uncaughtException(this, e);
//    }
//
//    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
//        //6.默认情况下，线程的异常处理都为 null，除非我们手动设置，所有线程的异常都交由线程组统一处理
//        return uncaughtExceptionHandler != null ?
//                uncaughtExceptionHandler : group;
//    }
//
//    //7.线程的普通方法，设置当前线程的处理器
//    public void setUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler eh) {
//        uncaughtExceptionHandler = eh;
//    }
//}
//
//public class ThreadGroup implements Thread.UncaughtExceptionHandler {
//
//    public void uncaughtException(java.lang.Thread t, Throwable e) {
//        //1.获取线程默认异常处理器，即在 RuntimeInitJava  -> commitInit 方法中设置的 KillApplicationHandler 异常处理器
//        Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
//        if (ueh != null) {
//            //1.交由默认处理器来处理
//            ueh.uncaughtException(t, e);
//        } else if (!(e instanceof ThreadDeath)) {
//            //否则就打印在控制台上
//            System.err.print("Exception in thread \""
//                    + t.getName() + "\" ");
//            e.printStackTrace(System.err);
//        }
//    }
//}
//
//public class RuntimeInit {
//
//    protected static final void commonInit() {
//        //在这里通过静态调用 为线程设置了默认的异常处理器。
//        Thread.setDefaultUncaughtExceptionHandler(new KillApplicationHandler(loggingHandler));
//    }
//}
//
//
//private static class KillApplicationHandler implements Thread.UncaughtExceptionHandler {
//
//    @Override
//    public void uncaughtException(java.lang.Thread t, Throwable e) {
//        try {
//            //在这里弹出崩溃弹窗
//            ActivityManager.getService().handleApplicationCrash(
//                    mApplicationObject, new ApplicationErrorReport.ParcelableCrashInfo(e));
//        } catch (Throwable t2) {
//
//        } finally {
//            // Try everything to make sure this process goes away.
//            //杀死进程，并退出 10，正常退出为 0.
//            Process.killProcess(Process.myPid());
//            System.exit(10);
//        }
//    }
//}
