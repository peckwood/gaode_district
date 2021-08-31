package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.utils.URIBuilder;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Application implements Serializable {
    private static final String key = "REPLACE_WITH_YOU_OWN_GAODE_API_KEY";
    private static final String url = "https://restapi.amap.com/v3/config/district?parameters";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static LocalDateTime lastQueryTime = LocalDateTime.MIN;
    //search '行政区查询' in https://lbs.amap.com/api/webservice/guide/tools/flowlevel
    private static final int MAX_QUERY_PER_SECOND = 50;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        //search china and get china and its provinces
        List<District> countries = getAllDistricts("中国");
        for (District country : countries) {
            country.setParent_adcode("0");
            session.save(country);
            if (country.getDistricts() != null && country.getDistricts().size() > 0) {
                //province
                for (District province : country.getDistricts()) {
                    province.setParent_adcode(country.getAdcode());
                    session.save(province);
                    List<District> provinces = getAllDistricts(province.getAdcode());
                    for (District provinceFromResponse : provinces) {
                        if (provinceFromResponse.getDistricts() != null && provinceFromResponse.getDistricts().size() > 0) {
                            provinceFromResponse.getDistricts().forEach(city -> {
                                city.setParent_adcode(province.getAdcode());
                                session.save(city);
                                if (city.getDistricts() != null && city.getDistricts().size() > 0) {
                                    city.getDistricts().forEach(district1 -> {
                                        district1.setParent_adcode(city.getAdcode());
                                        session.save(district1);
                                        if (district1.getDistricts() != null && district1.getDistricts().size() > 0) {
                                            for (District street : district1.getDistricts()) {
                                                street.setParent_adcode(district1.getAdcode());
                                                session.save(street);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }

                }

            }
        }
        session.getTransaction().commit();
        session.close();
    }

    private static Response convertToDistrictJson(String responseJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseJson, Response.class);
    }

    private static List<District> getDistricts(String keyword, int subDistrict, int page) throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URIBuilder(url)
                .addParameter("keywords", keyword)
                .addParameter("subdistrict", String.valueOf(subDistrict))
                .addParameter("key", key)
                .addParameter("page", String.valueOf(page))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        //make sure we are not exceeding the request frequency limit
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime waitTill = lastQueryTime.plus(1000 / MAX_QUERY_PER_SECOND, ChronoUnit.MILLIS);
        log.info("current time: {}", now);
        log.info("wait till:    {}", waitTill);
        if (now.isBefore(waitTill)) {
            long needToWaitMills = Duration.between(now, waitTill).toMillis();
            log.info("need to wait  {} mills", needToWaitMills);
            Thread.sleep(needToWaitMills);
        }
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        lastQueryTime = LocalDateTime.now();

        Response response = convertToDistrictJson(httpResponse.body());
        return response.getDistricts();
    }

    // the external "districts" property of response can return a maximum of 20 districts,
    //we need to use page to query the next page if this happens
    private static List<District> getAllDistricts(String keyword) throws URISyntaxException, IOException, InterruptedException {
        int page = 1;
        int subDistrict = 1;
        List<District> districts = getDistricts(keyword, subDistrict, page);
        List<District> allDistricts = new ArrayList<>(districts);
        if (districts.size() == 20) {
            districts = getDistricts(keyword, subDistrict, ++page);
            allDistricts.addAll(districts);
        }
        return allDistricts;
    }

}
