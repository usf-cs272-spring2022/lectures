package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 202210;
	private static final String TITLE = "Messages";

	private static Logger log = LogManager.getLogger();

	private final ConcurrentLinkedQueue<String> messages;
	private final String htmlTemplate;

	private static final Path BASE = Path.of("src", "main", "resources", "html");

	public MessageServlet() throws IOException {
		super();
		messages = new ConcurrentLinkedQueue<>();
		htmlTemplate = Files.readString(BASE.resolve("index.html"), UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		// TODO

		out.println(TITLE);
		out.println(messages);
		out.println(htmlTemplate);
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("{} handling: {}", this.hashCode(), request);

		// TODO
		// String format = "<p>%s<br><font size=\"-2\">[ posted by %s at %s ]</font></p>";
	}

	public static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		LocalDateTime today = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return today.format(formatter);
	}
}
