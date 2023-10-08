package momongo12.fintech.services.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.dto.WeatherApiErrorResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Momongo12
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeatherApiErrorHandler implements ResponseErrorHandler {

    ObjectMapper objectMapper;

    public WeatherApiErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (
                httpResponse.getStatusCode().is4xxClientError()
                        || httpResponse.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            HttpStatusCode httpStatus = response.getStatusCode();
            WeatherApiErrorResponse errorResponse = convertInputStreamToErrorResponse(response.getBody());

            throw new InternalServerErrorException("Something went wrong");
        }
    }

    private WeatherApiErrorResponse convertInputStreamToErrorResponse(InputStream inputStream) throws IOException {
        String s = new String(inputStream.readAllBytes());
        JsonNode jsonNode = objectMapper.readTree(s);

        return objectMapper.convertValue(jsonNode.get("error"), WeatherApiErrorResponse.class);
    }
}
