package com.yellowtractor.barbershop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        loadBookings();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Настройка pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadBookings();
        });

        // Цвета обновления
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_orange_dark,
            android.R.color.holo_orange_light
        );
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(this, bookingList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        
        NetworkManager.getBookings(new NetworkManager.BookingsCallback() {
            @Override
            public void onSuccess(List<Booking> bookings) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                // Обновляем список
                bookingList.clear();
                bookingList.addAll(bookings);
                adapter.notifyDataSetChanged();
                
                // Показываем сообщение если список пуст
                if (bookings.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    emptyStateText.setText("Нет записей\nПотяните вниз для обновления");
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Загружено записей: " + bookings.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                emptyStateText.setVisibility(View.VISIBLE);
                emptyStateText.setText("Ошибка загрузки\nПотяните вниз для повтора");
                
                Toast.makeText(MainActivity.this, "Ошибка загрузки: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
