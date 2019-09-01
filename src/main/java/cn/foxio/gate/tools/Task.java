package cn.foxio.gate.tools;

import java.util.function.Consumer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.Data;

@Data
public class Task implements Job {

	/**
	 * 回调参数
	 */
	private Object param;
	/**
	 * 回调
	 */
	private Consumer<Object> callBack;
	
	private int id;

	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("param:" + param);

		if (callBack != null) {
			callBack.accept(param);
		}

	}
}
