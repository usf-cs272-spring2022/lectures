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
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * An alternative implemention of the {@MessageServlet} class but using the
 * Bulma CSS framework.
 *
 * @see MessageServlet
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class BulmaMessageServlet extends HttpServlet {
	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202210;

	/** The title to use for this webpage. */
	private static final String TITLE = "Fancy Messages";

	/** The logger to use for this servlet. */
	private static Logger log = LogManager.getLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final ConcurrentLinkedQueue<String> messages;

	/** Template for starting HTML (including <head> tag). **/
	private final String headTemplate;

	/** Template for ending HTML (including <foot> tag). **/
	private final String footTemplate;

	/** Template for individual message HTML. **/
	private final String textTemplate;

	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources", "html");

	/**
	 * Initializes this message board. Each message board has its own collection
	 * of messages.
	 *
	 * @throws IOException if unable to read templates
	 */
	public BulmaMessageServlet() throws IOException {
		super();
		messages = new ConcurrentLinkedQueue<>();

		// load templates
		headTemplate = Files.readString(BASE.resolve("bulma-head.html"), UTF_8);
		footTemplate = Files.readString(BASE.resolve("bulma-foot.html"), UTF_8);
		textTemplate = Files.readString(BASE.resolve("bulma-text.html"), UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		values.put("updated", MessageServlet.getDate());

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String head = replacer.replace(headTemplate);
		String foot = replacer.replace(footTemplate);

		// prepare response
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(head);

		if (messages.isEmpty()) {
			out.printf("    <p>No messages.</p>%n");
		}
		else {
			// could be multiple threads, but the data structure handles synchronization
			for (String message : messages) {
				out.println(message);
			}
		}

		out.println(foot);
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		String name = request.getParameter("name");
		String message = request.getParameter("message");

		name = name == null ? "anonymous" : name;
		message = message == null ? "" : message;

		// avoid xss attacks using apache commons text
		name = StringEscapeUtils.escapeHtml4(name);
		message = StringEscapeUtils.escapeHtml4(message);

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("name", name);
		values.put("message", message);
		values.put("timestamp", MessageServlet.getDate());

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String formatted = replacer.replace(textTemplate);

		// keep in mind multiple threads may access at once
		// but we are using a thread-safe data structure here to avoid any issues
		messages.add(formatted);

		// only keep the latest 5 messages
		while (messages.size() > 5) {
			String first = messages.poll();
			log.info("Removing message: " + first);
		}

		response.sendRedirect(request.getServletPath());
	}
}
