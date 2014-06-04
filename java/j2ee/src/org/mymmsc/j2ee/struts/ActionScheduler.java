/**
 * 
 */
package org.mymmsc.j2ee.struts;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 定时任务调度
 * 
 * @author WangFeng
 * @remark 保护运行实体每次完整的运行
 */
public abstract class ActionScheduler extends TimerTask implements
		ServletContextListener {
	/** 定时器 */
	private Timer timer = null;
	/** 定时器运行状态 */
	private boolean isRunning = false;

	/**
	 * 任务调度单次执行单元
	 */
	protected abstract void execute();

	/**
	 * 同步方法, 获取运行状态
	 * @return
	 */
	public synchronized boolean isRunning() {
		return isRunning;
	}

	/**
	 * 同步方法, 设定运行状态
	 * @param isRunning
	 */
	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		timer.cancel();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		timer = new Timer(true);
		timer.schedule(this, 0, 2000);
	}

	@Override
	public void run() {
		if (!isRunning()) {
			setRunning(true);
			try {
				// 执行业务模块
				execute();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 绝对不允许抛出异常而造成流程中断
			}
			setRunning(false);
		}
	}
}
