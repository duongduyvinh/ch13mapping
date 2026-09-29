package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import murach.business.User;
import murach.data.UserDB_1;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String url = "/index.html";
        String message = "";
        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";  // default action
        }
        // perform action and set URL to appropriate page
        if (action.equals("join")) {
            url = "/index.jsp";    // the "join" page
        }
        else if (action.equals("add")) {
            // get parameters from the request
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            // store data in User object
            User user = new User(firstName, lastName, email);
            // validate the parameters
            if (UserDB_1.emailExists(user.getEmail())) {
                message = "This email address already exists.<br>" +
                        "Please enter another email address.";
                url = "/index.jsp";
            }
            else {
                message = "";
                url = "/thanks.jsp";
                UserDB_1.insert(user);
                
                // Gửi email xác nhận bằng Brevo trực tiếp
                String apiKey = System.getenv("BREVO_API_KEY");
                String senderEmail = "duongduyvinh206@gmail.com";
                
                String emailSubject = "Chào mừng bạn đến với hệ thống của chúng tôi!";
                String htmlContent = "<h1>Xin chào " + user.getFirstName() + "!</h1>"
                        + "<p>Cảm ơn bạn đã đăng ký tài khoản thành công bằng email <b>" + user.getEmail() + "</b>.</p>";

                try {
                    String jsonPayload = String.format(
                        "{" +
                        "  \"sender\": {" +
                        "    \"name\": \"My Java App\"," +
                        "    \"email\": \"%s\"" +
                        "  }," +
                        "  \"to\": [" +
                        "    {" +
                        "      \"email\": \"%s\"" +
                        "    }" +
                        "  ]," +
                        "  \"subject\": \"%s\"," +
                        "  \"htmlContent\": \"%s\"" +
                        "}", 
                        senderEmail, user.getEmail(), emailSubject, htmlContent
                    );

                    HttpRequest httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                            .header("api-key", apiKey)
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                            .build();

                    HttpClient client = HttpClient.newHttpClient();
                    HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                    if (httpResponse.statusCode() == 201 || httpResponse.statusCode() == 200) {
                        System.out.println("Gửi email thành công tới " + user.getEmail() + "! Phản hồi: " + httpResponse.body());
                    } else {
                        System.out.println("Lỗi gửi email: Code " + httpResponse.statusCode() + " - " + httpResponse.body());
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi gọi API gửi email:");
                    e.printStackTrace();
                }
            }
            request.setAttribute("user", user);
            request.setAttribute("message", message);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
