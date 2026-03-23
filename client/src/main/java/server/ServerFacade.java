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

    public LoginRegisterResult register(RegisterRequest registerAttempt, String authToken) throws ResponseException {
        var request = buildRequest("POST", "/user", registerAttempt, authToken);
        var response = sendRequest(request);
        return handleResponse(response, LoginRegisterResult.class);
    }

    public LoginRegisterResult login(LoginRequest loginAttempt, String authToken) throws ResponseException{
        var request = buildRequest("POST", "/session", loginAttempt, authToken);
        var response = sendRequest(request);
        return handleResponse(response, LoginRegisterResult.class);
    }

    public void logout(String authToken) throws ResponseException{
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, LogoutResponse.class);
    }

    public void joinGame(JoinGameRequest joinAttempt, String authToken) throws ResponseException{
        var request = buildRequest("PUT", "/game", joinAttempt, authToken);
        var response = sendRequest(request);
        handleResponse(response, JoinGameRequest.class);
    }

    public void createGame(CreateGameRequest createAttempt, String authToken) throws ResponseException{
        var request = buildRequest("POST", "/game", createAttempt, authToken);
        var response = sendRequest(request);
        handleResponse(response, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class);
    }

    public void clear(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, authToken);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .header("authorization", authToken)
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
                throw ResponseException.fromJson(body);
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
