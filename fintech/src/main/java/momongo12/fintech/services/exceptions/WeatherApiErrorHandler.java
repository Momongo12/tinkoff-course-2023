package momongo12.fintech.services.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.controllers.exceptions.NotFoundException;
import momongo12.fintech.api.dto.WeatherApiErrorResponse;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Momongo12
 * @version 1.0
 */
@RequiredArgsConstructor
public class WeatherApiErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            WeatherApiErrorResponse errorResponse = convertInputStreamToErrorResponse(response.getBody());

            if (errorResponse.getCode() == 1006) {
                throw new NotFoundException("No location matching the transmitted region was found");
            } else if (errorResponse.getCode() >= 2006 && errorResponse.getCode() <= 2009) {
                throw new WeatherApiTokenKeyException(errorResponse.getMessage());
            }

            throw new InternalServerErrorException(errorResponse.getMessage());
        }
    }

    private WeatherApiErrorResponse convertInputStreamToErrorResponse(InputStream inputStream) throws IOException {
        String s = new String(inputStream.readAllBytes());
        JsonNode jsonNode = objectMapper.readTree(s);

        return objectMapper.convertValue(jsonNode.get("error"), WeatherApiErrorResponse.class);
    }
}
