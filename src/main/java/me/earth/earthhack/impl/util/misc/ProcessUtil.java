package me.earth.earthhack.impl.util.misc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProcessUtil
{

	public static int exec(Class<?> klass) throws IOException, InterruptedException
	{
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
				File.separator + "bin" +
				File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getName();

		List<String> command = new LinkedList<>();
		command.add(javaBin);
		command.add("-cp");
		command.add(classpath);
		command.add(className);

		ProcessBuilder builder = new ProcessBuilder(command);

		Process process = builder.inheritIO().start();
		process.waitFor();
		return process.exitValue();
	}

}
