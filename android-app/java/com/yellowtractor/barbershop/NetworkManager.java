package com.yellowtractor.barbershop;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NetworkManager {
    
    // ЗАМЕНИТЕ НА ВАШ URL ИЗ GOOGLE APPS SCRIPT
    private static final String GAS_WEB_APP_URL = "https://script.google.com/macros/s/ВАШ_ИДЕНТИФИКАТОР/exec";
    
    public interface BookingsCallback {
        void onSuccess(List<Booking> bookings);
        void onError(String error);
    }
    
    public static void getBookings(BookingsCallback callback) {
        new GetBookingsTask(callback).execute();
    }
    
    private static class GetBookingsTask extends AsyncTask<Void, Void, List<Booking>> {
        private BookingsCallback callback;
        private String errorMessage;
        
        public GetBookingsTask(BookingsCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected List<Booking> doInBackground(Void... voids) {
            List<Booking> bookings = new ArrayList<>();
            
            try {
                // Формируем URL запроса
                String requestUrl = GAS_WEB_APP_URL + "?action=getBookings";
                URL url = new URL(requestUrl);
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                // Проверяем код ответа
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP error code: " + responseCode);
                }
                
                // Читаем ответ
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );
                
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                reader.close();
                
                // Парсим JSON
                JSONArray jsonArray = new JSONArray(response.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Booking booking = new Booking();
                    
                    // Заполняем данные бронирования
                    booking.setDate(jsonObject.optString("Дата"));
                    booking.setTime(jsonObject.optString("Время"));
                    booking.setClientName(jsonObject.optString("Имя ребенка"));
                    booking.setClientAge(jsonObject.optString("Возраст"));
                    booking.setParentName(jsonObject.optString("Имя родителя"));
                    booking.setPhone(jsonObject.optString("Телефон"));
                    booking.setService(jsonObject.optString("Услуга"));
                    booking.setNotes(jsonObject.optString("Примечания"));
                    booking.setStatus(jsonObject.optString("Статус"));
                    
                    // Обработка даты создания
                    String createdAtStr = jsonObject.optString("Дата создания");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        try {
                            // Пытаемся распарсить дату
                            if (createdAtStr.contains("/Date(")) {
                                // Формат timestamp
                                String timestamp = createdAtStr.replace("/Date(", "").replace(")/", "");
                                long timeInMillis = Long.parseLong(timestamp);
                                booking.setCreatedAt(new Date(timeInMillis));
                            } else {
                                // Другие форматы даты
                                booking.setCreatedAt(dateFormat.parse(createdAtStr));
                            }
                        } catch (Exception e) {
                            // Если не удалось распарсить, оставляем null
                            booking.setCreatedAt(null);
                        }
                    }
                    
                    bookings.add(booking);
                }
                
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return null;
            }
            
            return bookings;
        }
        
        @Override
        protected void onPostExecute(List<Booking> bookings) {
            if (bookings != null) {
                callback.onSuccess(bookings);
            } else {
                callback.onError(errorMessage != null ? errorMessage : "Неизвестная ошибка");
            }
        }
    }
}
