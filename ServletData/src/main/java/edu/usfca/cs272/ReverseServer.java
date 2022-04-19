package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A simple example of using Jetty and servlets to use an HTML form.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class ReverseServer {
	/** The hard-coded port to run this server. */
	public static final int PORT = 8080;

	/**
	 * The logger to use (Jetty is configured via the pom.xml to use Log4j2)
	 */
	public static Logger log = LogManager.getLogger();

	/**
	 * Sets up a Jetty server configured to use the servlets defined in this class.
	 *
	 * @param args unused
	 * @throws Exception if unable to start web server
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(ReverseServlet.class, "/reverse");

		server.setHandler(handler);
		server.start();

		log.info("Server: {} with {} threads", server.getState(), server.getThreadPool().getThreads());
		server.join();
	}

	/**
	 * Outputs and responds to HTML form.
	 */
	public static class ReverseServlet extends HttpServlet {
		/** Class version for serialization, in [YEAR][TERM] format (unused). */
		private static final long serialVersionUID = 202210;

		/** The title to use for this webpage. */
		private static final String TITLE = "Reverse";

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			log.info(request);

			String html = """
					<!DOCTYPE html>
					<html lang="en">

					<head>
					  <meta charset="utf-8">
					  <title>%1$s</title>
					</head>

					<body>
					<h1>%1$s</h1>

					<form method="get" action="/reverse">
					  <p>
					    <input type="text" name="word" size="50"></input>
					  </p>

					  <p>
					    <button>Reverse</button>
					  </p>
					</form>

					<pre>
					%2$s
					</pre>

					</body>
					</html>
					""";

			String reversed = request.getParameter("word");
			log.info("{}", reversed);

			if (reversed == null || reversed.isBlank()) {
				reversed = "";
			}
			else {
				reversed = new StringBuilder(reversed).reverse().toString();
				reversed = StringEscapeUtils.escapeHtml4(reversed);
			}

			// wolf, stressed, pool
			// <b>hiii</b>
			// >b/<iiih>b<

			PrintWriter out = response.getWriter();
			out.printf(html, TITLE, reversed, Thread.currentThread().getName());

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
