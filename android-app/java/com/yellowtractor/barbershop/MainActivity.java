package com.yellowtractor.barbershop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        
        // Добавляем разделитель между элементами
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        
        NetworkManager.getBookings(new NetworkManager.BookingsCallback() {
            @Override
            public void onSuccess(List<Booking> bookings) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                // Обновляем список
                bookingList.clear();
                bookingList.addAll(bookings);
                adapter.notifyDataSetChanged();
                
                // Показываем количество записей
                if (bookings.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Записей нет", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Загружено записей: " + bookings.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                Toast.makeText(MainActivity.this, "Ошибка загрузки: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    // Класс для разделителя элементов
    public class SimpleDividerItemDecoration extends androidx.recyclerview.widget.DividerItemDecoration {
        public SimpleDividerItemDecoration(MainActivity context) {
            super(context, LinearLayoutManager.VERTICAL);
        }
    }
}
