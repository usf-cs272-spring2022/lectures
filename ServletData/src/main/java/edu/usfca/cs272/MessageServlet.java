package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// https://commons.apache.org/proper/commons-lang/download_lang.cgi

/**
 * The servlet class responsible for setting up a simple message board.
 *
 * @see MessageServer
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class MessageServlet extends HttpServlet {
	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202210;

	/** The title to use for this webpage. */
	private static final String TITLE = "Messages";

	/** The logger to use for this servlet. */
	private static Logger log = LogManager.getLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final ConcurrentLinkedQueue<String> messages;

	/** Template for HTML. **/
	private final String htmlTemplate;

	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources", "html");

	/**
	 * Initializes this message board. Each message board has its own collection
	 * of messages.
	 *
	 * @throws IOException if unable to read template
	 */
	public MessageServlet() throws IOException {
		super();
		messages = new ConcurrentLinkedQueue<>();
		htmlTemplate = Files.readString(BASE.resolve("index.html"), UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// compile all of the messages together
		// keep in mind multiple threads may access this at once!
		values.put("messages", String.join("\n\n", messages));

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		String username = request.getParameter("name");
		String message = request.getParameter("message");

		username = username == null ? "anonymous" : username;
		message = message == null ? "" : message;

		// avoid xss attacks using apache commons text
		username = StringEscapeUtils.escapeHtml4(username);
		message = StringEscapeUtils.escapeHtml4(message);

		String formatted = String.format(
				"<p>%s<br><font size=\"-2\">[ posted by %s at %s ]</font></p>",
				message, username, getDate());

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

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	public static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		LocalDateTime today = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return today.format(formatter);
	}
}
