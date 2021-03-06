package net.explorviz.extension.comparison.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SetupListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetupListener.class);

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {

		LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
		LOGGER.info("Comparison Extension Servlet initialized.\n");
		LOGGER.info("* * * * * * * * * * * * * * * * * * *");

		// Comment out or remove line to use live monitoring data
		// Configuration.dummyMode = true;

	}

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		// Nothing to dispose
	}

}
