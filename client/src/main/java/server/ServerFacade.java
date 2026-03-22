package server;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginRegisterResult register(RegisterRequest registerAttempt) throws ResponseException {
        var request = buildRequest("POST", "/user", registerAttempt);
        var response = sendRequest(request);
        return handleResponse(response, LoginRegisterResult.class);
    }

    public LoginRegisterResult login(LoginRequest loginAttempt) throws ResponseException{
        var request = buildRequest("POST", "/session", loginAttempt);
        var response = sendRequest(request);
        return handleResponse(response, LoginRegisterResult.class);
    }

    public LogoutResponse logout(String logoutAttempt) throws ResponseException{
        var request = buildRequest("DELETE", "/session", logoutAttempt);
        var response = sendRequest(request);
        return handleResponse(response, LogoutResponse.class);
    }

    public void joinGame(JoinGameRequest joinAttempt) throws ResponseException{
        var request = buildRequest("PUT", "/game", joinAttempt);
        var response = sendRequest(request);
    }

    public CreateGameResponse createGame(CreateGameRequest createAttempt) throws ResponseException{
        var request = buildRequest("DELETE", "/session", createAttempt);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResponse.class);
    }

    public ListGamesResponse listGames() throws ResponseException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException("TESTING");
            }

            throw new ResponseException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
