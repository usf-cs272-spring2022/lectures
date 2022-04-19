package edu.usfca.cs272;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BulmaMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 202210;
	private static final String TITLE = "Fancy Messages";
	private static Logger log = LogManager.getLogger();

	private final ConcurrentLinkedQueue<String> messages;

	private final String headTemplate;
	private final String footTemplate;
	private final String textTemplate;

	private static final Path BASE = Path.of("src", "main", "resources", "html");

	public BulmaMessageServlet() throws IOException {
		super();
		messages = new ConcurrentLinkedQueue<>();

		headTemplate = Files.readString(BASE.resolve("bulma-head.html"), UTF_8);
		footTemplate = Files.readString(BASE.resolve("bulma-foot.html"), UTF_8);
		textTemplate = Files.readString(BASE.resolve("bulma-text.html"), UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		values.put("updated", MessageServlet.getDate());

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		// TODO

		out.println(headTemplate);
		out.println(textTemplate);
		out.println(footTemplate);

		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		String name = request.getParameter("name");
		String message = request.getParameter("message");

		name = name == null ? "anonymous" : name;
		message = message == null ? "" : message;

		name = StringEscapeUtils.escapeHtml4(name);
		message = StringEscapeUtils.escapeHtml4(message);

		// TODO

		// only keep the latest 5 messages
		while (messages.size() > 5) {
			String first = messages.poll();
			log.info("Removing message: " + first);
		}

		response.sendRedirect(request.getServletPath());
	}
}
