package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.JpaDiaryRepository;
import zerobase.weather.repository.JpaWeatherRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    private final JpaDiaryRepository jpaDiaryRepository;

    private final JpaWeatherRepository jpaWeatherRepository;
    @Value("${openweathermap.key}")
    private String apiKey;

    public DiaryService(JpaDiaryRepository jpaDiaryRepository, JpaWeatherRepository jpaWeatherRepository) {
        this.jpaDiaryRepository = jpaDiaryRepository;
        this.jpaWeatherRepository = jpaWeatherRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        jpaWeatherRepository.save(getWeatherFromApi());
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {

        DateWeather dateWeather = getDateWeather(date);






//put into database
        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        diary.setDate(date);
        jpaDiaryRepository.save(diary);
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherFromDB = jpaWeatherRepository.findAllByDate(date);

        if (dateWeatherFromDB.size() == 0) {
            return getWeatherFromApi();
        } else {
            return dateWeatherFromDB.get(0);
        }
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            BufferedReader bufferedReader;
            if (responseCode == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            return response.toString();

        } catch (Exception e) {
            return "Failed to get resposne";
        }

    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }

    public List<Diary> readDiary(LocalDate date) {
        return jpaDiaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return jpaDiaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = false)
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = jpaDiaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        jpaDiaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        jpaDiaryRepository.deleteAllByDate(date);
    }

    private DateWeather getWeatherFromApi() {
        String weatherString = getWeatherString();

//        json parsing
        Map<String, Object> parseWeather = parseWeather(weatherString);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setDate(LocalDate.now());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));

        return dateWeather;
    }
}
