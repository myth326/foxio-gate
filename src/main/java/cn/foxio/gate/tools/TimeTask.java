package cn.foxio.gate.tools;

import java.util.function.Consumer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.Data;

/**
 * 时间任务
 * @author lucky
 *
 */
@Data
public class TimeTask implements Job {

	/**
	 * 回调参数
	 */
	private Object param;
	/**
	 * 回调
	 */
	private Consumer<Object> callBack;
	/**
	 * id
	 */
	private int id;

	public void execute(JobExecutionContext context) throws JobExecutionException {

		//System.out.println("TimeTask id = "+id+", param:" + GsonUtil.toJson(param));

		if (callBack != null) {
			callBack.accept(param);
		}

	}
}
