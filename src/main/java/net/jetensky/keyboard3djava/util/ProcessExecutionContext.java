package net.jetensky.keyboard3djava.util;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class ProcessExecutionContext {
	public InputStream errorStream=null,inputStream=null;
	public OutputStream outputStream=null;
	public Process process;

	private static Runtime runtime = Runtime.getRuntime();

	public String execute(String command) {
		try {
			process = runtime.exec(command);
			errorStream = process.getErrorStream();
			inputStream = process.getInputStream();
			outputStream = process.getOutputStream();
			process.waitFor();
			return IOUtils.toString(errorStream);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot execute command " + command, e);
		}

	}

	public void handleProcessDestroy() {
		IOUtils.closeQuietly(errorStream);
		IOUtils.closeQuietly(inputStream);
		IOUtils.closeQuietly(outputStream);
		if(process!=null) process.destroy();
	}
}