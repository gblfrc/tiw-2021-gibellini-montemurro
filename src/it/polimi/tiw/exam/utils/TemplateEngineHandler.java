package it.polimi.tiw.exam.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class TemplateEngineHandler {

	public static TemplateEngine getEngine(ServletContext context) throws UnavailableException {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		return templateEngine;
	}

}
