package com.schibsted.hackathons.example.gotquotes.endpoints;

import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.channel.StringTransformer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import net.sf.cglib.core.Local;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import scmspain.karyon.restrouter.annotation.Endpoint;
import scmspain.karyon.restrouter.annotation.Path;
import scmspain.karyon.restrouter.annotation.PathParam;
import scmspain.karyon.restrouter.annotation.QueryParam;

import javax.ws.rs.HttpMethod;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Singleton
@Endpoint
public class DilbertController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DilbertController.class);
    final String dateTimeFormatPattern = "yyyy-MM-dd";

    public DilbertController() {
    }

    // This endpoint shouldn't be removed since will be always needed
    // Healthcheck endpoint needed by Asgard to validate the service is working
    @Path(value = "/healthcheck", method = HttpMethod.GET)
    public Observable<Void> healthcheck(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpResponseStatus.OK);
        response.write("{\"status\":\"ok\"}", StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }

    // This endpoint shouldn't be removed since will be always needed
    // Healthcheck endpoint needed by Prana to validate the service is working
    @Path(value = "/Status", method = HttpMethod.GET)
    public Observable<Void> status(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpResponseStatus.OK);
        response.write("Eureka!", StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }

    @Path(value = "/api/dilbert", method = HttpMethod.GET)
    public Observable<Void> getQuote(HttpServerResponse<ByteBuf> response) {
        return getQuote(response, getDefaultDate());
    }

        @Path(value = "/api/dilbert/{date}", method = HttpMethod.GET)
    public Observable<Void> getQuote(HttpServerResponse<ByteBuf> response, @PathParam("date") String date) {
        LOGGER.debug("Dilber 'date' received: {}", date);
        JSONObject content = new JSONObject();

        try {
            LocalDate localDate = getDate(date);
            content.put("date", localDate);

        } catch (JSONException e) {
            LOGGER.error("Error creating json response.", e);
            return Observable.error(e);
        } catch (MalformedURLException e) {
            LOGGER.error("Error creating URL",e);
            return Observable.error(e);
        }

        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.write(content.toString(), StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }

    private LocalDate getDate(String param) throws MalformedURLException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatPattern);
        LocalDate dateTime = (param != null) ? LocalDate.parse(param,formatter) : LocalDate.now();
        LOGGER.info("Returning " + dateTime);
        return dateTime;
    }

    private String getDefaultDate () {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatPattern);
        LocalDate now = LocalDate.now();
        return now.format(formatter);
    }
}
